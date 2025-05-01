package com.lancas.vs_wap.content.block.blocks.cartridge.warhead;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;

public interface IWarheadBlock {
    public void onDestroyByExplosion(ServerLevel level, BlockPos pos, BlockState state, Explosion explosion);
}
