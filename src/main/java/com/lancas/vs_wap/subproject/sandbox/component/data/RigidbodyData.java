package com.lancas.vs_wap.subproject.sandbox.component.data;

import com.lancas.vs_wap.content.info.block.WapBlockInfos;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vs_wap.subproject.sandbox.api.data.ITransformPrimitive;
import com.lancas.vs_wap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vs_wap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.NbtBuilder;
import com.lancas.vs_wap.util.SerializeUtil;
import net.minecraft.nbt.CompoundTag;
import org.joml.*;

import java.io.Serializable;
import java.lang.Math;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class RigidbodyData implements IComponentData<RigidbodyData>, IRigidbodyData {
    @FunctionalInterface
    public interface RigidbodyUpdate extends Serializable {
        void update(RigidbodyData data);
    }

    public final TransformPrimitive transform = new TransformPrimitive();
    public volatile Matrix4d localToWorldSnapshot = new Matrix4d();
    public volatile Matrix4d worldToLocalSnapshot = new Matrix4d();

    public double mass = 0;
    public final Vector3d localPosMassMul = new Vector3d();  //todo may exceed limit, sync
    public final Matrix3d localInertiaTensor = new Matrix3d();

    public final Vector3d velocity = new Vector3d();
    public final Vector3d omega = new Vector3d();
    public final Vector3d gravity = new Vector3d();

    public final AtomicBoolean isStatic = new AtomicBoolean(false);

    public final Queue<Vector3d> applyingForces = new ConcurrentLinkedQueue<>();
    public final Queue<Vector3d> applyingTorques = new ConcurrentLinkedQueue<>();
    public final Queue<RigidbodyUpdate> updates = new ConcurrentLinkedQueue<>();


    public RigidbodyData() {}
    public RigidbodyData(ITransformPrimitive initialTransform) {
        transform.set(initialTransform);
    }
    public RigidbodyData(ITransformPrimitive initialTransform, Vector3dc initialGravity) {
        transform.set(initialTransform);
        gravity.set(initialGravity);
    }
    //public RigidbodyData(CompoundTag tag) { load(tag); }
    public static RigidbodyData createEarthGravity(ITransformPrimitive initialTransform) {
        return new RigidbodyData(initialTransform, new Vector3d(0, -9.8, 0));
    }
    /*public static RigidbodyData createNoGravity() {
        return new RigidbodyData();  //actually default is no gravity
    }
    public static RigidbodyData createDefault() { return new RigidbodyData(); }
    public static RigidbodyData createEarthGravity() {
        RigidbodyData data = new RigidbodyData();
        initialGravity
        return data;
    }*/


    @Override
    public RigidbodyData copyData(RigidbodyData src) {
        mass = src.mass;
        localPosMassMul.set(src.localPosMassMul);
        localInertiaTensor.set(src.localInertiaTensor);

        transform.set(src.transform);
        //localToWorldSnapshot.set(src.localToWorldSnapshot);
        localToWorldSnapshot = src.transform.makeLocalToWorld(new Matrix4d());
        worldToLocalSnapshot = src.transform.makeWorldToLocal(new Matrix4d());

        velocity.set(src.velocity);
        omega.set(src.omega);
        gravity.set(src.gravity);

        isStatic.set(src.isStatic.get());

        applyingForces.clear();  applyingForces.addAll(src.applyingForces);
        EzDebug.log("applying force is cleared");
        applyingTorques.clear(); applyingTorques.addAll(src.applyingTorques);
        updates.clear();         updates.addAll(src.updates);

        return this;
    }
    @Override
    public RigidbodyData overwriteDataByShip(ISandBoxShip ship) {
        //I suppose it's no need to sync when init
        mass = 0;
        localPosMassMul.zero();
        localInertiaTensor.zero();
        ship.getBlockCluster().getDataReader().seekAllBlocks((localPos, state) -> {
            double curMass = WapBlockInfos.mass.valueOrDefaultOf(state);

            mass += curMass;
            localPosMassMul.add(JomlUtil.d(localPos).mul(curMass));

            //EzDebug.log("shipType:" + ship.getClass().getSimpleName() + ", state:" + StrUtil.getBlockName(state) + "mass add " + curMass);
        });
        //EzDebug.log("shipType:" + ship.getClass().getSimpleName() + ", mass by ship is :" + mass + ", cnt:" + ship.getBlockCluster().getDataReader().getBlockCnt());

        updateInertia(ship);  //todo dirty inertia
        return this;
    }
    //如果同时有多个更新，还是不安全
    //todo sync
    public void updateInertia(ISandBoxShip ship) {
        localInertiaTensor.zero();
        Vector3d massCenter = localPosMassMul.div(mass, new Vector3d());//ship.getRigidbody().calLocalMassCenter();

        ship.getBlockCluster().getDataReader().seekAllBlocks((localPos, state) -> {
            Vector3d delta = JomlUtil.d(localPos).sub(massCenter);
            double sqDx = delta.x * delta.x, sqDy = delta.y * delta.y, sqDz = delta.z * delta.z;
            double m = WapBlockInfos.mass.valueOrDefaultOf(state);

            //沟槽的平行轴定理还在追我
            double self_xxyyzz = 0.01667 * m;//(1.0f/12.0f) * m * 2;// * (Ly*Ly + Lz*Lz);
            //double self_yy = 0.01667 * m;//(1.0f/12.0f) * m * 2;// * (Lx*Lx + Lz*Lz);
            //double self_zz = 0.01667 * m;//(1.0f/12.0f) * m * 2;// * (Lx*Lx + Ly*Ly);

            localInertiaTensor.m00 += self_xxyyzz + m * (sqDy + sqDz);
            localInertiaTensor.m11 += self_xxyyzz + m * (sqDx + sqDz);
            localInertiaTensor.m22 += self_xxyyzz + m * (sqDx + sqDy);

            // 不考虑非对角项
            /*I.xy -= m * delta.x * delta.y;
            I.xz -= m * delta.x * delta.z;
            I.yz -= m * delta.y * delta.z;*/
        });
    }
    /*public void updateInertia(SandBoxServerShip ship) {
        inertiaTensor.zero();
        if (mass < 1E-10 || ship.getCluster().blockCount() == 0) return;

        //只有一个方块时特殊计算:拆成八个角算
        if (ship.getCluster().blockCount() == 1) {
            updateInertiaOnlyOneBlock(ship);
            return;
        }

        Vector3d massCenter = ship.getRigidbody().calLocalMassCenter();
        ship.getCluster().foreach((localPos, state) -> {
            Vector3d delta = JomlUtil.d(localPos).sub(massCenter);
            double dx = delta.x, dy = delta.y, dz = delta.z;
            double m = WapBlockInfos.mass.valueOrDefaultOf(state);

            double xx = m * (dy*dy + dz*dz);
            double yy = m * (dx*dx + dz*dz);
            double zz = m * (dx*dx + dy*dy);
            double xy = m * dx * dy;
            double xz = m * dx * dz;
            double yz = m * dy * dz;

            inertiaTensor.m00 += xx;
            inertiaTensor.m11 += yy;
            inertiaTensor.m22 += zz;
        });
        /.*for (var blockEntry : ship.getCluster().allBlocks()) {

            //忽略对角项
            //data.inertiaTensor.m10 -= xy;
            //data.inertiaTensor.m20 -= xz;

            data.inertiaTensor.m01 -= xy;

            data.inertiaTensor.m21 -= yz;

            data.inertiaTensor.m02 -= xz;
            data.inertiaTensor.m12 -= yz;


            //data.inertiaTensor.
        }*./
    }*/
    /*private void updateInertiaOnlyOneBlock(SandBoxServerShip ship) {
        Vector3d massCenter = ship.getRigidbody().calLocalMassCenter();
        BiConsumer<Vector3ic, BlockState> calOneCorner = (localPos, state) -> {
            double cm = WapBlockInfos.mass.valueOrDefaultOf(state) / 8.0;
            for (double cx : new double[]{ -0.25, 0.25 }) {
                for (double cy : new double[]{ -0.25, 0.25 }) {
                    for (double cz : new double[]{ -0.25, 0.25 }) {
                        double
                            dx = cx - massCenter.x,
                            dy = cy - massCenter.y,
                            dz = cz - massCenter.z;

                        double xx = cm * (dy*dy + dz*dz);
                        double yy = cm * (dx*dx + dz*dz);
                        double zz = cm * (dx*dx + dy*dy);
                        double xy = cm * dx * dy;
                        double xz = cm * dx * dz;
                        double yz = cm * dy * dz;

                        //忽略对角项
                        inertiaTensor.m00 += xx;
                        inertiaTensor.m11 += yy;
                        inertiaTensor.m22 += zz;
                        /.*
                        data.inertiaTensor.m10 -= xy;
                        data.inertiaTensor.m20 -= xz;

                        data.inertiaTensor.m01 -= xy;

                        data.inertiaTensor.m21 -= yz;

                        data.inertiaTensor.m02 -= xz;
                        data.inertiaTensor.m12 -= yz;*./

                    }
                }
            }
        };

        ship.getCluster().foreach(calOneCorner);
    }*/
    @Override
    public CompoundTag saved() {
        return new NbtBuilder()
            .putCompound("transform", transform.saved())
            .putMatrix4d("local_to_world_snapshot", localToWorldSnapshot)
            .putMatrix4d("world_to_local_snapshot", worldToLocalSnapshot)
            //.putNumber("mass", mass)
            //.putVector3d("local_pos_mass_mul", localPosMassMul)  //note localPosMassMul and inertiaTensor are setted by ship
            //.putMatrix3d()
            .putVector3d("velocity", velocity)
            .putVector3d("omega", omega)
            .putVector3d("gravity", gravity)
            .putBoolean("static", isStatic.get())

            .putEach("applying_forces", applyingForces, NbtBuilder::tagOfVector3d)
            .putEach("applying_torque", applyingTorques, NbtBuilder::tagOfVector3d)
            .putEach("updates", updates, update -> {
                CompoundTag savedUpdate = new CompoundTag();
                byte[] bytes = SerializeUtil.safeSerialize(update);
                if (bytes == null || bytes.length == 0) {
                    EzDebug.warn("fail to save a unapplied update");
                    return savedUpdate;
                }
                savedUpdate.putByteArray("bytes", bytes);
                return savedUpdate;
            })

            .get();
    }
    @Override
    public RigidbodyData load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readCompoundDo("transform", transform::load)
            .readMatrix4d("local_to_world_snapshot", localToWorldSnapshot)
            .putMatrix4d("world_to_local_snapshot", worldToLocalSnapshot)
            //.readDoubleDo("mass", v -> mass = v)
            //.readVector3d("local_pos_mass_mul", localPosMassMul);
            .readVector3d("velocity", velocity)
            .readVector3d("omega", omega)
            .readVector3d("gravity", gravity)
            .readBooleanDo("static", isStatic::set)

            .readEachCompoundOverwrite("applying_forces", NbtBuilder::vector3dOf, applyingForces)
            .readEachCompoundOverwrite("applying_torque", NbtBuilder::vector3dOf, applyingTorques)
            .readEachCompound("updates", t -> {
                if (!t.contains("bytes")) return null;
                return SerializeUtil.safeDeserialize(t.getByteArray("bytes"));
            }, updates);


        updates.removeIf(Objects::isNull);
        //EzDebug.log("load omega: " + omega);

        return this;
    }


    @Override
    public double getMass() { return mass; }
    @Override  //todo cached ot something
    public Vector3d getLocalMassCenter(Vector3d dest) { return localPosMassMul.div(mass, dest); }
    @Override
    public Vector3d getWorldMassCenter(Vector3d dest) { return localToWorldPos(getLocalMassCenter(dest)); }
    @Override
    public Matrix3dc getLocalInertia() { return localInertiaTensor; }


    @Override
    public Vector3dc getPosition() { return transform.getPosition(); }
    @Override
    public Quaterniondc getRotation() { return transform.getRotation(); }
    @Override
    public Vector3dc getScale() { return transform.getScale(); }


    @Override
    public ITransformPrimitive getTransform() { return transform; }

    @Override
    public Matrix4dc getLocalToWorld() { return localToWorldSnapshot; }
    @Override
    public Matrix4dc getWorldToLocal() { return worldToLocalSnapshot; }


    @Override
    public Vector3dc getVelocity() { return velocity; }

    @Override
    public Vector3dc getOmega() { return omega; }
    @Override
    public Vector3dc getGravity() { return gravity; }

    @Override
    public Stream<Vector3dc> allForces() { return applyingForces.stream().map(f -> f); }
    @Override
    public Stream<Vector3dc> allTorques() { return applyingTorques.stream().map(t -> t); }

    @Override
    public boolean isStatic() { return isStatic.get(); }


    @Override
    public IRigidbodyDataWriter setVelocity(Vector3dc v) {
        Vector3d newVelImmutable = new Vector3d(v);
        updates.add(d -> d.velocity.set(newVelImmutable));

        return this;
    }
    @Override
    public IRigidbodyDataWriter setOmega(Vector3dc newOmega) {
        Vector3dc newOmegaImmutable = new Vector3d(newOmega);
        updates.add(d -> d.omega.set(newOmegaImmutable));
        return this;
    }
    @Override
    public IRigidbodyDataWriter setGravity(Vector3dc newGravity) {
        Vector3dc newGravityImmutable = new Vector3d(newGravity);
        updates.add(d -> d.gravity.set(newGravityImmutable));
        return this;
    }

    @Override
    public IRigidbodyDataWriter setStatic(boolean newVal) { isStatic.set(newVal); return this; }

    @Override
    public IRigidbodyDataWriter setPosition(Vector3dc p) {
        Vector3dc newPosImmutable = new Vector3d(p);
        updates.add(d -> d.transform.position.set(newPosImmutable));
        return this;
    }
    @Override
    public IRigidbodyDataWriter setRotation(Quaterniondc r) {
        Quaterniondc newRotImmutable = new Quaterniond(r);
        updates.add(d -> d.transform.rotation.set(newRotImmutable));
        return this;
    }
    @Override
    public IRigidbodyDataWriter setScale(Vector3dc s) {
        Vector3dc newScaleImmutable = new Vector3d(s);
        updates.add(d -> d.transform.scale.set(s));
        return this;
    }
    @Override
    public IRigidbodyDataWriter setTransform(ITransformPrimitive newTransform) {
        TransformPrimitive transformImmutable = new TransformPrimitive(newTransform);
        updates.add(d -> d.transform.set(transformImmutable));
        return this;
    }

    @Override
    public IRigidbodyDataWriter applyWorldForce(Vector3dc f) {
        applyingForces.add(new Vector3d(f));
        //EzDebug.log("applying forces:" + applyingForces.size());
        return this;
    }
    @Override
    public IRigidbodyDataWriter applyWorldTorque(Vector3dc t) { applyingTorques.add(new Vector3d(t)); return this; }

    @Override
    public IRigidbodyDataWriter applyWork(double work) {
        updates.add(d -> {
            double postVelSqLen = 2 * work / d.mass + d.velocity.lengthSquared();
            double postVelLen = Math.sqrt(Math.abs(postVelSqLen)) * Math.signum(postVelSqLen);
            Vector3d postVel = d.velocity.normalize(postVelLen, new Vector3d());
            if (!postVel.isFinite()) {
                EzDebug.warn("the velocity is invalid after apply work " + work + "J.");
                return;
            }
            d.velocity.set(postVel);
        });
        return this;
    }


    @Override
    public IRigidbodyDataWriter moveLocalPosToWorld(Vector3dc localPos, Vector3dc toWorld) {
        Vector3d localPosImmutable = new Vector3d(localPos);
        Vector3d toWorldImmutable = new Vector3d(toWorld);
        updates.add(d -> {
            Vector3d transformedPos = d.localToWorldPos(localPosImmutable, new Vector3d());
            Vector3d movement = toWorldImmutable.sub(transformedPos, new Vector3d());
            //the update pos is done sequently, don't worry the concurrent
            d.transform.addPosition(movement);
        });
        return this;
    }

    /*@Override
    public RigidbodyData setVelocity(Vector3dc newVel) { velocity.set(newVel); return this; }  //todo sync
    @Override
    public RigidbodyData setOmega(Vector3dc newOmega) { omega.set(newOmega); return this; }  //todo sync

    @Override
    public Vector3dc getGravity() { return gravity; }
    @Override
    public void setGravity(Vector3dc newGravity) { gravity.set(newGravity); }  //todo sync*/
}
