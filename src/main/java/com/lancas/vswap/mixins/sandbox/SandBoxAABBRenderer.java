package com.lancas.vswap.mixins.sandbox;

import com.lancas.vswap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vswap.util.JomlUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({DebugRenderer.class})
public class SandBoxAABBRenderer {
    @Inject(method = {"render"}, at = {@At("HEAD")})
    private void postRender(PoseStack poseStack, MultiBufferSource.BufferSource vertexConsumersIgnore, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        SandBoxClientWorld world = SandBoxClientWorld.INSTANCE;

        if (!Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) return;

        world.allClientShips().forEach(s -> {

            AABB localAABB = JomlUtil.aabb(s.getLocalAABB());
            AABB renderAABB = JomlUtil.aabb(s.getWorldAABB());

            poseStack.pushPose();
            //将相对于摄像机的poseStack转换为相对世界的poseStack
            poseStack.translate(-cameraX, -cameraY, -cameraZ);
            //render world render aabb
            LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.LINES), renderAABB, 0.91764706f, 0.0f, 0.8509804f, 1.0f);


            poseStack.pushPose();
            Matrix4f localToWorld = s.getCurrentTransform().makeLocalToWorld(new Matrix4f());
            //将相对于摄像机的poseStack转换为船本地空间的poseStack
            poseStack.mulPoseMatrix(new Matrix4f(localToWorld));

            LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.LINES), localAABB, 1.0f, 0.0f, 0.0f, 1.0f);

            //EzDebug.log("render AABB of ship:" + s.getUuid() + ", loc:" + localAABB + ", render:" + renderAABB);

            poseStack.popPose();
            poseStack.popPose();
        });
        bufferSource.endBatch();
    }
}