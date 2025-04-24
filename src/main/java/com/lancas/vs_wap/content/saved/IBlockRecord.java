package com.lancas.vs_wap.content.saved;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.minecraft.core.BlockPos;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
public interface IBlockRecord {

    public default boolean shouldTick() { return false; }
    public default void onTick(BlockPos bp) {}
}
