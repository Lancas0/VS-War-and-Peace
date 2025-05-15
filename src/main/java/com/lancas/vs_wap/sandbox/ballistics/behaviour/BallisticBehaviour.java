package com.lancas.vs_wap.sandbox.ballistics.behaviour;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.sandbox.ballistics.data.BallisticData;
import com.lancas.vs_wap.sandbox.ballistics.trigger.SandBoxTriggerInfo;
import com.lancas.vs_wap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.abs.ServerOnlyBehaviour;
import net.minecraft.server.level.ServerLevel;
import org.joml.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
        double torqueDamping = 1.5;
        Vector3dc omega = rigidbody.getDataReader().getOmega();
        Matrix3dc inertia = rigidbody.getDataReader().getLocalInertia();
        Vector3d dampingTorque = omega.mul(inertia, new Vector3d()).mul(-torqueDamping);  //τ=I⋅α
        rigidbody.getDataWriter().applyWorldTorque(dampingTorque);


        /*synchronized (data.initialStateData.ballisticBlockLocPoses) {

            //EzDebug.log("try phys ticks for all block");
        }*/
        data.initialStateData.foreachBallisticBlock(ship, (locPos, state, bb) ->
            bb.physTick(ship, data)
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
            bb.serverTick(level, ship, locPos);
            bb.appendTriggerInfos(level, locPos, state, ship, infos);

            //EzDebug.log("try server ticks, triggerInfos");
        });



        AtomicBoolean needTerminate = new AtomicBoolean(false);
        data.initialStateData.foreachBallisticBlock(ship, (locPos, state, bb) -> {
            Dest<Boolean> curNeedTerminate = new Dest<>(false);
            bb.doTerminalEffect(level, ship, locPos, state, infos, curNeedTerminate);

            if (curNeedTerminate.get())
                needTerminate.set(true);
        });

        /*for (SandBoxTriggerInfo info : infos) {
            Dest<Boolean> curNeedTerminate = new Dest<>(false);

            data.initialStateData.foreachBallisticBlock(ship, (locPos, state, bb) -> {

                //EzDebug.log("try doTerminalEffect");
            });

            if (curNeedTerminate.get())
                needTerminate.set(true);
        }*/


        data.elapsedTime += 0.05;  //todo provided by method arg;
        if (data.elapsedTime > BallisticData.TIME_OUT_SECONDS)
            needTerminate.set(true);
        if (ship.getRigidbody().getDataReader().getTransform().getPosition().y() < BallisticData.RANGE_OUT_LOWER_Y)
            needTerminate.set(true);

        //needTerminate || stopped for too long?
        if (needTerminate.get()) {
            data.terminated = true;
            SandBoxServerWorld.getOrCreate(level).markShipDeleted(ship.getUuid());
            EzDebug.highlight("terminate this ship");
        }
    }
}
