package com.lancas.vswap.content.block.blocks.abstrac;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface IDirectionalBlock {
    public Direction getDirection(BlockState state);
}
