package com.lancas.vs_wap.content.block.blocks.scope;

import com.lancas.vs_wap.content.WapBlockEntites;
import com.lancas.vs_wap.content.block.blockentity.ScopeBE;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class TelescopicScope extends Block implements IBE<ScopeBE>, IScopeBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    @Override
    public Vector3d getCameraOffsetAlongForward() { return new Vector3d(0, 0.25, 2); }
    @Override
    public float getFovMultiplier() { return 0.2f; }

    public TelescopicScope(Properties p_52591_) {
        super(p_52591_);
        registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public Class<ScopeBE> getBlockEntityClass() { return ScopeBE.class; }
    @Override
    public BlockEntityType<? extends ScopeBE> getBlockEntityType() { return WapBlockEntites.SCOPE_BE.get(); }
    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // tick in both client and server
        return (lvl, pos, bs, be) -> ((ScopeBE)be).tick();
    }


}
