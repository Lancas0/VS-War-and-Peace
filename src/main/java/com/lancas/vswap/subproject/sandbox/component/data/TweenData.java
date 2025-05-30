package com.lancas.vswap.subproject.sandbox.component.data;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vswap.subproject.sandbox.api.data.ITransformPrimitive;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.util.SerializeUtil;
import net.minecraft.nbt.CompoundTag;

import java.io.*;

public class TweenData implements IComponentData<TweenData> {
    @Override
    public TweenData copyData(TweenData src) {
        this.function = src.function;  //todo can i remove the ref holding?
        this.curve = src.curve;
        this.duration = src.duration;
        this.elapsedTime = src.elapsedTime;
        this.loop = src.loop;
        return null;
    }
    @Override
    public CompoundTag saved() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("elapsed_time", elapsedTime);
        tag.putDouble("duration", duration);
        tag.putBoolean("loop", loop);

        byte[] functionBytes = SerializeUtil.safeSerialize(function);
        if (functionBytes != null && functionBytes.length > 0) {
            tag.putByteArray("function_bytes", functionBytes);
        } else {
            EzDebug.warn("fail to serialize tween function.");
        }

        return tag;
    }
    @Override
    public IComponentData<TweenData> load(CompoundTag tag) {
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
    @FunctionalInterface
    public static interface TweenFunction extends Serializable {
        public TransformPrimitive getNextTransform(ITransformPrimitive prev, double t01);
    }
    @FunctionalInterface
    public static interface Curve extends Serializable {
        public double evaluate(double t);
        public static Curve Linear = t -> t;
        public static Curve LinearInverse = t -> 1 - t;
    }

    public TweenFunction function;
    public Curve curve = Curve.Linear;
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
