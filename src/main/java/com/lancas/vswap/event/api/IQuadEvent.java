package com.lancas.vswap.event.api;

import com.lancas.vswap.subproject.blockplusapi.util.QuadConsumer;

public interface IQuadEvent<T, U, V, W> {
    //public void invokeAll(T t, U u, V v, W w);
    public IQuadEvent<T, U, V, W> addListener(QuadConsumer<T, U, V, W> listener);
    public IQuadEvent<T, U, V, W> remove(QuadConsumer<T, U, V, W> listener);
}
