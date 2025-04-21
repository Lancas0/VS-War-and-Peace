package com.lancas.vs_wap.ship.helper.builder;

import com.lancas.vs_wap.debug.EzDebug;
import net.minecraft.world.level.Level;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class TeleportDataBuilder {
    private Vector3d worldPos;
    private Quaterniond rotation;
    private Vector3d velocity;
    private Vector3d omega;
    private String dimension;
    private double scale;  //todo 3d scale?

    public TeleportDataBuilder(Level level, Vector3dc inWorldPos, Quaterniondc inRotation, Vector3dc inVelocity, Vector3dc inOmega, double inScale) {
        worldPos = new Vector3d(inWorldPos);
        rotation = new Quaterniond(inRotation);
        velocity = new Vector3d(inVelocity);
        omega = new Vector3d(inOmega);
        dimension = VSGameUtilsKt.getDimensionId(level);
        scale = inScale;

        //EzDebug.highlight("rotation:" + rotation);
    }
    public static TeleportDataBuilder copy(Level level, Ship ship) {
        ShipTransform transform = ship.getTransform();
        return new TeleportDataBuilder(
            level,
            transform.getPositionInWorld(),
            transform.getShipToWorldRotation(),
            ship.getVelocity(),
            ship.getOmega(),
            transform.getShipToWorldScaling().x()  //todo 3d scale?
        );
    }
    public static TeleportDataBuilder noMovementOf(Level level, Ship ship) {
        ShipTransform transform = ship.getTransform();
        return new TeleportDataBuilder(
            level,
            transform.getPositionInWorld(),
            transform.getShipToWorldRotation(),
            new Vector3d(),
            new Vector3d(),
            transform.getShipToWorldScaling().x()  //todo 3d scale?
        );
    }

    public TeleportDataBuilder setPos(Vector3dc inWorldPos) { worldPos.set(inWorldPos); return this; }
    public TeleportDataBuilder setRotation(Quaterniondc inRotation) { rotation.set(inRotation); return this; }
    public TeleportDataBuilder setVel(Vector3dc inVel) { velocity.set(inVel); return this; }
    public TeleportDataBuilder setOmega(Vector3dc inOmega) { omega.set(inOmega); return this; }
    public TeleportDataBuilder setDimension(Level newLevel) { dimension = VSGameUtilsKt.getDimensionId(newLevel); return this; }
    public TeleportDataBuilder setScale(double inScale) { velocity.set(inScale); return this; }

    public TeleportDataBuilder addPos(Vector3dc add) { worldPos.add(add); return this; }

    public ShipTeleportDataImpl withVel(Vector3dc inVel) { return setVel(inVel).get(); }
    public ShipTeleportDataImpl withPos(Vector3dc inPos) { return setPos(inPos).get(); }

    public TeleportDataBuilder defaultRotation() { rotation.set(0, 0, 0, 1); return this; }

    public ShipTeleportDataImpl get() {
        return new ShipTeleportDataImpl(
            worldPos,
            rotation,
            velocity,
            omega,
            dimension,
            scale
        );
    }
}
