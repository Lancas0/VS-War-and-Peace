package com.lancas.vs_wap.content.block.blocks.artillery;

import com.lancas.vs_wap.content.block.blocks.abstrac.DirectionalBlockImpl;
import com.lancas.vs_wap.util.ShapeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ArtilleryPart1Block extends DirectionalBlockImpl {
    public static final String ID = "artillery_part1";

    public static final ShapeBuilder UP_SHAPE = ShapeBuilder.ofSide(Direction.DOWN, 2);
    public ArtilleryPart1Block(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        Direction direction = state.getValue(FACING);
        return UP_SHAPE.getRotated(direction);
    }
}
