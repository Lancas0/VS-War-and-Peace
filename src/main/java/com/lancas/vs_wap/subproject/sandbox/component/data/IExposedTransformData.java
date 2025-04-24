package com.lancas.vs_wap.subproject.sandbox.component.data;

import org.joml.Quaterniondc;
import org.joml.Vector3dc;

public interface IExposedTransformData extends IExposedComponentData<SandBoxTransformData> {
    public Vector3dc getPosition();
    public Quaterniondc getRotation();
    public Vector3dc getScale();
}