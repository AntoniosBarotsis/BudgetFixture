package io.github.antoniosbarotsis;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class Util {
    static Set<Class<?>> findAllGenerators(String packageName) {
        var lines = listAllClassesFromPackage(packageName);

        return lines.stream()
            .filter(line -> line._1().endsWith(".class"))
            .map(line -> Util.getClass(line._1(), line._2()))
            .filter(el ->
                el.getSuperclass() != null &&
                el.getSuperclass().equals(Generator.class) &&
                Util.isNotMain(el)
            )
            .collect(Collectors.toSet());
    }

    private static Class<?> getClass(String className, String packageName) {
        var newClassName = packageName
            .replaceAll("/", ".") + "." +
            className.substring(0, className.lastIndexOf('.'));

        try {
            return Class.forName(newClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class " + newClassName + " was not found");
        }
    }

    private static boolean isNotMain(Class<?> el) {
        try {
            var newName = el.getName().replaceAll("\\$\\d+", "");
            el.getClassLoader().loadClass(newName).getMethod("main", String[].class);
            return false;
        } catch (NoSuchMethodException ignored) {
            // if the method doesn't exist then it's not main
            return true;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Tuple<String, String>> listAllClassesFromPackage(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName);
        if (stream == null) {
            return List.of();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        var list = reader.lines().toList();
        var res = new LinkedList<Tuple<String, String>>();

        for (String s : list) {
            if (s.endsWith(".class")) {
                res.add(new Tuple<>(s, packageName));
            }

            res.addAll(listAllClassesFromPackage(packageName + "/" + s));
        }

        return res;
    }
}
