package com.lancas.vswap.obsolete.blockplusapi;

/*
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.blockitem.IBlockItemAdderSupplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public abstract class BlockItemPlus extends BlockItem implements IItemPlus {
    private static final Hashtable<BlockPlus, BlockItemPlus> generatedItems = new Hashtable<>();

    private BlockItemPlus(Block p_40565_, Properties p_40566_) {
        super(p_40565_, p_40566_);
    }
    public static BlockItemPlus getOrCreateFrom(BlockPlus block, @Nullable Properties properties) {
        if (block == null)
            throw new IllegalArgumentException("block plus is null");

        BlockItemPlus generated = generatedItems.get(block);
        if (generated != null)
            return generated;


        List<ItemAdder> itemAdders = new ArrayList<>();

        for (IBlockAdder blockAdder : block.getAdders()) {
            if (blockAdder instanceof IBlockItemAdderSupplier itemAdderSupplier) {
                itemAdderSupplier.supplyItemAdders(itemAdders);
            }
        }
        if (properties == null)
            properties = new Properties();
        BlockItemPlus newGenerate = new BlockItemPlus(block, properties) {
            @Override
            public List<ItemAdder> getAdders() { return itemAdders; }
        };
        generatedItems.put(block, newGenerate);
        return newGenerate;
    }
    /.*public static BlockItemPlus get(BlockPlus block) {
        return generatedItems.get(block);
    }*./
    public ItemStack defaultStack() {
        return getDefaultInstance();
    }
    public ItemStack stackWithNbt(CompoundTag nbt) {
        ItemStack stack = defaultStack();
        stack.setTag(nbt);
        return stack;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> texts, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, texts, flag);
        getAdders().forEach(a -> a.appendHoverText(stack, level, texts, flag));
    }
}
*/