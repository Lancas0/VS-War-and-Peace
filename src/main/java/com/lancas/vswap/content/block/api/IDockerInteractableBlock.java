package com.lancas.vswap.content.block.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public interface IDockerInteractableBlock {
    public boolean mayInteract(ItemStack handDocker, Level level, Player player, BlockPos bp, BlockState state);
    public @NotNull ItemStack interact(ItemStack handDocker, Level level, Player player, BlockPos bp, BlockState state);
}
