package com.lancas.vswap.subproject.sandbox.api;

import net.minecraft.nbt.CompoundTag;

import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentTickDown implements ISavedObject<ConcurrentTickDown> {
    private AtomicInteger lifeTick = new AtomicInteger(-1);  //-1 for never timeout, 0 for to die, >0 for ticking down

    public ConcurrentTickDown() {}
    public ConcurrentTickDown(int inTicks) { lifeTick.set(inTicks); }

    public int get() { return lifeTick.get(); }
    public void set(int newTick) { lifeTick.set(newTick); }

    public boolean tickAlive() {
        int next = lifeTick.updateAndGet(t -> {
            if (t > 0) return t - 1;
            return t;
        });
        if (next == 0) return false;
        return true;
    }


    @Override
    public CompoundTag saved() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("tick", lifeTick.get());
        return tag;
    }
    @Override
    public ConcurrentTickDown load(CompoundTag tag) {
        lifeTick.set(tag.getInt("tick"));
        return this;
    }
}
