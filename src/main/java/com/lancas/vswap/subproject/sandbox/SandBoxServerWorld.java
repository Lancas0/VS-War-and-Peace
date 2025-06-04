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

    /*private static Thread physicsThread = new Thread(SandBoxServerWorld::physTick, ModMain.MODID + "SandBox-Physics-Thread");
    //private static final long UPDATE_INTERVAL_NS = 16_666_666; // ≈16.67ms (60Hz)
    public static final AtomicInteger shouldServerTickCntDown = new AtomicInteger(0);
    //public static final boolean DO_PHYS_IN_SERVER_THREAD = false;
    private static double lastMicroS = 0;

    public static final long SERVER_TICK_INTERVAL_MS = 50;
    public static final double SERVER_TICK_TIME_S = 0.05;*/
    private final SandBoxThreadRegistry<SandBoxServerWorld> threadRegistry = new SandBoxThreadRegistry<>();
    public void registerThread(ISandBoxThread<SandBoxServerWorld> thread) {
        threadRegistry.register(thread);
        thread.initial(this);
    }


    public static final long PHYS_TICK_INTERVAL_MS = 16;
    public static final double PHYS_TICK_TIME_S = 0.016;


    static {
    /*    Timer physTimer = new Timer(ModMain.MODID + "-server-sandbox-phys-tick", true);
        physTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (Minecraft.getInstance().isPaused()) return;  //works in multiplayer?
                    /.*double currentMs = sw.getTime(TimeUnit.MICROSECONDS);
                    //EzDebug.log("thread consume time:" + (currentMs - lastMicroS));
                    lastMicroS = currentMs;

                    //EzDebug.log("phys ticking");
                    if (shouldServerTickCntDown.get() <= 0) {
                        shouldServerTickCntDown.set(2);
                        serverTick();
                    } else {
                        shouldServerTickCntDown.decrementAndGet();
                    }*./

                    //EzDebug.light("server world phys thread running");

                    physTick();
                } catch (Exception e) {
                    EzDebug.error("phys thread failed.");
                    e.printStackTrace();
                }
            }
        }, 0, PHYS_TICK_INTERVAL_MS);*/
        /*
        Timer syncTimer = new Timer(ModMain.MODID + "-server-sync-tick", true);
        syncTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                onServerShipTransformDirty.invokeAll();
            }
        }, 0, 20);
        */
        /*Timer serverThreadTimer = new Timer(ModMain.MODID + "_sandbox_server_tick", true);
        serverThreadTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (Minecraft.getInstance().isPaused()) return;  //works in multiplayer?
                //EzDebug.log("server ticking");
                serverTick();
            }
        }, 0, SERVER_TICK_INTERVAL_MS);*/
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
    //private final Map<UUID, SandBoxServerShip> serverShips = new ConcurrentHashMap<>();
    //private final VsShipsCompactor vsShipsCompactor = new VsShipsCompactor();
    private GroundShipWrapped wrappedGroundShip;

    //LazyDelete,将会在获取船或者遍历船的时候删除
    //private final Set<UUID> toDeleteShips = ConcurrentHashMap.newKeySet();
    //private final Map<UUID, ScheduleShipData> scheduleShips = new ConcurrentHashMap<>();  //map use uuid key avoid add ships with same key
    private final SandBoxConstraintSolver constraintSolver = new SandBoxConstraintSolver(this);

    public final ServerLevel level;
    //private final AtomicBoolean initialized = new AtomicBoolean(false);  //make sure ship are fully loaded before tick event
    //private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicBoolean anyClientSynced = new AtomicBoolean(false);  //todo further change it to anyClientLoading - stop ticks when no client is in the world.
    private final AtomicBoolean running = new AtomicBoolean(false);

    //todo make clientLoading as a int refer to the count that client try loading it.
    //when it's <= 0, don't run ticks.
    public void notifyClientLoading(boolean isLoading) {
        anyClientSynced.set(isLoading);
    }

    private SandBoxServerWorld(ServerLevel inLevel) {
        level = inLevel;
        allWorlds.put(VSGameUtilsKt.getDimensionId(level), this);

        //put default threads
        SandBoxServerThread serverThread = new SandBoxServerThread();
        SandBoxServerPhysThread physThread = new SandBoxServerPhysThread();
        SandBoxServerToClientSyncThread syncThread = new SandBoxServerToClientSyncThread();
        SandBoxServerAsyncLogicThread asyncLogicThread = new SandBoxServerAsyncLogicThread();

        threadRegistry.register(serverThread);
        threadRegistry.register(physThread);
        threadRegistry.register(syncThread);
        threadRegistry.register(asyncLogicThread);

        serverThread.initial(this);
        physThread.initial(this);
        syncThread.initial(this);
        asyncLogicThread.initial(this);
        //todo serverThread.scheduleExecutor.register();
    }


    @Override
    public SandBoxConstraintSolver getConstraintSolver() { return constraintSolver; }

    @Override
    public Level getMcLevel() { return level; }

    @Nullable
    public IServerSandBoxShip getShip(UUID uuid) { return getServerShip(uuid); }
    /*@Override
    public IServerSandBoxShip getShipIncludeVS(UUID uuid) {
        IServerSandBoxShip ship = serverShips.get(uuid);
        return ship == null ? vsShipsCompactor.getWrappedVsShip(uuid) : ship;
    }
    @Override
    public IServerSandBoxShip getShipIncludeVSAndGround(UUID uuid) {
        IServerSandBoxShip ship = getShipIncludeVS(uuid);
        if (ship != null) return ship;
        return wrapOrGetGround().getUuid().equals(uuid) ? wrappedGroundShip : null;
    }

    @Override
    public WrappedVsShip wrapOrGetVs(Ship vsShip) {
        //todo setDirty();
        return vsShipsCompactor.wrapOrGet(vsShip);
    }*/
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
        /*if (toDeleteShips.contains(uuid)) {
            serverShips.remove(uuid);
            toDeleteShips.remove(uuid);
            return null;
        }*/
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

    /*@Override
    public Stream<IServerSandBoxShip> allShipsIncludeVs() {
        Stream<IServerSandBoxShip> sandBoxShips = allShips();
        return Stream.concat(sandBoxShips, vsShipsCompactor.allWrapped());
    }*/
    public Stream<SandBoxServerShip> allServerShips() {
        /*if (!toDeleteShips.isEmpty()) {
            var toDeleteShipsIt = toDeleteShips.iterator();
            while (toDeleteShipsIt.hasNext()) {
                UUID toDeleteUuid = toDeleteShipsIt.next();
                toDeleteShipsIt.remove();
                serverShips.remove(toDeleteUuid);
                vsShipsCompactor.remove(toDeleteUuid);
            }
        }*/
        return serverShips.values();
    }
    /*public Map<UUID, CompoundTag> getSavedRenderers() {
        Hashtable<UUID, CompoundTag> allSavedRenderers = new Hashtable<>();

        serverShips.entrySet().stream().map(entry ->
            new BiTuple<>(entry.getKey(), entry.getValue().createRenderer().saved())
        ).forEach(
            tuple -> allSavedRenderers.put(tuple.getFirst(), tuple.getSecond())
        );

        return allSavedRenderers;
    }*/
    /*public Map<UUID, CompoundTag> getAllSaved() {
        var allSaved = serverShips.entrySet().stream().map(entry ->
            new AbstractMap.SimpleEntry<UUID, CompoundTag>(entry.getKey(), SerializeUtil.serializeShip(entry.getValue()))
        ).toList();
        Hashtable<UUID, CompoundTag> allSavedMap = new Hashtable<>();
        for (var saved : allSaved) {
            allSavedMap.put(saved.getKey(), saved.getValue());
        }
        return allSavedMap;
    }*/

    //todo server ship only createable in SandBoxServerWorld, want to create need a ServerShipCreateData or something
    //todo schedule add ship, and add ship only initialized
    /*public static void addShipAndSyncClient(ServerLevel level, SandBoxServerShip ship) {
        SandBoxServerWorld world = SandBoxServerWorld.getOrCreate(level);
        world.addShipImpl(ship);
        SandBoxEventMgr.onSyncServerShipToClient.invokeAll(level, ship);
    }*/
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
        /*SandBoxServerShip removedShip = serverShips.remove(shipUuid);
        if (removedShip != null) {
            SandBoxEventMgr.onRemoveShipFromServerWorld.invokeAll(level, removedShip);
            setDirty();
            return true;
        }

        return false;*/
        /*var toRemoveSandBoxShip = serverShips.get(shipUuid);
        if (toRemoveSandBoxShip != null) {
            toDeleteShips.add(shipUuid);
            SandBoxEventMgr.onRemoveShip.invokeAll(this, toRemoveSandBoxShip);
            return;
        }

        IServerSandBoxShip toRemoveVsShip = vsShipsCompactor.getWrappedVsShip(shipUuid);
        if (toRemoveVsShip != null) {
            toDeleteShips.add(shipUuid);
            SandBoxEventMgr.onRemoveShip.invokeAll(this, toRemoveVsShip);  //should i invoke the event?
            return;
        }*/
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
    /*public static void removeAllShip(ServerLevel level) {
        SandBoxServerWorld world = SandBoxServerWorld.getOrCreate(level);
        for (UUID key : world.serverShips.keySet()) {
            world.markShipDeletedImpl(key);
        }
    }*/


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
                //world.serverTickLoadedShip();
                //world.serverTickScheduleShip();

                world.setDirty();  //todo should set dirty every server tick?
            }
            /*if (DO_PHYS_IN_SERVER_THREAD) {
                physTick();
            }*/
        } catch (Exception e) {
            EzDebug.error("server tick exception:" + e.toString());
            e.printStackTrace();
        }
    }
    //todo pool
    /*private static void serverTick() {
        try {
            for (SandBoxServerWorld world : allWorlds.values()) {
                if (!world.initialized.get() || !world.anyClientSynced.get()) continue;
                if (world.lazy.get() > 0) {
                    EzDebug.log("lazy:" + world.lazy.decrementAndGet());
                    continue;
                }

                //world.serverTickLoadedShip();
                world.serverTickScheduleShip();

                world.setDirty();  //todo should set dirty every server tick?
            }
            /.*if (DO_PHYS_IN_SERVER_THREAD) {
                physTick();
            }*./
        } catch (Exception e) {
            EzDebug.error("server tick exception:" + e.toString());
            e.printStackTrace();
        }
    }*/
    /*private void serverTickLoadedShip() {
        var shipsIt = serverShips.values().iterator();
        while (shipsIt.hasNext()) {
            SandBoxServerShip ship = shipsIt.next();
            if (ship.tickDownTimeOut()) {
                //EzDebug.light("time out a ship");

                SandBoxEventMgr.onRemoveShipFromServerWorld.invokeAll(level, ship);
                shipsIt.remove();  //hopefully ConcurrentMap will successfully handle this
                continue;
            }

            ship.serverTick(level);

            onServerShipTransformDirty.schedule(
                ship.getUuid(),
                new UUIDLazyParamWrapper(ship.getUuid()),
                new TransformPrimitive(ship.getRigidbody().getDataReader().getTransform()),
                new AABBdLazyParamWrapper(ship.getLocalAABB())
            );
        }
    }*/
    /*private void serverTickScheduleShip() {
        var scheduleShipIt = scheduleShips.values().iterator();
        while (scheduleShipIt.hasNext()) {
            var curSchedule = scheduleShipIt.next();
            if (!(curSchedule.ship instanceof SandBoxServerShip serverShip)) {
                EzDebug.warn("try schedule a client ship in client is illegal!");
                scheduleShipIt.remove();
                continue;
            }

            curSchedule.scheduleTick(level);

            if (curSchedule.isCanceled()) {
                scheduleShipIt.remove();
                continue;
            }
            if (curSchedule.shouldSpawn()) {
                scheduleShipIt.remove();
                addShipImpl(serverShip);
                continue;
            }
        }
    }*/
    /*private void scheduleShips() {
        var schedulingIt = scheduleShips.values().iterator();
        while (schedulingIt.hasNext()) {
            var scheduling = schedulingIt.next();
            SandBoxServerShip ship = scheduling.getFirst();
            Integer remainTick = scheduling.getSecond();
            @Nullable var callback = scheduling.getThird();

            if (remainTick < 0) {  //remainTick = 0也会进行一次callback, < 0才加入world
                EzDebug.highlight("add a scheduled ship!");
                addShipImpl(ship);
                schedulingIt.remove();
                continue;
            }

            if (callback != null)
                callback.onScheduling(level, ship, remainTick);

            scheduling.setSecond(remainTick - 1);
        }
    }*/
    /*
    private static void physTick() {
        if (Minecraft.getInstance().isPaused())  //can it work in multiplayer?
            return;

        // 物理帧行为
        for (SandBoxServerWorld world : allWorlds.values()) {
            if (!world.initialized.get() || !world.anyClientSynced.get()) continue;
            if (world.lazy.get() > 0) {
                EzDebug.log("lazy:" + world.lazy.decrementAndGet());
                continue;
            }

            try {
                for (SandBoxServerShip ship : world.serverShips.values()) {
                    if (ship.isTimeOut()) continue;

                    ship.physTick();

                    //NetworkHandler.sendToAllPlayers(new UpdateShipTransformPacketS2C(ship.getUuid(), transformData));
                    //todo temp: i just want to see if it's smooth
                    //SandBoxEventMgr.onServerShipTransformDirty.invokeAll();
                }
            } catch (Exception e) {
                EzDebug.error("server tick failed.");
                e.printStackTrace();
            }
        }
    }*/

    @Override
    public CompoundTag save(CompoundTag tag) {
        EzDebug.light("to save server world");

        //List<SandBoxServerShip> toSaveShips = serverShips.values().stream().filter(ship -> !ship.isTimeOut()).toList();
        NbtBuilder builder = new NbtBuilder()
            .putCompound("constraint_data", constraintSolver.saved())
            .putStream("ships", serverShips.values(), ship -> ship.saved(level));
            //.putCompound("vs_compact_ships", vsShipsCompactor.saved());
            //.putEach("schedule_ships", scheduleShips.values(), scheduleShip -> scheduleShip.saved(level));

        if (wrappedGroundShip != null) {
            builder.putCompound("ground_ship", wrappedGroundShip.saved());
        }

        return builder.get();
    }
    public SandBoxServerWorld load(CompoundTag tag) {
        //List<SandBoxServerShip> loadedShips = new ArrayList<>();
        //List<ScheduleShipData> schedulingShips = new ArrayList<>();
        try {
            NbtBuilder builder = NbtBuilder.modify(tag)
                .readCompoundDo("constraint_data", constraintSolver::load)
                .readEachCompoundDo("ships", t -> {
                    SandBoxServerShip ship = new SandBoxServerShip(level, t);
                    serverShips.put(ship.getUuid(), ship);
                });
                //.readCompoundDo("vs_compact_ships", vsShipsCompactor::load);
                //.readEachCompound("schedule_ships", t -> ScheduleShipData.getServerBySavedData(level, t), schedulingShips);

            if (builder.contains("ground_ship"))
                wrappedGroundShip = new GroundShipWrapped(builder.getCompound("ground_ship"));

                /*.readEachCompound("schedule_ships", t -> {
                    NbtBuilder tReader = NbtBuilder.modify(t);
                    ScheduleCallback scheduleCb = null;

                    if (tReader.contains("schedule_callback")) {
                        scheduleCb = SerializeUtil.safeDeserialize(tReader.getBytes("scheduling_callback"));
                        if (scheduleCb == null) {
                            EzDebug.warn("fail to read schedule callback");
                        }
                    }

                    SandBoxServerShip ship = new SandBoxServerShip(tReader.getCompound("saved_ship"));
                    int remainTick = tReader.getInt("remain_tick");

                    return new TriTuple<>(ship, remainTick, scheduleCb);
                }, schedulingShips);*/
        } catch (Exception e) {
            EzDebug.error("fail to load ship.");
            e.printStackTrace();
        }

        /*serverShips.clear();
        for (var ship : loadedShips)
            serverShips.put(ship.getUuid(), ship);

        /*scheduleShips.clear();
        for (var scheduleShip : schedulingShips)
            scheduleShips.put(scheduleShip.ship.getUuid(), scheduleShip);*/

        return this;
    }
}
