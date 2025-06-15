package com.lancas.vswap.subproject.sandbox.api.data;

import org.joml.*;

public interface ITransformPrimitive {
    public TransformPrimitive copy();

    public Vector3dc getPosition();
    public Quaterniondc getRotation();
    public Vector3dc getScale();

    public default Vector3f getPosition(Vector3f dest) { return dest.set(getPosition()); }
    public default Quaternionf getRotation(Quaternionf dest) { return dest.set(getRotation()); }
    public default Vector3f getScale(Vector3f dest) { return dest.set(getScale()); }

    public default Matrix4d makeLocalToWorld(Matrix4d dest) {
        return dest.translationRotateScale(getPosition(), getRotation(), getScale());
    }
    public default Matrix4f makeLocalToWorld(Matrix4f dest) {
        return dest.translationRotateScale(
            getPosition(new Vector3f()),
            getRotation(new Quaternionf()),
            getScale(new Vector3f())
        );
    }

    public default Matrix4d makeWorldToLocal(Matrix4d dest) {
        return dest.translationRotateScaleInvert(getPosition(), getRotation(), getScale());
    }

    public ITransformPrimitive lerp(ITransformPrimitive target, double t, TransformPrimitive dest);
}
