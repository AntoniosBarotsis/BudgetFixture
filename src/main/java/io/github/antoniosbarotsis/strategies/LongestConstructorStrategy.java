package io.github.antoniosbarotsis.strategies;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;

public class LongestConstructorStrategy<T> implements ConstructorFinder<T> {
    @Override
    public Constructor<T> getConstructor(Class<T> obj) {
        var ctors = obj.getConstructors();
        var tmp = Arrays.stream(ctors)
            .max(Comparator.comparingInt(x -> x.getParameterTypes().length));

        return (Constructor<T>) tmp.get();
    }
}
