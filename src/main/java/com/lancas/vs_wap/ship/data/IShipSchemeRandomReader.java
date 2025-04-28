package com.lancas.vs_wap.ship.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;

public interface IShipSchemeRandomReader {
    public IShipSchemeRandomReader getRandomReader();

    public BlockState getBlockStateByLocalBp(BlockPos pos);
    public void foreachBlock(BiConsumer<BlockPos, BlockState> consumer);

    public boolean isEmpty();
}
