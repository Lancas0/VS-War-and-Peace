package com.lancas.vs_wap.content.block.blocks.cartridge.ticker;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;

public interface ITicker {
    public void serverTicker(BlockState state, BlockPos bp, ServerLevel level, ServerShip projectileShip);
    public void physicTicker(PhysShip physShip);
}
