package com.lancas.vswap.subproject.sandbox.api;

import net.minecraft.nbt.CompoundTag;

public interface ISavedObject<T> {
    public CompoundTag saved();
    public T load(CompoundTag tag);
}
