package com.lancas.vswap.sandbox.industry;

import com.lancas.vswap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class MountToDockData implements IComponentData<MountToDockData> {
    public @NotNull BlockPos dockBp;
    protected MountToDockData() {}
    public MountToDockData(@NotNull BlockPos inDockBp) {
        dockBp = inDockBp;
    }

    @Override
    public MountToDockData copyData(MountToDockData src) {
        dockBp = src.dockBp;
        return this;
    }

    @Override
    public CompoundTag saved() {
        return NbtBuilder.tagOfBlockPos(dockBp);
    }

    @Override
    public MountToDockData load(CompoundTag tag) {
        dockBp = NbtBuilder.blockPosOf(tag);
        return this;
    }
}
