package com.lancas.vswap.ship.data;

import com.lancas.vswap.foundation.TriTuple;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.primitives.AABBic;

import java.util.List;
import java.util.function.BiConsumer;

public interface IShipSchemeRandomReader {
    public IShipSchemeRandomReader getRandomReader();

    public @NotNull AABBic getLocalAabbContainsCoordinate();
    public @NotNull AABBic getLocalAabbContainsShape();
    public BlockState getBlockStateByLocalPos(BlockPos pos);
    public void foreachBlockInLocal(BiConsumer<BlockPos, BlockState> consumer);

    public List<TriTuple<BlockPos, BlockState, CompoundTag>> getCopyOfAllBlocksOverwrite(List<TriTuple<BlockPos, BlockState, CompoundTag>> dest);

    public boolean isEmpty();
    //public boolean isEmpty();
}
