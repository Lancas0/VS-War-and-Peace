package com.lancas.vswap.subproject.blockplusapi.util;

@FunctionalInterface
public interface QuadConsumer <T, U, V, W> {
    public void apply(T t, U u, V v, W w);
}
