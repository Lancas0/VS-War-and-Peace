package com.lancas.vs_wap.subproject.sandbox.component.data;

import org.joml.Vector3d;
import org.joml.Vector3dc;

public interface IExposedRigidbodyData extends IExposedComponentData<SandBoxRigidbodyData> {
    public double getMass();
    //public Vector3d getLocalMassCenter(Vector3d dest);
}
