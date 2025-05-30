package com.lancas.vswap.subproject.blockplusapi.itemplus.adder;

import com.lancas.vswap.subproject.blockplusapi.itemplus.ItemAdder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ItemAttackAdder extends ItemAdder {
    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player);
}
