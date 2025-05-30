package com.lancas.vswap.obsolete;

/*
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class ShipManipulator {
    /.*private static Matrix4dc getShipToWorld(Ship ship) {
        return ship instanceof ClientShip cShip ? cShip.getRenderTransform().getShipToWorld() : ship.getShipToWorld();
    }*./

    public static void moveShipPosToWorld(ServerLevel level, ServerShip ship, Vector3dc shipPos, Vector3dc worldPos) {
        Vector3d shipPosInWorld = ship.getShipToWorld().transformPosition(shipPos, new Vector3d());
        Vector3d movement = worldPos.sub(shipPosInWorld, new Vector3d());

        VSGameUtilsKt.getShipObjectWorld(level).teleportShip(ship, new ShipTeleportDataImpl(
            worldPos.add(movement, new Vector3d()),
            ship.getTransform().getShipToWorldRotation(),
            ship.getVelocity(),
            ship.getOmega(),
            VSGameUtilsKt.getDimensionId(level),
            ship.getTransform().getShipToWorldScaling().x()  //todo 3d scale?
        ));
    }
}
*/