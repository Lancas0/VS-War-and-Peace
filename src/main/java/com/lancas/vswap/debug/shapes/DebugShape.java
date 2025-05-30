package com.lancas.vswap.debug.shapes;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public abstract class DebugShape {
    protected final Vector3d position;
    protected final int color;       // RGB 颜色（0xRRGGBB）
    protected final long createTime; // 创建时间（毫秒）
    protected final long duration;   // 持续时间（毫秒）

    public DebugShape(Vector3dc pos, int color, long duration) {
        this.position = new Vector3d(pos);
        this.color = color;
        this.createTime = System.currentTimeMillis();
        this.duration = duration;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - createTime > duration;
    }

    public abstract void render(PoseStack poseStack, MultiBufferSource buffer, Vector3dc cameraPos);
}




