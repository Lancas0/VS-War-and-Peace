package com.lancas.vswap.obsolete;
/*
import com.lancas.einherjar.debug.EzDebug;
import com.lancas.einherjar.foundation.api.ICanCarryShip;
import com.lancas.einherjar.register.ServerDataCollector;
import com.lancas.einherjar.ship.attachment.HoldableAttachment;
import com.lancas.einherjar.ship.feature.builder.ShipTransformBuilder;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4dc;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShipTransformProvider;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;

import java.util.UUID;

//removed after game restart.
//do not need serialziation
public class ServerCarryTransformProvider implements ServerShipTransformProvider {
    private UUID playerUUID;

    private final HoldableAttachment holdable;
    public ServerCarryTransformProvider(@NotNull HoldableAttachment att, UUID inPlayerUUID) {
        holdable = att;
        playerUUID = inPlayerUUID;
    }

    @Override
    public @Nullable NextTransformAndVelocityData provideNextTransformAndVelocity(@NotNull ShipTransform shipTransform, @NotNull ShipTransform shipTransform1) {
        if (holdable == null) {
            EzDebug.warn("holdable attachment is null. the ship will not be holded.");
            return null;
        }

        ServerPlayer holder = ServerDataCollector.playerList.getPlayer(playerUUID);
        if (holder == null) {
            EzDebug.warn("can not find player that is holding this ship.");
            return null;
        }
        if (!(holder instanceof ICanCarryShip icanCarryShip)) {
            EzDebug.fatal("player can not hold ship because of unkown reason");
            return null;
        }

        Matrix4dc shipToWorld = shipTransform.getShipToWorld();
        Vector3d newWorldPos = icanCarryShip.getCarryPos(
            holdable.holdPivotBpInShip.toBp(),
            holdable.forwardInShip,
            shipTransform.getShipToWorld(),
            shipTransform.getPositionInWorld()
        );
        Quaterniond newRotation = icanCarryShip.getCarryRotation(/.*shipToWorld, *./holdable.forwardInShip);

        return new NextTransformAndVelocityData(
            ShipTransformBuilder.copy(shipTransform)
                .setPosInWorld(newWorldPos)
                .setRotation(newRotation)
                .get(),

            new Vector3d(),
            new Vector3d()
        );
    }
}*/
