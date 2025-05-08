package com.lancas.vs_wap.subproject.sandbox.thread.impl.server;

import com.lancas.vs_wap.subproject.sandbox.SandBoxServerWorld;
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
                onServerShipTransformDirty.invokeAll();
            }
        };
    }
    @Override
    protected int getTimerPeriodMS() { return 20; }
}
