package com.lancas.vs_wap.obsolete;


import com.lancas.vs_wap.register.ServerDataCollector;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.UUID;

public class PlayerShipMgr {

    private static Dictionary<UUID, Long> playerUUID2ShipID = new Hashtable<>();

    public static ServerShip getOrCreateShip(ServerLevel level, UUID playerUUID) {
        Long id = playerUUID2ShipID.get(playerUUID);
        ServerPlayer player = ServerDataCollector.playerList.getPlayer(playerUUID);

        if (player == null) return null;
        return null;
/*
        ServerShip existedShip = id == null ? null : VSGameUtilsKt.getShipObjectWorld(level).getAllShips().getById(id);
        if (existedShip == null) {
            ServerShip ship =
                new ShipBuilder(
                    player.getOnPos().above(),
                    //player.position().add(0, 0.5, 0),
                    level,
                    1f
                ).addBlock(new BlockPos(0, 0, 0), AllBlocks.SolidAirBlock.get().defaultBlockState())
                .addBlock(new BlockPos(0, 1, 0), AllBlocks.SolidAirBlock.get().defaultBlockState())
                .setNoCollisionWithPlayer()
                .withShipDo(buildingShip -> { TestForceInductor.getOrCreate(buildingShip, playerUUID); })
                .setWorldPos(JomlUtil.d(player.position()))
                //.setTransformProvider(new FollowPlayerTP(playerUUID))
                //.setStatic(true)
                .get();

            EzDebug.Log("new ship create with player:" + player.getUUID());

            playerUUID2ShipID.put(playerUUID, ship.getId());
            return ship;
        } else {
            return existedShip;
        }
       */
    }
    public static Long getShipID(UUID playerUUID) { return playerUUID2ShipID.get(playerUUID); }
}