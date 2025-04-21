package com.lancas.vs_wap.content.blocks.artillery;

import com.lancas.vs_wap.content.blocks.abstrac.HorizontalDirectionalBlockImpl;
import com.lancas.vs_wap.util.ShapeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CartridgeRailBlock extends HorizontalDirectionalBlockImpl {
    public static final ShapeBuilder NORTH_SHAPE =
        //todo thicker for test
        ShapeBuilder.ofConcaveUp(2);
        /*Shapes.join(
            Shapes.join(
                box(0, 0, 0, 1, 16, 16),
                box(15, 0, 0, 16, 16, 16),
                BooleanOp.OR
            ),
            box(1, 0, 0, 15, 1, 16),
            BooleanOp.OR
        );*/

    public CartridgeRailBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return NORTH_SHAPE.getHorizonRotated(state.getValue(FACING));
    }
}
