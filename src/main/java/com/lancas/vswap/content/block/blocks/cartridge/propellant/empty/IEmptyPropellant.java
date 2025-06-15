package com.lancas.vswap.content.block.blocks.cartridge.propellant.empty;

import com.lancas.vswap.content.block.blocks.cartridge.propellant.IPropellant;
import com.lancas.vswap.debug.EzDebug;
import net.minecraft.world.level.block.state.BlockState;

public interface IEmptyPropellant extends IPropellant {
    @Override
    public default boolean isEmpty(BlockState state) { return true; }
    @Override
    public default BlockState getEmptyState(BlockState state) {
        EzDebug.warn("a empty propellant shouldn't be called setAsEmpty");
        return state;
    }
    @Override
    public default double getSPE(BlockState state) { return 0; }
}
