package com.lancas.vswap.content.block.blocks.cartridge.propellant;

import net.minecraft.world.level.block.state.BlockState;

public interface IPropellant {
    public boolean isEmpty(BlockState state);

    public BlockState getEmptyState(BlockState state);
    //public void setEmpty(BlockState state, boolean val);
    public double getEnergy(BlockState state);
}
