package com.lancas.vswap.obsolete.mixin;


/*
import com.lancas.einherjar.content.EinherjarItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.valkyrienskies.mod.common.util.VectorConversionsMCKt.toJOML;


@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Inject(
        method = "renderItem",
        at = @At("HEAD"),
        cancellable = true
    )
    public void onRenderItem(
        LivingEntity entity,
        ItemStack stack,
        ItemDisplayContext displayContext,
        boolean leftHanded,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int combinedLight,
        CallbackInfo ci
    ) {
        //EzDebug.Log("renderering: " + stack.getItem().getName(stack));
        if (stack.getItem() == EinherjarItems.EINHERJAR_WAND.get()) {
            ci.cancel(); // 取消渲染
        }
    }
}*/