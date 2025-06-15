package com.lancas.vswap.obsolete.blocks;

/*
public class AmmoHolderBlock extends HorizontalDirectionalBlockImpl {
    public static final ShapeBuilder NORTH_SHAPE =
        Shapes.join(
            box(3, 2, 0, 13, 3, 16),
            Shapes.join(
                box(2, 2, 0, 3, 14, 16),
                box(13, 2, 0, 14, 14, 16),
                BooleanOp.OR
            ),
            BooleanOp.OR
        );

    public AmmoHolderBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        Direction direction = state.getValue(FACING);
        return ShapeBuilder.horizonRotated(NORTH_SHAPE, direction);
    }
}
*/