package com.lancas.vswap.sandbox.ballistics.behaviour;

import com.lancas.vswap.sandbox.ballistics.data.AirDragSubData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticData;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.MathUtil;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class AirDragHandler {
    public static final double DRAG_FACTOR = 0.05;

    //todo sort
    public static void applyAirDragIfShould(SandBoxServerShip ship, BallisticData data) {
        //if (data.terminated || !data.barrelCtx.isAbsoluteExitBarrel()) return;
        if (data.terminated) return;

        //gather data
        var rigidbody = ship.getRigidbody();
        IRigidbodyDataReader rigidbodyDataReader = rigidbody.getDataReader();
        AirDragSubData airDragData = data.airDragData;
        Vector3dc velocity = rigidbodyDataReader.getVelocity();
        double velocitySqLen = velocity.lengthSquared();

        if (velocitySqLen < 10) return;  //no air drag when vel is too small

        Vector3d worldMassCenter = rigidbodyDataReader.getWorldMassCenter(new Vector3d());
        Vector3d worldAirDragCenter = rigidbodyDataReader.localToWorldPos(airDragData.localAirDragCenter, new Vector3d());
        Vector3d worldForward = rigidbodyDataReader.localIToWorldNoScaleDir(data.initialStateData.localForward);

        double projectArea = calAirDragAreaInWorld(ship, airDragData, velocity);
        double dragForceLen = DRAG_FACTOR * projectArea * velocitySqLen * 1;  //the last arg is air drag multiplier
        Vector3d airDragForce = velocity.normalize(-dragForceLen, new Vector3d());

        if (airDragForce.isFinite()) {
            Vector3d linearDrag = new Vector3d();
            Vector3d rotateDrag = new Vector3d();
            MathUtil.orthogonality(airDragForce, worldForward, linearDrag, rotateDrag);

            rigidbody.getDataWriter().applyWorldForce(linearDrag);

            Vector3d torque = worldAirDragCenter.sub(worldMassCenter, new Vector3d()).cross(rotateDrag);
            rigidbody.getDataWriter().applyWorldTorque(torque);

            //EzDebug.log("worldAirDragCenter:" + StrUtil.F2(worldAirDragCenter) + ", worldMassCenter:" + StrUtil.F2(worldMassCenter));
            //EzDebug.log("dragForceLen:" + dragForceLen + "projectArea:" + projectArea + "linearDrag:" + StrUtil.F2(linearDrag) + ", moment:" + StrUtil.F2(torque) + ", locAirDragCenter:" + StrUtil.F2(airDragData.localAirDragCenter));
        }
    }

    /*private static Vector3d calAirDragCenter(ServerLevel level, ServerShip projectile) {
        //todo will excess the max?
        AtomicReference<Double> totalWeight = new AtomicReference<>((double) 0);
        Vector3d sumCenter = new Vector3d();
        ShipUtil.foreachBlock(projectile, level, (pos, state, be) -> {
            if (state.isAir()) return;

            double curDragFactor = WapBlockInfos.drag_factor.valueOrDefaultOf(state);
            totalWeight.updateAndGet(v -> v + curDragFactor);
            sumCenter.add(JomlUtil.dCenter(pos).mul(curDragFactor));
        });
        return sumCenter.div(totalWeight.get());
    }*/
    private static double calAirDragAreaInWorld(SandBoxServerShip ship, AirDragSubData data, Vector3dc worldVel) {
        Vector3d localVelDir = ship.getRigidbody().getDataReader().localToWorldNoScaleDir(worldVel, new Vector3d()).normalize();

        //EzDebug.log("locVelDir:" + StrUtil.F2(localVelDir) + ", shipLocAABB:" + ship.getLocalAABB());

        return Math.abs(localVelDir.x) * data.localYzArea * ship.getAreaScale(0) +
            Math.abs(localVelDir.y) * data.localXzArea * ship.getAreaScale(1) +
            Math.abs(localVelDir.z) * data.localXyArea * ship.getAreaScale(2);
    }
}
