package com.lancas.vs_wap.content.blocks.blockplus;

import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShapeBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.primitives.AABBd;

import static net.minecraft.world.level.block.Block.box;

public class DefaultCartridgeAdder extends DirectionAdder {
    public static final VoxelShape UP_SHAPE =
        box(2, 0, 2, 14, 16, 14);  //todo model remake

    public DefaultCartridgeAdder() {
        super(true, true, UP_SHAPE);
    }

    public AABBd getLocalBound(BlockState state) {
        Direction dir = state.getValue(FACING);
        AABB localBound = new ShapeBuilder(this.upShape).getRotated(dir).bounds();
        return JomlUtil.d(localBound);
    }
}
