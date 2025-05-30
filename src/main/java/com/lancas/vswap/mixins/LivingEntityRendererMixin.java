package com.lancas.vswap.mixins;


import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/*
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {
    @Inject(
        method = "render",
        /.*at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/layers/ItemInHandLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V"
        ),*./
        at = @At("HEAD"),
        cancellable = true
    )
    public void test(
        T entity,
        float entityYaw,
        float partialTicks,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        CallbackInfo ci
    ) {
        EzDebug.Log("test invoke of living entity renderer");
    }
    /.*private void onRenderItemInHand(
        T entity,
        float entityYaw,
        float partialTicks,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        CallbackInfo ci
    ) {
        // 检查主手或副手持是否为玻璃剑
        if (entity.getMainHandItem().getItem() == AllItems.VSWeaponItem.get()
            || entity.getOffhandItem().getItem() == AllItems.VSWeaponItem.get()) {
            ci.cancel(); // 取消渲染
        }
    }*./

}*/


@Mixin(Entity.class)
public abstract class LivingEntityRendererMixin {

    // region collision

    /**
     * Cancel movement of entities that are colliding with unloaded ships
     */
    @Inject(
        at = @At("HEAD"),
        method = "move",
        cancellable = true
    )
    private void beforeMove(final MoverType type, final Vec3 pos, final CallbackInfo ci) {
        ci.cancel();
    }
}