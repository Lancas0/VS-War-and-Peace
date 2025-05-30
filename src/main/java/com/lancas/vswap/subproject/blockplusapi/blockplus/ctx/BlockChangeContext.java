package com.lancas.vswap.subproject.blockplusapi.blockplus.ctx;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockChangeContext {
    public Level level;
    public BlockPos pos;
    public BlockState oldState;
    public BlockState newState;
    public boolean isMoving;

    public BlockChangeContext(Level inLevel, BlockPos inPos, BlockState inOldState, BlockState inNewState, boolean inIsMoving) {
        level = inLevel;
        pos = inPos;
        oldState = inOldState;
        newState = inNewState;
        isMoving = inIsMoving;
    }

    public boolean blockChanged() { return oldState.getBlock() != newState.getBlock(); }
}
