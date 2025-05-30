package com.lancas.vswap.foundation;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class BiKey<T, V> {
    private T t;
    private V v;

    public BiKey(@NotNull T inT) { setFirst(inT); }
    public BiKey(Object placeholder, @NotNull V inV) { setSecond(inV); }

    public void setFirst(@NotNull T inT) {
        t = inT;
        v = firstToSecond();
    }
    public void setSecond(@NotNull V inV) {
        v = inV;
        t = secondToFirst();
    }

    public abstract @NotNull V firstToSecond();
    public abstract @NotNull T secondToFirst();
    /*public void fill() {
        if (t == null && v == null) return;  //can't fill

        if (t == null) v = firstToSecond();
        if (v == null) t = secondToFirst();

        if (t == null || v == null) {
            EzDebug.error("[BiKey]t or v can't be null after fill!");
        }
    }*/

    @Override
    public boolean equals(Object o) {
        /*

        boolean thisNull = (t == null && v == null);
        boolean otherNull = (other.t == null && other.v == null);

        if (thisNull && otherNull) return true;
        if (thisNull != otherNull) return false;

        if (t != null && other.t != null) return t.equals(other.t);
        if (v != null && other.v != null) return v.equals(other.v);

        this.fill();
        if (t )*/
        if (o == null || getClass() != o.getClass()) return false;
        BiKey<?, ?> other = (BiKey<?, ?>) o;
        return Objects.equals(t, other.t);
    }
    @Override
    public int hashCode() {
        return Objects.hash(t);
    }
}
