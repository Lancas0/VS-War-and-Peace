package com.lancas.vswap.content.block.blocks.rocket;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public interface ISolidFuelBlock {
    public int getMaxBurnTicks();

    public void setAsLited(ServerLevel level, BlockPos pos);
}
