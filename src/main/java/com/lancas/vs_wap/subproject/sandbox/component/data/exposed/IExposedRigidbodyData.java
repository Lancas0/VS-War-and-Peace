package com.lancas.vs_wap.subproject.sandbox.component.data.exposed;

import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxRigidbodyData;
import org.joml.Matrix3dc;
import org.joml.Vector3dc;

public interface IExposedRigidbodyData extends IExposedComponentData<SandBoxRigidbodyData> {
    public double getMass();
    public Matrix3dc getInertia();
    //public Vector3d getLocalMassCenter(Vector3d dest);
    public Vector3dc getVelocity();
    public Vector3dc getOmega();
}
