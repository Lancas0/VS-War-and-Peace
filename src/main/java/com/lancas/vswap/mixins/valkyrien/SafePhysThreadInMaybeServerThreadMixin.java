package com.lancas.vswap.mixins.valkyrien;

import com.lancas.vswap.WapConfig;
import com.lancas.vswap.debug.EzDebug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.impl.shadow.Ak;
import org.valkyrienskies.core.impl.shadow.Ao;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(Ak.class)
public abstract class SafePhysThreadInMaybeServerThreadMixin {
    @Unique private AtomicBoolean handlingPhysFrame = new AtomicBoolean(false);

    @Shadow(remap = false) @Final protected abstract void b(Ao ao);


    @Inject(
        method = "b(Lorg/valkyrienskies/core/impl/shadow/Ao;)V",
        at = @At("HEAD"),
        remap = false
    )
    private void safeHandlePhysFrame(Ao par1, CallbackInfo ci) {
        if (!WapConfig.vsPhysSafeThread)
            return;

        if (handlingPhysFrame.compareAndSet(false, true)) {
            //start handling
            try {
                b(par1);
            } catch (Exception e) {
                EzDebug.warn("catch exception:" + e.toString());
                e.printStackTrace();
            }
        } else {
            handlingPhysFrame.set(false);
            //let origin logic do
        }
    }

}
