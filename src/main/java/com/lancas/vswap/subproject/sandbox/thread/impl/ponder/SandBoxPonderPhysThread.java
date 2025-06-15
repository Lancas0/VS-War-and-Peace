package com.lancas.vswap.subproject.sandbox.thread.impl.ponder;

import com.lancas.vswap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vswap.subproject.sandbox.SandBoxPonderWorld;
import com.lancas.vswap.subproject.sandbox.ship.IClientSandBoxShip;
import com.lancas.vswap.subproject.sandbox.thread.impl.TimerBasedThread;

import java.util.TimerTask;

/*
public class SandBoxPonderPhysThread extends TimerBasedThread<SandBoxPonderWorld> {
    protected SandBoxPonderWorld world;
    public SandBoxPonderPhysThread(SandBoxPonderWorld inWorld) {
        world = inWorld;
    }

    @Override
    protected String getTimerName() {
        return "sandbox-ponder-phys-thread";
    }

    @Override
    protected TimerTask getTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                //FIXME it really never stops?
                world.getConstraintSolver().solve();
                world.allShips().forEach(IClientSandBoxShip::physTick);
            }
        };
    }

    @Override
    protected int getTimerPeriodMS() {
        return 16;
    }
}
*/