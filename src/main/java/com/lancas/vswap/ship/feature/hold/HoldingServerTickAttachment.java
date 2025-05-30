package com.lancas.vswap.ship.feature.hold;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.content.saved.RetrievableDisabledCollisionMgr;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ServerTickListener;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.UUID;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HoldingServerTickAttachment implements ServerTickListener {

    @JsonIgnore
    private ServerLevel level;
    @JsonIgnore
    private ServerShip ship;
    @JsonIgnore
    private UUID playerHoldingThis;

    public boolean isHoldBy(Player player) { return player.getUUID().equals(playerHoldingThis); }
    public boolean isHoldBy(UUID playerUUID) { return playerUUID.equals(playerHoldingThis); }
    public UUID getHolderUUID() { return playerHoldingThis; }

    private HoldingServerTickAttachment() {}
    public static void apply(ServerLevel inLevel, ServerShip ship, UUID holderUUID) {
        HoldingServerTickAttachment ticker = ShipUtil.computeIfAbsent(ship, HoldingServerTickAttachment.class, HoldingServerTickAttachment::new);
        ticker.ship = ship;
        ticker.level = inLevel;
        ticker.playerHoldingThis = holderUUID;
    }
    public static void disable(ServerLevel level, ServerShip ship) {
        int rc = RetrievableDisabledCollisionMgr.retrieveAllCollisionsOf(level, ship.getId());
        EzDebug.log("retrieve count:" + rc);

        var att = ship.getAttachment(HoldingServerTickAttachment.class);
        if (att != null) {
            att.level = null;
            att.ship = null;
            att.playerHoldingThis = null;
        }

        ship.saveAttachment(HoldingServerTickAttachment.class, null);
    }

    @Override
    public void onServerTick() {
        if (level == null || ship == null) return;
        //EzDebug.log("holding ticking");

        for (Ship nearShip : VSGameUtilsKt.getShipsIntersecting(level, ship.getWorldAABB())) {
            if (nearShip.getId() == ship.getId()) continue;

            RetrievableDisabledCollisionMgr.disableCollisionBetween(level, nearShip.getId(), ship.getId());
        }
    }
}
