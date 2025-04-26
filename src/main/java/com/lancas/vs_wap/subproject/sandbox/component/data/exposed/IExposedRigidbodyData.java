package com.lancas.vs_wap.subproject.sandbox.component.data.exposed;

import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxRigidbodyData;
import org.joml.Matrix3dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public interface IExposedRigidbodyData extends IExposedComponentData<SandBoxRigidbodyData> {
    public double getMass();
    public Matrix3dc getInertia();

    //public Vector3d getLocalMassCenter(Vector3d dest);
    public Vector3dc getVelocity();
    public Vector3dc getOmega();

    public void setVelocity(Vector3dc newVel);
    public void setOmega(Vector3dc newOmega);

    public Vector3dc getGravity();
    public void setGravity(Vector3dc newGravity);
    public default void setNoGravity() { setGravity(new Vector3d()); }
    public default void setEarthGravity() { setGravity(new Vector3d(0, -9.8, 0)); }
}
