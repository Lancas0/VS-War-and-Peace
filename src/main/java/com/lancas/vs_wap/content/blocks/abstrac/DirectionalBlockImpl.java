package com.lancas.vs_wap.content.blocks.abstrac;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public abstract class DirectionalBlockImpl extends Block implements IDirectionalBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public DirectionalBlockImpl(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    // 设置放置方向（根据玩家视线方向）
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        //EzDebug.Log("direction:" + ctx.getNearestLookingDirection().getOpposite());

        //return this.defaultBlockState()
        //    .setValue(FACING, ctx.getNearestLookingDirection().getOpposite());
        return this.defaultBlockState().setValue(FACING, getDirectionForPlacement(ctx));
    }

    public Direction getDirectionForPlacement(BlockPlaceContext ctx) {
        return ctx.getNearestLookingDirection().getOpposite();
    }


    @Override
    public Direction getDirection(BlockState state) {
        return state.getValue(FACING);
    }
}
