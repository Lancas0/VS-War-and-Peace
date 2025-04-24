package com.lancas.vs_wap.subproject.sandbox.util;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxTransformData;
import com.lancas.vs_wap.subproject.sandbox.ship.ShipClientRenderer;
import com.lancas.vs_wap.util.JomlUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3d;

public class RenderHelper {
    public static void renderShip(ShipClientRenderer renderer, PoseStack poseStack, MultiBufferSource bufferSource, double partialTick) {
        //SandBoxTransform lerpTransform = ship.getLerpTransform(lerp);
        SandBoxTransformData lerpTransformData = renderer.getRenderTransformData(partialTick);

        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        //EzDebug.log("render block at :" + lerpTransformData.getPosition() + ", partialTick:" + partialTick);

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        poseStack.pushPose();
        poseStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);
        //now stack is relate to world

        //local to world
        Matrix4d localToWorld = lerpTransformData.makeLocalToWorld(new Matrix4d());
        poseStack.mulPoseMatrix(new Matrix4f(localToWorld));

        //todo only render visible blocks
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        for (var curBlock : renderer.getVisibleBlocks()) {
            BlockState state = curBlock.getValue();
            BlockPos localPos = curBlock.getKey();

            if (state == null) {
                EzDebug.warn("visible bps should exist a null blockstate");
                continue;
            }

            //Vector3d blockLocalCenter = JomlUtil.dCenter(localPos);
            //后续需要world blockpos 来获取正确光照
            Vector3d blockWorldCenter = JomlUtil.dWorldCenter(localToWorld, localPos);

            // 偏移到方块中心
            double x = localPos.getX() - 0.5;
            double y = localPos.getY() - 0.5;
            double z = localPos.getZ() - 0.5;

            // 应用方块位置偏移
            poseStack.pushPose();
            poseStack.translate(x, y, z);

            dispatcher.getModelRenderer().tesselateBlock(
                level,
                dispatcher.getBlockModel(state),
                state,
                JomlUtil.bpContaining(blockWorldCenter),
                poseStack,
                bufferSource.getBuffer(RenderType.solid()),  //todo change render type by state
                false, // 不检查相邻方块遮挡
                RandomSource.create(),
                state.getSeed(BlockPos.ZERO),
                OverlayTexture.NO_OVERLAY
            );

            poseStack.popPose();
        }

        poseStack.popPose();
    }
}
