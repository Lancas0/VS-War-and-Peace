package com.lancas.vswap.ship.ballistics.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface IPhysicalBehaviourBlock {
    public IPhysBehaviour getPhysicalBehaviour(BlockPos bp, BlockState state);
}
