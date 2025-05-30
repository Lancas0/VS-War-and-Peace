package com.lancas.vswap.ship.ballistics.handler;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.ship.ballistics.api.TriggerInfo;
import com.lancas.vswap.ship.ballistics.data.BallisticData;
import com.lancas.vswap.ship.ballistics.data.BallisticStateData;
import com.lancas.vswap.ship.ballistics.data.BallisticsComponentData;
import com.lancas.vswap.ship.ballistics.data.BallisticsShipData;
import net.minecraft.server.level.ServerLevel;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BallisticsUpdateHandler {

    public static void update(ServerLevel level, BallisticData ballisticData, Dest<Boolean> terminatedDest) {
        if (ballisticData.isTerminated()) {
            terminatedDest.set(true);
            return;
        }

        BallisticsShipData shipData = Objects.requireNonNull(ballisticData.shipData);
        BallisticStateData stateData = Objects.requireNonNull(ballisticData.stateData);
        BallisticsComponentData componentData = Objects.requireNonNull(ballisticData.componentData);

        ServerShip projectileShip = shipData.getProjectileShip(level);
        ServerShip artilleryShip = shipData.getArtilleryShip(level);
        if (projectileShip == null) {
            EzDebug.warn("projectile ship is null, ballistic controller will terminate");
            //terminateAndTryReturnToPool(level);
            //BallisticsClientManager.terminateIdFromServer(shipData.getProjectileId());
            return;
        }


        //applyReactionForces(artilleryShip);

        stateData.updateState(level, projectileShip, artilleryShip);
        //EzDebug.log("is out artillery:" + stateData.getIsOutArtillery());
        componentData.foreachTicker(level, (tickerBp, state, ticker) -> {
            ticker.serverTicker(state, tickerBp, level, projectileShip);
        });

        /*airDragMultiplierCalInServer = 1;
        componentData.foreachModifier(level, (pos, state, modifier) -> {
            airDragMultiplierCalInServer *= modifier.getAirDragMultiplier(shipData.projectile, pos, state);

            modifier.modifyTempPhysBehaviour(shipData.projectile, pos, state, tempPhyBehaviours);
        });*/
        /*componentData.foreachModifier(level, (modifierBp, state, modifier) -> {
            var data = new IModifier.ModifierData(
                level,
                projectileShip,
                modifierBp,
                stateData.getIsOutArtillery(),
                new Vector3i(shipData.headDirInShip.getStepX(), shipData.headDirInShip.getStepY(), shipData.headDirInShip.getStepZ()),
                shipData.launchDir
            );
            Vector3dc modifierForce = modifier.calculateForceInServerTick(data);
            Vector3dc modifierTorque = modifier.calculateTorqueInServerTick(data);
            if (modifierForce == null) modifierForce = new Vector3d();
            if (modifierTorque == null) modifierTorque = new Vector3d();
            calculatedModifierForce.put(modifierBp, modifierForce);  //must put even it is null. because it is neccessy to override the last forece
            calculatedModifierTorque.put(modifierBp, modifierTorque);
        });*/
        //physBehaviours = getPhysicalBehaviours(level);


        List<TriggerInfo> infos = getTriggerInfos(level, ballisticData);
        //todo avoid multi effect on a one-time effector
        tryTerminalEffect(level, ballisticData, infos, terminatedDest);
        terminatedDest.update(v -> v ||  stateData.tickStopped(projectileShip));
    }


    private static List<TriggerInfo> getTriggerInfos(ServerLevel level, BallisticData ballisticData) {
        List<TriggerInfo> infos = new ArrayList<>();
        ballisticData.componentData.foreachTrigger(level, (bp, state, trigger) -> {
            if (!trigger.shouldCheck(ballisticData.shipData, ballisticData.stateData))
                return;

            trigger.appendTriggerInfos(level, bp, state, ballisticData.shipData, ballisticData.stateData, infos);
        });
        return infos;
    }
    private static void tryTerminalEffect(ServerLevel level, BallisticData ballisticData, List<TriggerInfo> infos, Dest<Boolean> shouldTerminate) {
        shouldTerminate.set(false);
        for (TriggerInfo info : infos) {
            ballisticData.componentData.foreachEffector(level, (bp, state, effector) -> {
                if (!effector.canAccept(level, bp, state, info))
                    return;

                effector.effect(level, bp, state, info);

                if (effector.shouldTerminateAfterEffecting(info))
                    shouldTerminate.set(true);
            });
        }
    }
}
