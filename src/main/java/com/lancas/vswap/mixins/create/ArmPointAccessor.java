package com.lancas.vswap.mixins.create;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmInteractionPoint.class)
public interface ArmPointAccessor {
    //to do may cause over flow?
    @Invoker("getInteractionPositionVector")
    public Vec3 getInteractionPositionVector();

    @Invoker("getInteractionDirection")
    public Direction getInteractionDirection();
}
