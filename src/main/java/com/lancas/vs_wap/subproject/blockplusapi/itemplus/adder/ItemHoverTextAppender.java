package com.lancas.vs_wap.subproject.blockplusapi.itemplus.adder;

import com.lancas.vs_wap.subproject.blockplusapi.itemplus.ItemAdder;
import com.lancas.vs_wap.subproject.blockplusapi.util.QuadConsumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ItemHoverTextAppender implements ItemAdder {
    /*private final QuadConsumer<ItemStack, Level, List<Component>, TooltipFlag> appender;

    public ItemHoverTextAppender(QuadConsumer<ItemStack, Level, List<Component>, TooltipFlag> inAppender) {
        appender = inAppender;
    }*/

    @Override
    public abstract void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> texts, TooltipFlag flag);/* {
        if (appender != null)
            appender.apply(stack, level, texts, flag);
    }*/
}
