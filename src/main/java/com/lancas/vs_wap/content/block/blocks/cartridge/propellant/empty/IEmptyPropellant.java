package com.lancas.vs_wap.content.block.blocks.cartridge.propellant.empty;

import com.lancas.vs_wap.content.block.blocks.cartridge.propellant.IPropellant;
import com.lancas.vs_wap.debug.EzDebug;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
    public default double getEnergy(BlockState state) { return 0; }
}
