package com.lancas.vs_wap.subproject.sandbox.component.data;

import com.lancas.vs_wap.content.info.block.WapBlockInfos;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedRigidbodyData;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.SandBoxRigidbody;

public class SandBoxRigidbodyData implements IComponentData<SandBoxRigidbodyData>, IExposedRigidbodyData {
    public double mass = 0;
    public final Vector3d localPosMassMul = new Vector3d();  //todo may exceed limit
    public final Matrix3d inertiaTensor = new Matrix3d();

    public final Vector3d velocity = new Vector3d();
    public final Vector3d omega = new Vector3d();

    public final Queue<Vector3d> applyingForces = new ConcurrentLinkedQueue<>();
    public final Queue<Vector3d> applyingTorques = new ConcurrentLinkedQueue<>();

    public final Vector3d gravity = new Vector3d();

    private SandBoxRigidbodyData() {}
    public static SandBoxRigidbodyData createNoGravity() {
        return new SandBoxRigidbodyData();  //actually default is no gravity
    }
    public static SandBoxRigidbodyData createDefault() { return new SandBoxRigidbodyData(); }
    public static SandBoxRigidbodyData createEarthGravity() {
        SandBoxRigidbodyData data = new SandBoxRigidbodyData();
        data.setGravity(new Vector3d(0, -9.8, 0));
        return data;
    }


    @Override
    public SandBoxRigidbodyData copyData(SandBoxRigidbodyData src) {
        mass = src.mass;
        localPosMassMul.set(src.localPosMassMul);
        inertiaTensor.set(src.inertiaTensor);

        velocity.set(src.velocity);
        omega.set(src.omega);

        applyingForces.clear();  applyingForces.addAll(src.applyingForces);
        applyingTorques.clear(); applyingTorques.addAll(src.applyingTorques);
        return this;
    }
    @Override
    public SandBoxRigidbodyData overwriteDataByShip(SandBoxServerShip ship) {
        //I suppose it's no need to sync when init
        mass = 0;
        localPosMassMul.zero();
        inertiaTensor.zero();
        ship.getCluster().foreach((localPos, state) -> {
            double curMass = WapBlockInfos.mass.valueOrDefaultOf(state);

            mass += curMass;
            localPosMassMul.add(JomlUtil.d(localPos).mul(curMass));
        });

        updateInertia(ship);  //todo dirty inertia
        return this;
    }
    public void updateInertia(SandBoxServerShip ship) {
        inertiaTensor.zero();
        Vector3d massCenter = ship.getRigidbody().calLocalMassCenter();

        ship.getCluster().foreach((localPos, state) -> {
            Vector3d delta = JomlUtil.d(localPos).sub(massCenter);
            double sqDx = delta.x * delta.x, sqDy = delta.y * delta.y, sqDz = delta.z * delta.z;
            double m = WapBlockInfos.mass.valueOrDefaultOf(state);

            //沟槽的平行轴定理还在追我
            double self_xxyyzz = 0.01667 * m;//(1.0f/12.0f) * m * 2;// * (Ly*Ly + Lz*Lz);
            //double self_yy = 0.01667 * m;//(1.0f/12.0f) * m * 2;// * (Lx*Lx + Lz*Lz);
            //double self_zz = 0.01667 * m;//(1.0f/12.0f) * m * 2;// * (Lx*Lx + Ly*Ly);

            inertiaTensor.m00 += self_xxyyzz + m * (sqDy + sqDz);
            inertiaTensor.m11 += self_xxyyzz + m * (sqDx + sqDz);
            inertiaTensor.m22 += self_xxyyzz + m * (sqDx + sqDy);

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
            .putNumber("mass", mass)
            //.putVector3d("local_pos_mass_mul", localPosMassMul)  //note localPosMassMul and inertiaTensor are setted by ship
            //.putMatrix3d()
            .putVector3d("velocity", velocity)
            .putVector3d("omega", omega)
            .putEach("applying_forces", applyingForces, NbtBuilder::tagOfVector3d)
            .putEach("applying_torque", applyingTorques, NbtBuilder::tagOfVector3d)
            .putVector3d("gravity", gravity)
            .get();
    }
    @Override
    public SandBoxRigidbodyData load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readDoubleDo("mass", v -> mass = v)
            //.readVector3d("local_pos_mass_mul", localPosMassMul);
            .readVector3d("velocity", velocity)
            .readVector3d("omega", omega)
            .readEachCompoundOverwrite("applying_forces", NbtBuilder::vector3dOf, applyingForces)
            .readEachCompoundOverwrite("applying_torque", NbtBuilder::vector3dOf, applyingTorques)
            .readVector3d("gravity", gravity);

        EzDebug.log("load omega: " + omega);

        return this;
    }


    @Override
    public double getMass() { return mass; }
    @Override
    public Matrix3dc getInertia() { return inertiaTensor; }
    @Override
    public Vector3dc getVelocity() { return velocity; }
    @Override
    public Vector3dc getOmega() { return omega; }

    @Override
    public void setVelocity(Vector3dc newVel) { velocity.set(newVel); }  //todo sync
    @Override
    public void setOmega(Vector3dc newOmega) { omega.set(newOmega); }  //todo sync

    @Override
    public Vector3dc getGravity() { return gravity; }
    @Override
    public void setGravity(Vector3dc newGravity) { gravity.set(newGravity); }  //todo sync
}
