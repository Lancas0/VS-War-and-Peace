package com.lancas.vswap.content.saved.blockrecord;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.minecraft.core.BlockPos;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
public interface IBlockRecord {
    /*@FunctionalInterface
    public static interface ChunkLoadedTicker {
        public void tick(ServerLevel level, BlockPos bp);
    }
    @FunctionalInterface
    public static interface AlwaysTicker {
        public void tick(ServerLevel level, BlockPos bp, boolean isChunkLoaded);
    }
    @FunctionalInterface
    public static interface AsyncTicker {
        public void tick(BlockPos bp);
    }*/

    public default void onAdded(BlockPos bp, BlockRecordRWMgr mgr) {}
    public default void onRemoved(BlockPos bp, BlockRecordRWMgr mgr) {}
    //public abstract BlockPos getBlockPos();

    /*public default void getTickerData(Dest<Boolean> shouldTick, Dest<Boolean>) { return false; }
    public default void tickWhenLoaded(BlockPos bp) {}
    public default void tickForever(BlockPos bp) {}*/
    /*public default ChunkLoadedTicker chunkLoadedTicker() { return null; }
    public default AlwaysTicker alwaysTicker() { return null; }
    public default AsyncTicker asyncTicker(Dest<Long> periodMs) { return null; }*/
}
