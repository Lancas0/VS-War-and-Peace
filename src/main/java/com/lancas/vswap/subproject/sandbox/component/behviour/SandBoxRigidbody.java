package com.lancas.vswap.subproject.sandbox.component.behviour;

import com.lancas.vswap.content.info.block.WapBlockInfos;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.component.behviour.abs.BothSideBehaviour;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vswap.subproject.sandbox.constraint.SandBoxConstraintSolver;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxClientShip;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.StrUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;
import java.util.UUID;

//todo sync
//todo make it a necessary behaviour?
public class SandBoxRigidbody
    extends BothSideBehaviour<RigidbodyData>
    implements IRigidbodyBehaviour {

    @Override
    protected RigidbodyData makeInitialData() { return new RigidbodyData(); }

    @Override
    public IRigidbodyDataReader getDataReader() { return data; }
    @Override
    public IRigidbodyDataWriter getDataWriter() { return data; }

    //public RigidbodyData getDataInPhysThread() { return data; } //todo check phy thread

    public static @Nullable RigidbodyData resolveRigidData(UUID uuid, SandBoxConstraintSolver constraintSolver) {
        ISandBoxShip s = constraintSolver.getWorld().getShip(uuid);
        if (s == null)
            return null;
        if (s.getRigidbody() instanceof SandBoxRigidbody r)
            return r.data;
        return null;
    }


    @Override
    public Class<RigidbodyData> getDataType() { return RigidbodyData.class; }

    @Override
    public synchronized void physTick(double dt) {
        //EzDebug.log("mass:" + data.mass + ", static:" + data.isStatic.get());
        if (isZero(data.mass) || data.isStatic()) {
            //EzDebug.log("applying force is cleared due to zeroMass or static, mass:" + data.mass + ", static?:" + data.isStatic);
            data.applyingForces.clear();
            data.applyingTorques.clear();
        } else {
            //EzDebug.light("applying force and torque");
            applyForcesAndVelocity(dt);
            applyTorqueAndOmega(dt);
        }

        //update at last so that constraint can be accurate
        applyUpdates();

        //todo how about move snapshot to server tick?
        data.localToWorldSnapshot = new Matrix4d().translationRotateScale(
            data.transform.position,
            data.transform.rotation,
            data.transform.scale
        );
        data.worldToLocalSnapshot = data.localToWorldSnapshot.invert(new Matrix4d());
    }
    private boolean isZero(Vector3dc v) { return isZero(v.x()) && isZero(v.y()) && isZero(v.z()); }
    private boolean isZero(double x) { return Math.abs(x) < 1E-4; }
    private void applyUpdates() {
        var updateIt = data.updates.iterator();
        while (updateIt.hasNext()) {
            var update = updateIt.next();
            if (update == null) {
                updateIt.remove();
                continue;
            }

            try {
                update.update(data);
            } catch (Exception e) {
                EzDebug.warn("fail to update the rigidbody data");
                e.printStackTrace();
            }
            updateIt.remove();
        }
    }
    private void applyForcesAndVelocity(double dt) {
        while (!data.applyingForces.isEmpty()) {
            Vector3d force = data.applyingForces.poll();

            Vector3d addVelocity = force.div(data.mass, new Vector3d()).mul(dt);
            if (force.isFinite())
                data.velocity.add(addVelocity);
            else
                EzDebug.warn("force is invalid, force:" + StrUtil.F2(force) + "\naddVel:" + StrUtil.F2(addVelocity) + "\nmass:" + data.mass);
        }

        if (!isZero(data.gravity))
            data.velocity.add(data.gravity.mul(dt, new Vector3d()));

        if (data.velocity.isFinite() && !isZero(data.velocity)) {
            Vector3d movement = data.velocity.mul(dt, new Vector3d());
            data.transform.position.add(movement);
        }

        if (!data.velocity.isFinite()) EzDebug.warn("ship:" + ship.getUuid() + ", have invalid velocity:" + data.velocity);
    }
    private void applyTorqueAndOmega(double dt) {
        //todo save invInertia
        //todo check the ineratia?
        Matrix3d localInvInertia = new Matrix3d(
            1.0 / data.localInertiaTensor.m00, 0.0, 0.0,
            0.0, 1.0 / data.localInertiaTensor.m11, 0.0,
            0.0, 0.0, 1.0 / data.localInertiaTensor.m22
        );//data.inertiaTensor.invert(new Matrix3d());
        // 局部转动惯量转换为世界转动惯量
        //todo can apply world or local torque/force
        Matrix3d rotationMatrix = data.transform.rotation.get(new Matrix3d());
        Matrix3d invInertiaWorld = rotationMatrix.mul(localInvInertia, new Matrix3d()).mul(rotationMatrix.transpose(new Matrix3d()));

        while (!data.applyingTorques.isEmpty()) {
            Vector3d torque = data.applyingTorques.poll();
            Vector3d addOmega = torque.mul(invInertiaWorld, new Vector3d()).mul(dt);//torque.mul(localInvInertia, new Vector3d()).mul(PHYS_TICK_TIME_S);

            if (addOmega.isFinite()) {
                data.omega.add(addOmega);
            } else {
                EzDebug.warn("torque is invalid, torque:" + StrUtil.F2(torque) + "\naddOmega:" + StrUtil.F2(addOmega)/* + "\ninvInertia:" + localInvInertia*/);
            }
        }

        if (data.omega.isFinite() && !isZero(data.omega)) {
            double angle = data.omega.length() * dt;
            Vector3d rotateAxis = data.omega.normalize(new Vector3d());
            Quaterniond deltaQ = new Quaterniond().fromAxisAngleRad(rotateAxis, angle);

            if (deltaQ.isFinite()) {
                data.transform.rotateWorld(deltaQ);  //todo notice sync
            } else {
                EzDebug.error("get invalid dRotate:" + StrUtil.F2(deltaQ) + " by omega:" + StrUtil.F2(data.omega) + ", axis:" + StrUtil.F2(rotateAxis) + ", rad:" + angle);
            }
        }
        if (!data.omega.isFinite()) EzDebug.warn("ship:" + ship.getUuid() + ", have invalid omega:" + data.omega);
    }


    @Override
    public synchronized void serverTick(ServerLevel level) { }
    @Override
    public synchronized void clientTick(ClientLevel level) { }


    @Override
    public void onBlockReplaced(Vector3ic localPos, BlockState oldState, BlockState newState) {
        if (oldState == null && newState == null) return;  //for safe, in fact it's included by following codes.

        double oldStateMass = WapBlockInfos.Mass.valueOrDefaultOf(oldState);  //it's safe for handle null or air
        double newStateMass = WapBlockInfos.Mass.valueOrDefaultOf(newState);

        data.localPosMassMul.add(JomlUtil.d(localPos).mul(newStateMass - oldStateMass));
        data.mass += newStateMass - oldStateMass;

        data.updateInertia(ship);
    }
}
