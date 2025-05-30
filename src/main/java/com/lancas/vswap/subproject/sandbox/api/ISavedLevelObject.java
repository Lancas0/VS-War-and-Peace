package com.lancas.vswap.subproject.sandbox.api;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

public interface ISavedLevelObject<T extends ISavedLevelObject<T>> {
    public CompoundTag saved(ServerLevel level);
    public T load(ServerLevel level, CompoundTag tag);
}
