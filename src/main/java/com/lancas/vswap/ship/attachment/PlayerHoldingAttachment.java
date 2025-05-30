package com.lancas.vswap.ship.attachment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vswap.obsolete.ship.MountToPlayerTypes;
import com.lancas.vswap.register.ServerDataCollector;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.UUID;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerHoldingAttachment implements IAttachment/*, ShipForcesInducer*/ {
    private static Vector3d getFarPos(Vector3dc curPos) { return new Vector3d(curPos.x(), 300, curPos.z()); }

    /*public static PlayerHoldingAttachment getOrAdd(@NotNull ServerShip ship) {
        PlayerHoldingAttachment attachment = ship.getAttachment(PlayerHoldingAttachment.class);
        if (attachment == null) {
            attachment = new PlayerHoldingAttachment();
            ship.saveAttachment(PlayerHoldingAttachment.class, attachment);
        }

        return attachment;
    }*/
    public UUID playerUUID;
    public long shipID;
    public MountToPlayerTypes mountType;

    @JsonIgnore
    public boolean holding = false;

    public PlayerHoldingAttachment() {}
    public PlayerHoldingAttachment(@NotNull UUID inPlayerUUID, long inShipID, MountToPlayerTypes inMountType) {
        playerUUID = inPlayerUUID;
        shipID = inShipID;
        mountType = inMountType;
    }

    @Override
    public void addTo(@NotNull ServerShip ship) {
        ship.saveAttachment(PlayerHoldingAttachment.class, this);
    }

    public ServerPlayer getPlayer(/*@NotNull ServerLevel level*/) {
        //return (ServerPlayer)level.getPlayerByUUID(playerUUID);
        return ServerDataCollector.playerList.getPlayer(playerUUID);
    }

    /*@Override
    public void applyForces(@NotNull PhysShip physShip) {
        //EzDebug.Log("apply forces");
        //VSGameUtilsKt.getLevelFromDimensionId(MinecraftServer.class, dimID);
        //ServerPlayer player = ServerDataCollector.playerList.getPlayer(playerUUID);
        ServerPlayer player = getPlayer();

        EzDebug.Log("holding:" + holding + ", player is null:" + (player == null));
        //for (Player tp : ServerDataCollector.playerList.getPlayers())
        //    EzDebug.Log("all players:" + tp.getUUID());

        if (!holding || player == null) {
            //moveToFarPos(physShip);
            return;
        }

        //moveToPlayer(player, physShip);
    }
    private void moveToFarPos(@NotNull PhysShip physShip) {
        EzDebug.Log("move to far pos");
        Vector3dc shipPos = physShip.getTransform().getPositionInWorld();
        double mass = physShip.getMass();

        Vector3d offset = new Vector3d();
        getFarPos(shipPos).sub(shipPos.x(), shipPos.y(), shipPos.z(), offset);

        physShip.applyInvariantForce(offset.mul(mass));
    }
    private void moveToPlayer(@NotNull ServerPlayer player, @NotNull PhysShip physShip) {
        EzDebug.Log("move to player:" + player.getUUID());
        //physShip.getTransform().getPositionInWorld();
        //physShip.applyInvariantForce();
        Vector3dc shipPos = physShip.getTransform().getPositionInWorld();
        Vector3d mountPos = JomlUtil.d(player.position());
        double mass = physShip.getMass();

        Vector3d offset = new Vector3d();
        mountPos.sub(shipPos.x(), shipPos.y(), shipPos.z(), offset);

        physShip.applyInvariantForce(offset.mul(mass));
        physShip.applyInvariantForce(new Vector3d(0, 10, 0).mul(mass));
    }*/
}
