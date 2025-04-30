package com.lancas.vs_wap.subproject.sandbox.obsolete;

/*
import com.lancas.vs_wap.event.api.ILazyEventParam;
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vs_wap.subproject.sandbox.component.data.reader.IExposedTransformData;
import com.lancas.vs_wap.subproject.sandbox.util.SerializeUtil;
import com.lancas.vs_wap.util.StrUtil;
import net.minecraft.nbt.CompoundTag;
import org.joml.*;

public class TransformData implements ILazyEventParam<TransformData>, IComponentData<TransformData>, IExposedTransformData {
    @Override
    public TransformData copyData(TransformData src) { return this.set(src); }

    public final Vector3d position = new Vector3d();
    public final Quaterniond rotation = new Quaterniond();
    public final Vector3d scale = new Vector3d(1, 1, 1);
    public TransformData() { }
    public TransformData(Vector3dc inPos, Quaterniondc inRot, Vector3dc inScale) {
        position.set(inPos); rotation.set(inRot); scale.set(inScale);
    }
    public static TransformData copy(IExposedTransformData src) {
        return new TransformData(src.getPosition(), src.getRotation(), src.getScale());
    }

    public TransformData setPos(Vector3dc inPos) { position.set(inPos); return this; }
    public TransformData setRotation(Quaterniondc inRot) { rotation.set(inRot); return this; }
    public TransformData setScale(Vector3dc inScale) { scale.set(inScale); return this; }
    public TransformData setScaleXYZ(double xyz) { scale.set(xyz, xyz, xyz); return this; }
    public TransformData set(Vector3dc inPos, Quaterniondc inRot, Vector3dc inScale) {
        position.set(inPos); rotation.set(inRot); scale.set(inScale);
        return this;
    }
    public TransformData set(IExposedTransformData other) {
        position.set(other.getPosition()); rotation.set(other.getRotation()); scale.set(other.getScale());
        return this;
    }

    @Override
    public Vector3dc getPosition() { return position; }
    @Override
    public Quaterniondc getRotation() { return rotation; }
    @Override
    public Vector3dc    getScale() { return scale; }

    public TransformData lerp(TransformData other, double t, TransformData dest) {
        Vector3d lerpPos = new Vector3d();
        Quaterniond lerpRot = new Quaterniond();
        Vector3d lerpScale = new Vector3d();

        position.lerp(other.position, t, lerpPos);
        rotation.slerp(other.rotation, t, lerpRot);
        scale.lerp(other.scale, t, lerpScale);

        return dest.set(lerpPos, lerpRot, lerpScale);
    }

    //public DataAccessor getAccessor() { return new DataAccessor(this); }

    @Override
    public TransformData getImmutable() {
        return new TransformData(position, rotation, scale);
    }

    @Override
    public CompoundTag saved() { return SerializeUtil.saveTransformLike(position, rotation, scale); }
    @Override
    public TransformData load(CompoundTag tag) {
        SerializeUtil.loadTransformLike(tag, this::set);
        return this;
    }
    /.*
    public static SandBoxTransformData fromNbt(CompoundTag nbt) {
        SandBoxTransformData data = new SandBoxTransformData();
        data.load(nbt);
        return data;
    }*./
    public Matrix4f makeLocalToWorld(Matrix4f dest) {
        return dest.translationRotateScale(
            (float)position.x, (float)position.y, (float)position.z,
            (float)rotation.x, (float)rotation.y, (float)rotation.z, (float)rotation.w,
            (float)scale.x, (float)scale.y, (float)scale.z
        );
    }
    public Matrix4d makeLocalToWorld(Matrix4d dest) {
        return dest.translationRotateScale(
            position.x, position.y, position.z,
            rotation.x, rotation.y, rotation.z, rotation.w,
            scale.x, scale.y, scale.z
        );
    }

    @Override
    public String toString() {
        return "SandBoxTransformData{" +
            "position=" + StrUtil.F2(position) +
            ", rotation=" + StrUtil.F2(rotation) +
            ", scale=" + StrUtil.F2(scale) +
            '}';
    }
    public String toPosString() {
        return "SandBoxTransformData position = " + StrUtil.F2(position);
    }
}
*/