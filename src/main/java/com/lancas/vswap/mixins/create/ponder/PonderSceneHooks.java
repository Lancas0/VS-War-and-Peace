package com.lancas.vswap.mixins.create.ponder;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.mixinfriend.HookedPonderScene;
import com.lancas.vswap.subproject.sandbox.SandBoxPonderWorld;
import com.lancas.vswap.subproject.sandbox.api.data.ITransformPrimitive;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.util.JomlUtil;
import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.ponder.PonderWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(PonderScene.class)
public class PonderSceneHooks implements HookedPonderScene {

    @Shadow(remap = false)
    private PonderWorld world;

    @Unique
    private Consumer<PonderWorld> sandBoxTicker = null;
    @Unique
    private SandBoxPonderWorld sandBoxPonderWorld = null;

    @Inject(
        method = "tick",
        at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/ponder/PonderScene$SceneTransform;tick()V", shift = At.Shift.AFTER),
        remap = false
    )
    public void onTick(CallbackInfo ci) {
        if (sandBoxTicker != null)
            sandBoxTicker.accept(world);
    }

    @Override
    public void setSandBoxTicker(Consumer<PonderWorld> ticker) {
        sandBoxTicker = ticker;
    }
    @Override
    public void setSandBoxPonderWorld(SandBoxPonderWorld inSandBoxPonderWorld) {
        sandBoxPonderWorld = inSandBoxPonderWorld;
    }

    @Override
    public @Nullable SandBoxPonderWorld getSandBoxPonderWorld() {
        return sandBoxPonderWorld;
    }

    @Unique final @NotNull Matrix4f prevOverScene = new Matrix4f();
    @Unique final @NotNull Matrix4f curOverScene = new Matrix4f();
    @Unique final @NotNull Matrix4f targetOverScener = new Matrix4f();
    @Override
    public void setOverSceneTarget(@NotNull Matrix4fc m) { targetOverScener.set(m); }
    @Override
    public @NotNull Matrix4fc getOverSceneTarget() { return targetOverScener; }

    @Override
    public Matrix4fc getCurrentOverScene(float pt) {
        //Matrix4fc curRender = JomlUtil.lerpTransformerF(prevOverScene, curOverScene, pt, new Matrix4f());
        Matrix4fc curRender = prevOverScene.lerp(curOverScene, pt, new Matrix4f());
        prevOverScene.set(curOverScene);
        //curOverScene.lerp(targetOverScener, 0.2f);
        JomlUtil.lerpTransformerF(curOverScene, targetOverScener, 0.2f, curOverScene);
        //EzDebug.log("cur:" + curOverScene.getTranslation(new Vector3f()) + "\n" + "tar:" + targetOverScener.getTranslation(new Vector3f()));
        return curRender;
    }

    @Override
    public Matrix4fc getPrevOverScene() { return prevOverScene; }

    @Override
    public void tickOverScene() {
        //prevOverScene.set(curOverScene);
        //JomlUtil.lerpTransformerF(curOverScene, targetOverScener, 0.2f, curOverScene);
    }



    @Unique float targetScaleFactor = 1;
    @Override
    public void setTargetScaleFactor(float target) { targetScaleFactor = target; }



}
