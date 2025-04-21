package com.lancas.vs_wap.event.api;

import java.util.function.BiConsumer;

public interface IBiEvent<T, U> {
    public void invokeAll(T t, U u);
    public void add(BiConsumer<T, U> listener);
    public void remove(BiConsumer<T, U> listener);
}
