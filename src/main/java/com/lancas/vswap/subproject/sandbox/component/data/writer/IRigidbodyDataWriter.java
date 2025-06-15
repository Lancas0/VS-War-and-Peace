package com.lancas.vswap.subproject.sandbox.component.data.writer;

import com.lancas.vswap.subproject.sandbox.api.component.IComponentDataWriter;
import com.lancas.vswap.subproject.sandbox.api.data.ITransformPrimitive;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3ic;

import java.util.function.Function;

public interface IRigidbodyDataWriter extends IComponentDataWriter<RigidbodyData> {
    public IRigidbodyDataWriter set(RigidbodyData other);

    public IRigidbodyDataWriter setPosition(Vector3dc p);
    public IRigidbodyDataWriter updatePosition(Function<Vector3dc, Vector3d> pTransformer);

    public default IRigidbodyDataWriter addPosition(Vector3dc movement) {
        Vector3d movementImm = new Vector3d(movement);
        return updatePosition(p -> movementImm.add(p));
    }

    public IRigidbodyDataWriter setRotation(Quaterniondc r);
    public IRigidbodyDataWriter setScale(Vector3dc s);
    public IRigidbodyDataWriter mulScale(Vector3dc s);
    public default IRigidbodyDataWriter mulScale(double s) {
        return mulScale(new Vector3d(s, s, s));
    }
    public IRigidbodyDataWriter setTransform(ITransformPrimitive newTransform);

    public default IRigidbodyDataWriter setVelocity(Vector3dc v) { return setVelocity(v.x(), v.y(), v.z()); }
    public IRigidbodyDataWriter setVelocity(double x, double y, double z);
    public IRigidbodyDataWriter updateVelocity(Function<Vector3dc, Vector3d> vTransformer);
    public IRigidbodyDataWriter setOmega(Vector3dc v);
    public IRigidbodyDataWriter setGravity(Vector3dc newGravity);

    public default IRigidbodyDataWriter setNoMovement() {
        return setVelocity(0, 0, 0).setOmega(new Vector3d(0, 0, 0));
    }

    public IRigidbodyDataWriter setStatic(boolean newVal);

    public IRigidbodyDataWriter applyWorldForce(Vector3dc f);
    public IRigidbodyDataWriter applyWorldTorque(Vector3dc t);
    public default IRigidbodyDataWriter applyWorldImpulse(Vector3dc f) { return applyWorldForce(f.mul(60, new Vector3d())); }

    public IRigidbodyDataWriter applyWork(double work);


    public default IRigidbodyDataWriter setNoGravity() { setGravity(new Vector3d()); return this; }
    public default IRigidbodyDataWriter setEarthGravity() { setGravity(new Vector3d(0, -9.8, 0)); return this; }


    public IRigidbodyDataWriter moveLocalPosToWorld(Vector3dc localPos, Vector3dc toWorld);
    public default IRigidbodyDataWriter moveLocalPosToWorld(Vector3ic localPos, Vector3dc toWorld) {
        return moveLocalPosToWorld(new Vector3d(localPos), toWorld);
    }
}
