package com.lancas.vswap.event.listener;


import java.util.function.BiConsumer;

public abstract class BiRemoveAfterSuccessListener<T, U> implements BiConsumer<T, U> {
    @Override
    public abstract void accept(T t, U u);
    public abstract boolean isSuccess();
}
