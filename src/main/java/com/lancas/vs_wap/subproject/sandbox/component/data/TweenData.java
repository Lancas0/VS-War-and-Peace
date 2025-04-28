package com.lancas.vs_wap.subproject.sandbox.component.data;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedComponentData;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedTransformData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

import java.io.*;

public class TweenData implements IComponentData<TweenData>, IExposedComponentData<TweenData> {
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

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(function);
            tag.putByteArray("function", baos.toByteArray());

            oos.reset();
            oos.writeObject(curve);
            tag.putByteArray("curve", baos.toByteArray());

            oos.close();

        } catch (Exception e) {
            EzDebug.error("fail to serialzie function.");
            e.printStackTrace();
        }

        return tag;
    }
    @Override
    public IComponentData<TweenData> load(CompoundTag tag) {
        elapsedTime = tag.getDouble("elapsed_time");
        duration = tag.getDouble("duration");
        loop = tag.getBoolean("loop");

        try {
            byte[] functionBytes = tag.getByteArray("function");
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(functionBytes));
            function = (TweenFunction)ois.readObject();
            ois.close();

            functionBytes = tag.getByteArray("curve");
            ois = new ObjectInputStream(new ByteArrayInputStream(functionBytes));
            curve = (Curve) ois.readObject();
            ois.close();

        } catch (Exception e) {
            EzDebug.error("fail to deserialzie function.");
            e.printStackTrace();
        }

        return this;
    }


    //这个不能捕获非可序列化的参数
    @FunctionalInterface
    public static interface TweenFunction extends Serializable {
        public SandBoxTransformData getNextTransform(IExposedTransformData prev, double t01);
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
