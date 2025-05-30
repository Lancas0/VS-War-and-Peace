package com.lancas.vswap.subproject.blockplusapi.blockplus.adder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AnalogSignalAdder implements IBlockAdder {
    @Override
    public abstract int getAnalogModifySignal(BlockState state, Level level, BlockPos pos);
}
