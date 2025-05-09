package com.lancas.vs_wap.content.block.blocks.cartridge;

import net.minecraft.world.level.block.state.BlockState;

public interface IPrimer {
    public int getPixelLength();
    public boolean isTriggered(BlockState state);
    public BlockState getTriggeredState(BlockState prevState);
    //public void setTriggered(BlockState state, boolean val);
    //return the power
    //public TriTuple<Double, Vector3dc, Long> trigger(ServerLevel level, long artilleryBreechId, BlockPos pos, BlockState state, Vector3i projectileStartPosDest);
}
