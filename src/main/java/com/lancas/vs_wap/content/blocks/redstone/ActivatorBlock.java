package com.lancas.vs_wap.content.blocks.redstone;

import com.lancas.vs_wap.content.WapBlockEntites;
import com.lancas.vs_wap.content.blockentity.ActivatorBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ActivatorBlock extends Block implements IBE<ActivatorBlockEntity> {
    public ActivatorBlock(Properties p_49795_) {
        super(p_49795_);
    }
    @Override
    public Class<ActivatorBlockEntity> getBlockEntityClass() { return ActivatorBlockEntity.class; }
    @Override
    public BlockEntityType<? extends ActivatorBlockEntity> getBlockEntityType() { return WapBlockEntites.ACTIVATOR_BE.get(); }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        if (blockAccess.getBlockEntity(pos) instanceof ActivatorBlockEntity abe) {
            return abe.getRedstone();
        }
        return 0;
    }
    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }
    @Override
    public int getDirectSignal(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) { return getSignal(state, blockAccess, pos, side); }


    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        Level level, BlockState state, BlockEntityType<T> type
    ) {
        return (lvl, pos, bs, be) -> ((ActivatorBlockEntity)be).tick();
    }

}
