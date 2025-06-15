package com.lancas.vswap.subproject.sandbox.component.data;

import com.lancas.vswap.subproject.sandbox.api.component.IComponentData;
import net.minecraft.nbt.CompoundTag;

public class EmptyData implements IComponentData<EmptyData> {
    @Override
    public EmptyData copyData(EmptyData src) {
        return this;
    }

    @Override
    public CompoundTag saved() {
        return new CompoundTag();
    }

    @Override
    public IComponentData<EmptyData> load(CompoundTag tag) {
        return this;
    }
}
