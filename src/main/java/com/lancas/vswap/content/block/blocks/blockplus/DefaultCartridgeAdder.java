package com.lancas.vswap.content.block.blocks.blockplus;

import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.ShapeBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.primitives.AABBd;

import static net.minecraft.world.level.block.Block.box;

public class DefaultCartridgeAdder extends DirectionAdder {
    public static final VoxelShape UP_SHAPE =
        box(3, 0, 3, 13, 16, 13);  //todo model remake

    public DefaultCartridgeAdder(boolean dirOppositeToLook) {
        super(dirOppositeToLook, true, UP_SHAPE);
    }

    public AABBd getLocalBound(BlockState state) {
        Direction dir = state.getValue(FACING);
        AABB localBound = new ShapeBuilder(this.upShape).getRotated(dir).bounds();
        return JomlUtil.d(localBound);
    }
}
