package com.lancas.vswap.ship.data;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IShipSchemeRandomWriter {
    public IShipSchemeRandomReader getRandomWriter();

    public void setBlockAtLocalBp(BlockPos pos, BlockState state, @Nullable CompoundTag beNbt);
}
