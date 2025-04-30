package com.lancas.vs_wap.subproject.sandbox.util;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.lang.reflect.Constructor;

//public class SerializeUtil {


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



//}
