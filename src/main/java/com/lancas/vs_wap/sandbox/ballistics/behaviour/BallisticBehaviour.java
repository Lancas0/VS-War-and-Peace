package com.lancas.vs_wap.sandbox.ballistics.behaviour;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.sandbox.ballistics.data.BallisticData;
import com.lancas.vs_wap.sandbox.ballistics.trigger.SandBoxTriggerInfo;
import com.lancas.vs_wap.ship.ballistics.api.TriggerInfo;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.AbstractComponentBehaviour;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedComponentData;
import net.minecraft.server.level.ServerLevel;
import org.joml.*;

import java.util.ArrayList;
import java.util.List;

public class BallisticBehaviour extends AbstractComponentBehaviour<BallisticData> {
    @Override
    protected BallisticData makeData() { return BallisticData.makeDefault(); }

    @Override
    public void physTick() {
        if (data.terminated) return;

        var rigidbody = ship.getRigidbody();

        PropellingForceHandler.applyPropellingForceIfShould(ship, data);
        AirDragHandler.applyAirDragIfShould(ship, data);


        //damping
        double torqueDamping = 1;
        Vector3dc omega = rigidbody.getExposedData().getOmega();
        Matrix3dc inertia = rigidbody.getInertia();
        Vector3d dampingTorque = omega.mul(inertia, new Vector3d()).mul(-torqueDamping);  //τ=I⋅α
        rigidbody.applyTorque(dampingTorque);


        synchronized (data.initialStateData.ballisticBlockLocPoses) {
            data.initialStateData.foreachBallisticBlock(ship, (locPos, state, bb) -> bb.physTick(ship));
            //EzDebug.log("try phys ticks for all block");
        }
    }

    @Override
    public IExposedComponentData<BallisticData> getExposedData() { return data; }  //not exposed thou

    @Override
    public void serverTick(ServerLevel level) {
        if (data.terminated) return;

        //todo reaction force

        BarrelCtxUpdateHandler.updateBarrelCtx(level, ship, data);
        if (!data.barrelCtx.isAbsoluteExitBarrel()) {
            ship.getRigidbody().getExposedData().setNoGravity();
            return;
        } else {
            ship.getRigidbody().getExposedData().setEarthGravity();
        }

        List<SandBoxTriggerInfo> infos = new ArrayList<>();
        synchronized (data.initialStateData.ballisticBlockLocPoses) {
            //warn: may change during foreach
            data.initialStateData.foreachBallisticBlock(ship, (locPos, state, bb) -> {
                bb.serverTick(level, ship);
                bb.appendTriggerInfos(level, locPos, state, ship, infos);

                //EzDebug.log("try server ticks, triggerInfos");
            });
        }


        boolean needTerminate = false;
        for (SandBoxTriggerInfo info : infos) {
            Dest<Boolean> curNeedTerminate = new Dest<>(false);

            synchronized (data.initialStateData.ballisticBlockLocPoses) {
                //warn: may change during foreach
                data.initialStateData.foreachBallisticBlock(ship, (locPos, state, bb) -> {
                    bb.doTerminalEffect(level, locPos, state, info, curNeedTerminate);
                    //EzDebug.log("try doTerminalEffect");
                });
            }

            if (curNeedTerminate.get())
                needTerminate = true;
        }

        //needTerminate || stopped for too long?
        if (needTerminate) {
            data.terminated = true;
            ship.addDestroyMark();
            EzDebug.highlight("terminate this ship");
        }
    }
}
