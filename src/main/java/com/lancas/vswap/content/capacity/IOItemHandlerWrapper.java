package com.lancas.vswap.content.capacity;

import com.lancas.vswap.debug.EzDebug;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class IOItemHandlerWrapper implements IItemHandler {
    protected final IItemHandler in;
    protected final IItemHandler out;

    public IOItemHandlerWrapper(IItemHandler inIn, IItemHandler inOut) { in = inIn; out = inOut; }


    @Override
    public int getSlots() { return in.getSlots() + out.getSlots(); }
    @Override
    public @NotNull ItemStack getStackInSlot(int i) {
        if (i < in.getSlots())
            return in.getStackInSlot(i);

        int outIx = i - in.getSlots();
        if (0 <= outIx && outIx < out.getSlots())
            return out.getStackInSlot(outIx);

        EzDebug.warn("try access invalid ix:" + i);
        return ItemStack.EMPTY;
    }
    @Override
    public @NotNull ItemStack insertItem(int i, @NotNull ItemStack stack, boolean b) {
        if (0 <= i && i < in.getSlots())
            return in.insertItem(i, stack, b);
        return stack;
    }
    @Override
    public @NotNull ItemStack extractItem(int i, int amount, boolean b) {
        EzDebug.log("try extract item at i:" + i + ", outIx:" + (i - in.getSlots()));
        int outIx = i - in.getSlots();
        if (0 <= outIx && outIx < out.getSlots())
            return out.extractItem(outIx, amount, b);
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int i) {
        if (0 <= i && i < in.getSlots())
            return in.getSlotLimit(i);

        int outIx = i - in.getSlots();
        if (0 <= outIx && outIx < out.getSlots())
            return out.getSlotLimit(outIx);

        EzDebug.warn("try access invalid ix:" + i);
        return 0;
    }

    @Override
    public boolean isItemValid(int i, @NotNull ItemStack stack) {
        if (0 <= i && i < in.getSlots())
            return in.isItemValid(i, stack);

        int outIx = i - in.getSlots();
        if (0 <= outIx && outIx < out.getSlots())
            return out.isItemValid(outIx, stack);

        EzDebug.warn("try access invalid ix:" + i);
        return false;
    }
}
