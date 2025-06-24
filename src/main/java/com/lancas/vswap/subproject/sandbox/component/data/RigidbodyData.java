package com.lancas.vswap.subproject.sandbox.component.data;

import com.lancas.vswap.content.info.block.WapBlockInfos;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vswap.subproject.sandbox.api.data.ITransformPrimitive;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.NbtBuilder;
import com.lancas.vswap.util.StrUtil;
import net.minecraft.nbt.CompoundTag;
import org.joml.*;

import java.io.Serializable;
import java.lang.Math;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
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
    public static RigidbodyData createEarthGravity(ITransformPrimitive initialTransform) {
        return new RigidbodyData(initialTransform, new Vector3d(0, -9.8, 0));
    }

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
        //EzDebug.log("applying force is cleared");
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
            double curMass = WapBlockInfos.Mass.valueOrDefaultOf(state);

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
            double m = WapBlockInfos.Mass.valueOrDefaultOf(state);

            //沟槽的平行轴定理还在追我
            double self_xxyyzz = 0.01667 * m;//(1.0f/12.0f) * m * 2;// * (Ly*Ly + Lz*Lz);
            //double self_yy = 0.01667 * m;//(1.0f/12.0f) * m * 2;// * (Lx*Lx + Lz*Lz);
            //double self_zz = 0.01667 * m;//(1.0f/12.0f) * m * 2;// * (Lx*Lx + Ly*Ly);

            localInertiaTensor.m00 += self_xxyyzz + m * (sqDy + sqDz);
            localInertiaTensor.m11 += self_xxyyzz + m * (sqDx + sqDz);
            localInertiaTensor.m22 += self_xxyyzz + m * (sqDx + sqDy);
        });
    }
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
            /*.putEach("updates", updates, update -> {  //todo save & load updates?
                CompoundTag savedUpdate = new CompoundTag();
                byte[] bytes = SerializeUtil.safeSerialize(update);
                if (bytes == null || bytes.length == 0) {
                    EzDebug.warn("fail to save a unapplied update");
                    return savedUpdate;
                }
                savedUpdate.putByteArray("bytes", bytes);
                return savedUpdate;
            })*/
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
            /*.readEachCompound("updates", t -> {
                if (!t.contains("bytes")) return null;
                return SerializeUtil.safeDeserialize(t.getByteArray("bytes"));
            }, updates)*/;


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
    public RigidbodyData getCopiedData(RigidbodyData dest) {
        return dest.copyData(this);
    }


    /*@Override
    public IRigidbodyDataWriter setVelocity(Vector3dc v) {
        if (!v.isFinite()) {
            EzDebug.warn("to set invalid velocity:" + v);
            return this;
        }

        Vector3d newVelImmutable = new Vector3d(v);
        updates.add(d -> d.velocity.set(newVelImmutable));

        return this;
    }*/
    @Override
    public IRigidbodyDataWriter set(RigidbodyData other) {
        RigidbodyData otherImm = other.getCopiedData();
        updates.add(d -> d.copyData(otherImm));
        return this;
    }

    //todo this is dangrous! may the upadter can't be saved!
    @Override
    public IRigidbodyDataWriter update(Consumer<RigidbodyData> updater) {
        updates.add(updater::accept);
        return this;
    }

    @Override
    public IRigidbodyDataWriter setVelocity(double x, double y, double z) {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            EzDebug.warn("to set invalid velocity:" + StrUtil.poslike(x, y, z));
            return this;
        }

        //Vector3d newVelImmutable = new Vector3d(x, y, z);
        //updates.add(d -> d.velocity.set(newVelImmutable));
        updates.add(d -> d.velocity.set(x, y, z));

        return this;
    }

    @Override
    public IRigidbodyDataWriter updateVelocity(Function<Vector3dc, Vector3d> vTransformer) {
        updates.add(d -> {
            Vector3d newV = vTransformer.apply(d.velocity);
            if (!newV.isFinite()) {
                EzDebug.warn("to set invalid vel:" + newV);
                return;
            }

            d.velocity.set(newV);
        });
        return this;
    }

    @Override
    public IRigidbodyDataWriter setOmega(Vector3dc newOmega) {
        if (!newOmega.isFinite()) {
            EzDebug.warn("to set invalid omega:" + newOmega);
            return this;
        }

        Vector3dc newOmegaImmutable = new Vector3d(newOmega);
        updates.add(d -> d.omega.set(newOmegaImmutable));
        return this;
    }
    @Override
    public IRigidbodyDataWriter setGravity(Vector3dc newGravity) {
        if (!newGravity.isFinite()) {
            EzDebug.warn("to set invalid gravity:" + newGravity);
            return this;
        }

        Vector3dc newGravityImmutable = new Vector3d(newGravity);
        updates.add(d -> d.gravity.set(newGravityImmutable));
        return this;
    }

    @Override
    public IRigidbodyDataWriter setStatic(boolean newVal) { isStatic.set(newVal); return this; }

    @Override
    public IRigidbodyDataWriter setPosition(Vector3dc p) {
        if (!p.isFinite()) {
            EzDebug.warn("to set invalid pos:" + p);
            return this;
        }

        Vector3dc newPosImmutable = new Vector3d(p);
        updates.add(d -> d.transform.position.set(newPosImmutable));
        return this;
    }

    @Override
    public IRigidbodyDataWriter updatePosition(Function<Vector3dc, Vector3d> pTransformer) {
        updates.add(d -> {
            Vector3d newP = pTransformer.apply(d.transform.position);
            if (!newP.isFinite()) {
                EzDebug.warn("to set invalid pos:" + newP);
                return;
            }

            d.transform.position.set(newP);
        });
        return this;
    }

    @Override
    public IRigidbodyDataWriter rotateWorld(Quaterniondc r) {
        if (!r.isFinite()) {
            EzDebug.warn("fail to rotate world, invalid rot:" + r);
            return this;
        }
        Quaterniond rotImm = new Quaterniond(r);
        updates.add(d -> transform.rotateWorld(rotImm));
        return this;
    }

    @Override
    public IRigidbodyDataWriter setRotation(Quaterniondc r) {
        Quaterniondc newRotImmutable = r.normalize(new Quaterniond());
        if (!newRotImmutable.isFinite()) {
            EzDebug.warn("to set invalid rotation:" + newRotImmutable);
            return this;
        }

        updates.add(d -> d.transform.rotation.set(newRotImmutable));
        return this;
    }
    @Override
    public IRigidbodyDataWriter setScale(Vector3dc s) {
        Vector3dc newScaleImmutable = new Vector3d(s);
        if (!newScaleImmutable.isFinite()) {
            EzDebug.warn("to set invalid scale:" + newScaleImmutable);
            return this;
        }

        updates.add(d -> d.transform.scale.set(newScaleImmutable));
        return this;
    }

    @Override
    public IRigidbodyDataWriter mulScale(Vector3dc s) {
        Vector3dc mulScaleImmutable = new Vector3d(s);
        if (!mulScaleImmutable.isFinite()) {
            EzDebug.warn("to set invalid scale:" + mulScaleImmutable);
            return this;
        }

        updates.add(d -> d.transform.scale.mul(mulScaleImmutable));
        return this;
    }

    @Override
    public IRigidbodyDataWriter setTransform(ITransformPrimitive newTransform) {
        //todo check if transform is valid

        TransformPrimitive transformImmutable = new TransformPrimitive(newTransform);
        updates.add(d -> d.transform.set(transformImmutable));
        return this;
    }



    @Override
    public IRigidbodyDataWriter applyWorldForce(Vector3dc f) {
        if (!f.isFinite()) {
            EzDebug.warn("to apply invalid force:" + f);
            return this;
        }

        Vector3d forceImm = new Vector3d(f);
        applyingForces.add(forceImm);
        //EzDebug.log("applying forces:" + applyingForces.size());
        return this;
    }
    @Override
    public IRigidbodyDataWriter applyWorldTorque(Vector3dc t) {
        if (!t.isFinite()) {
            EzDebug.warn("to apply invalid torque:" + t);
            return this;
        }

        Vector3d torqueImm = new Vector3d(t);
        applyingTorques.add(torqueImm); return this;
    }

    @Override
    public IRigidbodyDataWriter applyWork(double work) {
        /*if (work < 0) {
            EzDebug.warn("to apply invalid work:" + work);
            return this;
        }*/

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
        if (!localPos.isFinite() || !toWorld.isFinite()) {
            EzDebug.warn("apply invalid move: local " + localPos + ", to worldPos:" + toWorld);
            return this;
        }

        Vector3d localPosImmutable = new Vector3d(localPos);
        Vector3d toWorldImmutable = new Vector3d(toWorld);
        updates.add(d -> {
            Vector3d transformedPos = d.localToWorldPos(localPosImmutable, new Vector3d());
            Vector3d movement = toWorldImmutable.sub(transformedPos, new Vector3d());
            //the update pos is done sequently, don't worry the concurrent
            d.transform.translate(movement);
        });
        return this;
    }


    public RigidbodyData setPositionImmediately(Vector3dc pos) {
        transform.setPosition(pos);
        return this;
    }
    public RigidbodyData setPositionImmediately(double x, double y, double z) {
        transform.setPosition(x, y, z);
        return this;
    }
    public RigidbodyData setScaleImmediately(Vector3dc scale) {
        transform.setScale(scale);
        return this;
    }
    public RigidbodyData setRotImmediately(Quaterniondc rot) {
        transform.setRotation(rot);
        return this;
    }
    public RigidbodyData setTransformImmediately(ITransformPrimitive trans) {
        transform.set(trans);
        return this;
    }

    public RigidbodyData setVelocityImmediately(Vector3dc v) { velocity.set(v); return this; }
    public RigidbodyData setOmegaImmediately(Vector3dc o) { omega.set(o); return this; }

    public RigidbodyData setGravityImmediately(Vector3dc g) {
        gravity.set(g);
        return this;
    }
    public RigidbodyData setEarthGravityImmediately() { return setGravityImmediately(new Vector3d(0, -9.8, 0)); }
}
