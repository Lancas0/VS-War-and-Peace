package com.lancas.vswap.subproject.pondervs.outline;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.mixins.create.ui.OutlineParamsAccessor;
import com.lancas.vswap.util.JomlUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.outliner.AABBOutline;
import com.simibubi.create.foundation.outliner.Outline;
import com.simibubi.create.foundation.render.RenderTypes;
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.Supplier;

public class OBBOutline extends AbstractOBBOutline implements IOutlineChaser {
    protected final Matrix4f aabbToObb = new Matrix4f();

    public OBBOutline(AABB bb, Matrix4fc inAabbToObb) {
        super(bb);
        aabbToObb.set(inAabbToObb);
    }

    @Override
    protected Matrix4f getAabbToObb() { return aabbToObb; }

    @Override
    public void tick() {
        super.tick();
        prev = super.bb;
        if (target != null) {
            super.bb = JomlUtil.lerpAABB(prev, target, 0.5);
        }
    }

    protected AABB prev = null;
    protected AABB target = null;

    public @Nullable AABB getTarget() { return target; }
    public void setTarget(AABB inTarget) { target = inTarget; }

    @Override
    public boolean tryChase(Outline outline) {
        if (!(outline instanceof OBBOutline obb)) {
            return false;
        }

        obb.aabbToObb.lerp(this.aabbToObb, 0.5f);
        obb.target = this.bb;
        return true;
    }
}