package com.lancas.vs_wap.subproject.sandbox.thread.impl.client;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vs_wap.subproject.sandbox.thread.api.ISandBoxThread;
import com.lancas.vs_wap.subproject.sandbox.thread.api.IScheduleExecutor;
import com.lancas.vs_wap.subproject.sandbox.thread.impl.ThreadScheduleExecutorImpl;
import com.lancas.vs_wap.subproject.sandbox.thread.schedule.IScheduleData;
import com.lancas.vs_wap.subproject.sandbox.thread.schedule.IScheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

import java.util.function.Consumer;

public class SandBoxClientThread implements ISandBoxThread<SandBoxClientWorld>, IScheduleExecutor<SandBoxClientThread> {
    private SandBoxClientWorld world;
    public SandBoxClientWorld getSandBoxWorld() { return world; }
    public ClientLevel getLevel() { return Minecraft.getInstance().level; }


    //public final ThreadBridgeImpl<SandBoxClientThread> threadBridge = new ThreadBridgeImpl<>();
    public final ThreadScheduleExecutorImpl<SandBoxClientThread> scheduleExecutor = new ThreadScheduleExecutorImpl<>();


    private final Consumer<ClientLevel> clientTickWork = level -> {
        world.getConstraintSolver().tick();
        world.allShipsIncludeVs().forEach(s -> s.clientTick(level));
        scheduleExecutor.doScheduleAll(this);
        //var shipsIt = world.allShips().iterator();
        //while (shipsIt.hasNext()) {
        //    SandBoxClientShip ship = (SandBoxClientShip)shipsIt.next();
            /*onServerShipTransformDirty.schedule(
                ship.getUuid(),
                new UUIDLazyParamWrapper(ship.getUuid()),
                new TransformPrimitive(ship.getRigidbody().getDataReader().getTransform()),
                new AABBdLazyParamWrapper(ship.getLocalAABB())
            );*/
            //threadBridge.doAllTasks(this);
        //}
    };



    @Override
    public void initial(SandBoxClientWorld inWorld) {
        this.world = inWorld;
    }

    @Override
    public void start() {
        if (world == null) {
            EzDebug.warn("server thread start before init");
            return;
        }

        world.clientTickSetEvent.addListener(clientTickWork);
    }
    @Override
    public void pause() {
        world.clientTickSetEvent.remove(clientTickWork);
    }

    @Override
    public void register(IScheduler<SandBoxClientThread, ? extends IScheduleData> scheduler) { scheduleExecutor.register(scheduler); }
    @Override
    public void schedule(IScheduleData schedulerData) { scheduleExecutor.schedule(schedulerData); }
}
