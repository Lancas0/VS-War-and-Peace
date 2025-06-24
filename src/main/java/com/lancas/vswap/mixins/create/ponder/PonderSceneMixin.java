package com.lancas.vswap.mixins.create.ponder;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.mixinfriend.HookedPonderScene;
import com.lancas.vswap.subproject.sandbox.SandBoxPonderWorld;
import com.lancas.vswap.subproject.sandbox.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.ponder.PonderWorld;
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PonderScene.class)
public class PonderSceneMixin {


    @Shadow(remap = false) private PonderWorld world;


    @Inject(
        method = "renderScene",
        at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"),
        remap = true
    )
    public void overSceneTransform(SuperRenderTypeBuffer buffer, PoseStack ms, float pt, CallbackInfo ci) {
        if (!(this instanceof HookedPonderScene hooked)) {
            EzDebug.warn("can't get hooked");
            return;
        }

        ms.mulPoseMatrix(new Matrix4f(hooked.getCurrentOverScene(pt)));
    }

    @Inject(
        method = "renderScene",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/foundation/outliner/Outliner;renderOutlines(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/simibubi/create/foundation/render/SuperRenderTypeBuffer;Lnet/minecraft/world/phys/Vec3;F)V"
        ),
        remap = false
    )
    public void renderScene(SuperRenderTypeBuffer buffer, PoseStack ms, float pt, CallbackInfo ci) {
        if (!(this instanceof HookedPonderScene hooked)) {
            EzDebug.warn("can't get hooked");
            return;
        }

        SandBoxPonderWorld saPonderWorld = hooked.getSandBoxPonderWorld();
        if (saPonderWorld == null)
            return;

        saPonderWorld.allShips().forEach(s -> {
            RenderHelper.renderShipInPonder(this.world, s, ms, buffer, pt, saPonderWorld.isShipHiding(s.getUuid()));
            s.postRender();
        });

    }

}
