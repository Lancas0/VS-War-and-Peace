package com.lancas.vs_wap.content.blocks.scope;

import com.lancas.vs_wap.content.WapBlockEntites;
import com.lancas.vs_wap.content.blockentity.ScopeBE;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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


public class MechScopeBlock extends Block implements IBE<ScopeBE>, IScopeBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    @Override
    public Vector3d getCameraOffsetAlongForward() { return new Vector3d(0, 0.25, -2); }
    @Override
    public float getFovMultiplier() { return 0.5f; }


    public MechScopeBlock(Properties p_52591_) {
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
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placedBy, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placedBy, stack);
        if (level.isClientSide) return;
        if (!(placedBy instanceof ServerPlayer player)) return;

        //ServerShip ship = ShipUtil.getShipAt((ServerLevel)level, pos);
        //if (ship == null) return;

        //todo rotation
        //HasScopeAttachment hasScope = new HasScopeAttachment(ship, pos, Direction.NORTH, player);
        //ship.saveAttachment(HasScopeAttachment.class, hasScope);
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



    /*
    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return AllBlockEntites.ScopeBlockEntityRO.get().create(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // 仅在服务端执行
        return level.isClientSide ? null : (lvl, pos, bs, be) -> ((ScopeBlockEntity)be).tick();
    }*/

}
