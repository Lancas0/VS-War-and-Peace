package com.lancas.vs_wap.event.api;

import java.util.function.Consumer;

public interface ISingleEvent<T> {
    public void invokeAll(T t);
    public void add(Consumer<T> listener);
    public void remove(Consumer<T> listener);
}
