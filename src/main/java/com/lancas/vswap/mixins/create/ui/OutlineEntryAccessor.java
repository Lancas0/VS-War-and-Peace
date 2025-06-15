package com.lancas.vswap.mixins.create.ui;

import com.simibubi.create.foundation.outliner.Outline;
import com.simibubi.create.foundation.outliner.Outliner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Outliner.OutlineEntry.class)
public interface OutlineEntryAccessor {
    @Accessor(remap = false)
    public int getTicksTillRemoval();

    @Accessor(remap = false)
    public void setTicksTillRemoval(int t);

    @Accessor(remap = false)
    public Outline getOutline();
}
