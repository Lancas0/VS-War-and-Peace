package com.lancas.vswap.debug.shapes;

import com.lancas.vswap.debug.shapes.builder.SphereBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Vector3dc;

// 点（球体）的调试对象
public class DebugPoint extends DebugShape {
    private static final float RADIUS = 0.2f; // 球体半径

    public DebugPoint(Vector3dc pos, int color, long duration) {
        super(pos, color, duration);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, Vector3dc cameraPos) {
        poseStack.pushPose();
        // 移动到实际坐标（减去摄像机位置以适配渲染坐标系）
        poseStack.translate(position.x - cameraPos.x(), position.y - cameraPos.y(), position.z - cameraPos.z());

        // 设置颜色（RGBA）
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = 0.8f; // 透明度

        // 渲染球体
        VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.LINES);
        new SphereBuilder()
            .radius(RADIUS)
            .color(255)
            .build(poseStack, vertexBuilder);

        poseStack.popPose();
    }
}