package com.lancas.vswap.mixins.create;

import com.lancas.vswap.subproject.lostandfound.content.LostAndFoundBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(SmartBlockEntity.class)
public class SmartBlockEntityMixin {
    @Inject(
        method = "addBehavioursDeferred",
        at = @At("HEAD"),
        remap = false
    )
    private void onAddBehaviours(List<BlockEntityBehaviour> par1, CallbackInfo ci) {
        SmartBlockEntity be = (SmartBlockEntity)(Object)this;
        par1.add(new LostAndFoundBehaviour(be));
    }
}
