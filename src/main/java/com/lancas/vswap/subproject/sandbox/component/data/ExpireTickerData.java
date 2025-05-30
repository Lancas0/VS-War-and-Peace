package com.lancas.vswap.subproject.sandbox.component.data;

import com.lancas.vswap.subproject.sandbox.api.component.IComponentData;
import net.minecraft.nbt.CompoundTag;

public class ExpireTickerData implements IComponentData<ExpireTickerData> {
    public int tick = 0;

    public ExpireTickerData(int inTicker) { tick = inTicker; }

    @Override
    public ExpireTickerData copyData(ExpireTickerData src) {
        tick = src.tick;
        return this;
    }

    @Override
    public CompoundTag saved() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("tick", tick);
        return tag;
    }
    @Override
    public IComponentData<ExpireTickerData> load(CompoundTag tag) {
        tick = tag.getInt("tick");
        return this;
    }
}
