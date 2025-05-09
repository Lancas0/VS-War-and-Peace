package com.lancas.vs_wap.subproject.sandbox.util;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.api.data.ITransformPrimitive;
import com.lancas.vs_wap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxClientShip;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.StrUtil;
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
import org.joml.*;

public class RenderHelper {
    public static void renderShip(SandBoxClientShip clientShip, PoseStack poseStack, MultiBufferSource bufferSource, double partialTick) {
        ITransformPrimitive renderTransformData = clientShip.getRenderTransform(partialTick);

        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        Camera camera = mc.gameRenderer.getMainCamera();
        if (level == null) return;

        poseStack.pushPose();
        //将相对于摄像机的poseStack转换为相对世界的poseStack
        poseStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);

        Matrix4d localToWorld = renderTransformData.makeLocalToWorld(new Matrix4d());
        //将相对于摄像机的poseStack转换为船本地空间的poseStack
        poseStack.mulPoseMatrix(new Matrix4f(localToWorld));


        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        //todo only render visible blocks
        clientShip.getBlockCluster().getDataReader().seekAllBlocks((localPos, state) -> {
            if (state == null || state.isAir()) {
                EzDebug.warn("visible bps exist a null blockstate");
                return;
            }

            //需要world blockpos 来获取正确光照
            Vector3d blockWorldCenter = localToWorld.transformPosition(JomlUtil.d(localPos), new Vector3d());
            //EzDebug.log("render" + StrUtil.getBlockName(state) + "at " + StrUtil.F2(blockWorldCenter) + ", rot:" + renderTransformData.getRotation() + ", scale:" + renderTransformData.getScale());

            // 偏移到方块中心
            double x = localPos.x() - 0.5;
            double y = localPos.y() - 0.5;
            double z = localPos.z() - 0.5;

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
        });

        poseStack.popPose();
    }
}
