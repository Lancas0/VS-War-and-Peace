package com.lancas.vswap.subproject.sandbox.thread.impl.server;

import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.api.UUIDLazyParamWrapper;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.subproject.sandbox.thread.impl.TimerBasedThread;

import java.util.TimerTask;

import static com.lancas.vswap.subproject.sandbox.event.SandBoxEventMgr.onServerShipTransformDirty;

public class SandBoxServerToClientSyncThread extends TimerBasedThread<SandBoxServerWorld> {
    protected SandBoxServerWorld world;
    public SandBoxServerToClientSyncThread(SandBoxServerWorld inWorld) { world = inWorld; }

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
