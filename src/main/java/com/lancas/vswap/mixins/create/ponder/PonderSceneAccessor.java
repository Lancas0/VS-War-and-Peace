package com.lancas.vswap.mixins.create.ponder;

import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.ponder.instruction.PonderInstruction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PonderScene.class)
public interface PonderSceneAccessor {
    @Accessor(remap = false)
    public List<PonderInstruction> getSchedule();
}
