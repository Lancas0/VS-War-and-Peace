package com.lancas.vswap.subproject.pondervs.instructions;

import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.ponder.instruction.TickingInstruction;

public abstract class TweenInstruction extends TickingInstruction {
    public TweenInstruction(boolean blocking, int ticks) {
        super(blocking, ticks);
    }

    public abstract void tween(PonderScene scene, double t01);

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        double t01 = 1.0 - (double)remainingTicks / totalTicks;
        tween(scene, t01);
    }
}
