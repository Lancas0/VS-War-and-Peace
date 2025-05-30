package com.lancas.vswap.subproject.blockplusapi.itemplus.adder;

import com.lancas.vswap.subproject.blockplusapi.itemplus.ItemAdder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ItemInventoryTickAdder extends ItemAdder {
    @Override
    public abstract void inventoryTick(ItemStack stack, Level level, Entity entity, int ix, boolean selecting);
}
