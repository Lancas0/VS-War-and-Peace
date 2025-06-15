package com.lancas.vswap.subproject.pondervs.instructions;

import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.ponder.instruction.PonderInstruction;

public abstract class OnceInstruction extends PonderInstruction {
    public abstract void execute(PonderScene scene);
    @Override
    public void tick(PonderScene scene) { execute(scene); }
    @Override
    public boolean isComplete() { return true; }
}