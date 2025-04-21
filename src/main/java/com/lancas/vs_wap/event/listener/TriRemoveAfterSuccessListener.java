package com.lancas.vs_wap.event.listener;

import org.apache.logging.log4j.util.TriConsumer;

public abstract class TriRemoveAfterSuccessListener<T, U, V> implements TriConsumer<T, U, V> {
    @Override
    public abstract void accept(T t, U u, V v);
    public abstract boolean isSuccess();
}
