package com.lancas.vs_wap.subproject.sandbox;

import com.lancas.vs_wap.ModMain;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.IComponentBehaviour;
import com.lancas.vs_wap.subproject.sandbox.event.SandBoxEventMgr;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.subproject.sandbox.util.SerializeUtil;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber
public class SandBoxServerWorld extends SavedData implements ISandBoxWorld {
    //private static Thread physicsThread = new Thread(SandBoxServerWorld::physTick, ModMain.MODID + "SandBox-Physics-Thread");
    //private static final long UPDATE_INTERVAL_NS = 16_666_666; // ≈16.67ms (60Hz)
    public static final long PHYS_TICK_INTERVAL_MS = 16;
    public static final double PHYS_TICK_TIME_S = 0.016;
    static {
        Timer timer = new Timer(ModMain.MODID + "-SandBox-PhysTick", true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    //EzDebug.log("phys ticking");
                    physTick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, PHYS_TICK_INTERVAL_MS);
    }

    //todo only update ACTIVE worlds
    private static final Map<String, SandBoxServerWorld> allWorlds = new Hashtable<>();

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

            ModMain.MODID + "_sandbox"
        );
    }

    private final Map<UUID, SandBoxServerShip> serverShips = new ConcurrentHashMap<>();
    private final ServerLevel level;
    private final AtomicBoolean initialized = new AtomicBoolean(false);  //make sure ship are fully loaded before tick event
    //private final AtomicBoolean started = new AtomicBoolean(false);
    private SandBoxServerWorld(ServerLevel inLevel) {
        level = inLevel;
        allWorlds.put(VSGameUtilsKt.getDimensionId(level), this);
    }

    @Nullable
    public ISandBoxShip getShip(UUID uuid) { return serverShips.get(uuid); }
    @Nullable
    public SandBoxServerShip getServerShip(UUID uuid) { return serverShips.get(uuid); }

    public Map<UUID, CompoundTag> getSavedRenderers() {
        Hashtable<UUID, CompoundTag> allSavedRenderers = new Hashtable<>();

        serverShips.entrySet().stream().map(entry ->
            new BiTuple<>(entry.getKey(), entry.getValue().createRenderer().saved())
        ).forEach(
            tuple -> allSavedRenderers.put(tuple.getFirst(), tuple.getSecond())
        );

        return allSavedRenderers;
    }
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
    public static void addShip(ServerLevel level, SandBoxServerShip ship) {
        EzDebug.log("adding ship uuid:" + ship.getUuid());

        SandBoxServerWorld world = SandBoxServerWorld.getOrCreate(level);
        world.serverShips.put(ship.getUuid(), ship);

        SandBoxEventMgr.onAddNewShipInServerWorld.invokeAll(level, ship);
        world.setDirty();

        EzDebug.log("added ship:" + ship);
    }
    public static boolean deleteShip(ServerLevel level, UUID shipUuid) {
        SandBoxServerWorld world = SandBoxServerWorld.getOrCreate(level);

        SandBoxServerShip removedShip = world.serverShips.remove(shipUuid);
        if (removedShip != null) {
            SandBoxEventMgr.onRemoveShipFromServerWorld.invokeAll(level, removedShip);
            world.setDirty();
            return true;
        }

        return false;
    }


    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (SandBoxServerWorld world : allWorlds.values()) {
            if (!world.initialized.get()) continue;

            var shipsIt = world.serverShips.values().iterator();
            while (shipsIt.hasNext()) {
                SandBoxServerShip ship = shipsIt.next();
                if (ship.isDestroyMarked()) {
                    shipsIt.remove();  //hopefully ConcurrentMap will successfully handle this
                    continue;
                }

                ship.getRigidbody().serverTick(world.level);
                ship.getAllBehaviours().forEach(beh -> beh.serverTick(world.level));
            }

            world.setDirty();  //todo should set dirty every server tick?
        }
    }
    private static void physTick() {
        // 物理帧行为
        for (SandBoxServerWorld world : allWorlds.values()) {
            if (!world.initialized.get()) continue;

            for (SandBoxServerShip ship : world.serverShips.values()) {
                if (ship.isDestroyMarked()) continue;

                ship.getRigidbody().physTick();
                ship.getAllBehaviours().forEach(IComponentBehaviour::physTick);
            }
        }
    }


    @Override
    public CompoundTag save(CompoundTag tag) {
        EzDebug.light("to save server world");

        List<SandBoxServerShip> toSaveShips = serverShips.values().stream().filter(ship -> !ship.isDestroyMarked()).toList();
        return new NbtBuilder().putEach(
            "ships",
            toSaveShips,
            SandBoxServerShip::saved
        ).get();
    }
    public SandBoxServerWorld load(CompoundTag tag) {
        List<SandBoxServerShip> loadedShips = new ArrayList<>();
        NbtBuilder.modify(tag).readEachCompound(
            "ships",
            SandBoxServerShip::new,
            loadedShips
        );
        for (var ship : loadedShips) {
            serverShips.put(ship.getUuid(), ship);
        }

        return this;
    }
}
