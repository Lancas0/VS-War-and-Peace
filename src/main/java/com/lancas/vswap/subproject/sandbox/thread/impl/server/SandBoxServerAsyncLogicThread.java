package com.lancas.vswap.subproject.sandbox.thread.impl.server;

import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.api.UUIDLazyParamWrapper;
import com.lancas.vswap.subproject.sandbox.api.component.IServerAsyncLogicBehaviour;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.subproject.sandbox.thread.impl.TimerBasedThread;

import java.util.Objects;
import java.util.TimerTask;

import static com.lancas.vswap.subproject.sandbox.event.SandBoxEventMgr.onServerShipTransformDirty;

public class SandBoxServerAsyncLogicThread extends TimerBasedThread<SandBoxServerWorld> {
    @Override
    protected String getTimerName() { return "server-async-logic-thread"; }

    @Override
    protected TimerTask getTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                world.allServerShips().forEach(s -> {
                    s.allAddedBehaviours()
                        .map(x -> {
                            if (x instanceof IServerAsyncLogicBehaviour<?> y)
                                return y;
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .forEach(IServerAsyncLogicBehaviour::serverAsyncLogicTick);
                });
                    /*onServerShipTransformDirty.schedule(
                        s.getUuid(),
                        new UUIDLazyParamWrapper(s.getUuid()),
                        new TransformPrimitive(s.getRigidbody().getDataReader().getTransform())//,
                        //new AABBdLazyParamWrapper(s.getLocalAABB())
                    ));
                onServerShipTransformDirty.invokeAll();*/
            }
        };
    }

    @Override
    protected int getTimerPeriodMS() {
        return 20;
    }
}
