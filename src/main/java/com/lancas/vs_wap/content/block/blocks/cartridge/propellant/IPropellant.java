package com.lancas.vs_wap.content.block.blocks.cartridge.propellant;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public interface IPropellant {
    public boolean isEmpty(BlockState state);

    public void setAsEmpty(ServerLevel level, BlockPos pos, BlockState state);
    //public void setEmpty(BlockState state, boolean val);
    public double getEnergy(BlockState state);
}
