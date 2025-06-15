package com.lancas.vswap.subproject.pondervs.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.outliner.Outline;
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer;
import net.minecraft.world.phys.Vec3;

public interface IOutlineChaser {
    //public boolean canChase(Outline outline);
    public boolean tryChase(Outline outline);
}
