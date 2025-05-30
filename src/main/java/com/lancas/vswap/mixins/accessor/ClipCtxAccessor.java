package com.lancas.vswap.mixins.accessor;

import net.minecraft.world.level.ClipContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClipContext.class)
public interface ClipCtxAccessor {
    @Accessor
    public ClipContext.Block getBlock();

    @Accessor
    public ClipContext.Fluid getFluid();
}
