package com.lancas.vswap.subproject.sandbox.component.data.writer;

import com.lancas.vswap.subproject.sandbox.api.component.IComponentDataWriter;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3ic;

public interface IBlockClusterDataWriter extends IComponentDataWriter<BlockClusterData> {

    public default BlockState setBlock(Vector3ic localPos, BlockState state) { return setBlock(localPos, state, true); }
    public BlockState setBlock(Vector3ic localPos, BlockState state, boolean syncOtherSide);

    public void clear(boolean syncOtherSide);
    public default void clear() { clear(true); }
}
