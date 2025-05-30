package com.lancas.vswap.content.recipe;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import java.util.function.Function;
import java.util.function.Supplier;

public class PersistenceKineticProcessContext implements IProcessContext {
    public static final float MAX_SPEED = 256;

    public static KineticProcessCtxHalfBuilt speedFromBe(KineticBlockEntity be) {
        return new KineticProcessCtxHalfBuilt(be::getSpeed);
    }

    public static class KineticProcessCtxHalfBuilt {
        private final Supplier<Float> speedGetter;

        public KineticProcessCtxHalfBuilt(Supplier<Float> inSpeedGetter) {
            speedGetter = inSpeedGetter;
        }

        public PersistenceKineticProcessContext build(Function<Float, Float> inSpeedToProgress) {
            return new PersistenceKineticProcessContext(speedGetter, inSpeedToProgress);
        }

        public PersistenceKineticProcessContext buildByTicksUnderMaxSpeed(int ticks) {
            float eachSpeedTickProgress = 1f / (ticks * MAX_SPEED);
            return build(spd -> Math.abs(spd) * eachSpeedTickProgress);
        }
    }


    private final Supplier<Float> speedGetter;
    private final Function<Float, Float> speedToProgress;

    protected PersistenceKineticProcessContext(Supplier<Float> inSpeedGetter, Function<Float, Float> inSpeedToProgress) {
        speedGetter = inSpeedGetter;
        speedToProgress = inSpeedToProgress;
    }

    @Override
    public float getTickProgression() { return speedToProgress.apply(speedGetter.get()); }
}
