package com.lancas.vs_wap.subproject.sandbox.api.data;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.event.api.ILazyEventParam;
import com.lancas.vs_wap.subproject.sandbox.api.ISavedObject;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class TransformPrimitive implements ITransformPrimitive, ISavedObject<TransformPrimitive>, ILazyEventParam<TransformPrimitive> {
    public final Vector3d position = new Vector3d();
    public final Quaterniond rotation = new Quaterniond();
    public final Vector3d scale = new Vector3d(1, 1, 1);

    public TransformPrimitive() {}
    public TransformPrimitive(Vector3dc inPos, Quaterniondc inRot, Vector3dc inScale) {
        position.set(inPos);
        rotation.set(inRot);
        scale.set(inScale);
    }
    public TransformPrimitive(ITransformPrimitive src) {
        position.set(src.getPosition());
        rotation.set(src.getRotation());
        scale.set(src.getScale());
    }

    @Override
    public Vector3dc getPosition() { return position; }
    @Override
    public Quaterniondc getRotation() { return rotation; }
    @Override
    public Vector3dc getScale() { return scale; }

    public TransformPrimitive lerp(ITransformPrimitive other, double t, TransformPrimitive dest) {
        Vector3d lerpPos = new Vector3d();
        Quaterniond lerpRot = new Quaterniond();
        Vector3d lerpScale = new Vector3d();

        position.lerp(other.getPosition(), t, lerpPos);
        rotation.slerp(other.getRotation(), t, lerpRot);
        scale.lerp(other.getScale(), t, lerpScale);

        return dest.set(lerpPos, lerpRot, lerpScale);
    }


    public TransformPrimitive setPosition(Vector3dc newPos) {
        position.set(newPos);
        return this;
    }
    public TransformPrimitive setRotation(Quaterniondc newRot) {  //todo check rot normilized?
        rotation.set(newRot);
        return this;
    }
    public TransformPrimitive setScale(Vector3dc newScale) {
        /*if (newScale.x() < 0 || newScale.y() < 0 || newScale.z() < 0) {
            EzDebug.warn("can't set scale with a negative number!");
            return this;
        }*/ //sometimes set inv scale do have it's meaning
        scale.set(newScale);
        return this;
    }
    public TransformPrimitive setScale(double xyz) {
        scale.set(xyz, xyz, xyz);
        return this;
    }


    public TransformPrimitive rotateWorld(Quaterniondc rot) {
        rot.mul(rotation, rotation);
        return this;
    }
    public TransformPrimitive rotateLocal(Quaterniondc rot) {
        rotation.mul(rot).normalize();
        return this;
    }

    public TransformPrimitive set(ITransformPrimitive src) {
        position.set(src.getPosition());
        rotation.set(src.getRotation());
        scale.set(src.getScale());
        return this;
    }
    public TransformPrimitive set(Vector3dc inPos, Quaterniondc inRot, Vector3dc inScale) {
        position.set(inPos);
        rotation.set(inRot);
        scale.set(inScale);
        return this;
    }

    @Override
    public CompoundTag saved() {
        return new NbtBuilder()
            .putVector3d("position", position)
            .putQuaternion("rotation", rotation)
            .putVector3d("scale", scale)
            .get();
    }
    @Override
    public TransformPrimitive load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readVector3d("position", position)
            .readQuaternionD("rotation", rotation)
            .readVector3d("scale", scale);
        return this;
    }

    @Override
    public TransformPrimitive getImmutable() { return new TransformPrimitive(this); }
}
