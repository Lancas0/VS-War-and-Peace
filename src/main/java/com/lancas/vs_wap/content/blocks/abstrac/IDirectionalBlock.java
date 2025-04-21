package com.lancas.vs_wap.content.blocks.abstrac;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface IDirectionalBlock {
    public Direction getDirection(BlockState state);
}
