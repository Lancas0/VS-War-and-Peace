package com.lancas.vs_wap.content.block.blocks.cartridge;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public interface IPrimer {
    public int getPixelLength();
    public boolean isTriggered(BlockState state);
    public void setAsTriggered(ServerLevel level, BlockPos pos, BlockState state);
    //public void setTriggered(BlockState state, boolean val);
    //return the power
    //public TriTuple<Double, Vector3dc, Long> trigger(ServerLevel level, long artilleryBreechId, BlockPos pos, BlockState state, Vector3i projectileStartPosDest);
}
