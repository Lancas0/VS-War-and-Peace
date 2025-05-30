package com.lancas.vswap.content.block.blocks.blockplus;

import com.lancas.vswap.content.saved.blockrecord.BlockRecordRWMgr;
import com.lancas.vswap.content.saved.blockrecord.IBlockRecord;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriFunction;

public class RefreshBlockRecordAdder implements IBlockAdder {
    private final TriFunction<ServerLevel, BlockPos, BlockState, IBlockRecord> defaultRecordSupplier;
    public RefreshBlockRecordAdder(TriFunction<ServerLevel, BlockPos, BlockState, IBlockRecord> inDefaultRecord) {
        defaultRecordSupplier = inDefaultRecord;
    }

    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!(level instanceof ServerLevel sLevel)) return;
        //todo 这个对复杂的BlockRecord不支持，如果需要和方块有关的数据(比如ShellFrame)则不行
        if (state.getBlock() == oldState.getBlock()) return;  //大概是方块更新

        IBlockRecord record = defaultRecordSupplier.apply(sLevel, pos, state);

        BlockRecordRWMgr.putRecord(sLevel, pos, record);
        EzDebug.light("put " + record.getClass().getSimpleName() + " at " + pos.toShortString());
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!(level instanceof ServerLevel sLevel)) return;
        if (state.getBlock() == newState.getBlock()) return;  //大概是方块更新

        BlockRecordRWMgr.removeRecord(sLevel, pos);
        //EzDebug.light("remove " + defaultRecordSupplier.apply(pos, state).getClass().getSimpleName() + " at " + pos.toShortString());
        EzDebug.light("remove record " + " at " + pos.toShortString());
    }
}
