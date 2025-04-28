package com.lancas.vs_wap.subproject.sandbox.component.data.exposed;

import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxTransformData;
import org.joml.Quaterniondc;
import org.joml.Vector3dc;
import org.joml.primitives.AABBdc;
import org.joml.primitives.AABBic;

public interface IExposedTransformData extends IExposedComponentData<SandBoxTransformData> {
    public Vector3dc getPosition();
    public Quaterniondc getRotation();
    public Vector3dc getScale();
}