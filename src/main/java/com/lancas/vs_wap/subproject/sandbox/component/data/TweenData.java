package com.lancas.vs_wap.subproject.sandbox.component.data;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedComponentData;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedTransformData;
import net.minecraft.nbt.CompoundTag;

import java.io.*;

public class TweenData implements IComponentData<TweenData>, IExposedComponentData<TweenData> {
    @Override
    public TweenData copyData(TweenData src) {
        this.function = src.function;  //todo can i remove the ref holding?
        this.elapsedTime = src.elapsedTime;
        return null;
    }

    @Override
    public CompoundTag saved() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("elapsed_time", elapsedTime);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(function);
            oos.close();
            tag.putByteArray("function", baos.toByteArray());

        } catch (Exception e) {
            EzDebug.error("fail to serialzie function.");
            e.printStackTrace();
        }

        return tag;
    }

    @Override
    public IComponentData<TweenData> load(CompoundTag tag) {
        elapsedTime = tag.getDouble("elapsed_time");

        try {
            byte[] functionBytes = tag.getByteArray("function");
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(functionBytes));
            function = (TweenFunction)ois.readObject();
        } catch (Exception e) {
            EzDebug.error("fail to deserialzie function.");
            e.printStackTrace();
        }

        return this;
    }


    //这个不能捕获非可序列化的参数
    @FunctionalInterface
    public static interface TweenFunction extends Serializable {
        public SandBoxTransformData getNextTransform(IExposedTransformData prev, double et);
    }
    public TweenFunction function;
    public double elapsedTime = 0;

    private TweenData() {}
    public TweenData(TweenFunction inFunction) {
        function = inFunction;
    }
}
