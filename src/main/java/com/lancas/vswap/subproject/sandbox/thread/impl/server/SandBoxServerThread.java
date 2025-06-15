package com.lancas.vswap.subproject.sandbox.thread.impl.server;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.thread.api.ISandBoxThread;
//import com.lancas.vs_wap.subproject.sandbox.thread.impl.ThreadBridgeImpl;
import com.lancas.vswap.subproject.sandbox.thread.impl.ThreadScheduleExecutorImpl;
import com.lancas.vswap.subproject.sandbox.thread.schedule.experimental.IServerThreadScheduler;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class SandBoxServerThread implements ISandBoxThread<SandBoxServerWorld>, INBTSerializable<CompoundTag> {
    protected SandBoxServerWorld world;
    public SandBoxServerWorld getSandBoxWorld() { return world; }
    public ServerLevel getLevel() { return world.level; }

    //public final ThreadBridgeImpl<SandBoxServerThread> threadBridge = new ThreadBridgeImpl<>();
    protected final ThreadScheduleExecutorImpl<SandBoxServerThread> scheduleExecutor = new ThreadScheduleExecutorImpl<>();

    protected final Queue<IServerThreadScheduler> schedulers = new ConcurrentLinkedQueue<>();  //expiermenting

    public void addScheduler(@NotNull IServerThreadScheduler scheduler) {
        schedulers.add(scheduler);
    }


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

            //exp
            var schedulerIt = schedulers.iterator();
            while (schedulerIt.hasNext()) {
                boolean discard = schedulerIt.next().tick(level);
                if (discard)
                    schedulerIt.remove();
            }
            //schedulers.forEach(x -> x.tick(level));
        });
    };



    //todo remove @Override
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



    @Override
    public CompoundTag serializeNBT() {
        return new NbtBuilder()
            //.putEach("schedulers", schedulers, INBTSerializable::serializeNBT)
            .putEachSimpleJackson("schedulers", schedulers)
            .get();
    }
    @Override
    public void deserializeNBT(CompoundTag tag) {
        schedulers.clear();

        NbtBuilder.modify(tag)
            .readEachSimpleJackson("schedulers", IServerThreadScheduler.class, schedulers);
            //.readEachCompoundOverwrite("schedulers", t -> )
    }
}
