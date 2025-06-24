package com.lancas.vswap.subproject.sandbox;

import com.lancas.vswap.VsWap;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.event.impl.SingleEventSetImpl;
import com.lancas.vswap.foundation.AlwaysSafeRemoveMap;
import com.lancas.vswap.subproject.sandbox.compact.mc.GroundShipWrapped;
import com.lancas.vswap.subproject.sandbox.constraint.SandBoxConstraintSolver;
import com.lancas.vswap.subproject.sandbox.event.SandBoxEventMgr;
import com.lancas.vswap.subproject.sandbox.ship.IServerSandBoxShip;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.subproject.sandbox.thread.impl.server.SandBoxServerAsyncLogicThread;
import com.lancas.vswap.subproject.sandbox.thread.impl.server.SandBoxServerPhysThread;
import com.lancas.vswap.subproject.sandbox.thread.impl.server.SandBoxServerToClientSyncThread;
import com.lancas.vswap.subproject.sandbox.thread.impl.server.SandBoxServerThread;
import com.lancas.vswap.subproject.sandbox.thread.api.ISandBoxThread;
import com.lancas.vswap.subproject.sandbox.thread.SandBoxThreadRegistry;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Mod.EventBusSubscriber
public class SandBoxServerWorld extends SavedData implements ISandBoxWorld<IServerSandBoxShip> {

    private final SandBoxThreadRegistry<SandBoxServerWorld> threadRegistry = new SandBoxThreadRegistry<>();
    public @Nullable <T extends ISandBoxThread<SandBoxServerWorld>> T getThread(Class<T> type) {
        try {
            return (T)threadRegistry.getThread(type);
        } catch (Exception e) {
            return null;
        }
    }


    //todo only update ACTIVE worlds
    //key is dimId by VSGame.dimIdOf()
    private static final Map<String, SandBoxServerWorld> allWorlds = new Hashtable<>();
    public static @Nullable SandBoxServerWorld fromDimId(String dimId) {
        SandBoxServerWorld world = allWorlds.get(dimId);
        if (world == null) {
            EzDebug.warn("fail to get sandboxServerWorld of " + dimId);
        }
        return world;
    }

    public static SandBoxServerWorld getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            tag -> {
                var world = new SandBoxServerWorld(level).load(tag);
                world.initialized.set(true);
                return world;
            },
            () -> {
                var world = new SandBoxServerWorld(level);
                world.initialized.set(true);
                return world;
            },
            VsWap.MODID + "_sandbox"
        );
    }

    private final AlwaysSafeRemoveMap<UUID, SandBoxServerShip> serverShips = new AlwaysSafeRemoveMap<>();
    private GroundShipWrapped wrappedGroundShip;

    private final SandBoxConstraintSolver constraintSolver = new SandBoxConstraintSolver(this);

    public final ServerLevel level;
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicBoolean anyClientSynced = new AtomicBoolean(false);  //todo further change it to anyClientLoading - stop ticks when no client is in the world.
    private final AtomicBoolean running = new AtomicBoolean(false);

    //todo make clientLoading as a int refer to the count that client try loading it.
    //when it's <= 0, don't run ticks.
    public void notifyClientLoading(boolean isLoading) {
        anyClientSynced.set(isLoading);
    }


    private final SandBoxServerThread serverThread;  //experimental
    private SandBoxServerWorld(ServerLevel inLevel) {
        level = inLevel;
        allWorlds.put(VSGameUtilsKt.getDimensionId(level), this);

        //put default threads
        //SandBoxServerThread serverThread = new SandBoxServerThread();
        SandBoxServerPhysThread physThread = new SandBoxServerPhysThread(this);
        SandBoxServerToClientSyncThread syncThread = new SandBoxServerToClientSyncThread(this);
        SandBoxServerAsyncLogicThread asyncLogicThread = new SandBoxServerAsyncLogicThread(this);

        serverThread = new SandBoxServerThread();
        threadRegistry.register(serverThread);
        threadRegistry.register(physThread);
        threadRegistry.register(syncThread);
        threadRegistry.register(asyncLogicThread);

        serverThread.initial(this);
        //todo serverThread.scheduleExecutor.register();
    }


    @Override
    public SandBoxConstraintSolver getConstraintSolver() { return constraintSolver; }

    @Override
    public Level getWorld() { return level; }

    @Nullable
    public IServerSandBoxShip getShip(UUID uuid) { return getServerShip(uuid); }

    @Override
    public GroundShipWrapped wrapOrGetGround() {
        if (wrappedGroundShip == null) {
            wrappedGroundShip = new GroundShipWrapped(UUID.randomUUID());
            setDirty();
        }
        return wrappedGroundShip;
    }

    @Nullable
    public SandBoxServerShip getServerShip(UUID uuid) {
        if (uuid == null) return null;
        return serverShips.get(uuid);
    }

    //use stream because stream don't provide remove operation.
    @Override
    public Stream<IServerSandBoxShip> allShips() {
        return allServerShips().map(s -> s);
    }

    @Override
    public void markAllDeleted() { serverShips.markRemoveIf((uuid, ship) -> {
        SandBoxEventMgr.onRemoveShip.invokeAll(this, ship);
        return true;
    }); }

    public Stream<SandBoxServerShip> allServerShips() {
        return serverShips.values();
    }

    public static void addShip(ServerLevel level, SandBoxServerShip ship, boolean syncToClient) {
        SandBoxServerWorld world = SandBoxServerWorld.getOrCreate(level);
        world.addShipImpl(ship, syncToClient);
    }
    public boolean containsShip(UUID uuid) {
        return serverShips.containsKey(uuid) || (wrappedGroundShip != null && wrappedGroundShip.getUuid().equals(uuid));
    }
    //todo add server only ship
    /*public static void addShipOnlySync(ServerLevel level, SandBoxServerShip ship) {
        SandBoxServerWorld world = SandBoxServerWorld.getOrCreate(level);
        world.addShipImpl(ship);
        SandBoxEventMgr.onSyncServerShipToClient.invokeAll(level, ship);
    }*/
    public void addShipImpl(@NotNull SandBoxServerShip ship, boolean syncToClient) {
        if (ship.getWorldDimId() != null) {
            EzDebug.warn("ship has been added to " + ship.getWorldDimId() + ", fail to add.");
            return;
        }
        if (serverShips.containsKey(ship.getUuid())) {
            EzDebug.warn("the serverShip with uuid:" + ship.getUuid() + " is existed, may fail to add");
        }

        //由于是并发环境，还是需要putIfAbsent
        if (serverShips.putIfAbsent(ship.getUuid(), ship) == null) {  //successfully add (putIfAbsent return null if add, or ship if fail to add)
            ship.setWorldDimId(VSGameUtilsKt.getDimensionId(level));
            if (syncToClient)
                SandBoxEventMgr.onSyncServerShipToClient.invokeAll(level, ship);
            setDirty();
        }
    }

    /*public static WrappedVsShip wrapVsShip(ServerLevel level, ServerShip vsShip) {
        SandBoxServerWorld world = getOrCreate(level);
        return world.wrapVsShipImpl(vsShip);
    }
    public WrappedVsShip wrapVsShipImpl(@NotNull ServerShip vsShip) {
        WrappedVsShip wrapShip = new WrappedVsShip(UUID.randomUUID(), vsShip.getId());
        wrappedVsShips.put(wrapShip.getUuid(), wrapShip);
        return wrapShip;
    }*/

    /*public static void scheduleShipOverwriteIfExisted(ServerLevel level, ScheduleShipData scheduleShipData) {
        SandBoxServerWorld world = SandBoxServerWorld.getOrCreate(level);
        world.scheduleShips.put(scheduleShipData.ship.getUuid(), scheduleShipData);
    }*/
    /*public static void scheduleShip(ServerLevel level, SandBoxServerShip ship, int tick, @Nullable SandBoxServerWorld.ScheduleCallback scheduleCb) {
        SandBoxServerWorld world = SandBoxServerWorld.getOrCreate(level);
        world.scheduleShips.put(ship.getUuid(), new TriTuple<>(ship, tick, scheduleCb));
        //world.serverShips.put(ship.getUuid(), ship);
        //SandBoxEventMgr.onAddNewShipInServerWorld.invokeAll(level, ship);
        world.setDirty();
    }*/

    public static void markShipDeleted(ServerLevel level, UUID shipUuid) {
        SandBoxServerWorld world = SandBoxServerWorld.getOrCreate(level);
        world.markShipDeleted(shipUuid);
    }
    @Override
    public void markShipDeleted(UUID uuid) {
        if (uuid == null) return;

        var ship = serverShips.get(uuid);
        if (ship == null) {
            EzDebug.warn("the to remove ship don't exist! uuid:" + uuid);
            return;
        }

        ship.onMarkDeleted();
        serverShips.markKeyRemoved(uuid);
        SandBoxEventMgr.onRemoveShip.invokeAll(this, ship);
        //FIXME don't remove inWorld because some behaviour have to use inWorld when deleting?
        ship.setWorldDimId(null);
    }

    private AtomicInteger lazy = new AtomicInteger(20);


    public SingleEventSetImpl<ServerLevel> serverTickSetEvent = new SingleEventSetImpl<>();
    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;  //我还不太理解这个Phase，不清楚要用哪个

        try {
            for (SandBoxServerWorld world : allWorlds.values()) {
                if (!world.initialized.get() || !world.anyClientSynced.get()) continue;
                if (world.lazy.get() > 0) {
                    EzDebug.log("lazy:" + world.lazy.decrementAndGet());
                    continue;
                }

                if (Minecraft.getInstance().isPaused()) {  //works in multiplayer?
                    boolean shouldNotifyPause = world.running.compareAndSet(true, false);  //其实只有主线程在控制，我是不是太小心了？
                    if (shouldNotifyPause) {
                        world.threadRegistry.notifyAllPause();
                        EzDebug.log("shouldNotifyPause:" + shouldNotifyPause);
                    }

                    return;
                }

                boolean shouldNotifyStart = world.running.compareAndSet(false, true);
                if (shouldNotifyStart) {  //find out if the world is not running, and set it running
                    world.threadRegistry.notifyAllStart();
                    EzDebug.log("shouldNotifyStart:" + shouldNotifyStart);
                }

                world.serverTickSetEvent.invokeAll(world.level);

                world.setDirty();  //todo should set dirty every server tick?
            }
        } catch (Exception e) {
            EzDebug.error("server tick exception:" + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        EzDebug.light("to save server world");

        //List<SandBoxServerShip> toSaveShips = serverShips.values().stream().filter(ship -> !ship.isTimeOut()).toList();
        NbtBuilder builder = new NbtBuilder()
            .putCompound("constraint_data", constraintSolver.saved())
            .putStream("ships", serverShips.values(), ship -> ship.saved(level))
            .putIfNonNull("ground_ship", wrappedGroundShip, (b, k, v) -> b.putCompound(k, v.saved()))
            .putCompound("server_thread_saved", serverThread.serializeNBT());
            //.putCompound("vs_compact_ships", vsShipsCompactor.saved());
            //.putEach("schedule_ships", scheduleShips.values(), scheduleShip -> scheduleShip.saved(level));


        return builder.get();
    }
    public SandBoxServerWorld load(CompoundTag tag) {
        try {
            NbtBuilder builder = NbtBuilder.modify(tag)
                .readCompoundDo("constraint_data", constraintSolver::load)
                .readEachCompoundDo("ships", t -> {
                    SandBoxServerShip ship = new SandBoxServerShip(level, t);
                    serverShips.put(ship.getUuid(), ship);
                });

            if (builder.contains("ground_ship")) {
                wrappedGroundShip = new GroundShipWrapped(builder.getCompound("ground_ship"));
            }

            serverThread.deserializeNBT(builder.getCompound("server_thread_saved"));
        } catch (Exception e) {
            EzDebug.error("fail to load ship.");
            e.printStackTrace();
        }

        return this;
    }
}
