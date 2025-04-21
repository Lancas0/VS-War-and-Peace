package com.lancas.vs_wap.content.blocks.blockplus;

import com.lancas.vs_wap.content.saved.BlockRecordRWMgr;
import com.lancas.vs_wap.content.saved.IBlockRecord;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class RefreshBlockRecordAdder implements IBlockAdder {
    private final Supplier<IBlockRecord> defaultRecordSupplier;
    public RefreshBlockRecordAdder(Supplier<IBlockRecord> inDefaultRecord) {
        defaultRecordSupplier = inDefaultRecord;
    }

    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!(level instanceof ServerLevel sLevel)) return;
        BlockRecordRWMgr.putRecord(sLevel, pos, defaultRecordSupplier.get());
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!(level instanceof ServerLevel sLevel)) return;
        BlockRecordRWMgr.removeRecord(sLevel, pos);
    }
}
