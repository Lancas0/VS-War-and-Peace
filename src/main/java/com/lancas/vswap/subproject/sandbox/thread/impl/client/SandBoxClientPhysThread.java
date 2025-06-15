package com.lancas.vswap.subproject.sandbox.thread.impl.client;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vswap.subproject.sandbox.ship.IClientSandBoxShip;
import com.lancas.vswap.subproject.sandbox.thread.impl.TimerBasedThread;
import com.lancas.vswap.subproject.sandbox.thread.impl.server.SandBoxServerPhysThread;
import net.minecraft.client.Minecraft;

import java.util.TimerTask;

public class SandBoxClientPhysThread extends TimerBasedThread<SandBoxClientWorld> {
    private final SandBoxClientWorld world;
    public SandBoxClientPhysThread(SandBoxClientWorld inWorld) { world = inWorld; }

    @Override
    protected String getTimerName() { return "sandbox-client-phys-thread"; }
    @Override
    protected TimerTask getTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    //todo only use Minecraft.pause
                    if (pausing) return;

                    if (Minecraft.getInstance().isPaused())
                        return;


                    //EzDebug.light("server world phys thread running");
                    //if (s.isTimeOut()) return;  //deleting ship is in server thread
                    world.getConstraintSolver().solve();
                    world.allClientShips().forEach(s -> s.physTick(0.0167));
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
