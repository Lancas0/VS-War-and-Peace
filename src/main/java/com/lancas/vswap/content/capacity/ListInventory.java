package com.lancas.vswap.content.capacity;

import com.lancas.vswap.debug.EzDebug;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListInventory implements IItemHandlerModifiable, INBTSerializable<CompoundTag> {
    public static final int DEFAULT_ADDITIONAL_SLOT = 8;

    public final List<ItemStack> stacks = new ArrayList<>();
    protected int slotLimit = 64;
    public ListInventory() { }
    public ListInventory(int inSlotLimit) { slotLimit = inSlotLimit; }

    public void clearEmptySlots() {
        for (int i = stacks.size() - 1; i >= 0; --i) {
            ItemStack cur = stacks.get(i);
            if (cur == null || cur.isEmpty())
                stacks.remove(i);
        }
    }

    public void clear() { stacks.clear(); }
    public boolean isEmpty() {
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty())
                return false;
        }
        return true;
    }
    public void insertItemNotStack(@NotNull ItemStack stack) {
        if (!stack.isEmpty())
            stacks.add(stack);
    }
    public void insertItemStacked(@NotNull ItemStack stack) {
        int itCnt = 0;
        ItemStack remain = stack;
        while (!remain.isEmpty()) {
            EzDebug.log("remain:" + remain);
            remain = ItemHandlerHelper.insertItemStacked(this, remain, false);
            itCnt++;

            if (itCnt > 100) {
                EzDebug.warn("iterator too much times, will no longer insert");
                break;
            }
        }

        if (!remain.isEmpty()) {
            EzDebug.warn("ListInventory insertItemStacked has remain:" + remain);
        }
    }
    public void insertAllNoStack(@NotNull List<ItemStack> stacks) {
        stacks.stream().filter(x -> x != null && !x.isEmpty()).forEach(stacks::add);
    }
    public void insertAllStacked(@NotNull List<ItemStack> stacks) {
        stacks.stream()
            .filter(x -> x != null && !x.isEmpty())
            .forEach(this::insertItemStacked);
    }

    @Override
    public int getSlots() { return stacks.size() + DEFAULT_ADDITIONAL_SLOT; }  //always preserve a empty slot


    @Override
    public void setStackInSlot(int i, @NotNull ItemStack itemStack) {
        if (i < 0) {
            EzDebug.warn("ListInventory invalid ix:" + i);
            return;
        }

        //fill empty stack util i is valid for list
        while (i >= stacks.size()) {
            stacks.add(ItemStack.EMPTY);
        }
        stacks.set(i, itemStack);
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int i) {
        if (i < 0) {
            EzDebug.warn("ListInventory invalid ix:" + i);
            return ItemStack.EMPTY;
        }

        if (i >= stacks.size()) {
            return ItemStack.EMPTY;
        }
        return stacks.get(i);
    }

    @Override
    public @NotNull ItemStack insertItem(int i, @NotNull ItemStack stack, boolean simulate) {
        if (i < 0) {
            EzDebug.warn("ListInventory invalid ix:" + i);
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else if (!this.isItemValid(i, stack)) {
            return stack;
        }


        // || i >= stacks.size()
        ItemStack existing = getStackInSlot(i);//(ItemStack)this.stacks.get(slot);
        int limit = this.getStackLimit(i, stack);
        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                return stack;
            }

            limit -= existing.getCount();
        }

        if (limit <= 0) {
            return stack;
        } else {
            boolean reachedLimit = stack.getCount() > limit;
            if (!simulate) {
                if (existing.isEmpty()) {
                    //this.stacks.set(i, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
                    setStackInSlot(i, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
                } else {
                    existing.grow(reachedLimit ? limit : stack.getCount());
                    //todo this.onContentsChanged(slot);
                }
            }

            return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
        }
    }
    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        }

        //this.validateSlotIndex(slot);
        ItemStack existing = getStackInSlot(slot);//this.stacks.get(slot);
        EzDebug.log("list inv extract item exist:" + existing + " at slot " + slot);
        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int toExtract = Math.min(amount, existing.getMaxStackSize());
        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                setStackInSlot(slot, ItemStack.EMPTY);
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulate) {
                setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public int getSlotLimit(int i) { return slotLimit; }

    @Override
    public boolean isItemValid(int i, @NotNull ItemStack itemStack) { return i >= 0; }

    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
    }


    @Override
    public CompoundTag serializeNBT() {
        ListTag nbtTagList = new ListTag();

        for(int i = 0; i < this.stacks.size(); ++i) {
            if (!(this.stacks.get(i)).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("slot", i);
                this.stacks.get(i).save(itemTag);
                nbtTagList.add(itemTag);
            }
        }

        CompoundTag nbt = new CompoundTag();
        nbt.put("items", nbtTagList);
        //nbt.putInt("size", this.stacks.size());
        return nbt;
    }
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        //this.setSize(nbt.contains("Size", 3) ? nbt.getInt("Size") : this.stacks.size());
        clear();
        ListTag tagList = nbt.getList("items", Tag.TAG_COMPOUND);

        for(int i = 0; i < tagList.size(); ++i) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("slot");
            /*if (slot >= 0 && slot < this.stacks.size()) {
                this.stacks.set(slot, ItemStack.of(itemTags));
            }*/
            setStackInSlot(slot, ItemStack.of(itemTags));  //todo should notify event?
        }

        //todo this.onLoad();
    }
}
