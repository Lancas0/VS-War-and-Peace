package com.lancas.vs_wap.debug.shapes.builder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3f;

// 球体构建器（线框模式）
public class SphereBuilder implements ShapeBuilder {
    private float radius = 1.0f;
    private int rgb8 = 255;

    public SphereBuilder radius(float radius) {
        this.radius = radius;
        return this;
    }

    public SphereBuilder color(int inRGB8) {
        this.rgb8 = inRGB8;
        return this;
    }

    @Override
    public void build(PoseStack poseStack, VertexConsumer builder) {
        int segments = 16; // 球体细分段数
        for (int i = 0; i < segments; i++) {
            float theta1 = (float) (Math.PI * i / segments);
            float theta2 = (float) (Math.PI * (i + 1) / segments);

            for (int j = 0; j < segments; j++) {
                float phi1 = (float) (2 * Math.PI * j / segments);
                float phi2 = (float) (2 * Math.PI * (j + 1) / segments);

                // 计算顶点坐标
                Vector3f p1 = ShapeBuilder.sphericalToCartesian(radius, theta1, phi1);
                Vector3f p2 = ShapeBuilder.sphericalToCartesian(radius, theta2, phi1);
                Vector3f p3 = ShapeBuilder.sphericalToCartesian(radius, theta2, phi2);
                Vector3f p4 = ShapeBuilder.sphericalToCartesian(radius, theta1, phi2);

                // 绘制四边形边框
                ShapeBuilder.drawLine(builder, poseStack, p1, p2, rgb8);
                ShapeBuilder.drawLine(builder, poseStack, p2, p3, rgb8);
                ShapeBuilder.drawLine(builder, poseStack, p3, p4, rgb8);
                ShapeBuilder.drawLine(builder, poseStack, p4, p1, rgb8);
            }
        }
    }


}