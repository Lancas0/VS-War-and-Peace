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

@Mod.EventBusSubscriber
public class SandBoxServerWorld extends SavedData implements ISandBoxWorld {
    //private static Thread physicsThread = new Thread(SandBoxServerWorld::physTick, ModMain.MODID + "SandBox-Physics-Thread");
    //private static final long UPDATE_INTERVAL_NS = 16_666_666; // ≈16.67ms (60Hz)
    private static final long PHYS_TICK_INTERVAL_MS = 16;
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

    //todo all ACTIVE worlds
    private static Map<String, SandBoxServerWorld> allWorlds = new Hashtable<>();
    public static SandBoxServerWorld getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            tag -> { return new SandBoxServerWorld(level).load(tag); },
            () -> new SandBoxServerWorld(level),
            ModMain.MODID + "_sandbox"
        );
    }

    private final Map<UUID, SandBoxServerShip> serverShips = new ConcurrentHashMap<>();
    private final ServerLevel level;
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

        for (ServerLevel curLevel : event.getServer().getAllLevels()) {
            SandBoxServerWorld world = getOrCreate(curLevel);

            for (SandBoxServerShip ship : world.serverShips.values()) {
                ship.getAllBehaviours().forEach(IComponentBehaviour::serverTick);
            }
        }
    }
    private static void physTick() {
        // 物理帧行为
        for (SandBoxServerWorld world : allWorlds.values()) {
            for (SandBoxServerShip ship : world.serverShips.values()) {
                ship.getAllBehaviours().forEach(IComponentBehaviour::physTick);
            }
        }
    }


    @Override
    public CompoundTag save(CompoundTag tag) {
        return new NbtBuilder()
            .putMap("ships", serverShips, (uuid, ship) ->
                new NbtBuilder()
                    .putUUID("uuid", uuid)
                    .putCompound("ship_data", ship.saved())
                    .get()
            ).get();
    }
    public SandBoxServerWorld load(CompoundTag tag) {
        NbtBuilder.modify(tag).readMapOverwrite("ships",
            entryTag ->
                new BiTuple<>(
                    entryTag.getUUID("uuid"),
                    new SandBoxServerShip(entryTag.getCompound("ship_data"))
                )
        , serverShips);

        return this;
    }
}
