package com.lancas.vs_wap.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class Util {
    public static @Nullable <T> T NonNullIfPossible(@NotNull Supplier<T> a, @NotNull Supplier<T> b) {
        T aget = a.get();
        if (aget != null) return aget;
        return b.get();
    }
}
