package com.lancas.vs_wap.subproject.sandbox.thread.client;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vs_wap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vs_wap.subproject.sandbox.api.AABBdLazyParamWrapper;
import com.lancas.vs_wap.subproject.sandbox.api.UUIDLazyParamWrapper;
import com.lancas.vs_wap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vs_wap.subproject.sandbox.event.SandBoxEventMgr;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxClientShip;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.subproject.sandbox.thread.api.ISandBoxThread;
import com.lancas.vs_wap.subproject.sandbox.thread.api.IScheduleExecutor;
import com.lancas.vs_wap.subproject.sandbox.thread.impl.ThreadScheduleExecutorImpl;
import com.lancas.vs_wap.subproject.sandbox.thread.schedule.IScheduleData;
import com.lancas.vs_wap.subproject.sandbox.thread.schedule.IScheduler;
import com.lancas.vs_wap.subproject.sandbox.thread.server.SandBoxServerThread;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;

import java.util.function.Consumer;

import static com.lancas.vs_wap.subproject.sandbox.event.SandBoxEventMgr.onServerShipTransformDirty;

public class SandBoxClientThread implements ISandBoxThread<SandBoxClientWorld>, IScheduleExecutor<SandBoxClientThread> {
    private SandBoxClientWorld world;
    public SandBoxClientWorld getSandBoxWorld() { return world; }
    public ClientLevel getLevel() { return Minecraft.getInstance().level; }


    //public final ThreadBridgeImpl<SandBoxClientThread> threadBridge = new ThreadBridgeImpl<>();
    public final ThreadScheduleExecutorImpl<SandBoxClientThread> scheduleExecutor = new ThreadScheduleExecutorImpl<>();


    private final Consumer<ClientLevel> clientTickWork = level -> {
        world.allClientShips().forEach(s -> {
            if (s.tickDownTimeOut()) {
                world.markShipDeleted(s.getUuid());
                return;
            }

            s.clientTick(level);
        });


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
