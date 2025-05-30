package com.lancas.vswap.mixins.client.render;

import com.lancas.vswap.content.WapItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityMixin extends EntityRenderer<ItemEntity> {

    @Shadow @Final
    private ItemRenderer itemRenderer;

    @Shadow @Final
    private RandomSource random;

    @Shadow
    public abstract boolean shouldBob();
    @Shadow
    public abstract boolean shouldSpreadItems();
    @Shadow
    protected abstract int getRenderAmount(ItemStack p_115043_);


    protected ItemEntityMixin(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Inject(
        method = "Lnet/minecraft/client/renderer/entity/ItemEntityRenderer;render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At("HEAD"),
        cancellable = true)
    private void onRender(ItemEntity itemEntity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, CallbackInfo ci) {
        boolean isDocker = itemEntity.getItem().is(WapItems.DOCKER.get());
        //EzDebug.log("onItemEntityRender, is docker?:" + isDocker);
        /*if (isDocker) {
            itemEntity.setYRot(0);
            itemEntity.setXRot(0);
            itemEntity.setYBodyRot(0);
            itemEntity.setYHeadRot(0);
        }*/
        if (isDocker) {
            poseStack.pushPose();
            ItemStack itemstack = itemEntity.getItem();

            BakedModel bakedmodel = this.itemRenderer.getModel(itemstack, itemEntity.level(), (LivingEntity)null, itemEntity.getId());
            boolean flag = bakedmodel.isGui3d();
            int j = this.getRenderAmount(itemstack);
            float f = 0.25F;
            float f1 = 0f;//this.shouldBob() ? Mth.sin(((float)itemEntity.getAge() + partialTick) / 10.0F + itemEntity.bobOffs) * 0.1F + 0.1F : 0.0F;
            float f2 = bakedmodel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
            poseStack.translate(0.0F, f1 + 0.25F * f2, 0.0F);
            //float f3 = itemEntity.getSpin(partialTick);
            this.random.setSeed(itemEntity.getUUID().getLeastSignificantBits());
            poseStack.mulPose(Axis.YP.rotation(this.random.nextFloat() * (float)Math.PI * 2));

            if (!flag) {
                float f7 = -0.0F * (float)(j - 1) * 0.5F;
                float f8 = -0.0F * (float)(j - 1) * 0.5F;
                float f9 = -0.09375F * (float)(j - 1) * 0.5F;
                poseStack.translate(f7, f8, f9);
            }

            int i = itemstack.isEmpty() ? 187 : Item.getId(itemstack.getItem()) + itemstack.getDamageValue();
            this.random.setSeed((long)i);

            for(int k = 0; k < j; ++k) {
                poseStack.pushPose();
                if (k > 0) {
                    if (flag) {
                        float f11 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float f13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        poseStack.translate(this.shouldSpreadItems() ? f11 : 0.0F, this.shouldSpreadItems() ? f13 : 0.0F, this.shouldSpreadItems() ? f10 : 0.0F);
                    } else {
                        float f12 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                        float f14 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                        poseStack.translate(this.shouldSpreadItems() ? (double)f12 : (double)0.0F, this.shouldSpreadItems() ? (double)f14 : (double)0.0F, (double)0.0F);
                    }
                }

                this.itemRenderer.render(itemstack, ItemDisplayContext.GROUND, false, poseStack, multiBufferSource, packedLight, OverlayTexture.NO_OVERLAY, bakedmodel);
                poseStack.popPose();
                if (!flag) {
                    poseStack.translate((double)0.0F, (double)0.0F, (double)0.09375F);
                }
            }
            poseStack.popPose();
            super.render(itemEntity, entityYaw, partialTick, poseStack, multiBufferSource, packedLight);

            ci.cancel();
        }
    }
}
