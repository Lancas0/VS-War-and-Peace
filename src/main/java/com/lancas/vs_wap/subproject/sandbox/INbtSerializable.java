package com.lancas.vs_wap.subproject.sandbox;

import net.minecraft.nbt.CompoundTag;

public interface INbtSerializable<T extends INbtSerializable<T>> {
    public CompoundTag saved();
    public T load(CompoundTag tag);
}
