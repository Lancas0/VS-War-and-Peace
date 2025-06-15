package com.lancas.vswap.subproject.pondervs;

import com.lancas.vswap.subproject.sandbox.component.data.TweenData;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TweenBuilder {
    protected Consumer<TweenData.Curve> curveSetter;
    protected final Supplier<TweenData.Curve> curveGetter;
    //protected Consumer<Integer> tickSetter;
    //protected final Supplier<Integer> ticksGetter;

    protected TweenBuilder(Consumer<TweenData.Curve> inCurveSetter, Supplier<TweenData.Curve> inCurveGetter
        /*, Consumer<Integer> inTickSetter, Supplier<Integer> inTickGetter*/) {
        curveSetter = inCurveSetter;
        curveGetter = inCurveGetter;
        //tickSetter = inTickSetter;
        //ticksGetter = inTickGetter;
    }

    public TweenBuilder curve(@NotNull TweenData.Curve curveVal) {
        curveSetter.accept(curveVal);
        return this;
    }
        /*public TweenBuilder overrideTicks(int newTicks) {
            tickSetter.accept(newTicks);
            return this;
        }*/

    public TweenBuilder companyTween(TweenBuilder builder) {
        //builder.overrideTicks(ticksGetter.get());
        builder.curve(curveGetter.get());

        curveSetter = curveSetter.andThen(builder::curve);
        //tickSetter = tickSetter.andThen(builder::overrideTicks);

        return this;
    }
}