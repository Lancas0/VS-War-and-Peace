package com.lancas.vs_wap.sandbox.ballistics.behaviour;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.sandbox.ballistics.data.BallisticData;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.util.StrUtil;
import org.joml.Vector3d;

public class PropellingForceHandler {
    private static final double INITIAL_GAS_VOL = 1;  //todo config
    private static final double PRESS_AREA = 1;  //don't scale: scale increase power and area, same as neither is scaled

    public static void applyPropellingForceIfShould(SandBoxServerShip ship, BallisticData data) {
        //EzDebug.log("termianted:" + data.terminated + ", always:" + data.barrelCtx.alwaysInBarrelSinceLaunch);
        if (data.terminated || !data.barrelCtx.alwaysInBarrelSinceLaunch) return;

        var initialStateData = data.initialStateData;

        double traveledInBarrel = initialStateData.launchWorldPos.distance(ship.getRigidbody().getDataReader().getPosition());
        double curGasVol = INITIAL_GAS_VOL + PRESS_AREA * traveledInBarrel;
        double forceRatio = INITIAL_GAS_VOL / curGasVol;

        double initialForceLen = initialStateData.totalPropellingEnergy / INITIAL_GAS_VOL * PRESS_AREA;

        Vector3d propellingForce = initialStateData.worldLaunchDir.normalize(initialForceLen * forceRatio, new Vector3d());
        ship.getRigidbody().getDataWriter().applyWorldForce(propellingForce);
        //EzDebug.log("applying force:" + propellingForce + ", after apply force:");
        //EzDebug.logs(ship.getRigidbody().getDataReader().allForces().toList(), f -> StrUtil.F2(f));
    }
}
