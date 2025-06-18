package com.lancas.vswap.content.item;

import com.simibubi.create.content.logistics.filter.FilterItemStack;
import net.minecraft.world.item.ItemStack;

public interface IFilterItem {
    public FilterItemStack getFilterItemStack(ItemStack stack);
}
