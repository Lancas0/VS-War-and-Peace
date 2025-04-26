package com.lancas.vs_wap.sandbox.ballistics.behaviour;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.sandbox.ballistics.data.AirDragSubData;
import com.lancas.vs_wap.sandbox.ballistics.data.BallisticData;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedRigidbodyData;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.util.MathUtil;
import com.lancas.vs_wap.util.StrUtil;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class AirDragHandler {

    //todo sort
    public static void applyAirDragIfShould(SandBoxServerShip ship, BallisticData data) {
        if (data.terminated || !data.barrelCtx.isAbsoluteExitBarrel()) return;

        //gather data
        var rigidbody = ship.getRigidbody();
        IExposedRigidbodyData rigidbodyData = rigidbody.getExposedData();
        AirDragSubData airDragData = data.airDragData;
        Vector3dc velocity = rigidbodyData.getVelocity();
        double velocitySqLen = velocity.lengthSquared();

        if (velocitySqLen < 10) return;  //no air drag when vel is too small

        Vector3d worldMassCenter = rigidbody.calWorldMassCenter();
        Vector3d worldAirDragCenter = ship.getTransform().localToWorldPos(airDragData.localAirDragCenter, new Vector3d());
        Vector3d worldForward = ship.getTransform().localToWorldNoScaleDir(data.initialStateData.localForward, new Vector3d());

        double projectArea = calAirDragAreaInWorld(ship, airDragData, velocity);
        double dragForceLen = 0.5 * projectArea * velocitySqLen * 1;  //the last arg is air drag multiplier
        Vector3d airDragForce = velocity.normalize(-dragForceLen, new Vector3d());

        if (airDragForce.isFinite()) {
            Vector3d linearDrag = new Vector3d();
            Vector3d rotateDrag = new Vector3d();
            MathUtil.orthogonality(airDragForce, worldForward, linearDrag, rotateDrag);

            rigidbody.addForce(linearDrag);

            Vector3d torque = worldAirDragCenter.sub(worldMassCenter, new Vector3d()).cross(rotateDrag);
            rigidbody.applyTorque(torque);

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
        Vector3d localVelDir = ship.getTransform().localToWorldNoScaleDir(worldVel, new Vector3d()).normalize();

        //EzDebug.log("locVelDir:" + StrUtil.F2(localVelDir) + ", shipLocAABB:" + ship.getLocalAABB());

        return Math.abs(localVelDir.x) * data.localYzArea * ship.getAreaScale(0) +
            Math.abs(localVelDir.y) * data.localXzArea * ship.getAreaScale(1) +
            Math.abs(localVelDir.z) * data.localXyArea * ship.getAreaScale(2);
    }
}
