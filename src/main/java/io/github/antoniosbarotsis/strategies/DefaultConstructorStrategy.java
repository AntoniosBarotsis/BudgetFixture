package io.github.antoniosbarotsis.strategies;

import java.lang.reflect.Constructor;

public class DefaultConstructorStrategy<T> implements ConstructorFinder<T>{
    @Override
    public Constructor<T> getConstructor(Class<T> obj) throws NoSuchMethodException {
        return obj.getConstructor();
    }
}
