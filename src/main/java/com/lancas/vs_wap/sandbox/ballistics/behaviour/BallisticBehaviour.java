package com.lancas.vs_wap.sandbox.ballistics.behaviour;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.sandbox.ballistics.data.BallisticData;
import com.lancas.vs_wap.sandbox.ballistics.trigger.SandBoxTriggerInfo;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.abs.ServerOnlyBehaviour;
import net.minecraft.server.level.ServerLevel;
import org.joml.*;

import java.util.ArrayList;
import java.util.List;

public class BallisticBehaviour extends ServerOnlyBehaviour<BallisticData> {
    @Override
    protected BallisticData makeInitialData() { return BallisticData.makeDefault(); }

    @Override
    public Class<BallisticData> getDataType() { return BallisticData.class; }

    @Override
    public synchronized void physTick() {
        if (data.terminated) return;

        var rigidbody = ship.getRigidbody();

        PropellingForceHandler.applyPropellingForceIfShould(ship, data);
        AirDragHandler.applyAirDragIfShould(ship, data);


        //damping
        double torqueDamping = 1;
        Vector3dc omega = rigidbody.getDataReader().getOmega();
        Matrix3dc inertia = rigidbody.getDataReader().getLocalInertia();
        Vector3d dampingTorque = omega.mul(inertia, new Vector3d()).mul(-torqueDamping);  //τ=I⋅α
        rigidbody.getDataWriter().applyWorldTorque(dampingTorque);


        /*synchronized (data.initialStateData.ballisticBlockLocPoses) {

            //EzDebug.log("try phys ticks for all block");
        }*/
        data.initialStateData.foreachBallisticBlock(ship, (locPos, state, bb) ->
            bb.physTick(ship)
        );
    }

    @Override
    public synchronized void serverTick(ServerLevel level) {
        //EzDebug.log("ballstic ticking");
        if (data.terminated) return;

        //todo reaction force

        BarrelCtxUpdateHandler.updateBarrelCtx(level, ship, data);

        //EzDebug.log("barrel ctx updated");

        if (!data.barrelCtx.isAbsoluteExitBarrel()) {
            ship.getRigidbody().getDataWriter().setNoGravity();
            return;
        } else {
            ship.getRigidbody().getDataWriter().setEarthGravity();
        }

        //EzDebug.log("handling SandBoxTriggerInfo");

        List<SandBoxTriggerInfo> infos = new ArrayList<>();
        /*synchronized (data.initialStateData.ballisticBlockLocPoses) {
            //warn: may change during foreach
        }*/
        data.initialStateData.foreachBallisticBlock(ship, (locPos, state, bb) -> {
            //EzDebug.log("foreach block:" + StrUtil.getBlockName(state));
            bb.serverTick(level, ship);
            bb.appendTriggerInfos(level, locPos, state, ship, infos);

            //EzDebug.log("try server ticks, triggerInfos");
        });



        boolean needTerminate = false;
        for (SandBoxTriggerInfo info : infos) {
            Dest<Boolean> curNeedTerminate = new Dest<>(false);

            /*synchronized (data.initialStateData.ballisticBlockLocPoses) {
                //warn: may change during foreach
            }*/
            data.initialStateData.foreachBallisticBlock(ship, (locPos, state, bb) -> {
                bb.doTerminalEffect(level, ship, locPos, state, info, curNeedTerminate);
                //EzDebug.log("try doTerminalEffect");
            });

            if (curNeedTerminate.get())
                needTerminate = true;
        }


        data.elapsedTime += 0.05;  //todo constant;
        if (data.elapsedTime > BallisticData.TIME_OUT_SECONDS)
            needTerminate = true;
        if (ship.getRigidbody().getDataReader().getTransform().getPosition().y() < BallisticData.RANGE_OUT_LOWER_Y)
            needTerminate = true;

        //needTerminate || stopped for too long?
        if (needTerminate) {
            data.terminated = true;
            ship.setRemainLifeTick(1);
            EzDebug.highlight("terminate this ship");
        }
    }
}
