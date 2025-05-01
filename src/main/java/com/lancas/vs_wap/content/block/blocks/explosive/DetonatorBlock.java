package com.lancas.vs_wap.content.block.blocks.explosive;

import com.lancas.vs_wap.debug.EzDebug;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DetonatorBlock extends AbstractExplosiveBlock {
    public static final String ID = "detonator";
    public static final int EXPLOSIVE_POWER = 2;

    public DetonatorBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.FACING, Direction.NORTH));
    }

    @Override public void explodeByRedstone(ServerLevel level, BlockPos pos, BlockState state) { explode(level, pos, state); }
    @Override public void explodeByExplosion(ServerLevel level, BlockPos pos, BlockState state) { explode(level, pos, state); }


    public void explode(ServerLevel level, BlockPos pos, BlockState state) {
        AbstractExplosiveBlock.simpleExplosion(level, pos, EXPLOSIVE_POWER);
    }





    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }
    // 设置放置方向（根据玩家视线方向）
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction direction = ctx.getNearestLookingDirection().getOpposite();
        EzDebug.log("direction:" + direction);
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, direction);
    }
}
