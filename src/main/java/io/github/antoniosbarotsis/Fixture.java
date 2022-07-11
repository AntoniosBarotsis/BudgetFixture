package io.github.antoniosbarotsis;

import io.github.antoniosbarotsis.strategies.ConstructorFinder;
import io.github.antoniosbarotsis.strategies.LongestConstructorStrategy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Collectors;

public class Fixture {
    private static final ConstructorFinder constructorFinder = new LongestConstructorStrategy<>();
    private static final HashMap<String, Generator<?>> map = new HashMap<>();

    private Fixture() { }

    private static <T> Object[] getConstructorParams(Constructor<T> ctor) {
        return Arrays.stream(ctor.getParameters()).map(Parameter::getType).map(Class::getName)
            .map(el -> getMap().get(el).call()).toArray();
    }

    public static <T> T Generate(Class<T> obj) {
        try {
            var ctor = (Constructor<T>) constructorFinder.getConstructor(obj);

            T res = ctor.newInstance(getConstructorParams(ctor));

            useSetters(res);

            return res;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> void useSetters(T res) {
        var fieldTypes = Arrays.stream(res.getClass().getDeclaredFields())
            .collect(Collectors.toMap(Field::getName, Field::getType));

        var setters = Arrays.stream(res.getClass().getDeclaredMethods())
            .filter(el -> el.getName().toLowerCase(Locale.ROOT).contains("set")).toList();

        // for each field
        fieldTypes.forEach((fieldName, fieldType) -> {
            // Find related setter if exists
            var setter = setters.stream()
                .filter(el -> el.getName().toLowerCase(Locale.ROOT).contains(fieldName))
                .findFirst();

            setter.ifPresent(el -> {
                try {
                    // Find generator for given field type
                    var generator = getMap().get(fieldType.getName());

                    // Pass generator output to setter
                    el.invoke(res, generator.call());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    public static <T> void Register(Generator<T> callable) {
        getMap().put(callable.getType().getName(), callable);
    }

    private static HashMap<String, Generator<?>> getMap() {
        return map;
    }

    private static String getPackageName() throws ClassNotFoundException {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();

        // this corresponds to the context that called this method
        StackTraceElement e = stacktrace[3];
        return ClassLoader
            .getSystemClassLoader()
            .loadClass(e.getClassName())
            .getPackageName()
            .replaceAll("\\.", "/");
    }

    /**
     * Finds and registers all classes that extend {@link Generator} in the package where the
     * method was called from including nested packages.
     */
    public static void registerGenerators() {
        try {
            var res = Util.findAllGenerators(getPackageName());
            res.forEach(el -> {
                try {
                    // I know this is a mess, the actual code is here
                    Register((Generator<?>) el.getConstructor().newInstance());
                    // cheers
                } catch (NoSuchMethodException e) {
                  throw new RuntimeException("No constructor was found for class " + el.getName());
                } catch (IllegalAccessException e) {
                    try {
                        throw new RuntimeException("Constructor " + el.getConstructor() + " is not public");
                    } catch (NoSuchMethodException ignored) {}  // already caught above
                } catch (InstantiationException e) {
                    throw new RuntimeException("The class " + el.getName() + " could not be instantiated");
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
