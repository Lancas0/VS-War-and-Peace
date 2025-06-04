package com.lancas.vswap.ship.helper.builder;

import com.lancas.vswap.subproject.sandbox.api.data.ITransformPrimitive;
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
    private Vector3d scale;  //todo 3d scale?
    private Vector3d velocity;
    private Vector3d omega;
    private String dimension;


    public TeleportDataBuilder(Level level, Vector3dc inWorldPos, Quaterniondc inRotation, Vector3dc inScale, Vector3dc inVelocity, Vector3dc inOmega) {
        worldPos = new Vector3d(inWorldPos);
        rotation = new Quaterniond(inRotation);
        velocity = new Vector3d(inVelocity);
        omega = new Vector3d(inOmega);
        dimension = VSGameUtilsKt.getDimensionId(level);
        scale = new Vector3d(inScale);

        //EzDebug.highlight("rotation:" + rotation);
    }
    public static TeleportDataBuilder copy(Level level, Ship ship) {
        ShipTransform transform = ship.getTransform();
        return new TeleportDataBuilder(
            level,
            transform.getPositionInWorld(),
            transform.getShipToWorldRotation(),
            transform.getShipToWorldScaling(),
            ship.getVelocity(),
            ship.getOmega()
        );
    }
    public static TeleportDataBuilder noMovementOf(Level level, Ship ship) {
        ShipTransform transform = ship.getTransform();
        return new TeleportDataBuilder(
            level,
            transform.getPositionInWorld(),
            transform.getShipToWorldRotation(),
            transform.getShipToWorldScaling(),
            new Vector3d(),
            new Vector3d()
        );
    }

    public TeleportDataBuilder setPos(Vector3dc inWorldPos) { worldPos.set(inWorldPos); return this; }
    public TeleportDataBuilder setRotation(Quaterniondc inRotation) { rotation.set(inRotation); return this; }
    public TeleportDataBuilder setVel(Vector3dc inVel) { velocity.set(inVel); return this; }
    public TeleportDataBuilder setVel(double x, double y, double z) { velocity.set(x, y, z); return this; }
    public TeleportDataBuilder setOmega(Vector3dc inOmega) { omega.set(inOmega); return this; }
    public TeleportDataBuilder setDimension(Level newLevel) { dimension = VSGameUtilsKt.getDimensionId(newLevel); return this; }
    public TeleportDataBuilder setScale(double inScale) { velocity.set(inScale); return this; }

    public TeleportDataBuilder addPos(Vector3dc add) { worldPos.add(add); return this; }

    public ShipTeleportDataImpl withPos(Vector3dc inPos) { return setPos(inPos).get(); }
    public ShipTeleportDataImpl withRot(Quaterniondc inRot) { return setRotation(inRot).get(); }
    public ShipTeleportDataImpl withScale(double inScale) { return setScale(inScale).get(); }
    public ShipTeleportDataImpl withVel(Vector3dc inVel) { return setVel(inVel).get(); }
    public ShipTeleportDataImpl withVel(double x, double y, double z) { return setVel(x, y, z).get(); }
    public ShipTeleportDataImpl withOmega(Vector3dc inOmega) { return setOmega(inOmega).get(); }
    public ShipTeleportDataImpl withTransform(ITransformPrimitive inTransform) {
        return setPos(inTransform.getPosition())
            .setRotation(inTransform.getRotation())
            .setScale(inTransform.getScale().x())  //todo 3d scale
            .get();
    }

    public TeleportDataBuilder moveShipPosToWorld(Ship ship, Vector3dc posInShip, Vector3dc toWorld) {
        Vector3d fromInWorld = ship.getShipToWorld().transformPosition(posInShip, new Vector3d());
        Vector3dc movement = toWorld.sub(fromInWorld, new Vector3d());
        worldPos = ship.getTransform().getPositionInWorld().add(movement, new Vector3d());
        return setPos(worldPos);
    }

    public TeleportDataBuilder defaultRotation() { rotation.set(0, 0, 0, 1); return this; }

    public ShipTeleportDataImpl get() {
        return new ShipTeleportDataImpl(
            worldPos,
            rotation,
            velocity,
            omega,
            dimension,
            scale.x
        );
    }
}
