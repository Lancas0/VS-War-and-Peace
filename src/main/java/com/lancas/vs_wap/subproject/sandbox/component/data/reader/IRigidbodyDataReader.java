package com.lancas.vs_wap.subproject.sandbox.component.data.reader;

import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentDataReader;
import com.lancas.vs_wap.subproject.sandbox.api.data.ITransformPrimitive;
import com.lancas.vs_wap.subproject.sandbox.component.data.RigidbodyData;
import org.joml.*;

import java.util.stream.Stream;

public interface IRigidbodyDataReader extends IComponentDataReader<RigidbodyData> {
    public double getMass();
    public Vector3d getLocalMassCenter(Vector3d dest);  //todo cached or something
    public Vector3d getWorldMassCenter(Vector3d dest);
    public Matrix3dc getLocalInertia();

    public Vector3dc getPosition();
    public Quaterniondc getRotation();
    public Vector3dc getScale();

    public ITransformPrimitive getTransform();
    public Matrix4dc getLocalToWorld();

    //public Vector3d getLocalMassCenter(Vector3d dest);
    public Vector3dc getVelocity();
    public Vector3dc getOmega();
    public Vector3dc getGravity();

    public Stream<Vector3dc> allForces();
    public Stream<Vector3dc> allTorques();

    public boolean isStatic();


    public default Vector3d localToWorldPos(Vector3dc localPos, Vector3d dest) { return getLocalToWorld().transformPosition(localPos, dest); }
    public default Vector3d localToWorldPos(Vector3d localPos) { return getLocalToWorld().transformPosition(localPos); }
    public default Vector3d localToWorldPos(Vector3ic localPos) { return getLocalToWorld().transformPosition(new Vector3d(localPos)); }
    public default Vector3d localToWorldNoScaleDir(Vector3dc localDir, Vector3d dest) { return getRotation().transform(localDir, dest); }
    public default Vector3d localToWorldNoScaleDir(Vector3d localDir) { return getRotation().transform(localDir); }
    public default Vector3d localToWorldNoScaleDir(Vector3ic localDir) { return getRotation().transform(new Vector3d(localDir)); }
}
