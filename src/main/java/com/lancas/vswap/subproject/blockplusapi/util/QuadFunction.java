package com.lancas.vswap.subproject.blockplusapi.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@FunctionalInterface
public interface QuadFunction<P1, P2, P3, P4, R> {
    R apply(P1 var1, P2 var2, P3 var3, P4 var4);

    default <S> QuadFunction<P1, P2, P3, P4, S> andThen(@NotNull final Function<? super R, ? extends S> after) {
        return (final P1 var1, final P2 var2, final P3 var3, final P4 var4) -> after.apply(apply(var1, var2, var3, var4));
    }
}
/*
@FunctionalInterface
public interface PentaFunction<P1, P2, P3, P4, P5, R> {
    R apply(P1 var1, P2 var2, P3 var3, P4 var4, P5 var5);
}

@FunctionalInterface
public interface QuadFunction<P1, P2, P3, P4, R> {
    R apply(P1 var1, P2 var2, P3 var3, P4 var4);
}

@FunctionalInterface
public interface TriFunction<P1, P2, P3, R> {
    R apply(P1 var1, P2 var2, P3 var3);
}*/