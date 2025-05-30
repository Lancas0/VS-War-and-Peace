package com.lancas.vswap.foundation.api;

import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public class Immutable<T> {
    public static Immutable<ItemStack> of(ItemStack stack) {
        return new Immutable<>(stack, ItemStack::copy);
    }

    private final T inVal;
    private final Function<T, T> copier;
    private Function<T, T> changer = null;  //should not change

    public Immutable(T inInVal, Function<T, T> inCopier) {
        inVal = inInVal;
        copier = inCopier;
    }

    public void change(Function<T, T> newChanger) {
        if (changer == null)
            changer = newChanger;
        else
            changer = changer.andThen(newChanger);
    }
    public void change(Consumer<T> newChanger) {
        Function<T, T> c = t -> { newChanger.accept(t); return t; };
        if (changer == null)
            changer = c;
        else
            changer = changer.andThen(c);
    }

    public boolean anyChange() { return changer != null; }

    public T getIn() { return inVal; }
    public T getResult() {
        T copy = copier.apply(inVal);
        if (changer != null)
            copy = changer.apply(copy);
        return copy;
    }
}
