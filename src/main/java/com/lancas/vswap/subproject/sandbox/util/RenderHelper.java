package com.lancas.vswap.subproject.sandbox.util;

import com.jozufozu.flywheel.core.model.ModelUtil;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.api.data.ITransformPrimitive;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxClientShip;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxPonderShip;
import com.lancas.vswap.util.JomlUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.ponder.PonderWorld;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.*;

import java.util.Map;

public class RenderHelper {
    public static void renderShipInPonder(PonderWorld world, SandBoxPonderShip ponderShip, PoseStack poseStack, MultiBufferSource bufferSource, double partialTick, boolean isHiding) {
        ITransformPrimitive renderTransformData = ponderShip.getRenderTransform(partialTick); //also update transform, but don't render if is hiden
        if (isHiding)
            return;

        Minecraft mc = Minecraft.getInstance();
        //Level level = mc.level;
        //Camera camera = mc.gameRenderer.getMainCamera();
        //if (level == null) return;

        poseStack.pushPose();
        //No need to transform camera pos in ponder world
        //poseStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);

        Matrix4d localToWorld = renderTransformData.makeLocalToWorld(new Matrix4d());
        //将相对于摄像机的poseStack转换为船本地空间的poseStack
        poseStack.mulPoseMatrix(new Matrix4f(localToWorld));


        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();

        //todo lazy get?
        PoseStack overlayMS = new PoseStack();
        overlayMS.last().pose().set(poseStack.last().pose());
        overlayMS.last().normal().set(poseStack.last().normal());

        //todo only render visible blocks
        ponderShip.getBlockCluster().getDataReader().seekAllBlocks((localPos, state) -> {
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

            /*dispatcher.renderSingleBlock();
            dispatcher.getModelRenderer().tesselateBlock(

            );
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
            );*/
            BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

            //blockRenderer.renderSingleBlock();
            //todo replace with create like renderering
            /*dispatcher.getModelRenderer().tesselateBlock(
                world,
                dispatcher.getBlockModel(state),
                state,
                JomlUtil.bpContaining(blockWorldCenter),
                poseStack,
                bufferSource.getBuffer(RenderType.solid()),  //todo change render type by state?
                false, // 不检查相邻方块遮挡
                RandomSource.create(),
                state.getSeed(BlockPos.ZERO),
                OverlayTexture.NO_OVERLAY
            );*/


            blockRenderer.renderSingleBlock(state, poseStack, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            //dispatcher.getModelRenderer()

            Integer breakProgress = ponderShip.getBreakProgress(localPos);
            if (breakProgress != null) {
                SheetedDecalTextureGenerator sheetedDecalTextureGenerator = new SheetedDecalTextureGenerator(
                    bufferSource.getBuffer(ModelBakery.DESTROY_TYPES.get(breakProgress)),
                    overlayMS.last().pose(),
                    overlayMS.last().normal(),
                    1.0f
                );

                ModelUtil.VANILLA_RENDERER.renderBreakingTexture(
                    state,
                    JomlUtil.bpContaining(blockWorldCenter),
                    world,
                    poseStack,
                    sheetedDecalTextureGenerator,
                    ModelData.EMPTY
                );
            }

            poseStack.popPose();
        });

        poseStack.popPose();
    }



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
