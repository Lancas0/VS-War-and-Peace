package com.lancas.vs_wap.util;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.component.data.TweenData;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.io.*;
import java.lang.reflect.Constructor;

public class SerializeUtil {
    public static <T extends Serializable> byte[] serialize(@NotNull T obj) throws IOException {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(obj);
                return baos.toByteArray();
            }
        }/* catch (Exception e) {
            EzDebug.error("fail to serialize:" + obj + ", type:" + obj.getClass().getName());
            e.printStackTrace();
            return null;
        }*/
    }

    public static <T extends Serializable> T deserilaize(byte[] bytes) throws IOException, ClassNotFoundException {
        try(ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (T)ois.readObject();
        }/* catch (Exception e) {
            EzDebug.error("fail to deserialize");
            e.printStackTrace();
            return null;
        }*/
    }

    public static <T extends Serializable> byte[] safeSerialize(@NotNull T obj) {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(obj);
                return baos.toByteArray();
            }
        } catch (Exception e) {
            return null;
        }
    }
    @Nullable
    public static <T extends Serializable> T safeDeserialize(byte[] bytes) {
        try(ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (T)ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }



    public static CompoundTag saveTransformLike(Vector3dc pos, Quaterniondc rot, Vector3dc scale) {
        //no need to save aabb, it is initialy dirty
        return new NbtBuilder()
            .putVector3d("pos", pos)
            .putQuaternion("rot", rot)
            .putVector3d("scale", scale)
            .get();
    }
    public static void loadTransformLike(CompoundTag tag, TriConsumer<Vector3d, Quaterniond, Vector3d> loader) {
        //no need to load aabb, it is initialy dirty
        Vector3d pos = new Vector3d();
        Quaterniond rot = new Quaterniond();
        Vector3d scale = new Vector3d();

        NbtBuilder.modify(tag)
            .readVector3d("pos", pos)
            .readQuaternionD("rot", rot)
            .readVector3d("scale", scale);

        loader.accept(pos, rot, scale);
    }
    @Nullable
    public static  <T> T createByClassName(String className) {
        try {
            Constructor<T> constructor = (Constructor<T>)Class.forName(className).getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            EzDebug.error("fail to create class:" + className + ", exception:");
            e.printStackTrace();
            return null;
        }

    }
}
