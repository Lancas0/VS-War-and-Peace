package com.lancas.vswap.subproject.sandbox.compact.mc;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.api.data.ITransformPrimitive;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.subproject.sandbox.component.data.IRigidbodyData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import org.apache.commons.lang3.NotImplementedException;
import org.joml.*;

import java.util.function.Function;
import java.util.stream.Stream;

public class GroundRigidbodyDataWrapper implements IRigidbodyData {
    public static final Vector3dc ZERO = new Vector3d(0, 0, 0);
    public static final Vector3dc ONE = new Vector3d(1, 1, 1);
    public static final Quaterniondc IDENTICAL = new Quaterniond();
    public static final Matrix4dc WORLD_TO_WORLD = new Matrix4d().translationRotateScale(ZERO, IDENTICAL, ONE);
    public static final ITransformPrimitive WORLD_TRANSFORM = new TransformPrimitive();

    public static final Matrix3dc INERTIA = new Matrix3d(0, 0, 0, 0, 0, 0, 0, 0, 0);

    @Override
    public double getMass() { return 0; }  //todo may change other value

    @Override
    public Vector3d getLocalMassCenter(Vector3d dest) { return dest.set(ZERO); }

    @Override
    public Vector3d getWorldMassCenter(Vector3d dest) { return dest.set(ZERO); }

    @Override
    public Matrix3dc getLocalInertia() { return INERTIA; }

    @Override
    public Vector3dc getPosition() { return ZERO; }

    @Override
    public Quaterniondc getRotation() { return IDENTICAL; }

    @Override
    public Vector3dc getScale() { return ONE; }

    @Override
    public ITransformPrimitive getTransform() { return WORLD_TRANSFORM; }
    @Override
    public Matrix4dc getLocalToWorld() { return WORLD_TO_WORLD; }
    @Override
    public Matrix4dc getWorldToLocal() { return WORLD_TO_WORLD; }

    @Override
    public Vector3dc getVelocity() { return ZERO; }
    @Override
    public Vector3dc getOmega() { return ZERO; }
    @Override
    public Vector3dc getGravity() { return ZERO; }

    @Override
    public Stream<Vector3dc> allForces() {
        return Stream.empty();
    }
    @Override
    public Stream<Vector3dc> allTorques() {
        return Stream.empty();
    }

    @Override
    public boolean isStatic() { return true; }

    @Override
    public RigidbodyData getCopiedData(RigidbodyData dest) {
        throw new NotImplementedException();
    }

    @Override
    public IRigidbodyDataWriter set(RigidbodyData other) {
        EzDebug.warn("no use to set rigidbodyData of ground");
        return this;
    }

    @Override
    public IRigidbodyDataWriter setPosition(Vector3dc p) {
        EzDebug.warn("no use to set position of ground");
        return this;
    }

    @Override
    public IRigidbodyDataWriter updatePosition(Function<Vector3dc, Vector3d> pTransformer) {
        EzDebug.warn("no use to update position of ground");
        return this;
    }

    @Override
    public IRigidbodyDataWriter setRotation(Quaterniondc r) {
        EzDebug.warn("no use to set rotation of ground");
        return this;
    }
    @Override
    public IRigidbodyDataWriter setScale(Vector3dc s) {
        EzDebug.warn("no use to set scale of ground");
        return this;
    }

    @Override
    public IRigidbodyDataWriter mulScale(Vector3dc s) {
        EzDebug.warn("no use to mul scale of ground");
        return this;
    }

    @Override
    public IRigidbodyDataWriter setTransform(ITransformPrimitive newTransform) {
        EzDebug.warn("no use to set transform of ground");
        return this;
    }
    /*@Override
    public IRigidbodyDataWriter setVelocity(Vector3dc v) {
        EzDebug.warn("no use to set vel of ground");
        return this;
    }*/

    @Override
    public IRigidbodyDataWriter setVelocity(double x, double y, double z) {
        EzDebug.warn("no use to set vel of ground");
        return this;
    }

    @Override
    public IRigidbodyDataWriter updateVelocity(Function<Vector3dc, Vector3d> vTransformer) {
        EzDebug.warn("no use to update vel of ground");
        return this;
    }

    @Override
    public IRigidbodyDataWriter setOmega(Vector3dc v) {
        EzDebug.warn("no use to set omega of ground");
        return this;
    }
    @Override
    public IRigidbodyDataWriter setGravity(Vector3dc newGravity) {
        EzDebug.warn("no use to set gravity of ground");
        return this;
    }
    @Override
    public IRigidbodyDataWriter setStatic(boolean newVal) {
        EzDebug.warn("no use to set static of ground");
        return this;
    }
    @Override
    public IRigidbodyDataWriter applyWorldForce(Vector3dc f) {
        EzDebug.warn("no use to apply force to ground");
        return this;
    }
    @Override
    public IRigidbodyDataWriter applyWorldTorque(Vector3dc t) {
        EzDebug.warn("no use to apply torque to ground");
        return this;
    }

    @Override
    public IRigidbodyDataWriter applyWork(double work) { EzDebug.warn("no use to apply work to ground"); return this; }

    @Override
    public IRigidbodyDataWriter moveLocalPosToWorld(Vector3dc localPos, Vector3dc toWorld) {
        EzDebug.warn("no use to set position of ground");
        return this;
    }
}
