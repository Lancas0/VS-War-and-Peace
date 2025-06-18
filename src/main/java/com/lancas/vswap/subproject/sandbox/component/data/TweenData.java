package com.lancas.vswap.subproject.sandbox.component.data;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vswap.subproject.sandbox.api.data.ITransformPrimitive;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.util.NbtBuilder;
import com.lancas.vswap.util.SerializeUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraftforge.common.util.INBTSerializable;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.io.*;

public class TweenData implements IComponentData<TweenData> {
    @Override
    public TweenData copyData(TweenData src) {
        this.function = src.function;  //todo can i remove the ref holding?
        this.curve = src.curve;

        this.fromTrans = src.fromTrans.copy();
        this.toTrans = src.toTrans.copy();

        this.duration = src.duration;
        this.elapsedTime = src.elapsedTime;
        this.loop = src.loop;
        return null;
    }
    @Override
    public CompoundTag saved() {
        NbtBuilder builder = new NbtBuilder()
            .putCompound("from", fromTrans.saved())
            .putCompound("to", toTrans.saved())

            .putDouble("elapsed_time", elapsedTime)
            .putDouble("duration", duration)
            .putBoolean("loop", loop);

        byte[] functionBytes = SerializeUtil.safeSerialize(function);
        if (functionBytes != null && functionBytes.length > 0) {
            builder.get().putByteArray("function_bytes", functionBytes);
        } else {
            EzDebug.warn("fail to serialize tween function.");
        }

        byte[] curveBytes = SerializeUtil.safeSerialize(curve);
        if (curveBytes != null && curveBytes.length > 0) {
            builder.get().putByteArray("curve_bytes", curveBytes);
        } else {
            EzDebug.warn("fail to serialize curve.");
        }

        return builder.get();
    }
    @Override
    public IComponentData<TweenData> load(CompoundTag tag) {
        fromTrans = new TransformPrimitive().load(tag.getCompound("from"));
        toTrans = new TransformPrimitive().load(tag.getCompound("to"));

        elapsedTime = tag.getDouble("elapsed_time");
        duration = tag.getDouble("duration");
        loop = tag.getBoolean("loop");

        if (tag.contains("function_bytes")) {
            function = SerializeUtil.safeDeserialize(tag.getByteArray("function_bytes"));
        }

        if (function == null) {
            EzDebug.warn("fail to deserialize tween function.");
        }

        return this;
    }


    //这个不能捕获非可序列化的参数
    /*@FunctionalInterface
    public static interface TweenFunction extends Serializable {
        public TransformPrimitive getNextTransform(ITransformPrimitive prev, double t01, double step01);
    }*/
    @FunctionalInterface
    public static interface TweenFunction extends Serializable {
        public TransformPrimitive getNextTransform(ITransformPrimitive prev, ITransformPrimitive from, ITransformPrimitive to, double t01);

        public default TweenFunction andThen(TweenFunction other) {
            return (prev, from, to, t01) -> {
                TransformPrimitive trans = getNextTransform(prev, from, to, t01);
                return other.getNextTransform(trans, from, to, t01);
            };
        }

        public static final TweenFunction Position = (prev, from, to, t01) -> {
            Vector3d pos = from.getPosition().lerp(to.getPosition(), t01, new Vector3d());
            return prev.copy().setPosition(pos);
        };
        public static final TweenFunction Rotation = (prev, from, to, t01) -> {
            Quaterniond rot = from.getRotation().slerp(to.getRotation(), t01, new Quaterniond());
            return prev.copy().setRotation(rot);
        };
        public static final TweenFunction Scale = (prev, from, to, t01) -> {
            Vector3d scale = from.getScale().lerp(to.getScale(), t01, new Vector3d());
            return prev.copy().setScale(scale);
        };
        public static final TweenFunction PositionRotation = Position.andThen(Rotation);
        public static final TweenFunction PositionScale = Position.andThen(Scale);
        public static final TweenFunction RotationScale = Rotation.andThen(Scale);
        public static final TweenFunction All = Position.andThen(Rotation).andThen(Scale);
    }

    @FunctionalInterface
    public static interface Curve extends Serializable {
        public double evaluate(double t);

        public default Curve timeScale(double scale) {
            return t -> evaluate(Math.min(scale * t, 1));
        }
        public default Curve div(double d) { return t -> evaluate(t) / d; }
        public default Curve andThen(Curve other) { return t -> t < 0.5 ? evaluate(2 * t) : other.evaluate(2 * (t - 0.5)); }
        public default Curve reverse() { return t -> evaluate(1.0 - t); }
        public default Curve upsideDown() { return t -> 1 - evaluate(t); }

        public static Curve One = t -> 1;
        public static Curve Zero = t -> 0;

        public static Curve Linear = t -> t;
        public static Curve LinearInverse = t -> 1 - t;

        public static final Curve InSine = t -> 1 - Math.cos((t * Math.PI) / 2.0);
        public static final Curve OutSine = t -> Math.sin((t * Math.PI) / 2.0);
        public static final Curve InOutSine = t -> -(Math.cos(Math.PI * t) - 1.0) / 2.0;

        public static final Curve InQuad = t -> t * t;
        public static final Curve OutQuad = t -> 1.0 - (1.0 - t) * (1.0 - t);
        public static final Curve InOutQuad = t -> t < 0.5
            ? 2.0 * t * t
            : 1.0 - Math.pow(-2.0 * t + 2.0, 2.0) / 2.0;

        public static final Curve InCubic = t -> t * t * t;
        public static final Curve OutCubic = t -> 1.0 - Math.pow(1.0 - t, 3.0);
        public static final Curve InOutCubic = t -> t < 0.5
            ? 4.0 * t * t * t
            : 1.0 - Math.pow(-2.0 * t + 2.0, 3.0) / 2.0;

        public static final Curve InQuart = t -> t * t * t * t;
        public static final Curve OutQuart = t -> 1.0 - Math.pow(1.0 - t, 4.0);
        public static final Curve InOutQuart = t -> t < 0.5
            ? 8.0 * t * t * t * t
            : 1.0 - Math.pow(-2.0 * t + 2.0, 4.0) / 2.0;

        public static final Curve InQuint = t -> t * t * t * t * t;
        public static final Curve OutQuint = t -> 1.0 - Math.pow(1.0 - t, 5.0);
        public static final Curve InOutQuint = t -> t < 0.5
            ? 16.0 * t * t * t * t * t
            : 1.0 - Math.pow(-2.0 * t + 2.0, 5.0) / 2.0;

        //FIXME right?
        public static final Curve InExpo = t ->
            t == 0 ? 0 : Math.pow(2, 10.0 * t - 10.0);
        public static final Curve OutExpo = t ->
            t == 1 ? 1 : 1 - Math.pow(2, -10.0 * t);
        public static final Curve InOutExpo = t ->
            t == 0.0 ? 0.0 : t == 1.0 ? 1.0 : t < 0.5
                ? Math.pow(2.0, 20.0 * t - 10.0) / 2.0
                : (2.0 - Math.pow(2.0, -20.0 * t + 10.0)) / 2.0;

        public static final Curve InCirc = t ->
            1.0 - Math.sqrt(1.0 - Math.pow(t, 2.0));
        public static final Curve OutCirc = t ->
            Math.sqrt(1.0 - Math.pow(t - 1.0, 2.0));
        public static final Curve IN_OUT_CIRC = t ->
            t < 0.5
                ? (1.0 - Math.sqrt(1.0 - Math.pow(2.0 * t, 2.0))) / 2.0
                : (Math.sqrt(1.0 - Math.pow(-2.0 * t + 2.0, 2.0)) + 1.0) / 2.0;

        public static final Curve InBack = t -> {
            double c1 = 1.70158;
            double c3 = c1 + 1.0;
            return c3 * t * t * t - c1 * t * t;
        };
        public static final Curve OutBack = t -> {
            double c1 = 1.70158;
            double c3 = c1 + 1.0;
            return 1.0 + c3 * Math.pow(t - 1.0, 3.0) + c1 * Math.pow(t - 1.0, 2.0);
        };
        public static final Curve InOutBack = t -> {
            double c1 = 1.70158;
            double c2 = c1 * 1.525;
            return t < 0.5
                ? (Math.pow(2.0 * t, 2.0) * ((c2 + 1.0) * 2.0 * t - c2)) / 2.0
                : (Math.pow(2.0 * t - 2.0, 2.0) * ((c2 + 1.0) * (t * 2.0 - 2.0) + c2) + 2.0) / 2.0;
        };

        public static final Curve InElastic = t -> {
            double c4 = (2 * Math.PI) / 3;
            return t == 0
                ? 0
                : t == 1.0
                ? 1.0
                : -Math.pow(2.0, 10.0 * t - 10.0) * Math.sin((t * 10.0 - 10.75) * c4);
        };
        public static final Curve OutElastic = t -> {
            double c4 = (2.0 * Math.PI) / 3.0;
            return t == 0.0
                ? 0.0
                : t == 1.0
                ? 1.0
                : Math.pow(2.0, -10.0 * t) * Math.sin((t * 10 - 0.75) * c4) + 1.0;
        };
        public static final Curve InOutElastic = t -> {
            double c5 = (2.0 * Math.PI) / 4.5;
            return t == 0
                ? 0
                : t == 1.0
                ? 1.0
                : t < 0.5
                ? -(Math.pow(2.0, 20.0 * t - 10.0) * Math.sin((20.0 * t - 11.125) * c5)) / 2.0
                : (Math.pow(2.0, -20.0 * t + 10.0) * Math.sin((20.0 * t - 11.125) * c5)) / 2.0 + 1.0;
        };

        public static final Curve OutBounce = t -> {
            double n1 = 7.5625;
            double d1 = 2.75;

            if (t < 1 / d1) {
                return n1 * t * t;
            } else if (t < 2 / d1) {
                return n1 * (t -= 1.5 / d1) * t + 0.75;
            } else if (t < 2.5 / d1) {
                return n1 * (t -= 2.25 / d1) * t + 0.9375;
            } else {
                return n1 * (t -= 2.625 / d1) * t + 0.984375;
            }
        };
        public static final Curve InBounce = t ->
            1 - OutBounce.evaluate(1 - t);
        public static final Curve InOutBounce = t ->
            t < 0.5
                ? (1 - OutBounce.evaluate(1 - 2 * t)) / 2
                : (1 + OutBounce.evaluate(2 * t - 1)) / 2;


    }

    public TweenFunction function;
    public Curve curve = Curve.Linear;

    public TransformPrimitive fromTrans;
    public TransformPrimitive toTrans;

    public double elapsedTime = 0;
    public double duration = 0;
    public boolean loop = false;

    private TweenData() {}
    public TweenData(TweenFunction inFunction, double inDuration) {
        function = inFunction;
        duration = inDuration;
    }
    public TweenData(TweenFunction inFunction, double inDuration, boolean inLoop) {
        function = inFunction;
        duration = inDuration;
        loop = inLoop;
    }
    public TweenData(TweenFunction inFunction, double inDuration, boolean inLoop, Curve inCurve) {
        function = inFunction;
        duration = inDuration;
        loop = inLoop;
        curve = inCurve;
    }
}
