package com.lancas.vswap.subproject.sandbox.event;

import com.lancas.vswap.event.impl.*;
import com.lancas.vswap.foundation.network.NetworkHandler;
import com.lancas.vswap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.subproject.sandbox.api.UUIDLazyParamWrapper;
import com.lancas.vswap.subproject.sandbox.network.SyncRemoveClientRendererPacketS2C;
import com.lancas.vswap.subproject.sandbox.network.UpdateShipTransformPacketS2C;
import com.lancas.vswap.subproject.sandbox.network.sync.worldsync.SyncClientWorldIfNecessaryPacketS2C;
import com.lancas.vswap.subproject.sandbox.network.sync.worldsync.SyncServerShipToClientPacket;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3ic;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mod.EventBusSubscriber
public class SandBoxEventMgr {
    //todo onServerShipComponentUpdate which can update any component data
    public static BiLazyEvent<UUIDLazyParamWrapper, TransformPrimitive/*, AABBdLazyParamWrapper*/> onServerShipTransformDirty = new BiLazyEvent<>();
    public static BiEventImpl<ServerLevel, SandBoxServerShip> onSyncServerShipToClient = new BiEventImpl<>();
    //public static BiEventImpl<ServerLevel, IServerSandBoxShip> onRemoveShipFromServerWorld = new BiEventImpl<>();
    //目前能保证当删除一个空方块时不会触发
    public static QuadEventImpl<ISandBoxShip, Vector3ic, BlockState, BlockState> onShipBlockReplaced = new QuadEventImpl<>();

    public static BiEventImpl<ISandBoxWorld<?>, ISandBoxShip> onRemoveShip = new BiEventImpl<>();
    //public static

    /*@SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        onServerShipTransformDirty.invokeAll();

    }*/

    @SubscribeEvent
    public static void onPlayerJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel sLevel)) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        //SandBoxServerWorld world = SandBoxServerWorld.getOrCreate(sLevel);

        NetworkHandler.sendToClientPlayer(
            player,
            /*new SyncClientWorldIfNecessaryPacketS2C(
                VSGameUtilsKt.getDimensionId(sLevel),
                world.getAllSaved()
            )*/
            new SyncClientWorldIfNecessaryPacketS2C(VSGameUtilsKt.getDimensionId(sLevel))
        );
    }
    /*
    @SubscribeEvent
    public static void onPlayerEnterLevel(PlayerEvent.PlayerChangedDimensionEvent event) {
        // 获取玩家和维度信息
        //ResourceKey<Level> fromDim = event.getFrom();
        //ResourceKey<Level> toDim = event.getTo();
        Player player = event.getEntity();
        ServerLevel level = (ServerLevel)player.level();

        SandBoxServerWorld world = SandBoxServerWorld.getOrCreate(level);

        EzDebug.log("player enter level:" + VSGameUtilsKt.getDimensionId(level));

        /.*NetworkHandler.sendToClientPlayer(
            (ServerPlayer)event.getEntity(),

            new SyncClientShipWorldPacketS2C(
                VSGameUtilsKt.getDimensionId(level),
                world.getAllSaved()
            )
        );*./
        //EzDebug.log("current level:" + VSGameUtilsKt.getDimensionId(level));
    }*/

    public static void register() {
        onServerShipTransformDirty.addListener((uuid, transformData/*, localAABB*/) -> {
            //sync in server world thread.
            //todo only send to player near to the ship with uuid
            NetworkHandler.sendToAllPlayers(new UpdateShipTransformPacketS2C(uuid.uuid, transformData/*, localAABB.getAABB(new AABBd())*/));
        });

        onSyncServerShipToClient.addListener((level, ship) -> {
            NetworkHandler.sendToAllPlayers(
                new SyncServerShipToClientPacket(VSGameUtilsKt.getDimensionId(level), ship)
            );
        });
        /*onRemoveShipFromServerWorld.addListener(((level, ship) -> {
            NetworkHandler.sendToAllPlayers(
                new SyncRemoveClientRendererPacketS2C(VSGameUtilsKt.getDimensionId(level), ship.getUuid())
            );
        }));*/
        onRemoveShip.addListener((world, ship) -> {
            if (!(world instanceof SandBoxServerWorld)) return;

            NetworkHandler.sendToAllPlayers(
                new SyncRemoveClientRendererPacketS2C(VSGameUtilsKt.getDimensionId(world.getWorld()), ship.getUuid())
            );
        });

        onShipBlockReplaced.addListener(((ship, localPos, oldState, newState) -> {
            ship.allAddedBehaviours().forEach(beh -> beh.onBlockReplaced(localPos, oldState, newState));

            //todo update visible block
        }));

    }


}
