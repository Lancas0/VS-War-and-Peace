package com.lancas.vswap.content.block.blocks.debug;

/*
import com.lancas.vswap.content.WapBlockEntites;
import com.lancas.vswap.content.block.blockentity.LostAndFoundBe;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.util.StrUtil;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class LostAndFoundBlock extends Block implements IBE<LostAndFoundBe> {

    public LostAndFoundBlock(Properties p) {
        super(p);
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        EzDebug.light("lost and found block onRemove, state:" + StrUtil.getBlockName(state) + ", new state:" + StrUtil.getBlockName(newState) + ", isMoving:" + isMoving);
        IBE.onRemove(state, worldIn, pos, newState);

        if (state.hasBlockEntity()) {
            EzDebug.log("state " + StrUtil.getBlockName(state) + " has block entity");
            if (!state.is(newState.getBlock()) || !newState.hasBlockEntity()) {
                EzDebug.log("new state is not same block, and new state has no be");

                BlockEntity blockEntity = worldIn.getBlockEntity(pos);
                EzDebug.log("blockEntity:" + blockEntity);
                if (blockEntity instanceof SmartBlockEntity) {
                    EzDebug.warn("be is smartBe");
                    SmartBlockEntity sbe = (SmartBlockEntity)blockEntity;
                    sbe.destroy();
                }

                worldIn.removeBlockEntity(pos);
            }
        }
    }


    @Override
    public Class<LostAndFoundBe> getBlockEntityClass() { return LostAndFoundBe.class; }
    @Override
    public BlockEntityType<? extends LostAndFoundBe> getBlockEntityType() { return WapBlockEntites.LOST_AND_FOUND_BE.get(); }
}
*/