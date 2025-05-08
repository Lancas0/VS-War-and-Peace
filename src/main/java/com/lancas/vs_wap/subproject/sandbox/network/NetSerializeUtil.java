package com.lancas.vs_wap.subproject.sandbox.network;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.api.component.IClientBehaviour;
import com.lancas.vs_wap.subproject.sandbox.api.component.IServerBehaviour;
import com.lancas.vs_wap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vs_wap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxClientShip;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.util.NbtBuilder;
import com.lancas.vs_wap.util.SerializeUtil;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class NetSerializeUtil {
    public static CompoundTag serializeShipForSendToClient(ISandBoxShip ship) {
        return new NbtBuilder()
            .putUUID("uuid", ship.getUuid())
            //.putNumber("remain_life_tick", ship.getRemainLifeTick())
            .putCompound("rigidbody", ship.getRigidbody().getSavedData())
            .putCompound("blockCluster", ship.getBlockCluster().getSavedData())
            .putEach("behaviours",
                () -> ship.allAddedBehaviours().filter(b -> b instanceof IClientBehaviour<?>).iterator(),
                beh -> {
                    var n = new NbtBuilder()
                        .putString("behaviour_type", beh.getClass().getName())
                        //.putString("data_type", beh.getDataType().getName())
                        .putCompound("data", beh.getSavedData())
                        .get();

                    //EzDebug.highlight("saving behaviour:" + beh.getClass().getName());

                    return n;
                }
            ).get();
    }
    public static CompoundTag serializeShipForSendToServer(ISandBoxShip ship) {
        return new NbtBuilder()
            .putUUID("uuid", ship.getUuid())
            //.putNumber("remain_life_tick", ship.getRemainLifeTick())
            .putCompound("rigidbody", ship.getRigidbody().getSavedData())
            .putCompound("blockCluster", ship.getBlockCluster().getSavedData())
            .putEach("behaviours",
                () -> ship.allAddedBehaviours().filter(b -> b instanceof IServerBehaviour<?>).iterator(),
                beh -> new NbtBuilder()
                    .putString("behaviour_type", beh.getClass().getName())
                    //.putString("data_type", beh.getDataType().getName())
                    .putCompound("data", beh.getSavedData())
                    .get()
            ).get();
    }

    public static SandBoxClientShip deserializeAsClientShip(CompoundTag nbt) {
        UUID uuid = nbt.getUUID("uuid");
        //int remainLifeTick = nbt.getInt("remain_life_tick");
        RigidbodyData rigidbodyData = new RigidbodyData().load(nbt.getCompound("rigidbody"));
        BlockClusterData clusterData = new BlockClusterData().load(nbt.getCompound("blockCluster"));

        SandBoxClientShip clientShip = new SandBoxClientShip(uuid, rigidbodyData, clusterData);
        //clientShip.setRemainLifeTick(remainLifeTick);

        NbtBuilder.modify(nbt).readEachCompoundDo("behaviours", t -> {
            String typename = t.getString("behaviour_type");
            //String dataTypename = t.getString("data_type");
            CompoundTag savedData = t.getCompound("data");

            //EzDebug.highlight("now loading behaviour:" + typename);

            IServerBehaviour<?> behaviour = SerializeUtil.createByClassName(typename);
            //IComponentData<?> data = SerializeUtil.createByClassName(dataTypename);

            if (!(behaviour instanceof IClientBehaviour<?>)) {
                EzDebug.warn("the network shipNbt sent to client contains a non-client behaviour! ");
                return;
            }

            behaviour.loadSavedData(clientShip, savedData);
        });
        return clientShip;
    }
    public static SandBoxServerShip deserializeAsServerShip(CompoundTag nbt) {
        UUID uuid = nbt.getUUID("uuid");
        int remainLifeTick = nbt.getInt("remain_life_tick");
        RigidbodyData rigidbodyData = new RigidbodyData().load(nbt.getCompound("rigidbody"));
        BlockClusterData clusterData = new BlockClusterData().load(nbt.getCompound("blockCluster"));

        SandBoxServerShip serverShip = new SandBoxServerShip(uuid, rigidbodyData, clusterData);
        //serverShip.setRemainLifeTick(remainLifeTick);

        NbtBuilder.modify(nbt).readEachCompoundDo("behaviours", t -> {
            String typename = t.getString("behaviour_type");
            //String dataTypename = t.getString("data_type");
            CompoundTag savedData = t.getCompound("data");

            IServerBehaviour<?> behaviour;
            behaviour = SerializeUtil.createByClassName(typename);
            //IComponentData<?> data = SerializeUtil.createByClassName(dataTypename);

            if (!(behaviour instanceof IServerBehaviour<?>)) {
                EzDebug.warn("the network shipNbt sent to server contains a non-server behaviour!");
                return;
            }

            /*if (data != null) {
                data.load(savedData);
                behaviour.loadDataUnsafe(serverShip, data);
            }*/
            behaviour.loadSavedData(serverShip, savedData);
        });
        return serverShip;
    }
}
