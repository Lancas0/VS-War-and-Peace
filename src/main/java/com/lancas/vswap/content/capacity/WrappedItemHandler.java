package com.lancas.vswap.content.capacity;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class WrappedItemHandler implements IItemHandler {
    protected final IItemHandler wrapped;
    public WrappedItemHandler(IItemHandler inWrapped) { wrapped = inWrapped; }

    @Override
    public int getSlots() { return wrapped.getSlots(); }
    @Override
    public @NotNull ItemStack getStackInSlot(int i) { return wrapped.getStackInSlot(i); }
    @Override
    public @NotNull ItemStack insertItem(int i, @NotNull ItemStack itemStack, boolean b) { return wrapped.insertItem(i, itemStack, b); }
    @Override
    public @NotNull ItemStack extractItem(int i, int i1, boolean b) { return wrapped.extractItem(i, i1, b); }
    @Override
    public int getSlotLimit(int i) { return wrapped.getSlotLimit(i); }
    @Override
    public boolean isItemValid(int i, @NotNull ItemStack itemStack) { return wrapped.isItemValid(i, itemStack); }
}
