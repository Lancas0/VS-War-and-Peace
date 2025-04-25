package com.lancas.vs_wap.sandbox.component.ballistic.behaviour;

import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.sandbox.component.ballistic.data.BallisticData;
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
        }
    }

    @Override
    public IExposedComponentData<BallisticData> getExposedData() { return data; }  //not exposed thou

    @Override
    public void serverTick(ServerLevel level) {
        if (data.terminated) return;

        //todo reaction force

        BarrelCtxUpdateHandler.updateBarrelCtx(level, ship, data);
        if (!data.barrelCtx.isAbsoluteExitBarrel()) return;

        List<TriggerInfo> infos = new ArrayList<>();
        synchronized (data.initialStateData.ballisticBlockLocPoses) {
            data.initialStateData.foreachBallisticBlock(ship, (locPos, state, bb) -> {
                bb.serverTick(level, ship);
                bb.appendTriggerInfos(locPos, state, ship, infos);
            });
        }


        boolean needTerminate = false;
        for (TriggerInfo info : infos) {
            Dest<Boolean> curNeedTerminate = new Dest<>(false);

            synchronized (data.initialStateData.ballisticBlockLocPoses) {
                data.initialStateData.foreachBallisticBlock(ship, (locPos, state, bb) -> {
                    bb.doTerminalEffect(locPos, state, info, curNeedTerminate);
                });
            }

            if (curNeedTerminate.get())
                needTerminate = true;
        }

        //needTerminate || stopped for too long?
        if (needTerminate) {
            data.terminated = true;
            ship.addDestroyMark();
            //todo terminate server
        }
    }
}
