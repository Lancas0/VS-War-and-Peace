package com.lancas.vswap.content.capacity;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class IOInventoryUtil {
    public static class In {
        public static IItemHandler create() {
            return new ItemStackHandler() {
                @Override
                public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }
            };
        }
        public static IItemHandler create(int slotCnt) {
            return new ItemStackHandler(slotCnt) {
                @Override
                public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }
            };
        }
        public static IItemHandler create(NonNullList<ItemStack> stacks) {
            return new ItemStackHandler(stacks) {
                @Override
                public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }
            };
        }
        public static IItemHandler create(int slotCnt, int inStackLimit) {
            return new ItemStackHandler(slotCnt) {
                private final int stackLimit = inStackLimit;
                @Override
                public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }
                @Override
                public int getSlotLimit(int slot) { return stackLimit; }
            };
        }

        public static IItemHandler wrap(IItemHandler wrapped) {
            return new WrappedItemHandler(wrapped) {
                @Override
                public @NotNull ItemStack extractItem(int i, int i1, boolean b) { return ItemStack.EMPTY; }
            };
        }
    }

    public static class Out {
        public static IItemHandler create() {
            return new ItemStackHandler() {
                @Override
                public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) { return stack; }
            };
        }
        public static IItemHandler create(int slotCnt) {
            return new ItemStackHandler(slotCnt) {
                @Override
                public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) { return stack; }
            };
        }
        public static IItemHandler create(NonNullList<ItemStack> stacks) {
            return new ItemStackHandler(stacks) {
                @Override
                public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) { return stack; }
            };
        }
        public static IItemHandler create(int slotCnt, int inStackLimit) {
            return new ItemStackHandler(slotCnt) {
                private final int stackLimit = inStackLimit;
                @Override
                public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) { return stack; }
                @Override
                public int getSlotLimit(int slot) { return stackLimit; }
            };
        }

        public static IItemHandler wrap(IItemHandler wrapped) {
            return new WrappedItemHandler(wrapped) {
                @Override
                public @NotNull ItemStack insertItem(int i, @NotNull ItemStack stack, boolean b) { return stack; }
            };
        }
        /*public static IItemHandler wrap(IItemHandler inWrapped) {
            return new IItemHandler() {
                private final IItemHandler wrapped = inWrapped;

                @Override
                public @NotNull ItemStack extractItem(int i, int i1, boolean b) {
                    return ItemStack.EMPTY;
                    //return wrapped.extractItem(i, i1, b);
                }

                @Override
                public int getSlots() { return wrapped.getSlots(); }
                @Override
                public @NotNull ItemStack getStackInSlot(int i) { return wrapped.getStackInSlot(i); }
                @Override
                public @NotNull ItemStack insertItem(int i, @NotNull ItemStack itemStack, boolean b) { return wrapped.insertItem(i, itemStack, b); }
                @Override
                public int getSlotLimit(int i) { return wrapped.getSlotLimit(i); }
                @Override
                public boolean isItemValid(int i, @NotNull ItemStack itemStack) { return wrapped.isItemValid(i, itemStack); }
            };
        }*/
    }
}

