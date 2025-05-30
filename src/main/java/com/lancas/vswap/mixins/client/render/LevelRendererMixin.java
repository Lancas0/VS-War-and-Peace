package com.lancas.vswap.mixins.client.render;

import com.lancas.vswap.mixinfriend.LevelRenderTaskReceiver;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin implements LevelRenderTaskReceiver {
    @Unique
    private Map<Object, LevelRenderTask> tasks = new ConcurrentHashMap<>();

    @Inject(
        method = "renderLevel",
        at = @At("HEAD")
    )
    private void handleTasks(PoseStack poseStack, float partialTicks, long finishTimeNano, boolean drawBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        var taskIt = tasks.values().iterator();
        while (taskIt.hasNext()) {
            LevelRenderTask task = taskIt.next();
            if (!task.isAlive()) {
                taskIt.remove();
                continue;
            }

            task.render(bufferSource, poseStack, partialTicks, camera, gameRenderer);
        }
    }


    @Override
    public void accept(@NotNull Object key, @NotNull LevelRenderTask task) { tasks.put(key, task); }
    @Override
    public boolean contains(@NotNull Object key) { return tasks.containsKey(key); }
}
