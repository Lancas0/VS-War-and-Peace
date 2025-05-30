package com.lancas.vswap.subproject.blockplusapi.blockplus.adder;

import com.lancas.vswap.subproject.blockplusapi.blockplus.ctx.BlockChangeContext;
import com.lancas.vswap.subproject.blockplusapi.util.Action;

public interface IBlockRemoveCallbackAdder extends IBlockAdder {
    //@Override
    //public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving);
    @Override
    public Action<BlockChangeContext, Void> onRemove();
}
