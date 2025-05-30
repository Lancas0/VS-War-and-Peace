package com.lancas.vswap.content.block.blocks.industry.shredder;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IShredderableItem {
    public List<ItemStack> getProducts(ItemStack stack);
}
