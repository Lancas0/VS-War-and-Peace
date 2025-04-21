package com.lancas.vs_wap.debug.shapes.builder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3f;

// 圆锥构建器（箭头头部）
public class ConeBuilder implements ShapeBuilder {
    private float height = 1.0f;
    private float radius = 0.5f;
    private int rgb8 = 255;

    public ConeBuilder height(float height) {
        this.height = height;
        return this;
    }

    public ConeBuilder radius(float radius) {
        this.radius = radius;
        return this;
    }

    public ConeBuilder color(int inRGB8) {
        this.rgb8 = inRGB8;
        return this;
    }

    @Override
    public void build(PoseStack poseStack, VertexConsumer builder) {
        int segments = 12; // 圆锥细分段数
        Vector3f tip = new Vector3f(0, height, 0); // 圆锥尖端

        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (2 * Math.PI * i / segments);
            float angle2 = (float) (2 * Math.PI * (i + 1) / segments);

            // 计算底面圆上的点
            Vector3f p1 = new Vector3f(
                radius * (float) Math.cos(angle1),
                0,
                radius * (float) Math.sin(angle1)
            );
            Vector3f p2 = new Vector3f(
                radius * (float) Math.cos(angle2),
                0,
                radius * (float) Math.sin(angle2)
            );

            // 绘制底面边缘
            ShapeBuilder.drawLine(builder, poseStack, p1, p2, rgb8);
            // 绘制侧面线（从底到尖端）
            ShapeBuilder.drawLine(builder, poseStack, p1, tip, rgb8);
        }
    }
}