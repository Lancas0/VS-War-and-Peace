package com.lancas.vs_wap.subproject.sandbox.event;

import com.lancas.vs_wap.event.impl.BiEventImpl;
import com.lancas.vs_wap.event.impl.BiLazyEvent;
import com.lancas.vs_wap.event.impl.QuadEventImpl;
import com.lancas.vs_wap.foundation.network.NetworkHandler;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxTransformData;
import com.lancas.vs_wap.subproject.sandbox.api.UUIDParamWrapper;
import com.lancas.vs_wap.subproject.sandbox.network.SyncAddClientRendererPacketS2C;
import com.lancas.vs_wap.subproject.sandbox.network.SyncRemoveClientRendererPacketS2C;
import com.lancas.vs_wap.subproject.sandbox.network.UpdateShipTransformPacketS2C;
import com.lancas.vs_wap.subproject.sandbox.network.worldsync.SyncClientWorldIfNecessaryPacketS2C;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3ic;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mod.EventBusSubscriber
public class SandBoxEventMgr {
    public static BiLazyEvent<UUIDParamWrapper, SandBoxTransformData> onServerShipTransformDirty = new BiLazyEvent<>();
    public static BiEventImpl<ServerLevel, SandBoxServerShip> onAddNewShipInServerWorld = new BiEventImpl<>();
    public static BiEventImpl<ServerLevel, SandBoxServerShip> onRemoveShipFromServerWorld = new BiEventImpl<>();
    //目前能保证当删除一个空方块时不会触发
    public static QuadEventImpl<SandBoxServerShip, Vector3ic, BlockState, BlockState> onShipBlockReplaced = new QuadEventImpl<>();
    //public static

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        onServerShipTransformDirty.invokeAll();

    }

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
        onServerShipTransformDirty.addListener((uuid, transformData) -> {
            //sync in server world thread.
            NetworkHandler.sendToAllPlayers(new UpdateShipTransformPacketS2C(uuid.uuid, transformData));
        });

        onAddNewShipInServerWorld.addListener((level, ship) -> {
            NetworkHandler.sendToAllPlayers(
                new SyncAddClientRendererPacketS2C(VSGameUtilsKt.getDimensionId(level), ship.createRenderer().saved())
            );
        });
        onRemoveShipFromServerWorld.addListener(((level, ship) -> {
            NetworkHandler.sendToAllPlayers(
                new SyncRemoveClientRendererPacketS2C(VSGameUtilsKt.getDimensionId(level), ship.getUuid())
            );
        }));

        onShipBlockReplaced.addListener(((ship, localPos, oldState, newState) -> {
            ship.getAllBehaviours().forEach(beh -> beh.onBlockReplaced(localPos, oldState, newState));

            //todo update visible block
        }));

    }


}
