package com.lancas.vs_wap.subproject.sandbox.component.behviour;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.api.UUIDParamWrapper;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedTransformData;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxTransformData;
import com.lancas.vs_wap.subproject.sandbox.event.SandBoxEventMgr;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import org.joml.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class SandBoxTransform extends AbstractComponentBehaviour<SandBoxTransformData> /*implements IComponentBehaviour<SandBoxTransformData>*/ {
    //private final Vector3d position = new Vector3d(0, 0, 0);
    //private final Quaterniond rotation = new Quaterniond().identity();
    //private final Vector3d scale = new Vector3d(1, 1, 1);
    //private final SandBoxTransformData data;
    //private SandBoxTransformData.DataAccessor accessor;

    private final Matrix4d cachedLocalToWorld = new Matrix4d().identity();
    private final Matrix4d cachedWorldToLocal = new Matrix4d().identity();
    private final AtomicBoolean isMatrixDirty = new AtomicBoolean(true);

    //public SandBoxTransform() { data = new SandBoxTransformData(); }

    @Override
    protected SandBoxTransformData makeData() { return new SandBoxTransformData(); }
    @Override
    public IExposedTransformData getExposedData() { return data; }


    public Matrix4dc getWorldToLocal() {
        if (isMatrixDirty.get()) {
            updateMatrix();
            isMatrixDirty.set(false);
        }
        return cachedWorldToLocal;
    }
    public Matrix4dc getLocalToWorld() {
        if (isMatrixDirty.get()) {
            updateMatrix();
            isMatrixDirty.set(false);
        }
        return cachedLocalToWorld;
    }
    public Vector3d localToWorldPos(Vector3ic localPos, Vector3d dest) { return getLocalToWorld().transformPosition(dest.set(localPos)); }
    public Vector3d localToWorldPos(Vector3dc localPos, Vector3d dest) { return getLocalToWorld().transformPosition(localPos, dest); }
    public Vector3d worldToLocalPos(Vector3dc worldPos, Vector3d dest) { return getWorldToLocal().transformPosition(worldPos, dest); }
    public Vector3d localToWorldPos(Vector3d localPos) { return getLocalToWorld().transformPosition(localPos); }
    public Vector3d worldToLocalPos(Vector3d worldPos) { return getWorldToLocal().transformPosition(worldPos); }
    //public Vector3d localToWorldPos(BlockPos localPos, Vector3d dest) { return getLocalToWorld().transformPosition(JomlUtil.dLowerCorner(localPos), dest); }

    public Vector3d localToWorldNoScaleDir(Vector3dc dir, Vector3d dest) { return getRotation().transform(dir, dest); }
    public Vector3d localToWorldNoScaleDir(Vector3d dir) { return getRotation().transform(dir); }
    public Vector3d localToWorldNoScaleDir(Vector3ic dir, Vector3d dest) { return getRotation().transform(new Vector3d(dir), dest); }

    private void updateMatrix() {
        //need for translation: related to (0, 0, 0)
        /*cachedLocalToWorld.translationRotateScale(
            data.getPosition().x(), data.getPosition().y(), data.getPosition().z(),
            data.getRotation().x(), data.getRotation().y(), data.getRotation().z(), data.getRotation().w(),
            data.getScale().x(), data.getScale().y(), data.getScale().z()
        );*/
        synchronized (data) {
            data.makeLocalToWorld(cachedLocalToWorld);  //需要读取data所以加锁
            cachedLocalToWorld.invert(cachedWorldToLocal);  //这个也sync因为不希望cachedLocalToWorld在转换是改变导致cacheLocalWorld和cachedWorldToLocal不一致(for safe)
        }
    }


    public Vector3dc getPosition() {
        synchronized (data) {
            return data.position;
        }
    }
    public Quaterniondc getRotation() {
        synchronized (data) {
            return data.rotation;
        }
    }
    public Vector3dc getScale() {
        synchronized (data) {
            return data.scale;
        }
    }

    public SandBoxTransform setPosition(Vector3dc newPos) {
        //data update must be atomic
        synchronized (data) {
            data.position.set(newPos);
            SandBoxEventMgr.onServerShipTransformDirty.schedule(ship.getUuid(), UUIDParamWrapper.of(ship.getUuid()), data);
        }

        isMatrixDirty.set(true);
        return this;
    }
    public SandBoxTransform setRotation(Quaterniondc newRot) {  //todo check rot normilized?
        synchronized (data) {
            data.rotation.set(newRot);
            SandBoxEventMgr.onServerShipTransformDirty.schedule(ship.getUuid(), UUIDParamWrapper.of(ship.getUuid()), data);
        }

        isMatrixDirty.set(true);
        return this;
    }
    public SandBoxTransform setScale(Vector3dc newScale) {
        if (newScale.x() < 0 || newScale.y() < 0 || newScale.z() < 0) {
            EzDebug.warn("can't set scale with a negative number!");
            return this;
        }

        synchronized (data) {
            data.scale.set(newScale);
            SandBoxEventMgr.onServerShipTransformDirty.schedule(ship.getUuid(), UUIDParamWrapper.of(ship.getUuid()), data);
        }

        isMatrixDirty.set(true);
        return this;
    }
    public SandBoxTransform set(Vector3dc newPos, Quaterniondc newRot, Vector3dc newScale) {
        synchronized (data) {
            data.set(newPos, newRot, newScale);
            SandBoxEventMgr.onServerShipTransformDirty.schedule(ship.getUuid(), UUIDParamWrapper.of(ship.getUuid()), data);
        }

        isMatrixDirty.set(true);
        return this;
    }
    public SandBoxTransform set(SandBoxTransform other) {
        synchronized (data) {
            data.set(other.getPosition(), other.getRotation(), other.getScale());
            SandBoxEventMgr.onServerShipTransformDirty.schedule(ship.getUuid(), UUIDParamWrapper.of(ship.getUuid()), data);
        }

        isMatrixDirty.set(true);
        return this;
    }
    public SandBoxTransform set(SandBoxTransformData inData) {
        synchronized (data) {
            data.set(inData.getPosition(), inData.getRotation(), inData.getScale());
            SandBoxEventMgr.onServerShipTransformDirty.schedule(ship.getUuid(), UUIDParamWrapper.of(ship.getUuid()), data);
        }

        isMatrixDirty.set(true);
        return this;
    }
    public SandBoxTransform move(Vector3dc movement) {
        synchronized (data) {
            data.position.add(movement);
            SandBoxEventMgr.onServerShipTransformDirty.schedule(ship.getUuid(), UUIDParamWrapper.of(ship.getUuid()), data);
        }

        isMatrixDirty.set(true);
        return this;
    }
    public SandBoxTransform rotateWorld(Quaterniondc rotation) {  //todo check rot normilized?
        synchronized (data) {
            //data.rotation.mul(rotation).normalize();  //should normalize?
            rotation.mul(data.rotation, data.rotation);
            SandBoxEventMgr.onServerShipTransformDirty.schedule(ship.getUuid(), UUIDParamWrapper.of(ship.getUuid()), data);
        }

        isMatrixDirty.set(true);
        return this;
    }
    public SandBoxTransform rotateLocal(Quaterniondc rotation) {  //todo check rot normilized?
        synchronized (data) {
            data.rotation.mul(rotation).normalize();  //should normalize?
            SandBoxEventMgr.onServerShipTransformDirty.schedule(ship.getUuid(), UUIDParamWrapper.of(ship.getUuid()), data);
        }

        isMatrixDirty.set(true);
        return this;
    }
    public SandBoxTransform scale(Vector3dc scale) {
        if (scale.x() < 0 || scale.y() < 0 || scale.z() < 0) {
            EzDebug.warn("can't scale with a negative number!");
            return this;
        }

        synchronized (data) {
            data.scale.mul(scale);
            SandBoxEventMgr.onServerShipTransformDirty.schedule(ship.getUuid(), UUIDParamWrapper.of(ship.getUuid()), data);
        }

        isMatrixDirty.set(true);
        return this;
    }

    /*public SandBoxTransform lerp(SandBoxTransform other, double t, SandBoxTransform dest) {
        data.lerp(other.data, t, dest.data);
        dest.isMatrixDirty = true;

        return dest;
    }*/


    @Override
    public void loadData(SandBoxServerShip ship, SandBoxTransformData inData) {
        super.loadData(ship, inData);
        isMatrixDirty.set(true);
    }

    @Override
    public String toString() {
        return "SandBoxTransform{" +
            "data=" + data +
            '}';
    }

    /*public void setMatrixDirty() {
        isMatrixDirty = true;
    }*/

    /*
    @Override
    public CompoundTag save() { return SerializeUtil.saveTransformLike(getPosition(), getRotation(), getScale()); }
    @Override
    public void load(CompoundTag tag) { SerializeUtil.loadTransformLike(tag, this::set); }
     */
}
