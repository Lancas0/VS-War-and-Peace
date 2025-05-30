package com.lancas.vswap.content.capacity;

import com.lancas.vswap.debug.EzDebug;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InventoryWithCache implements IItemHandler {
    private final IItemHandler wrapped;
    private final ICache<ItemStack> cache;  //cache flush only when get item or something like, and cacheDirty is true
    private boolean cacheDirty = false;

    public InventoryWithCache(IItemHandler inWrapped) {
        wrapped = inWrapped;
        cache = ICache.of(
            new ArrayList<>(),
            stack -> {
                if (stack.isEmpty())  //empty item, dirty discard it
                    return true;

                ItemStack remainStack = ItemHandlerHelper.insertItemStacked(wrapped, stack, true);
                if (remainStack.isEmpty()) {  //must be all inserted
                    if (!ItemHandlerHelper.insertItemStacked(wrapped, stack, false).isEmpty()) {
                        EzDebug.error("stack cache flushing: the rela result don't match simulate result");
                    }
                    return true;  //return true as long as insertion is applied: I don't want to insert a same stack for many times.
                }
                return false;
            }
        );
    }
    public InventoryWithCache(IItemHandler inWrapped, ICache<ItemStack> inCache) {
        wrapped = inWrapped;
        cache = inCache;
    }

    protected void flushIfDirty() {
        if (cacheDirty) {
            cache.flush();
            cacheDirty = false;
        }
    }

    @Override
    public int getSlots() { return wrapped.getSlots() + cache.size() + 1; }  //an addition all means it can always insert item
    @Override
    public @NotNull ItemStack getStackInSlot(int i) {
        if (i < 0) {
            EzDebug.warn("try to access slot ix:" + i);
            return ItemStack.EMPTY;
        }

        flushIfDirty();

        if (i < wrapped.getSlots()) {
            return wrapped.getStackInSlot(i);
        } else {  //don't care about slotIx when searching in cache
            ItemStack inCache = cache.peek();
            return inCache == null ? ItemStack.EMPTY : inCache;
        }
    }
    @Override
    public @NotNull ItemStack insertItem(int i, @NotNull ItemStack stack, boolean simulate) {
        if (i < 0) {
            EzDebug.warn("try to access slot ix:" + i);
            return stack;
        }

        if (i < wrapped.getSlots()) {
            return wrapped.insertItem(i, stack, simulate);
        }
        if (cache.canAdd(stack)) {  //don't care slotIx when insert to cache
            if (!simulate) {
                boolean successful = cache.add(stack);
                cacheDirty = true;  //todo is it neccessary?
                if (!successful) {
                    EzDebug.warn("when predicate if item can be insert, it return true, but return fail when real insertion is applied");
                }
            }
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int i, int amount, boolean simulate) {
        if (i < 0) {
            EzDebug.warn("try to access slot ix:" + i);
            return ItemStack.EMPTY;
        }

        flushIfDirty();
        cacheDirty = !simulate;  //if not simulate : set cache dirty

        if (i < wrapped.getSlots()) {
            return wrapped.extractItem(i, amount, simulate);
        }
        if (!cache.isEmpty()) {  //don't care about slotIx when extract from cache
            ItemStack inCache = simulate ? cache.peek() : cache.pop();
            if (inCache == null) {
                EzDebug.warn("cache is not empty but get null itemStack");
            }
            return inCache == null ? ItemStack.EMPTY : inCache;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int i) {
        if (i < 0) {
            EzDebug.warn("try to access slot ix:" + i);
            return 0;
        }
        if (i < wrapped.getSlots())
            return wrapped.getSlotLimit(i);
        return Integer.MAX_VALUE;  //cache can hold inf item
    }
    @Override
    public boolean isItemValid(int i, @NotNull ItemStack stack) {
        if (i < 0) {
            EzDebug.warn("try to access slot ix:" + i);
            return false;
        }
        if (i < wrapped.getSlots())
            return wrapped.isItemValid(i, stack);
        return cache.canAdd(stack);
    }

    /*@Override
    public void setStackInSlot(int i, @NotNull ItemStack stack) {
        if (i <= 0) {
            EzDebug.warn("try to access slot ix:" + i);
            return;
        }
        if (i < wrapped.getSlots()) {
            wrapped.setStackInSlot(i, stack);
            return;
        }
        //add to cache instead

    }*/
    public void insertToCache(ItemStack stack) {
        cache.add(stack);
        cacheDirty = true;
    }
    public void insertToCache(List<ItemStack> stacks) {
        stacks.forEach(cache::add);
        cacheDirty = true;
    }

    public boolean hasItemInCache(boolean afterFlush) {
        if (afterFlush)
            flushIfDirty();
        return !cache.isEmpty();
    }
}
