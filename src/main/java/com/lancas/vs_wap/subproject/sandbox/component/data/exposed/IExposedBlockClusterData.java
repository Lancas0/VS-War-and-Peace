package com.lancas.vs_wap.subproject.sandbox.component.data.exposed;

import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxBlockClusterData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3ic;

import java.util.Map;
import java.util.function.BiConsumer;

public interface IExposedBlockClusterData extends IExposedComponentData<SandBoxBlockClusterData> {
    public BlockState getBlockState(Vector3ic localPos);
    public boolean contains(Vector3ic localPos);
    //public Iterable<Map.Entry<BlockPos, BlockState>> allBlocks();
    //public Iterable<BlockState> getBlockStates();
    //public Iterable<BlockPos> getLocalPoses();
    public void foreach(BiConsumer<Vector3ic, BlockState> consumer);
}
