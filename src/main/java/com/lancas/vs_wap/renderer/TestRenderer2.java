package com.lancas.vs_wap.renderer;


import com.lancas.vs_wap.ModMain;
import com.lancas.vs_wap.util.JomlUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kotlin.Pair;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.*;
import org.joml.primitives.AABBdc;

import java.util.Dictionary;
import java.util.Hashtable;


@Mod.EventBusSubscriber(modid = ModMain.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TestRenderer2 {

    public static Dictionary<String, Vector3d> poses = new Hashtable<>();
    public static Dictionary<String, BlockPos> bps = new Hashtable<>();
    public static Dictionary<String, Pair<Vector3d, Vector3d>> vecs = new Hashtable<>();

    public static Dictionary<String, AABBdc> aabbs = new Hashtable<>();

    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;


        // 渲染结构投影
        renderStructurePreview(
            event.getPoseStack(),
            mc.level,
            JomlUtil.d(player.getEyePosition()),
            JomlUtil.d(player.getLookAngle())
            //getRotationQuaternion(new Vector3d(1, 0, 0), JomlUtil.d(player.getLookAngle())),
            //new Quaternionf().rotateTo(new Vector3f(1, 0, 0), JomlUtil.f(player.getLookAngle()))
        );
        //EzDebug.Log(player.getLookAngle().toString());
    }

    private static void renderStructurePreview(PoseStack poseStack, Level level, Vector3d playerPos, Vector3d offsetByPlayer) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderType renderType = RenderType.translucent(); // 半透明渲染类型

        // 启用混合和深度调整
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        //float scale = (float)shipSchemeData.getScale().x();  //todo 3d rotation

        // 应用相机偏移
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        poseStack.pushPose();

        poseStack.translate(
            -camera.getPosition().x,
            -camera.getPosition().y,
            -camera.getPosition().z
        );
        //poseStack.rotateAround(rotationAroundPlayer, (float)playerPos.x, (float)playerPos.y, (float)playerPos.z);
        //poseStack.rotateAround(getRotationQuaternion(new Vector3d(1, 0, 0), ));

        //Vector3d origin = playerPos.add(offsetByPlayer);
        poses.elements().asIterator().forEachRemaining(pos -> {
            poseStack.pushPose();
            poseStack.translate(pos.x - 0.5, pos.y - 0.5, pos.z - 0.5);

            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                Blocks.DIRT.defaultBlockState(),
                poseStack,
                bufferSource,
                //packedLight,
                15,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                renderType
            );

            poseStack.popPose();
        });
        bps.elements().asIterator().forEachRemaining(bp -> {
            poseStack.pushPose();
            poseStack.translate(bp.getX(), bp.getY(), bp.getZ());

            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                Blocks.DIRT.defaultBlockState(),
                poseStack,
                bufferSource,
                //packedLight,
                15,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                renderType
            );

            poseStack.popPose();
        });
        vecs.elements().asIterator().forEachRemaining(vec -> {
            Vector3d start = vec.component1();
            Vector3d dir = vec.component2();

            poseStack.pushPose();
            poseStack.translate(start.x, start.y, start.z);
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                Blocks.DIRT.defaultBlockState(),
                poseStack,
                bufferSource,
                //packedLight,
                15,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                renderType
            );

            poseStack.translate(dir.x, dir.y, dir.z);
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                Blocks.IRON_BLOCK.defaultBlockState(),
                poseStack,
                bufferSource,
                //packedLight,
                15,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                renderType
            );

            poseStack.popPose();
        });
        drawLine(poseStack, bufferSource, playerPos.get(new Vector3f()), playerPos.add(0, 2, 0, new Vector3d()).get(new Vector3f()));

        // 提交批次并恢复状态
        bufferSource.endBatch(renderType);
        poseStack.popPose();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private static void drawLine(PoseStack poseStack, MultiBufferSource.BufferSource buf, Vector3fc p0, Vector3fc p1) {
        //poseStack.pushPose();
        //poseStack.translate(p0.x(), p0.y(), p0.z());

        VertexConsumer builder = buf.getBuffer(RenderType.LINES);
        Matrix4f matrix = poseStack.last().pose();
        builder.vertex(matrix, p0.x(), p0.y(), p0.z())
            .color(0xFFFFFF)
            .uv(0, 0)
            .uv2(0, 0)
            .normal(1, 0, 0)
            .endVertex();
        builder.vertex(matrix, p1.x(), p1.y(), p1.z())
            .color(0xFFFFFF)
            .uv(0, 0)
            .uv2(0, 0)
            .normal(1, 0, 0)
            .endVertex();

        //poseStack.popPose();
    }
}