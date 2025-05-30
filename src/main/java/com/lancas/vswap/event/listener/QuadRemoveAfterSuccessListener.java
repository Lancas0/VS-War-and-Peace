package com.lancas.vswap.event.listener;

import com.lancas.vswap.subproject.blockplusapi.util.QuadConsumer;

public abstract class QuadRemoveAfterSuccessListener<T, U, V, W> implements QuadConsumer<T, U, V, W> {
    public abstract boolean isSuccess();
}
