package com.lancas.vswap.subproject.blockplusapi.util;

@FunctionalInterface
public interface PentaConsumer<T, U, V, W, X> {
    public void apply(T t, U u, V v, W w, X x);
}
