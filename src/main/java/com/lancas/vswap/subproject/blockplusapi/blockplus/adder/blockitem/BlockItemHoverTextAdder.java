package com.lancas.vswap.subproject.blockplusapi.blockplus.adder.blockitem;

import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.itemplus.ItemAdder;
import com.lancas.vswap.subproject.blockplusapi.itemplus.adder.ItemHoverTextAppender;
import com.lancas.vswap.subproject.blockplusapi.util.QuadConsumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BlockItemHoverTextAdder implements IBlockAdder, IBlockItemAdderSupplier {

    public abstract void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> texts, TooltipFlag flag);

    @Override
    public void supplyItemAdders(List<ItemAdder> adderList) {
        QuadConsumer<ItemStack, Level, List<Component>, TooltipFlag> method = this::appendHoverText;

        adderList.add(new ItemHoverTextAppender() {
            @Override
            public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> texts, TooltipFlag flag) {
                method.apply(stack, level, texts, flag);
            }
        });
    }
}
