package com.lancas.vswap.debug.shapes.builder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface ShapeBuilder {

    public void build(PoseStack poseStack, VertexConsumer builder);

    public static Vector3f sphericalToCartesian(float radius, float theta, float phi) {
        float x = radius * (float) (Math.sin(theta) * Math.cos(phi));
        float y = radius * (float) (Math.sin(theta) * Math.sin(phi));
        float z = radius * (float) Math.cos(theta);
        return new Vector3f(x, z, y); // 调整坐标系适配Minecraft
    }

    public static void drawLine(VertexConsumer builder, PoseStack poseStack, Vector3f from, Vector3f to, int rgb8) {
        Matrix4f matrix = poseStack.last().pose();
        builder.vertex(matrix, from.x(), from.y(), from.z())
            .color(rgb8)
            .uv(0, 0)
            .uv2(0, 0)
            .normal(1, 0, 0)
            .endVertex();
        builder.vertex(matrix, to.x(), to.y(), to.z())
            .color(rgb8)
            .uv(0, 0)
            .uv2(0, 0)
            .normal(1, 0, 0)
            .endVertex();
    }
}


