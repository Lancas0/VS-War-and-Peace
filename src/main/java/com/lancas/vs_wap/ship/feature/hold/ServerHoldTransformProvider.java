package com.lancas.vs_wap.ship.feature.hold;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.register.ServerDataCollector;
import com.lancas.vs_wap.ship.attachment.HoldableAttachment;
import com.lancas.vs_wap.ship.helper.builder.ShipTransformBuilder;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShipTransformProvider;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;

import java.util.UUID;

//removed after game restart.
//do not need serialziation
public class ServerHoldTransformProvider implements ServerShipTransformProvider {
    private UUID playerUUID;
    private ShipHoldSlot slot;

    private final HoldableAttachment holdable;
    public ServerHoldTransformProvider(ShipHoldSlot inSlot, HoldableAttachment inHoldable, UUID inPlayerUUID) {
        holdable = inHoldable;
        playerUUID = inPlayerUUID;
        slot = inSlot;
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
        if (!(holder instanceof ICanHoldShip icanHoldShip)) {
            EzDebug.fatal("player can not hold ship because of unkown reason");
            return null;
        }


        Vector3d newWorldPos = slot.getHoldPos(
            holder,
            holdable.holdPivotBpInShip.toBp(),
            holdable.forwardInShip,
            shipTransform.getShipToWorld(),
            shipTransform.getPositionInWorld()
        );
        Quaterniond newRotation = slot.getHoldRotation(
            holder,
            holdable.holdPivotBpInShip.toBp(),
            holdable.forwardInShip
        );

        return new NextTransformAndVelocityData(
            ShipTransformBuilder.copy(shipTransform)
                .setPosInWorld(newWorldPos)
                .setRotation(newRotation)
                .get(),

            new Vector3d(),
            new Vector3d()
        );
    }
}
