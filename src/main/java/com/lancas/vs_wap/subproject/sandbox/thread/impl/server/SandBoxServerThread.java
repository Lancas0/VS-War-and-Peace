package com.lancas.vs_wap.subproject.sandbox.thread.impl.server;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vs_wap.subproject.sandbox.api.UUIDLazyParamWrapper;
import com.lancas.vs_wap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.subproject.sandbox.thread.api.ISandBoxThread;
//import com.lancas.vs_wap.subproject.sandbox.thread.impl.ThreadBridgeImpl;
import com.lancas.vs_wap.subproject.sandbox.thread.impl.ThreadScheduleExecutorImpl;
import net.minecraft.server.level.ServerLevel;

import java.util.function.Consumer;

import static com.lancas.vs_wap.subproject.sandbox.event.SandBoxEventMgr.onServerShipTransformDirty;

public class SandBoxServerThread implements ISandBoxThread<SandBoxServerWorld> {
    private SandBoxServerWorld world;
    public SandBoxServerWorld getSandBoxWorld() { return world; }
    public ServerLevel getLevel() { return world.level; }

    //public final ThreadBridgeImpl<SandBoxServerThread> threadBridge = new ThreadBridgeImpl<>();
    public final ThreadScheduleExecutorImpl<SandBoxServerThread> scheduleExecutor = new ThreadScheduleExecutorImpl<>();


    private final Consumer<ServerLevel> serverTickWork = level -> {
        world.allServerShips().forEach(s -> {
            /*if (s.tickDownTimeOut()) {
                world.markShipDeleted(s.getUuid());
                return;
            }*/
            world.getConstraintSolver().tick();
            s.serverTick(level);

            //todo move it to sync thread
            /*if (!(s instanceof SandBoxServerShip)) return;  //don't try to sync vs wrapped or ground(ground is not contained in allShipsInculdeVs, for safe)
            onServerShipTransformDirty.schedule(
                s.getUuid(),
                new UUIDLazyParamWrapper(s.getUuid()),
                new TransformPrimitive(s.getRigidbody().getDataReader().getTransform())//,
                //new AABBdLazyParamWrapper(s.getLocalAABB())
            );*/
            scheduleExecutor.doScheduleAll(this);
        });
    };



    @Override
    public void initial(SandBoxServerWorld inWorld) {
        this.world = inWorld;
    }

    @Override
    public void start() {
        if (world == null) {
            EzDebug.warn("server thread start before init");
            return;
        }

        world.serverTickSetEvent.addListener(serverTickWork);
    }
    @Override
    public void pause() {
        world.serverTickSetEvent.remove(serverTickWork);
    }
}
