package com.lancas.vswap.mixins.debug;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidTankBlockEntity.class)
public class FluidTankBlockEntityMixin {
    /*@Inject(
        method = "getMaxWidth",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void newMaxWidth(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(8);
    }

    @Inject(
        method = "getMaxHeight",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void newMaxHeight(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(1);
    }*/
}
