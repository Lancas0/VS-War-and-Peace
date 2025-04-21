package com.lancas.vs_wap.ship.helper.builder;

import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl;

public class ShipTransformBuilder {
    private final Vector3d posInWorld;
    private final Vector3d posInShip;
    private final Quaterniond rotation;
    private final Vector3d scale;

    public ShipTransformBuilder() {
        posInWorld = new Vector3d();
        posInShip = new Vector3d();
        rotation = new Quaterniond();
        scale = new Vector3d();
    }
    public ShipTransformBuilder(Vector3dc inPosInWorld, Vector3dc inPosInShip, Quaterniondc inRotation, Vector3dc inScale) {
        posInWorld = new Vector3d(inPosInWorld);
        posInShip = new Vector3d(inPosInShip);
        rotation = new Quaterniond(inRotation);
        scale = new Vector3d(inScale);
    }

    public static ShipTransformBuilder copy(ShipTransform fromTransform) {
        return new ShipTransformBuilder(
            fromTransform.getPositionInWorld(),
            fromTransform.getPositionInShip(),
            fromTransform.getShipToWorldRotation(),
            fromTransform.getShipToWorldScaling()
        );
    }
    public static ShipTransformBuilder copy(Ship ship) {
        return copy(ship.getTransform());
    }
    public static ShipTransformBuilder copyAndSetPosInWorld(ShipTransform fromTransform, Vector3dc posInWorld) {
        return copy(fromTransform).setPosInWorld(posInWorld);
    }


    public ShipTransformBuilder setPosInWorld(Vector3dc inPosInWorld) {
        posInWorld.set(inPosInWorld);
        return this;
    }
    public ShipTransformBuilder setXInWorld(double x) {
        posInWorld.setComponent(0, x);
        return this;
    }
    public ShipTransformBuilder setYInWorld(double y) {
        posInWorld.setComponent(1, y);
        return this;
    }public ShipTransformBuilder setZInWorld(double z) {
        posInWorld.setComponent(2, z);
        return this;
    }

    public ShipTransformBuilder setPosInShip(Vector3dc inPosInShip) {
        posInShip.set(inPosInShip);
        return this;
    }
    public ShipTransformBuilder setRotation(Quaterniondc inRotation) {
        rotation.set(inRotation);
        return this;
    }
    public ShipTransformBuilder setScale(Vector3dc inScale) {
        scale.set(inScale);
        return this;
    }




    public ShipTransformImpl get() {
        return new ShipTransformImpl(
            posInWorld,
            posInShip,
            rotation,
            scale
        );
    }


}
