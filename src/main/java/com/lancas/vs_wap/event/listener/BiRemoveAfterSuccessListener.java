package com.lancas.vs_wap.event.listener;


import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.BiConsumer;

public abstract class BiRemoveAfterSuccessListener<T, U> implements BiConsumer<T, U> {
    @Override
    public abstract void accept(T t, U u);
    public abstract boolean isSuccess();
}
