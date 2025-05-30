package com.lancas.vswap.foundation.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Dest<T> {
    public static <TT> void setIfExistDest(@Nullable Dest<TT> dest, TT v) {
        if (dest == null) return;
        dest.set(v);
    }
    public static <TT> void getAndSetIfExistDest(@Nullable Dest<TT> dest, Supplier<TT> vs) {
        if (dest == null) return;
        dest.set(vs.get());
    }

    public static <TT> TT getIfElse(@NotNull Dest<TT> dest, @NotNull Predicate<TT> predicate, TT elseVal) {
        if (predicate.test(dest.value))
            return dest.value;
        return elseVal;
    }
    public static <TT> TT getNonNullElse(@NotNull Dest<TT> dest, TT elseVal) {
        if (dest.value != null)
            return dest.value;
        return elseVal;
    }
    public static <TT extends Comparable<TT>> TT getAtLeast(@NotNull Dest<TT> dest, TT atLeast) {
        if (dest.value != null && dest.value.compareTo(atLeast) >= 0)
            return dest.value;
        return atLeast;
    }
    public static <TT extends Comparable<TT>> TT getNoMoreThan(@NotNull Dest<TT> dest, TT noMoreThan) {
        if (dest.value != null && dest.value.compareTo(noMoreThan) <= 0)
            return dest.value;
        return noMoreThan;
    }
    public static <TT extends Comparable<TT>> TT getInRange(@NotNull Dest<TT> dest, TT lower, TT upper) {
        if (dest.value == null) return lower;
        if (dest.value.compareTo(upper) > 0) return upper;
        if (dest.value.compareTo(lower) < 0) return lower;

        return dest.value;
    }

    public static <TT> Dest<TT> __() {
        return new Dest<>();
    }

    private T value;

    public Dest() { }
    public Dest(T defaultVal) { value = defaultVal; }

    public T get() { return value; }
    public void set(T value) { this.value = value; }
    public void update(Function<T, T> updater) { value = updater.apply(value); }

    public boolean hasValue() { return value != null; }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dest<?> dest = (Dest<?>) o;
        return Objects.equals(value, dest.value);
    }
    public boolean equalsValue(T otherVal) {
        if (otherVal == null && value == null) return true;
        if (otherVal != null && value != null) return value.equals(otherVal);
        return false;  //one null and one not null
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "Dest{" +
            "value=" + value +
            '}';
    }
}
