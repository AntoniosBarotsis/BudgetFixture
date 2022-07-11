package io.github.antoniosbarotsis;

import java.lang.reflect.ParameterizedType;

public abstract class Generator<T> {
    public abstract T call();
    final Class<T> getType() {
        ParameterizedType superclass = (ParameterizedType) getClass().getGenericSuperclass();

        return (Class<T>) superclass.getActualTypeArguments()[0];
    }
}
