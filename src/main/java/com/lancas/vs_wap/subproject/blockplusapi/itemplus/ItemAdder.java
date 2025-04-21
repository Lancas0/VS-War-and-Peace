package com.lancas.vs_wap.subproject.blockplusapi.itemplus;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ItemAdder {
    public default void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> texts, TooltipFlag flag) {}
}
