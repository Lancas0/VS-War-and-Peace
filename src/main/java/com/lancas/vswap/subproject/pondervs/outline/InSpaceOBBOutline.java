package com.lancas.vswap.subproject.pondervs.outline;

import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.function.Supplier;

public class InSpaceOBBOutline extends AbstractOBBOutline {
    protected volatile Supplier<Matrix4fc> aabbToObbGetter;
    public InSpaceOBBOutline(AABB bb, Supplier<Matrix4fc> inGetter) {
        super(bb);
        aabbToObbGetter = inGetter;
    }

    public void updateAabbToObbGetter(Supplier<Matrix4fc> newGetter) {
        aabbToObbGetter = newGetter;
    }

    @Override
    protected Matrix4f getAabbToObb() { return aabbToObbGetter.get().get(new Matrix4f()); }
}
