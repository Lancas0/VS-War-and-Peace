package com.lancas.vs_wap.debug.shapes;

import com.lancas.vs_wap.debug.shapes.builder.ConeBuilder;
import com.lancas.vs_wap.util.JomlUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;

// 向量的调试对象
public class DebugVector extends DebugShape {
    private final Vector3d dir;    // 向量方向
    private static final float ARROW_HEAD_SCALE = 0.3f; // 箭头头部比例

    public DebugVector(Vector3dc startPos, Vector3dc inDir, int color, long duration) {
        super(startPos, color, duration);
        this.dir = new Vector3d(inDir);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, Vector3dc cameraPos) {
        Vector3d endPos = position.add(dir);
        Vector3d start = position.sub(cameraPos, new Vector3d());
        Vector3d end = endPos.sub(cameraPos, new Vector3d());

        // 设置颜色
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = 1.0f;

        // 绘制线段
        VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.LINES);
        LevelRenderer.renderLineBox(
            poseStack, vertexBuilder,
            new AABB(JomlUtil.v3(start), JomlUtil.v3(end)),
            r, g, b, a
        );

        // 绘制箭头头部（圆锥）
        poseStack.pushPose();
        poseStack.translate(end.x, end.y, end.z);
        // 对齐箭头方向
        Quaterniond quat = new Quaterniond().lookAlong(dir, new Vector3d(0, 1, 0));
        poseStack.mulPose(new Quaternionf((float) quat.x, (float) quat.y, (float) quat.z, (float) quat.w));

        new ConeBuilder()
            .height(ARROW_HEAD_SCALE)
            .radius(ARROW_HEAD_SCALE * 0.5f)
            .color(255)
            .build(poseStack, vertexBuilder);

        poseStack.popPose();
    }
}