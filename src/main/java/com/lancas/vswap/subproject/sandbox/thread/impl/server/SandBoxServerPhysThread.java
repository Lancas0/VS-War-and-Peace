package com.lancas.vswap.subproject.sandbox.thread.impl.server;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.ship.IServerSandBoxShip;
import com.lancas.vswap.subproject.sandbox.thread.impl.TimerBasedThread;
import net.minecraft.client.Minecraft;

import java.util.TimerTask;

public class SandBoxServerPhysThread extends TimerBasedThread<SandBoxServerWorld> {
    private final SandBoxServerWorld world;
    public SandBoxServerPhysThread(SandBoxServerWorld inWorld) { world = inWorld; }

    @Override
    protected String getTimerName() { return "sandbox-server-phys-thread"; }
    @Override
    protected TimerTask getTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    if (pausing) return;

                    if (Minecraft.getInstance().isPaused()) {
                        return;
                    }

                    //EzDebug.light("server world phys thread running, pausing:" + pausing);
                    //if (s.isTimeOut()) return;  //deleting ship is in server thread
                    world.getConstraintSolver().solve();
                    world.allServerShips().forEach(s -> s.physTick(0.0167));
                } catch (Exception e) {
                    EzDebug.error("server phys thread failed.");
                    e.printStackTrace();
                }
            }
        };
    }
    @Override
    protected int getTimerPeriodMS() { return 16; }
}
