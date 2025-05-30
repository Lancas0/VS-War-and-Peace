package com.lancas.vswap.event.api;

import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

public interface ITriEvent<T, U, V> {
    //public void invokeAll(T t, U u, V v);
    public void addListener(@NotNull TriConsumer<T, U, V> listener);
    public void remove(@NotNull TriConsumer<T, U, V> listener);
}
