package com.lancas.vs_wap.event.api;

import com.lancas.vs_wap.subproject.blockplusapi.util.QuadConsumer;

public interface IQuadEvent<T, U, V, W> {
    public void invokeAll(T t, U u, V v, W w);
    public void add(QuadConsumer<T, U, V, W> listener);
    public void remove(QuadConsumer<T, U, V, W> listener);
}
