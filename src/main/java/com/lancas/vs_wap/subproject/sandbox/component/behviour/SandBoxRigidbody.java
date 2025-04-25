package com.lancas.vs_wap.subproject.sandbox.component.behviour;

import com.lancas.vs_wap.content.info.block.WapBlockInfos;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxRigidbodyData;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedRigidbodyData;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.StrUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.*;

import java.lang.Math;

import static com.lancas.vs_wap.subproject.sandbox.SandBoxServerWorld.PHYS_TICK_TIME_S;

//todo sync
//todo make it a necessary behaviour?
public class SandBoxRigidbody extends AbstractComponentBehaviour<SandBoxRigidbodyData> {
    //private boolean massCenterDirty = true;
    //private final Vector3d massCenter = new Vector3d();

    @Override
    protected SandBoxRigidbodyData makeData() {
        return new SandBoxRigidbodyData();
    }

    /*@Override
    public void loadData(SandBoxServerShip inShip, SandBoxRigidbodyData src) {
        ship = inShip;
        data.copyData(src);


        //massCenterDirty = true;
    }*/

    @Override
    public IExposedRigidbodyData getExposedData() { return data; }


    public Vector3d calLocalMassCenter() {
        /*if (data.mass < 1E-10)  {
            //when mass is 0, massCenter is at (0, 0, 0)
            massCenterDirty = true;
            return massCenter.zero();
        }

        if (massCenterDirty) {
            data.localPosMassMul.div(data.mass, massCenter);
            massCenterDirty = false;
        }

        return massCenter;*/
        //if (data.mass < 1E-10) return massCenter.zero();
        //return data.localPosMassMul.div(data.mass, massCenter);  //从质量位置积分计算质心很简单，不需要LazyUpdate，反而增加复杂度
        if (data.mass < 1E-10) return new Vector3d();
        return data.localPosMassMul.div(data.mass, new Vector3d());
    }
    //todo cache
    public Vector3d calWorldMassCenter() {
        return ship.getTransform().localToWorldPos(calLocalMassCenter(), new Vector3d());
    }
    public Matrix3dc getInertia() { return data.inertiaTensor; }


    public void addForce(Vector3dc force) {
        //if (data.mass < 1E-10) return;  //don't check mass now: sometimes mass is still zero right after created, physTick will handle it.
        data.applyingForces.add(new Vector3d(force));
    }
    public void applyTorque(Vector3dc torque) {
        //if (data.mass < 1E-10) return;
        data.applyingTorques.add(new Vector3d(torque));
    }


    @Override
    public void physTick() {
        if (!isZero(data.omega)) {
            EzDebug.log("phyTick get non-zero omega:" + data.omega);
        }

        if (isZero(data.mass)) {
            data.applyingForces.clear();
            data.applyingTorques.clear();
            return;
        }

        //todo save invInertia
        //todo check the ineratia?
        Matrix3d invInertia = new Matrix3d(
            1.0 / data.inertiaTensor.m00, 0.0, 0.0,
            0.0, 1.0 / data.inertiaTensor.m11, 0.0,
            0.0, 0.0, 1.0 / data.inertiaTensor.m22
        );//data.inertiaTensor.invert(new Matrix3d());

        while (!data.applyingForces.isEmpty()) {
            Vector3d force = data.applyingForces.poll();
            Vector3d addVelocity = force.div(data.mass, new Vector3d()).mul(PHYS_TICK_TIME_S);
            if (force.isFinite())
                data.velocity.add(addVelocity);
            else
                EzDebug.warn("force is invalid, force:" + StrUtil.F2(force) + "\naddVel:" + StrUtil.F2(addVelocity) + "\nmass:" + data.mass);
        }
        while (!data.applyingTorques.isEmpty()) {
            Vector3d torque = data.applyingTorques.poll();
            Vector3d addOmega = torque.mul(invInertia, new Vector3d()).mul(PHYS_TICK_TIME_S);

            if (addOmega.isFinite()) {
                data.omega.add(addOmega);
            } else {
                EzDebug.warn("torque is invalid, torque:" + StrUtil.F2(torque) + "\naddOmega:" + StrUtil.F2(addOmega) + "\ninvInertia:" + invInertia);
            }
        }

        if (data.velocity.isFinite() && !isZero(data.velocity)) {
            Vector3d movement = data.velocity.mul(PHYS_TICK_TIME_S, new Vector3d());
            ship.getTransform().move(movement);  //todo notice sync
        }
        if (!data.velocity.isFinite()) EzDebug.warn("ship:" + ship.getUuid() + ", have invalid velocity:" + data.velocity);

        // 更新旋转：将角速度转换为四元数增量
        if (data.omega.isFinite() && !isZero(data.omega)) {
            //EzDebug.log("applying omega:" + StrUtil.F2(data.omega));

            double angle = data.omega.length() * PHYS_TICK_TIME_S;
            Vector3d rotateAxis = data.omega.normalize(new Vector3d());
            Quaterniond deltaQ = new Quaterniond().fromAxisAngleRad(rotateAxis, angle);

            if (deltaQ.isFinite()) {
                ship.getTransform().rotate(deltaQ);  //todo notice sync
            } else {
                EzDebug.error("get invalid dRotate:" + StrUtil.F2(deltaQ) + " by omega:" + StrUtil.F2(data.omega) + ", axis:" + StrUtil.F2(rotateAxis) + ", rad:" + angle);
            }
        }
        if (!data.omega.isFinite()) EzDebug.warn("ship:" + ship.getUuid() + ", have invalid omega:" + data.omega);
    }
    private boolean isZero(Vector3dc v) { return isZero(v.x()) && isZero(v.y()) && isZero(v.z()); }
    private boolean isZero(double x) { return Math.abs(x) < 1E-4; }

    @Override
    public void serverTick(ServerLevel level) {
        //EzDebug.log("server tick:" + ship.getUuid());
        //EzDebug.log("local massC:" + StrUtil.F2(getLocalMassCenter()) + ", world massC:" + StrUtil.F2(getWorldMassCenter()));
    }

    @Override
    public void onBlockReplaced(Vector3ic localPos, BlockState oldState, BlockState newState) {
        if (oldState == null && newState == null) return;  //for safe, in fact it's included by following codes.

        double oldStateMass = WapBlockInfos.mass.valueOrDefaultOf(oldState);  //it's safe for handle null or air
        double newStateMass = WapBlockInfos.mass.valueOrDefaultOf(newState);

        data.localPosMassMul.add(JomlUtil.d(localPos).mul(newStateMass - oldStateMass));
        data.mass += newStateMass - oldStateMass;

        data.updateInertia(ship);
    }
}
