package com.lancas.vs_wap.event.listener;

import com.lancas.vs_wap.subproject.blockplusapi.util.QuadConsumer;

public abstract class QuadRemoveAfterSuccessListener<T, U, V, W> implements QuadConsumer<T, U, V, W> {
    public abstract boolean isSuccess();
}
