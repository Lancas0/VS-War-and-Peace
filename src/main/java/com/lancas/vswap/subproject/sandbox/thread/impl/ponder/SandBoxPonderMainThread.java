package com.lancas.vswap.subproject.sandbox.thread.impl.ponder;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.mixinfriend.HookedPonderScene;
import com.lancas.vswap.subproject.sandbox.SandBoxPonderWorld;
import com.lancas.vswap.subproject.sandbox.thread.api.ISandBoxThread;
import com.simibubi.create.foundation.ponder.PonderWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

import java.util.function.Consumer;

public class SandBoxPonderMainThread implements ISandBoxThread<SandBoxPonderWorld>/*, IScheduleExecutor<SandBoxPonderMainThread>*/ {
    protected static final double physDt = 0.05;
    protected volatile double physTimeScale = 1;
    public void setPhysTimeScale(double scale) { physTimeScale = scale; }

    private SandBoxPonderWorld world = null;
    public SandBoxPonderMainThread(SandBoxPonderWorld inWorld) {
        world = inWorld;
    }

    private final Consumer<PonderWorld> ponderTickWork = level -> {
        ClientLevel mcLevel = Minecraft.getInstance().level;
        if (world == null || mcLevel == null)
            return;

        world.getConstraintSolver().tick();
        world.getConstraintSolver().solve();

        world.allShips().forEach(s -> s.clientTick(mcLevel));
        world.allShips().forEach(s -> s.physTick(physDt * physTimeScale));
    };

    @Override
    public void start() {
        if (!(world.getWorld().scene instanceof HookedPonderScene hookedScene)) {
            EzDebug.warn("hookedScene is null");
            return;
        }

        hookedScene.setSandBoxTicker(ponderTickWork);
    }

    @Override
    public void pause() {
        if (!(world.getWorld().scene instanceof HookedPonderScene hookedScene)) {
            EzDebug.warn("hookedScene is null");
            return;
        }
        hookedScene.setSandBoxTicker(null);
    }
}
