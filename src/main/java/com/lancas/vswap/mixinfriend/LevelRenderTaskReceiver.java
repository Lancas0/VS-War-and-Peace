package com.lancas.vswap.mixinfriend;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.NotNull;

public interface LevelRenderTaskReceiver {
    public interface LevelRenderTask {
        public void render(MultiBufferSource bufSrc, PoseStack poseStack, float partialTicks, Camera camera, GameRenderer gameRenderer);
        public boolean isAlive();
    }

    public void accept(@NotNull Object key, @NotNull LevelRenderTask task);
    public boolean contains(@NotNull Object key);

    public default boolean acceptIfAbsent(@NotNull Object key, @NotNull LevelRenderTask task) {
        if (!contains(key)) {
            accept(key, task);
            return true;
        }
        return false;
    }
}
