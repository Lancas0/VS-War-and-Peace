package com.lancas.vswap.subproject.sandbox;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.event.impl.SingleEventSetImpl;
import com.lancas.vswap.foundation.AlwaysSafeRemoveMap;
import com.lancas.vswap.subproject.sandbox.compact.mc.GroundShipWrapped;
import com.lancas.vswap.subproject.sandbox.constraint.SandBoxConstraintSolver;
import com.lancas.vswap.subproject.sandbox.event.SandBoxEventMgr;
import com.lancas.vswap.subproject.sandbox.ship.IClientSandBoxShip;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxClientShip;
import com.lancas.vswap.subproject.sandbox.thread.SandBoxThreadRegistry;
import com.lancas.vswap.subproject.sandbox.thread.api.ISandBoxThread;
import com.lancas.vswap.subproject.sandbox.thread.impl.client.SandBoxClientPhysThread;
import com.lancas.vswap.subproject.sandbox.thread.impl.client.SandBoxClientThread;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

//todo store all renderers for each levelName
//get renderers by curLevelName ?

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber
public class SandBoxClientWorld implements ISandBoxWorld<IClientSandBoxShip> {
    //todo return null when in server
    public static SandBoxClientWorld INSTANCE = new SandBoxClientWorld();
    private final SandBoxThreadRegistry<SandBoxClientWorld> threadRegistry = new SandBoxThreadRegistry<>();
    @Nullable
    public ISandBoxThread<SandBoxClientWorld> getThread(Class<?> type) { return threadRegistry.getThread(type); }
    @Nullable
    public <T extends ISandBoxThread<SandBoxClientWorld>> T getSpecificThread(Class<T> type) {
        try {
            return (T)threadRegistry.getThread(type);
        } catch (Exception e) {
            return null;
        }
    }

    private SandBoxClientWorld() {
        SandBoxClientThread clientThread = new SandBoxClientThread();
        SandBoxClientPhysThread physThread = new SandBoxClientPhysThread(this);

        threadRegistry.register(clientThread);
        threadRegistry.register(physThread);

        clientThread.initial(this);
        //physThread.initial(this);
    }

    private boolean running = false;
    private String curLevelName;  //not actually player level, the unupdated level with all current ship data

    private final AlwaysSafeRemoveMap<UUID, SandBoxClientShip> clientShips = new AlwaysSafeRemoveMap<>();

    //private final Map<UUID, SandBoxClientShip> clientShips = new ConcurrentHashMap<>();
    //private final Map<UUID, WrappedVsShip> wrappedVsShips = new ConcurrentHashMap<>();
    //private final VsShipsCompactor vsShipsCompactor = new VsShipsCompactor();
    private GroundShipWrapped wrappedGroundShip;
    //private final Set<UUID> toDeleteShips = ConcurrentHashMap.newKeySet();

    private final SandBoxConstraintSolver constraintSolver = new SandBoxConstraintSolver(this);
    //private final Map<UUID, ScheduleShipData> scheduleShips = new ConcurrentHashMap<>();

    public void reloadLevel(String levelName, Iterable<SandBoxClientShip> syncingClientShips, UUID wrappedGroundShipUuid/*Map<UUID, CompoundTag> savedRenders*/) {
        curLevelName = levelName;

        //FIXME the client level is not unload when server exit
        //vsShipsCompactor.clear();
        wrappedGroundShip = new GroundShipWrapped(wrappedGroundShipUuid);

        clientShips.clear();
        for (var syncingShip : syncingClientShips) {
            clientShips.put(syncingShip.getUuid(), syncingShip);
        }
    }

    public String getCurLevelName() { return curLevelName; }
    public void setCurLevelName(String s) { curLevelName = s; }

    @Override
    public SandBoxConstraintSolver getConstraintSolver() { return constraintSolver; }


    @Nullable
    public SandBoxClientShip getClientShip(UUID uuid) {
        /*if (toDeleteShips.contains(uuid)) {
            toDeleteShips.remove(uuid);
            clientShips.remove(uuid);
            return null;
        }*/

        return clientShips.get(uuid);
    }

    //used by network : sync ship from server
    //used by other: create a client only ship
    public void addClientShip(SandBoxClientShip ship) {
        //EzDebug.log("add renderer: " + renderer.uuid + " had key:" + renderers.containsKey(renderer.uuid));
        if (clientShips.containsKey(ship.getUuid())) {
            EzDebug.warn("the clientShip with uuid:" + ship.getUuid() + " is existed, may fail to add");
        }

        //由于是并发环境，还是需要putIfAbsent
        //clientShips.putIfAbsent(ship.getUuid(), ship);
        if (clientShips.putIfAbsent(ship.getUuid(), ship) == null) {  //if successfully add
            ship.inWorld = true;
        }
    }
    @Override
    public void markShipDeleted(UUID uuid) {
        var ship = clientShips.get(uuid);
        if (ship == null) {
            EzDebug.warn("the to remove ship don't exist! uuid:" + uuid);
            return;
        }

        clientShips.markKeyRemoved(uuid);
        ship.onMarkDeleted();
        SandBoxEventMgr.onRemoveShip.invokeAll(this, ship);
        ship.inWorld = false;
    }

    /*public WrappedVsShip wrapVsShip(@NotNull ServerShip vsShip) {
        WrappedVsShip wrapShip = new WrappedVsShip(UUID.randomUUID(), vsShip.getId());
        wrappedVsShips.put(wrapShip.getUuid(), wrapShip);
        return wrapShip;
    }*/
    //should only be used for network sync
    /*public void removeClientShip(UUID uuid) {
        clientShips.remove(uuid);
    }*/

    /*public void scheduleShipOverwriteIfExisted(ScheduleShipData scheduleShip) {
        scheduleShips.put(scheduleShip.ship.getUuid(), scheduleShip);
    }*/


    public SingleEventSetImpl<ClientLevel> clientTickSetEvent = new SingleEventSetImpl<>();
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        SandBoxClientWorld world = INSTANCE;
        ClientLevel level = Minecraft.getInstance().level;
        try {
            if (Minecraft.getInstance().isPaused()) {
                if (world.running) {
                    world.running = false;
                    world.threadRegistry.notifyAllPause();
                }
                return;
            }

            if (!world.running) {
                world.running = true;
                world.threadRegistry.notifyAllStart();
            }

            world.clientTickSetEvent.invokeAll(level);
            //world.serverTickLoadedShip();
            //world.serverTickScheduleShip();
            /*if (DO_PHYS_IN_SERVER_THREAD) {
                physTick();
            }*/
        } catch (Exception e) {
            EzDebug.error("server tick exception:" + e.toString());
            e.printStackTrace();
        }


        /*if (event.phase != TickEvent.Phase.END) return;  //我还不太理解这个Phase，不清楚要用哪个


        if (level == null) return;

        SandBoxClientWorld clientWorld = INSTANCE;

        var shipsIt = clientWorld.clientShips.values().iterator();
        while (shipsIt.hasNext()) {
            SandBoxClientShip ship = shipsIt.next();
            if (ship.tickDownTimeOut()) {
                shipsIt.remove();  //hopefully ConcurrentMap will successfully handle this
                continue;
            }

            ship.clientTick(level);
        }

        //SandBoxClientWorld.INSTANCE.clientShips.values().forEach(s -> s.clientTick(level));
        SandBoxClientWorld.INSTANCE.clientTickScheduleShip();*/
    }
    /*private void clientTickScheduleShip() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        var scheduleShipIt = scheduleShips.values().iterator();
        while (scheduleShipIt.hasNext()) {
            var curSchedule = scheduleShipIt.next();
            if (!(curSchedule.ship instanceof SandBoxClientShip clientShip)) {
                EzDebug.warn("try schedule a server ship in client is illegal!");
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
                addClientShip(clientShip);
                EzDebug.log("spawn client only ship");
            }
        }
    }*/


    /*static {
        Timer physTimer = new Timer(ModMain.MODID + "-client-sandbox-phys-tick", true);
        physTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (Minecraft.getInstance().isPaused()) return;  //works in multiplayer?
                    /*double currentMs = sw.getTime(TimeUnit.MICROSECONDS);
                    //EzDebug.log("thread consume time:" + (currentMs - lastMicroS));
                    lastMicroS = currentMs;

                    //EzDebug.log("phys ticking");
                    if (shouldServerTickCntDown.get() <= 0) {
                        shouldServerTickCntDown.set(2);
                        serverTick();
                    } else {
                        shouldServerTickCntDown.decrementAndGet();
                    }*./
                    SandBoxClientWorld.INSTANCE.clientShips.values().forEach(SandBoxClientShip::physTick);

                } catch (Exception e) {
                    EzDebug.error("phys thread failed.");
                    e.printStackTrace();
                }
            }
        }, 0, 16);
    }*/


    @Override
    public Level getWorld() { return Minecraft.getInstance().level; }

    @Override
    public IClientSandBoxShip getShip(UUID uuid) {
        /*if (!toDeleteShips.isEmpty()) {
            var toDeleteShipsIt = toDeleteShips.iterator();
            while (toDeleteShipsIt.hasNext()) {
                UUID toDelete = toDeleteShipsIt.next();
                toDeleteShipsIt.remove();
                clientShips.remove(toDelete);
                vsShipsCompactor.remove(toDelete);
            }
        }*/
        return clientShips.get(uuid);
    }
    /*@Override
    public IClientSandBoxShip getShipIncludeVS(UUID uuid) {
        IClientSandBoxShip ship = clientShips.get(uuid);
        return ship == null ? vsShipsCompactor.getWrappedVsShip(uuid) : ship;
    }
    @Override
    public IClientSandBoxShip getShipIncludeVSAndGround(UUID uuid) {
        IClientSandBoxShip ship = getShipIncludeVS(uuid);
        if (ship != null) return ship;
        return wrapOrGetGround().getUuid().equals(uuid) ? wrappedGroundShip : null;
    }

    @Override
    public WrappedVsShip wrapOrGetVs(Ship vsShip) {
        return vsShipsCompactor.wrapOrGet(vsShip);
    }*/
    @Override
    public GroundShipWrapped wrapOrGetGround() {
        return wrappedGroundShip;
    }

    @Override
    public Stream<IClientSandBoxShip> allShips() { return allClientShips().map(s -> s); }

    /*@Override
    public Stream<IClientSandBoxShip> allShipsIncludeVs() {
        Stream<IClientSandBoxShip> sandBoxShips = allShips();
        return Stream.concat(sandBoxShips, vsShipsCompactor.allWrapped());
    }*/

    @Override
    public void markAllDeleted() {
        //clientShips.markRemoveIf((k, v) -> true);
        clientShips.markRemoveIf((uuid, ship) -> {
            SandBoxEventMgr.onRemoveShip.invokeAll(this, ship);
            return true;
        });
    }

    //use stream to prevent delete operation outside
    public Stream<SandBoxClientShip> allClientShips() {
        /*if (!toDeleteShips.isEmpty()) {
            var toDeleteShipsIt = toDeleteShips.iterator();
            while (toDeleteShipsIt.hasNext()) {
                UUID toDelete = toDeleteShipsIt.next();
                toDeleteShipsIt.remove();
                clientShips.remove(toDelete);
                vsShipsCompactor.remove(toDelete);
            }
        }*/
        return clientShips.values();
    }
}
