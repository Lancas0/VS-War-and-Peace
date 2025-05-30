package com.lancas.vswap.mixins.accessor;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(UseOnContext.class)
public interface UseOnCtxAccessor {
    @Accessor
    public BlockHitResult getHitResult();
}
