package com.lancas.vswap.event.api;

import java.util.function.BiConsumer;

public interface IBiEvent<T, U> {
    //public void invokeAll(T t, U u);
    public void addListener(BiConsumer<T, U> listener);
    public void remove(BiConsumer<T, U> listener);
}
