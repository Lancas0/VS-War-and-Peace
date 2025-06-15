package com.lancas.vswap.mixins.create.ui;

import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InputWindowElement.class)
public interface InputWindowElementAccessor {
    @Accessor(remap = false)
    public Vec3 getSceneSpace();

    @Accessor(remap = false)
    public void setSceneSpace(Vec3 pos);
}
