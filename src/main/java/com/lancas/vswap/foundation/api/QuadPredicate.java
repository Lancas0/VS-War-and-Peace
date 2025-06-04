package com.lancas.vswap.foundation.api;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@FunctionalInterface
public interface QuadPredicate<T, U, V, W> {
    public boolean test(T t, U u, V v, W w);

    public default QuadPredicate<T, U, V, W> and(@NotNull QuadPredicate<T, U, V, W> next) {
        return (t, u, v, w) -> this.test(t, u, v, w) && next.test(t, u, v, w);
    }
}
