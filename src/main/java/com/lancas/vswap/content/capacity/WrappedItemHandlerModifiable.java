package com.lancas.vswap.content.capacity;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class WrappedItemHandlerModifiable implements IItemHandlerModifiable {
    protected final IItemHandlerModifiable wrapped;
    public WrappedItemHandlerModifiable(IItemHandlerModifiable inWrapped) { wrapped = inWrapped; }

    @Override
    public int getSlots() { return wrapped.getSlots(); }
    @Override
    public @NotNull ItemStack getStackInSlot(int i) { return wrapped.getStackInSlot(i); }
    @Override
    public @NotNull ItemStack insertItem(int i, @NotNull ItemStack stack, boolean b) { return wrapped.insertItem(i, stack, b); }
    @Override
    public @NotNull ItemStack extractItem(int i, int i1, boolean b) { return wrapped.extractItem(i, i1, b); }
    @Override
    public int getSlotLimit(int i) { return wrapped.getSlotLimit(i); }
    @Override
    public boolean isItemValid(int i, @NotNull ItemStack stack) { return wrapped.isItemValid(i, stack); }
    @Override
    public void setStackInSlot(int i, @NotNull ItemStack stack) { wrapped.setStackInSlot(i, stack); }
}
