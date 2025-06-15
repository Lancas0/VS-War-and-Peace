package com.lancas.vswap.mixinfriend;

import com.lancas.vswap.subproject.sandbox.SandBoxPonderWorld;
import com.simibubi.create.foundation.ponder.PonderWorld;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fc;

import java.util.function.Consumer;

public interface HookedPonderScene {
    public void setSandBoxTicker(Consumer<PonderWorld> ticker);
    public void setSandBoxPonderWorld(SandBoxPonderWorld sandBoxPonderWorld);
    public @Nullable SandBoxPonderWorld getSandBoxPonderWorld();

    public void setOverSceneTarget(Matrix4fc m);
    public Matrix4fc getOverSceneTarget();
    public Matrix4fc getCurrentOverScene(float pt);
    public Matrix4fc getPrevOverScene();
    public void tickOverScene();

    public void setTargetScaleFactor(float target);
}
