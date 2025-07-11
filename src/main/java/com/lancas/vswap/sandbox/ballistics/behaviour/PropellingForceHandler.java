package com.lancas.vswap.sandbox.ballistics.behaviour;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.sandbox.ballistics.data.BallisticData;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import org.joml.Vector3d;

public class PropellingForceHandler {
    private static final double INITIAL_GAS_VOL = 0.1;  //todo config
    private static final double PRESS_AREA = 1000000;  //don't scale: scale increase power and area, same as neither is scaled
    public static final double GUNPOWDER_GAS_GAMMA = 1.25;
    public static final double HIGH_PRESSURE_STAGE_ALPHA = 0.9;

    public static final double STD_PROPELLANT_ENERGY = 1E5;//temp 1E4//1E5;

    public static void applyPropellingForceIfShould(SandBoxServerShip ship, BallisticData data) {
        //EzDebug.log("termianted:" + data.terminated + ", always:" + data.barrelCtx.alwaysInBarrelSinceLaunch);
        //if (data.terminated || !data.barrelCtx.alwaysInBarrelSinceLaunch) return;
        if (data.terminated) return;

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
            double energy = data.initialStateData.stdPropellingEnergy * STD_PROPELLANT_ENERGY;
            Vector3d vel = data.initialStateData.worldLaunchDir.normalize(Math.sqrt(2 * energy / rigidReader.getMass()), new Vector3d());
            //ship.getRigidbody().getDataWriter().setVelocity(vel);
            ship.getRigidbody().getDataWriter().addVelocity(vel);
            EzDebug.log("energy:" + data.initialStateData.stdPropellingEnergy / 1000 + " KJ" + ", vel:" + vel + ", velLen:" + vel.length());
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
