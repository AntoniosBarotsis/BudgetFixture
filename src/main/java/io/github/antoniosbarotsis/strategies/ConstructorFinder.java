package io.github.antoniosbarotsis.strategies;

import java.lang.reflect.Constructor;

public interface ConstructorFinder<T> {
    Constructor<T> getConstructor(Class<T> obj) throws NoSuchMethodException;
}
