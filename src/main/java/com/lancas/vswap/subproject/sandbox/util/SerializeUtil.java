package com.lancas.vswap.subproject.sandbox.util;

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
