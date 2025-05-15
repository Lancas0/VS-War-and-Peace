package com.lancas.vs_wap.subproject.blockplusapi.itemplus;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.List;
import java.util.function.Supplier;

public interface IItemPlus {
    public abstract List<ItemAdder> getAdders();

    public static final Hashtable<Class<? extends IItemPlus>, List<ItemAdder>> addersCache = new Hashtable<>();
    public static List<ItemAdder> addersIfAbsent(Class<? extends IItemPlus> type, Supplier<List<ItemAdder>> addersSupplier) {
        if (!addersCache.containsKey(type))
            addersCache.put(type, addersSupplier.get());

        return addersCache.get(type);
    }


    public default void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> texts, @NotNull TooltipFlag flag) {
        stack.getItem().appendHoverText(stack, level, texts, flag);  //super.appendHoverText
        getAdders().forEach(a -> a.appendHoverText(stack, level, texts, flag));
    }
}
