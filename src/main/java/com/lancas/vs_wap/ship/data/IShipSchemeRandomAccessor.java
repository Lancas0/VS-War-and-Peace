package com.lancas.vs_wap.ship.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;

public interface IShipSchemeRandomAccessor {
    public BlockState getBlockState(BlockPos pos);
    public void foreachBlock(BiConsumer<BlockPos, BlockState> consumer);
}
