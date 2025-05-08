package com.lancas.vs_wap.sandbox.ballistics.behaviour;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.sandbox.ballistics.data.BallisticBarrelContextSubData;
import com.lancas.vs_wap.sandbox.ballistics.data.BallisticData;
import com.lancas.vs_wap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.util.StrUtil;
import org.joml.Vector3d;

public class PropellingForceHandler {
    private static final double INITIAL_GAS_VOL = 0.1;  //todo config
    private static final double PRESS_AREA = 1000000;  //don't scale: scale increase power and area, same as neither is scaled
    public static final double GUNPOWDER_GAS_GAMMA = 1.25;
    public static final double HIGH_PRESSURE_STAGE_ALPHA = 0.9;

    public static void applyPropellingForceIfShould(SandBoxServerShip ship, BallisticData data) {
        //EzDebug.log("termianted:" + data.terminated + ", always:" + data.barrelCtx.alwaysInBarrelSinceLaunch);
        if (data.terminated || !data.barrelCtx.alwaysInBarrelSinceLaunch) return;

        IRigidbodyDataReader rigidReader = ship.getRigidbody().getDataReader();




        //var initialStateData = data.initialStateData;
        //double initialPressure = initialStateData.totalPropellingEnergy / INITIAL_GAS_VOL;
        if (!data.barrelCtx.appliedHighPressureStage) {
            //it's a formula
            /*double gamma = GUNPOWDER_GAS_GAMMA;
            double f = 1 - Math.pow(HIGH_PRESSURE_STAGE_ALPHA, (gamma - 1) / gamma);
            double afterHighPressureStageVelLen = Math.sqrt(2 * initialStateData.totalPropellingEnergy * f / (rigidReader.getMass() * gamma - 1));
            EzDebug.log("apply high stage vel: " + StrUtil.F2(initialStateData.worldLaunchDir.normalize(afterHighPressureStageVelLen, new Vector3d())));
            ship.getRigidbody().getDataWriter().setVelocity(initialStateData.worldLaunchDir.normalize(afterHighPressureStageVelLen, new Vector3d()));
            */
            //ship.getRigidbody().getDataWriter().applyWork(data.initialStateData.totalPropellingEnergy);
            ship.getRigidbody().getDataWriter().setVelocity(data.initialStateData.worldLaunchDir.normalize(Math.sqrt(2 * data.initialStateData.totalPropellingEnergy / rigidReader.getMass()), new Vector3d()));
            EzDebug.log("energy:" + data.initialStateData.totalPropellingEnergy / 1000 + " KJ");
            data.barrelCtx.appliedHighPressureStage = true;
        }

        /*double traveledInBarrel = initialStateData.launchWorldPos.distance(ship.getRigidbody().getDataReader().getPosition());
        double curGasVol = INITIAL_GAS_VOL + PRESS_AREA * traveledInBarrel;
        double forceRatio = INITIAL_GAS_VOL / curGasVol;

        double initialForceLen = initialPressure * PRESS_AREA;

        Vector3d propellingForce = initialStateData.worldLaunchDir.normalize(initialForceLen * forceRatio, new Vector3d());

        //EzDebug.log("propellingForce:" + propellingForce.length());

        ship.getRigidbody().getDataWriter().applyWorldForce(propellingForce);*/
        //EzDebug.log("applying force:" + propellingForce + ", after apply force:");
        //EzDebug.logs(ship.getRigidbody().getDataReader().allForces().toList(), f -> StrUtil.F2(f));
    }
}
