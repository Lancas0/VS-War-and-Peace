package com.lancas.vs_wap.ship.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.primitives.AABBic;

import java.util.function.BiConsumer;

public interface IShipSchemeRandomReader {
    public IShipSchemeRandomReader getRandomReader();

    public AABBic getLocalAABB();
    public BlockState getBlockStateByLocalPos(BlockPos pos);
    public void foreachBlockInLocal(BiConsumer<BlockPos, BlockState> consumer);

    //public boolean isEmpty();
}
