package com.lancas.vs_wap.subproject.sandbox.component.data.writer;

import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentDataWriter;
import com.lancas.vs_wap.subproject.sandbox.api.data.ITransformPrimitive;
import com.lancas.vs_wap.subproject.sandbox.component.data.RigidbodyData;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public interface IRigidbodyDataWriter extends IComponentDataWriter<RigidbodyData> {
    public IRigidbodyDataWriter setPosition(Vector3dc v);
    public IRigidbodyDataWriter setRotation(Quaterniondc r);
    public IRigidbodyDataWriter setScale(Vector3dc s);
    public IRigidbodyDataWriter setTransform(ITransformPrimitive newTransform);

    public IRigidbodyDataWriter setVelocity(Vector3dc v);
    public IRigidbodyDataWriter setOmega(Vector3dc v);
    public IRigidbodyDataWriter setGravity(Vector3dc newGravity);

    public void setStatic(boolean newVal);

    public IRigidbodyDataWriter applyWorldForce(Vector3dc v);
    public IRigidbodyDataWriter applyWorldTorque(Vector3dc v);


    public default void setNoGravity() { setGravity(new Vector3d()); }
    public default void setEarthGravity() { setGravity(new Vector3d(0, -9.8, 0)); }
}
