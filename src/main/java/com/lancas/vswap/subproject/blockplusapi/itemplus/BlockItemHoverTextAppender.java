package com.lancas.vswap.subproject.blockplusapi.itemplus;

/*
import com.lancas.einherjar.subproject.blockplusapi.util.PentaConsumer;
import com.lancas.einherjar.subproject.blockplusapi.util.QuadConsumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockItemHoverTextAppender implements IBlockItemAdder {
    private final BlockState defaultBlockState;
    private final PentaConsumer<BlockState, ItemStack, Level, List<Component>, TooltipFlag> appender;

    public BlockItemHoverTextAppender(BlockState inDefaultBlockState, PentaConsumer<BlockState, ItemStack, Level, List<Component>, TooltipFlag> inAppender) {
        defaultBlockState = inDefaultBlockState;
        appender = inAppender;
    }

    @Override
    public BlockState getDefaultBlockState() { return defaultBlockState; }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> texts, TooltipFlag flag) {
        if (appender != null)
            appender.apply(defaultBlockState, stack, level, texts, flag);
    }
}
*/