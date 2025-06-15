package com.lancas.vswap.mixins.create.ui;

import com.simibubi.create.foundation.outliner.Outline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Outline.OutlineParams.class)
public interface OutlineParamsAccessor {
    @Accessor(remap = false)
    public boolean getDisableLineNormals();

    @Accessor(remap = false)
    public int getLightmap();

    @Accessor(remap = false)
    public boolean getDisableCull();
}
