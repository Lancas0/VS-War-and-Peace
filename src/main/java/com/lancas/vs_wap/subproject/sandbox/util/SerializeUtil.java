package com.lancas.vs_wap.subproject.sandbox.util;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxBlockClusterData;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxTransformData;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.subproject.sandbox.ship.ShipFactory;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.lang.reflect.Constructor;

public class SerializeUtil {
    public static CompoundTag saveTransformLike(Vector3dc pos, Quaterniondc rot, Vector3dc scale) {
        //no need to save aabb, it is initialy dirty
        return new NbtBuilder()
            .putVector3("pos", pos)
            .putQuaternion("rot", rot)
            .putVector3("scale", scale)
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

    /*
    public static SandBoxClientShip deserializeAsClient(CompoundTag nbt) {
        NbtBuilder nbtBuilder = NbtBuilder.modify(nbt);

        return new ShipFactory()
            .uuid(() -> nbtBuilder.getUUID("uuid"))
            .transformData(() -> {
                SandBoxTransformData data = new SandBoxTransformData();
                nbtBuilder.readCompoundDo("transform_data", data::load);
                return data;
            })
            .blockClusterData(() -> {
                SandBoxBlockClusterData data = new SandBoxBlockClusterData();
                nbtBuilder.readCompoundDo("block_data", data::load);
                return data;
            })
            .createAsClient();
    }*/
    /*public static SandBoxServerShip deserializeAsServer(CompoundTag nbt) {
        NbtBuilder nbtBuilder = NbtBuilder.modify(nbt);

        return new ShipFactory()
            .uuid(() -> nbtBuilder.getUUID("uuid"))
            .transformData(() -> {
                SandBoxTransformData data = new SandBoxTransformData();
                nbtBuilder.readCompoundDo("transform_data", data::load);
                return data;
            })
            .blockClusterData(() -> {
                SandBoxBlockClusterData data = new SandBoxBlockClusterData();
                nbtBuilder.readCompoundDo("block_data", data::load);
                return data;
            })
            .createAsServer();
    }*/

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
