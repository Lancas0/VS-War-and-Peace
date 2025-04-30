package com.lancas.vs_wap.subproject.sandbox;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.event.impl.SingleEventSetImpl;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxClientShip;
import com.lancas.vs_wap.subproject.sandbox.thread.SandBoxThreadRegistry;
import com.lancas.vs_wap.subproject.sandbox.thread.api.ISandBoxThread;
import com.lancas.vs_wap.subproject.sandbox.thread.client.SandBoxClientPhysThread;
import com.lancas.vs_wap.subproject.sandbox.thread.client.SandBoxClientThread;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

//todo store all renderers for each levelName
//get renderers by curLevelName ?

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber
public class SandBoxClientWorld implements ISandBoxWorld {
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
        SandBoxClientPhysThread physThread = new SandBoxClientPhysThread();

        threadRegistry.register(clientThread);
        threadRegistry.register(physThread);

        clientThread.initial(this);
        physThread.initial(this);
    }

    private boolean running = false;
    private String curLevelName;  //not actually player level, the unupdated level with all current ship data
    private final Map<UUID, SandBoxClientShip> clientShips = new ConcurrentHashMap<>();
    private final Set<UUID> toDeleteShips = ConcurrentHashMap.newKeySet();
    //private final Map<UUID, ScheduleShipData> scheduleShips = new ConcurrentHashMap<>();

    public void reloadLevel(String levelName, Iterable<SandBoxClientShip> syncingClientShips/*Map<UUID, CompoundTag> savedRenders*/) {
        curLevelName = levelName;

        clientShips.clear();
        for (var syncingShip : syncingClientShips) {
            clientShips.put(syncingShip.getUuid(), syncingShip);
        }
    }

    public String getCurLevelName() { return curLevelName; }
    public void setCurLevelName(String s) { curLevelName = s; }


    @Nullable
    public SandBoxClientShip getClientShip(UUID uuid) {
        if (toDeleteShips.contains(uuid)) {
            toDeleteShips.remove(uuid);
            clientShips.remove(uuid);
            return null;
        }

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
        clientShips.putIfAbsent(ship.getUuid(), ship);
    }
    public void markShipDeleted(UUID uuid) {
        var ship = clientShips.get(uuid);
        if (ship == null) {
            EzDebug.warn("the to remove ship don't exist! uuid:" + uuid);
            return;
        }

        //todo event?
        toDeleteShips.add(uuid);
    }
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
                    return;
                }
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
    public ISandBoxShip getShip(UUID uuid) { return clientShips.get(uuid); }
    @Override
    public Stream<ISandBoxShip> allShips() { return allClientShips().map(s -> s); }
    //use stream to prevent delete operation outside
    public Stream<SandBoxClientShip> allClientShips() {
        if (!toDeleteShips.isEmpty()) {
            var toDeleteShipsIt = toDeleteShips.iterator();
            while (toDeleteShipsIt.hasNext()) {
                UUID toDelete = toDeleteShipsIt.next();
                toDeleteShipsIt.remove();
                clientShips.remove(toDelete);
            }
        }
        return clientShips.values().stream();
    }
}
