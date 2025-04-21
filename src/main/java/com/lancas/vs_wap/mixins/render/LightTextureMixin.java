package com.lancas.vs_wap.mixins.render;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightTexture.class)
public class LightTextureMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F", ordinal = 2), method = "updateLightTexture")
    public float updateLightTexture(float a, float b) {
        double gamma = Minecraft.getInstance().options.gamma().get();
        return (float) Math.max(gamma < 0 ? gamma : 0, b);
    }
}