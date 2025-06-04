package com.lancas.vswap.content.block.blocks.blockplus;

import com.lancas.vswap.content.saved.blockrecord.BlockRecordRWMgr;
import com.lancas.vswap.content.saved.blockrecord.IBlockRecord;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.ctx.BlockChangeContext;
import com.lancas.vswap.subproject.blockplusapi.util.Action;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriFunction;

public class RefreshBlockRecordAdder implements IBlockAdder {
    private final Action<BlockChangeContext, Void> onPlaceAction = new Action.Post<BlockChangeContext, Void>() {
        @Override
        public Void post(BlockChangeContext ctx, Void soFar, Dest<Boolean> cancel) {
            if (!(ctx.level instanceof ServerLevel level)) return null;
            //todo 这个对复杂的BlockRecord不支持，如果需要和方块有关的数据(比如ShellFrame)则不行
            if (ctx.newState.getBlock() == ctx.oldState.getBlock()) return null;  //大概是方块更新

            IBlockRecord record = defaultRecordSupplier.apply(level, ctx.pos, ctx.newState);

            BlockRecordRWMgr.putRecord(level, ctx.pos, record);
            EzDebug.light("put " + record.getClass().getSimpleName() + " at " + ctx.pos.toShortString());
            return null;
        }
    };
    private final Action<BlockChangeContext, Void> onRemoveAction = new Action.Post<BlockChangeContext, Void>() {
        @Override
        public Void post(BlockChangeContext ctx, Void soFar, Dest<Boolean> cancel) {
            if (!(ctx.level instanceof ServerLevel level)) return null;
            if (ctx.oldState.getBlock() == ctx.newState.getBlock()) return null;  //大概是方块更新

            BlockRecordRWMgr.removeRecord(level, ctx.pos);
            //EzDebug.light("remove " + defaultRecordSupplier.apply(pos, state).getClass().getSimpleName() + " at " + pos.toShortString());
            EzDebug.light("remove record " + " at " + ctx.pos.toShortString());
            return null;
        }
    };

    private final TriFunction<ServerLevel, BlockPos, BlockState, IBlockRecord> defaultRecordSupplier;
    public RefreshBlockRecordAdder(TriFunction<ServerLevel, BlockPos, BlockState, IBlockRecord> inDefaultRecord) {
        defaultRecordSupplier = inDefaultRecord;
    }

    @Override
    public Action<BlockChangeContext, Void> onPlace() { return onPlaceAction; }
    @Override
    public Action<BlockChangeContext, Void> onRemove() { return onRemoveAction; }

    /*public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
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
    }*/
}
