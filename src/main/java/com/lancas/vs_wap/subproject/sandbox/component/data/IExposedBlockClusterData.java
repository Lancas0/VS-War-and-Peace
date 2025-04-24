package com.lancas.vs_wap.subproject.sandbox.component.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public interface IExposedBlockClusterData extends IExposedComponentData<SandBoxBlockClusterData> {
    public BlockState getBlockState(BlockPos localPos);
    public boolean contains(BlockPos localPos);
    public Iterable<Map.Entry<BlockPos, BlockState>> allBlocks();
    public Iterable<BlockState> getBlockStates();
    public Iterable<BlockPos> getLocalPoses();
}
