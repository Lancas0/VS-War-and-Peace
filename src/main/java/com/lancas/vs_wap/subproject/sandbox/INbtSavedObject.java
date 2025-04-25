package com.lancas.vs_wap.subproject.sandbox;


import net.minecraft.nbt.CompoundTag;

public interface INbtSavedObject<T extends INbtSavedObject<T>> {
    public CompoundTag saved();
    public T load(CompoundTag tag);
}
