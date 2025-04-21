package com.lancas.vs_wap.ship.ballistics.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface IPhysicalBehaviourAdder {
    public IPhysBehaviour getPhysicalBehaviour(BlockPos bp, BlockState state);
}
