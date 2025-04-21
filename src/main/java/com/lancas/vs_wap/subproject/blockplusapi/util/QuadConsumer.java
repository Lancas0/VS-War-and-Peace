package com.lancas.vs_wap.subproject.blockplusapi.util;

@FunctionalInterface
public interface QuadConsumer <T, U, V, W> {
    public void apply(T t, U u, V v, W w);
}
