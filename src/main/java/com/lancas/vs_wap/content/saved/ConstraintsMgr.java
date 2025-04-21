package com.lancas.vs_wap.content.saved;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lancas.vs_wap.ModMain;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.event.EventMgr;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.util.NbtBuilder;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniondc;
import org.joml.Vector3dc;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;
import org.valkyrienskies.core.apigame.constraints.VSFixedOrientationConstraint;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.core.impl.util.serialization.VSJacksonUtil;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber
public class ConstraintsMgr extends SavedData {
    public static ConstraintsMgr getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            tag -> {
                ConstraintsMgr mgr = new ConstraintsMgr(level);
                return mgr.loadAndChangeGroundID(level, tag);
            },
            () -> new ConstraintsMgr(level),
            ModMain.MODID + "_constraints_mgr"
        );
    }

    /*private static final Hashtable<String, ConstraintsMgr> instanceEachLevel = new Hashtable<>();
    public static ConstraintsMgr instanceOf(ServerLevel level) {
        String dimId = VSGameUtilsKt.getDimensionId(level);
        return instanceEachLevel.computeIfAbsent(dimId, dId -> new ConstraintsMgr());
    }*/

    //no iterate or manual sync when interating
    private final Map<String, SavedConstraint> addingConstraints = new ConcurrentHashMap<>();
    private final Map<String, SavedConstraint> constraintsInLevel = new ConcurrentHashMap<>();

    public boolean allFinish = false;
    private ServerLevel serverLevel;

    public ConstraintsMgr(ServerLevel inSLevel) {
        serverLevel = inSLevel;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        try {
            /*ArrayList<String> keys = new ArrayList<>();
            ArrayList<SavedConstraint> constraints = new ArrayList<>();

            //avoid any change durning saving
            synchronized (constraintsInLevel) {
                for (var entry : constraintsInLevel.entrySet()) {
                    SavedConstraint value = entry.getValue();
                    if (value == null) continue;  //don't save loaded or failed ones.

                    assert(value.isInLevel());  //must be in level

                    keys.add(entry.getKey());
                    constraints.add(value);
                }
            }
            //todo maybe should also save adding constraints

            EzDebug.log("saved count:" + keys.size());*/
            EzDebug.light("saving " + constraintsInLevel.keySet().size() + " constraints.");
            return new NbtBuilder()
                .putSimpleJackson("keys", constraintsInLevel.keySet())
                .putEachJackson("constraints", constraintsInLevel.values(), VSJacksonUtil.INSTANCE.getDtoMapper())
                .get();


        } catch (Exception e) {
            EzDebug.error("fail to save constraints, will lose all constraints, exception:\n" + e.toString());
        }
        return new CompoundTag();
    }
    public ConstraintsMgr loadAndChangeGroundID(ServerLevel level, CompoundTag tag) {
        long groundID = ShipUtil.getGroundId(level);

        try {
            List<String> keys = NbtBuilder.copy(tag).readSimpleJackson("keys", new TypeReference<List<String>>() {});
            List<SavedConstraint> constraints = new ArrayList<>();
            NbtBuilder.copy(tag).readEachJacksonWhileDo(
                "constraints",
                SavedConstraint.class,
                VSJacksonUtil.INSTANCE.getDtoMapper(),
                root -> {
                    //change ground id when loading
                    ObjectNode mutableConstraintNode = (ObjectNode)(root.get("constraint"));

                    boolean isShip0Ground = root.get("ship0IsGround").asBoolean();
                    boolean isShip1Ground = root.get("ship1IsGround").asBoolean();
                    if (isShip0Ground && isShip1Ground) {
                        EzDebug.warn("loading a constraint between ground and ground");
                    }

                    if (isShip0Ground)
                        mutableConstraintNode.put("shipId0", groundID);
                    if (isShip1Ground)
                        mutableConstraintNode.put("shipId1", groundID);
                },
                constraints
            );
            LoggerFactory.getLogger("ConstraintMgr").info("load end");

            if (keys.size() != constraints.size()) {
                throw new RuntimeException("key size and constraint size are different!");
            } else {
                //EzDebug.log("key size:" + keys.size() + ", cons size:" + constraints.size());
            }

            //load all constraints at one time
            for (int i = 0; i < keys.size(); ++i) {
                if (constraints.get(i) == null) {
                    EzDebug.warn("skip a null constraint when loading");
                    continue;
                }
                //load while set the ground id;

                String key = keys.get(i);
                SavedConstraint constraint = constraints.get(i);

                addingConstraints.put(key, constraint);
            }

        } catch (Exception e) {
            EzDebug.error("fail to load constraints, will lose all constraints, exception:");
            throw new RuntimeException(e);
        }
        return this;
    }

    @Nullable
    public static Integer addConstraint(ServerLevel level, String key, VSConstraint constraint) {
        ConstraintsMgr mgr = ConstraintsMgr.getOrCreate(level);
        ServerShipWorldCore shipWorld = VSGameUtilsKt.getShipObjectWorld(level);

        Integer addID = shipWorld.createNewConstraint(constraint);
        //fail to create the constraint
        if (addID == null)
            return null;

        //successfully create the constraint
        SavedConstraint addingConsWithSameKey = mgr.addingConstraints.get(key);

        //remove the addingCons with same key if existed
        if (addingConsWithSameKey != null) {
            assert (!addingConsWithSameKey.isInLevel());
            mgr.addingConstraints.remove(key);  //no need to remove from level because it's not in level
        }

        Dest<VSConstraint> replaced = new Dest<>();
        replaceIfExistedThenAddToInLevel(level, mgr, key, constraint, addID, replaced);
        if (replaced.hasValue()) {
            EventMgr.Server.constraintRemoveEvent.invokeAll(level, key, replaced.get());
        }
        EventMgr.Server.constraintAddEvent.invokeAll(level, key, constraint, addID);

        mgr.setDirty();
        return addID;
    }
    public static boolean removeInLevelConstraint(ServerLevel level, String key) {
        if (key == null) return false;
        ConstraintsMgr mgr = getOrCreate(level);
        return mgr.removeInLevelConstraintImpl(key);
    }
    private boolean removeInLevelConstraintImpl(String key) {  //suppose key must exist
        SavedConstraint inLevel;
        inLevel = constraintsInLevel.remove(key);
        if (inLevel == null) return false;

        boolean removed = VSGameUtilsKt.getShipObjectWorld(serverLevel).removeConstraint(inLevel.getAddedID());
        if (!removed) {
            EzDebug.warn("fail to remove a constraint that is recorded in constraintInLevel");
        } else {
            EventMgr.Server.constraintRemoveEvent.invokeAll(serverLevel, key, inLevel.getConstraint());
        }
        this.setDirty();

        return removed;
    }

    public static boolean removeAddingConstraint(ServerLevel level, String key) {
        if (key == null) return false;
        ConstraintsMgr mgr = ConstraintsMgr.getOrCreate(level);
        return mgr.removeAddingConstraintImpl(key);
    }
    private boolean removeAddingConstraintImpl(String key) {
        SavedConstraint inLevel = addingConstraints.remove(key);
        if (inLevel == null) return false;

        this.setDirty();  //for safe
        return true;  //the adding constraints are not yet added to level, so don't call VS delete constraint, and no event emit.
    }

    public static boolean removeInLevelOrAddingConstraint(ServerLevel level, String key) {
        if (key == null) return false;
        ConstraintsMgr mgr = getOrCreate(level);
        return mgr.removeInLevelOrAddingConstraintImpl(key);
    }
    private boolean removeInLevelOrAddingConstraintImpl(String key) {
        return removeAddingConstraintImpl(key) || removeInLevelConstraintImpl(key);
    }

    public static void removeAllConstraintWith(ServerLevel level, Long shipId) {
        ConstraintsMgr mgr = getOrCreate(level);
        mgr.removeAllConstraintWithImpl(shipId);
    }
    public void removeAllConstraintWithImpl(long shipId) {
        for (var constraintEntry : addingConstraints.entrySet()) {
            VSConstraint constraint = constraintEntry.getValue().getConstraint();
            if (constraint.getShipId0() == shipId || constraint.getShipId1() == shipId) {
                removeAddingConstraintImpl(constraintEntry.getKey());
            }
        }

        for (var constraintEntry : constraintsInLevel.entrySet()) {
            VSConstraint constraint = constraintEntry.getValue().getConstraint();
            if (constraint.getShipId0() == shipId || constraint.getShipId1() == shipId) {
                removeInLevelConstraintImpl(constraintEntry.getKey());
            }
        }
    }
    private static void replaceIfExistedThenAddToInLevel(ServerLevel level, ConstraintsMgr mgr, String key, VSConstraint constraint, int constraintID, Dest<VSConstraint> replaced) {
        ServerShipWorldCore shipWorld = VSGameUtilsKt.getShipObjectWorld(level);
        long groundId = ShipUtil.getGroundId(level);

        synchronized (mgr.constraintsInLevel) {
            SavedConstraint prevInLevel = mgr.constraintsInLevel.get(key);

            //replace existed
            //todo try use shipWorld.updateConstraint?
            if (prevInLevel != null) {
                assert (prevInLevel.getAddedID() != null);
                shipWorld.removeConstraint(prevInLevel.getAddedID());
                replaced.set(prevInLevel.getConstraint());
            } else {
                replaced.set(null);
            }

            mgr.constraintsInLevel.put(key, SavedConstraint.inLevel(level, constraint, constraintID));
        }
    }

    /*
    @SubscribeEvent
    public static void onServerStaring(ServerStartingEvent event){
        VSEvents.INSTANCE.getShipLoadEvent().on(shipLoadEvent -> {
            shipLoadEvent.getShip().getId();
        });

        cache.clear();
        server = _server;

        ConstraintSavedData loadedStorage = ConstraintSavedData.load(server);
        List<SavedConstraintObject> constraintList =
            loadedStorage.data
                .entrySet()
                .stream()
                .map(entry -> new SavedConstraintObject(entry.getKey(), entry.getValue()))
                .toList();

        VSEvents.INSTANCE.getShipLoadEvent().on(((shipLoadEvent, registeredHandler) -> {
            // Execute All Recreating Constrain Tasks Shortly After Any Ship Being Reloaded
            ControlCraftServer
                .SERVER_DEFERRAL_EXECUTOR
                .executeLater(() -> constraintList.forEach(ConstraintCenter::createOrReplaceNewConstrain), 4);

            registeredHandler.unregister();
        }));

    }*/

    private static int lazy = 10;
    private static boolean allInstanceLoaded = false;
    //todo ship load events?
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (allInstanceLoaded) return;

        if (--lazy > 0) {
            return;
        }
        lazy = 10;


        //assume all inst are loaded, then set false if a inst is loading
        //allInstanceLoaded = true;
        //todo each level has data
        for (ServerLevel level : event.getServer().getAllLevels()) {
            tickLevel(level);
        }
    }
    private static void tickLevel(ServerLevel level) {
        ServerShipWorldCore shipWorld = VSGameUtilsKt.getShipObjectWorld(level);
        ConstraintsMgr mgr = ConstraintsMgr.getOrCreate(level);

        if (mgr.allFinish) return;
        else allInstanceLoaded = false;

        //assume all constraints are loaded, then set false if a constarint is not finish
        //inst.allFinish = true;
        //avoid change when other visiting
        synchronized (mgr.addingConstraints) {
            if (!mgr.addingConstraints.isEmpty())
                EzDebug.light("now loading constraints, has constraints size: " + mgr.addingConstraints.size());

            //todo  don't try to add a constraint which fail too many times
            var iter = mgr.addingConstraints.entrySet().iterator();
            while (iter.hasNext()) {
                var current = iter.next();
                String key = current.getKey();
                SavedConstraint value = current.getValue();

                //EzDebug.log("cur constraint:" + value.getConstraint().toString());
                //EzDebug.log("cur ground id: " + ShipUtil.getGroundId(level));

                if (value.tryAddToLevel(shipWorld)) {

                    EzDebug.log("sucessfully add to level");
                    //successfully add in level
                    Dest<VSConstraint> replaced = new Dest<>();
                    replaceIfExistedThenAddToInLevel(level, mgr, key, value.getConstraint(), value.getAddedID(), replaced);

                    if (replaced.hasValue()) {
                        EventMgr.Server.constraintRemoveEvent.invokeAll(level, key, replaced.get());
                    }
                    EventMgr.Server.constraintAddEvent.invokeAll(level, key, value.getConstraint(), value.getAddedID());

                    iter.remove();
                } else {
                    //EzDebug.log("fail to add to level");
                    mgr.allFinish = false;  //still need try load
                }

            }
        }
    }


    public static VSConstraint getLoadedAttachment(ServerLevel level, String key) {
        //ConstraintsMgr mgr = instanceOf(level);
        ConstraintsMgr mgr = getOrCreate(level);

        synchronized (mgr.constraintsInLevel) {
            SavedConstraint inLevel = mgr.constraintsInLevel.get(key);
            if (inLevel == null || !inLevel.isInLevel()) return null;

            return inLevel.getConstraint();
        }
    }
    public static VSConstraint getLoadedAttachmentById(ServerLevel level, int id) {
        if (id < 0) return null;

        //todo use any key map
        ConstraintsMgr mgr = getOrCreate(level);

        synchronized (mgr.constraintsInLevel) {
            for (SavedConstraint inLevel : mgr.constraintsInLevel.values()) {
                if (inLevel.getAddedID() == id)
                    return inLevel.getConstraint();
            }
        }
        return null;
    }


    public static Integer addAttachment(ServerLevel level, String key, @Nullable ServerShip ship0, @Nullable ServerShip ship1, double compliance, Vector3dc locPos0, Vector3dc locPos1, double maxForce, double fixedDist) {
        if (ship0 == null && ship1 == null) {
            EzDebug.warn("try make a constraint between ground and ground. It's meaningless and directly return.");
            return null;
        }

        long shipOrGroundId0 = ShipUtil.getShipOrGroundId(level, ship0);
        long shipOrGroundId1 = ShipUtil.getShipOrGroundId(level, ship1);
        return addConstraint(level, key, new VSAttachmentConstraint(
            shipOrGroundId0, shipOrGroundId1,
            compliance,
            locPos0, locPos1,
            maxForce, fixedDist
        ));
    }
    public static Integer addFixedOrientation(ServerLevel level, String key, @Nullable ServerShip ship0, @Nullable ServerShip ship1, double compliance, Quaterniondc locRot0, Quaterniondc locRot1, double maxTorque) {
        if (ship0 == null && ship1 == null) {
            EzDebug.warn("try make a constraint between ground and ground. It's meaningless and directly return.");
            return null;
        }

        long shipOrGroundId0 = ShipUtil.getShipOrGroundId(level, ship0);
        long shipOrGroundId1 = ShipUtil.getShipOrGroundId(level, ship1);
        return addConstraint(level, key, new VSFixedOrientationConstraint(
            shipOrGroundId0, shipOrGroundId1,
            compliance,
            locRot0, locRot1,
            maxTorque
        ));
    }
}
