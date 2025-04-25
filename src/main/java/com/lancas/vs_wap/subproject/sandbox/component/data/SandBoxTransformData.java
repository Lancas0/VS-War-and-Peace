package com.lancas.vs_wap.subproject.sandbox.component.data;

import com.lancas.vs_wap.event.api.ILazyEventParam;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedTransformData;
import com.lancas.vs_wap.subproject.sandbox.util.SerializeUtil;
import com.lancas.vs_wap.util.StrUtil;
import net.minecraft.nbt.CompoundTag;
import org.joml.*;

public class SandBoxTransformData implements ILazyEventParam<SandBoxTransformData>, IComponentData<SandBoxTransformData>, IExposedTransformData {
    @Override
    public SandBoxTransformData copyData(SandBoxTransformData src) { return this.set(src); }


    public final Vector3d position = new Vector3d();
    public final Quaterniond rotation = new Quaterniond();
    public final Vector3d scale = new Vector3d(1, 1, 1);
    public SandBoxTransformData() { }
    public SandBoxTransformData(Vector3dc inPos, Quaterniondc inRot, Vector3dc inScale) {
        position.set(inPos); rotation.set(inRot); scale.set(inScale);
    }
    public static SandBoxTransformData copy(IExposedTransformData src) {
        return new SandBoxTransformData(src.getPosition(), src.getRotation(), src.getScale());
    }

    public SandBoxTransformData setPos(Vector3dc inPos) { position.set(inPos); return this; }
    public SandBoxTransformData setRotation(Quaterniondc inRot) { rotation.set(inRot); return this; }
    public SandBoxTransformData setScale(Vector3dc inScale) { scale.set(inScale); return this; }
    public SandBoxTransformData set(Vector3dc inPos, Quaterniondc inRot, Vector3dc inScale) {
        position.set(inPos); rotation.set(inRot); scale.set(inScale);
        return this;
    }
    public SandBoxTransformData set(IExposedTransformData other) {
        position.set(other.getPosition()); rotation.set(other.getRotation()); scale.set(other.getScale());
        return this;
    }

    @Override
    public Vector3dc getPosition() { return position; }
    @Override
    public Quaterniondc getRotation() { return rotation; }
    @Override
    public Vector3dc    getScale() { return scale; }

    public SandBoxTransformData lerp(SandBoxTransformData other, double t, SandBoxTransformData dest) {
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
    public SandBoxTransformData getImmutable() {
        return new SandBoxTransformData(position, rotation, scale);
    }

    @Override
    public CompoundTag saved() {
        return SerializeUtil.saveTransformLike(position, rotation, scale);
    }
    @Override
    public SandBoxTransformData load(CompoundTag tag) {
        SerializeUtil.loadTransformLike(tag, this::set);
        return this;
    }
    /*
    public static SandBoxTransformData fromNbt(CompoundTag nbt) {
        SandBoxTransformData data = new SandBoxTransformData();
        data.load(nbt);
        return data;
    }*/
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
