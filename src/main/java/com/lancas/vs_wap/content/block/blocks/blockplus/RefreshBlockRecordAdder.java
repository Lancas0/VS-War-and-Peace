package com.lancas.vs_wap.content.block.blocks.blockplus;

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
        //todo 这个对复杂的BlockRecord不支持，如果需要和方块有关的数据(比如ShellFrame)则不行
        if (state.getBlock() == oldState.getBlock()) return;  //大概是方块更新
        BlockRecordRWMgr.putRecord(sLevel, pos, defaultRecordSupplier.get());
        EzDebug.light("put " + defaultRecordSupplier.get().getClass().getSimpleName() + " at " + pos.toShortString());
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!(level instanceof ServerLevel sLevel)) return;
        if (state.getBlock() == newState.getBlock()) return;  //大概是方块更新
        BlockRecordRWMgr.removeRecord(sLevel, pos);
        EzDebug.light("remove " + defaultRecordSupplier.get().getClass().getSimpleName() + " at " + pos.toShortString());
    }
}
