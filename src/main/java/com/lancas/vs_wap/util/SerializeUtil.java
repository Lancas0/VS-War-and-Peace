package com.lancas.vs_wap.util;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.component.data.TweenData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

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
}
