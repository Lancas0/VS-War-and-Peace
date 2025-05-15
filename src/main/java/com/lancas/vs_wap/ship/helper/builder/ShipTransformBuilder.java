package com.lancas.vs_wap.ship.helper.builder;

import net.minecraft.core.Direction;
import org.joml.*;
import org.joml.primitives.AABBic;
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
    public ShipTransformBuilder moveWorldPos(Vector3dc movement) {
        posInWorld.add(movement);
        return this;
    }
    public ShipTransformBuilder setWorldPosSoThatFaceCenterAt(AABBic shipAABB, Matrix4dc shipToWorld, Direction face, Vector3dc moveTo) {
        Vector3d faceCenterInShip = shipAABB.center(new Vector3d());
        switch (face) {
            case UP -> faceCenterInShip.setComponent(1, shipAABB.maxY());  //xz upper face, set y max
            case DOWN -> faceCenterInShip.setComponent(1, shipAABB.minY());
            case SOUTH -> faceCenterInShip.setComponent(2, shipAABB.maxZ());  //xy forward face, set z max
            case NORTH -> faceCenterInShip.setComponent(2, shipAABB.minZ());
            case EAST -> faceCenterInShip.setComponent(0, shipAABB.maxX());  //yz left face, set x max
            case WEST -> faceCenterInShip.setComponent(0, shipAABB.minX());

            default -> faceCenterInShip.setComponent(1, shipAABB.maxY());  //should never be called
        }


        Vector3d worldFaceCenter = shipToWorld.transformPosition(faceCenterInShip, new Vector3d());
        Vector3d movement = moveTo.sub(worldFaceCenter, new Vector3d());
        return moveWorldPos(movement);
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
    public ShipTransformBuilder rotate(Quaterniondc rot) {
        rot.mul(rotation, rotation);
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
