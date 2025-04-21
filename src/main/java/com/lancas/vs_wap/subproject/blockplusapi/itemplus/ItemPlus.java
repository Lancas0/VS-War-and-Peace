package com.lancas.vs_wap.subproject.blockplusapi.itemplus;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ItemPlus extends Item implements IItemPlus {
    public ItemPlus(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> texts, TooltipFlag flag) {
        super.appendHoverText(stack, level, texts, flag);
        getAdders().forEach(a -> a.appendHoverText(stack, level, texts, flag));
    }




}
