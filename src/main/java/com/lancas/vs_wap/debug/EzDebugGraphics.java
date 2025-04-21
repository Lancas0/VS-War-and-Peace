package com.lancas.vs_wap.debug;

import com.lancas.vs_wap.ModMain;
import com.lancas.vs_wap.debug.shapes.DebugShape;
import com.lancas.vs_wap.util.JomlUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.*;

import java.util.Dictionary;
import java.util.Hashtable;

@Mod.EventBusSubscriber(modid = ModMain.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EzDebugGraphics {
    private static final Dictionary<String, DebugShape> SHAPES = new Hashtable<>();

    public static void AddShape(String key, DebugShape shape) {
        if (key == null || shape == null) return;
        SHAPES.put(key, shape);
    }

    @SubscribeEvent
    public static void renderDebug(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        // 保存渲染状态
        poseStack.pushPose();
        RenderSystem.disableDepthTest(); // 禁用深度测试以覆盖其他图形
        RenderSystem.enableBlend();

        // 调用Debug渲染
        doRender(poseStack, buffer, JomlUtil.d(camera.getPosition()));

        // 恢复状态
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        poseStack.popPose();
        buffer.endBatch();
    }

    private static void doRender(PoseStack poseStack, MultiBufferSource buffer, Vector3dc cameraPos) {
        SHAPES.elements().asIterator().forEachRemaining(shape -> {
            shape.render(poseStack, buffer, cameraPos);
        });
    }


}
