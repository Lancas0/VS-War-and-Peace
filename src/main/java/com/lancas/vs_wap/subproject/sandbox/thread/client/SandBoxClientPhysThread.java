package com.lancas.vs_wap.subproject.sandbox.thread.client;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vs_wap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vs_wap.subproject.sandbox.thread.impl.TimerBasedThread;

import java.util.TimerTask;

public class SandBoxClientPhysThread extends TimerBasedThread<SandBoxClientWorld> {

    @Override
    protected String getTimerName() { return "sandbox-client-phys-thread"; }
    @Override
    protected TimerTask getTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    if (pausing) return;

                    //EzDebug.light("server world phys thread running");
                    world.allClientShips().forEach(s -> {
                        if (s.isTimeOut()) return;  //deleting ship is in server thread
                        s.physTick();
                    });
                } catch (Exception e) {
                    EzDebug.error("client phys thread failed.");
                    e.printStackTrace();
                }
            }
        };
    }
    @Override
    protected int getTimerPeriodMS() { return 16; }
}
