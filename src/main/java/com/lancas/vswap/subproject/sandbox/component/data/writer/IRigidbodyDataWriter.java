package com.lancas.vswap.subproject.sandbox.component.data.writer;

import com.lancas.vswap.subproject.sandbox.api.component.IComponentDataWriter;
import com.lancas.vswap.subproject.sandbox.api.data.ITransformPrimitive;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public interface IRigidbodyDataWriter extends IComponentDataWriter<RigidbodyData> {
    public IRigidbodyDataWriter setPosition(Vector3dc p);

    public IRigidbodyDataWriter setRotation(Quaterniondc r);
    public IRigidbodyDataWriter setScale(Vector3dc s);
    public IRigidbodyDataWriter setTransform(ITransformPrimitive newTransform);

    public IRigidbodyDataWriter setVelocity(Vector3dc v);
    public IRigidbodyDataWriter setOmega(Vector3dc v);
    public IRigidbodyDataWriter setGravity(Vector3dc newGravity);

    public IRigidbodyDataWriter setStatic(boolean newVal);

    public IRigidbodyDataWriter applyWorldForce(Vector3dc f);
    public IRigidbodyDataWriter applyWorldTorque(Vector3dc t);
    public default IRigidbodyDataWriter applyWorldImpulse(Vector3dc f) { return applyWorldForce(f.mul(60, new Vector3d())); }

    public IRigidbodyDataWriter applyWork(double work);


    public default void setNoGravity() { setGravity(new Vector3d()); }
    public default void setEarthGravity() { setGravity(new Vector3d(0, -9.8, 0)); }


    public IRigidbodyDataWriter moveLocalPosToWorld(Vector3dc localPos, Vector3dc toWorld);
}
