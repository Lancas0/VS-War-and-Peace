package com.lancas.vs_wap.foundation.api;

import java.util.Objects;

public record NoOrderTuple<T>(T a, T b) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NoOrderTuple<?> that = (NoOrderTuple<?>) o;

        if (Objects.equals(a, that.a) && Objects.equals(b, that.b))
            return true;

        if (Objects.equals(a, that.b) && Objects.equals(b, that.a))
            return true;

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b) + Objects.hash(b, a);
    }

    public boolean has(T v) { return Objects.equals(a, v) || Objects.equals(b, v); }
}
