package com.lancas.vswap.subproject.sandbox.component.data.reader;

import com.lancas.vswap.subproject.sandbox.api.component.IComponentDataReader;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3ic;
import org.joml.primitives.AABBdc;

import java.util.Map;
import java.util.function.BiConsumer;

public interface IBlockClusterDataReader extends IComponentDataReader<BlockClusterData> {
    public BlockState getBlockState(Vector3ic localPos);
    public boolean contains(Vector3ic localPos);
    public int getBlockCnt();
    public default boolean isEmpty() { return getBlockCnt() == 0; }
    //public Iterable<Map.Entry<BlockPos, BlockState>> allBlocks();
    //public Iterable<BlockState> getBlockStates();
    //public Iterable<BlockPos> getLocalPoses();
    //public void foreach(BiConsumer<Vector3ic, BlockState> consumer);


    public AABBdc getLocalAABB();

    //blockstate不可变，天然线程安全
    public void seekAllBlocks(BiConsumer<Vector3ic, BlockState> consumer);
    public Iterable<Vector3ic> allLocalPoses();
    public Iterable<BlockState> allBlockStates();
    public Iterable<Map.Entry<Vector3ic, BlockState>> allBlocks();

    public BlockClusterData getCopiedData(BlockClusterData dest);
    public default BlockClusterData getCopiedData() {
        return getCopiedData(new BlockClusterData());
    }
}
