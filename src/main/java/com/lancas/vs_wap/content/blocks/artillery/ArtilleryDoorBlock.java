package com.lancas.vs_wap.content.blocks.artillery;

import com.lancas.vs_wap.subproject.blockplusapi.blockplus.*;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.*;
import com.lancas.vs_wap.util.ShapeBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class ArtilleryDoorBlock extends BlockPlus {
    @Override
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            ArtilleryDoorBlock.class,

            () -> List.of(
                new DirectionAdder(false, false, null),
                new RedstoneLinkableBlockAdder(),
                new ShapeByStateAdder(state ->
                    state.getValue(RedstoneOnOffAdder.POWERED) ?
                        POWERED_UP_SHAPE :
                        UNPOWERED_UP_SHAPE.getRotated(state.getValue(DirectionAdder.FACING))
                )
            )
        );
    }

    public ArtilleryDoorBlock(Properties p_49795_) {
        super(p_49795_);
    }

    protected static final ShapeBuilder UNPOWERED_UP_SHAPE = ShapeBuilder.ofSide(Direction.DOWN, 4);
    protected static final VoxelShape POWERED_UP_SHAPE = ShapeBuilder.ofEmpty().get();

    /*
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction dir = state.getValue(FACING);
        if (state.getValue(POWERED)) {
            return POWERED_UP_SHAPE;
        } else {
            return UNPOWERED_UP_SHAPE.getRotated(dir);
        }
    }

    @Override
    public void onPowerChange(Level level, BlockPos pos, BlockState state, boolean powered) {
        ;
    }*/
}
