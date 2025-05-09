package com.lancas.vs_wap.subproject.sandbox.thread.impl.server;

import com.lancas.vs_wap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vs_wap.subproject.sandbox.api.UUIDLazyParamWrapper;
import com.lancas.vs_wap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vs_wap.subproject.sandbox.thread.impl.TimerBasedThread;

import java.util.TimerTask;

import static com.lancas.vs_wap.subproject.sandbox.event.SandBoxEventMgr.onServerShipTransformDirty;

public class SandBoxServerSyncThread extends TimerBasedThread<SandBoxServerWorld> {
    @Override
    protected String getTimerName() { return "sandbox-server-sync-thread"; }
    @Override
    protected TimerTask getTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                world.allServerShips().forEach(s ->
                    onServerShipTransformDirty.schedule(
                        s.getUuid(),
                        new UUIDLazyParamWrapper(s.getUuid()),
                        new TransformPrimitive(s.getRigidbody().getDataReader().getTransform())//,
                        //new AABBdLazyParamWrapper(s.getLocalAABB())
                ));
                onServerShipTransformDirty.invokeAll();
            }
        };
    }
    @Override
    protected int getTimerPeriodMS() { return 16; }
}
