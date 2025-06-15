package com.lancas.vswap.ship.feature.pool;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.lancas.vswap.VsWap;
import com.lancas.vswap.content.saved.vs_constraint.ConstraintsMgr;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.data.SavedBlockPos;
import com.lancas.vswap.ship.helper.builder.ShipBuilder;
import com.lancas.vswap.ship.helper.builder.TeleportDataBuilder;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.NbtBuilder;
import com.lancas.vswap.util.ShipUtil;
import com.lancas.vswap.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mod.EventBusSubscriber
public class ShipPool extends SavedData {
    /*public enum HideType {
        StaticAndInvisible(
            toHideShip -> {
                toHideShip.setStatic(true);
                NetworkHandler.sendToAllPlayers(new HideOrShowShipInClientS2C(toHideShip.getId(), true));
            },
            toShowShip -> {
                toShowShip.setStatic(false);
                NetworkHandler.sendToAllPlayers(new HideOrShowShipInClientS2C(toShowShip.getId(), false));
            }
        )
        ;

        private final Consumer<ServerShip> hider;
        private final Consumer<ServerShip> shower;
        HideType(Consumer<ServerShip> inHider, Consumer<ServerShip> inShower) {
            hider = inHider;
            shower = inShower;
        }
    }
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE
    )
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class HidingShip {
        public long shipId;
        public HideType hideType;

        public HidingShip(long inShipId, HideType inHideType) {
            shipId = inShipId;
            hideType = inHideType;
        }
        private HidingShip() {}
    }*/

    public static class ResetAndSet {

        public static BiConsumer<ServerLevel, ServerShip> moveToFaraway = (level, ship) -> {
            Vector3dc shipWorldPos = ship.getTransform().getPositionInWorld();
            ShipUtil.teleport(level, ship, TeleportDataBuilder.noMovementOf(level, ship).withPos(new Vector3d(shipWorldPos.x(), -2000, shipWorldPos.z())));
        };
        public static BiConsumer<ServerLevel, ServerShip> removeConstraints = (level, ship) -> {
            ConstraintsMgr.removeAllConstraintWith(level, ship.getId());
        };

        public static BiConsumer<ServerLevel, ServerShip> farawayAndNoConstraint = moveToFaraway.andThen(removeConstraints);
        //public static BiConsumer<ServerLevel, ServerShip>
    }

    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE
    )
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class BlockKeepAlivePooledShip {
        private BlockKeepAlivePooledShip() {}  //for json deserialize
        public BlockKeepAlivePooledShip(long inShipId, BlockPos inKeepAliveBp) {
            shipId = inShipId;
            keepAliveBp = new SavedBlockPos(inKeepAliveBp);
        }

        private long shipId;
        private SavedBlockPos keepAliveBp;
    }


    public static final int WATER_LINE = 50;
    //public static final int FILL_LINE = 8;
    /*public static class PooledShip {
        public boolean active;
        public long shipId;

        private PooledShip() {}
        private PooledShip(boolean inActive, long inShipId) { active = inActive; shipId = inShipId; }

        public static PooledShip load(CompoundTag tag) {
            return new PooledShip().loadOverwrite(tag);
        }
        public static CompoundTag saveAs(PooledShip pooledShip) {
            CompoundTag tag = new CompoundTag();
            pooledShip.save(tag);
            return tag;
        }
        public void save(CompoundTag tag) {
            tag.putBoolean("active", active);
            tag.putLong("ship_id", shipId);
        }
        public PooledShip loadOverwrite(CompoundTag tag) {
            active = tag.getBoolean("active");
            shipId = tag.getLong("ship_id");
            return this;
        }
    }*/
    private static final Logger log = LogManager.getLogger(ShipPool.class);
    //private static Lock lock = new ReentrantLock();
    //public Queue<PooledShip> poolQueue = new ConcurrentLinkedQueue<>();
    private final Map<Long, BlockKeepAlivePooledShip> poolMap = new ConcurrentHashMap<>();
    //private final Map<Long, HidingShip> hidingShips = new Hashtable<>();
    private final ServerLevel level;

    public static ShipPool getOrCreatePool(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            tag -> ShipPool.load(level, tag),
            () -> new ShipPool(level),
            VsWap.MODID + "_ship_pool"
        );
    }
    public static ShipPool load(ServerLevel level, CompoundTag tag) {
        ShipPool pool = new ShipPool(level);
        NbtBuilder nbtBuilder = NbtBuilder.copy(tag);

        ArrayList<Long> ids;
        try {
            ids = nbtBuilder.readSimpleJackson("pool_ids", new TypeReference<ArrayList<Long>>() {});
        } catch (Exception e) {
            EzDebug.error("fail to get pool ids by exception");
            throw new RuntimeException(e);
        }

        ArrayList<BlockKeepAlivePooledShip> poolValues = new ArrayList<>();
        nbtBuilder.readEachSimpleJackson("pool_values", BlockKeepAlivePooledShip.class, poolValues);

        if (ids.size() != poolValues.size()) {
            EzDebug.warn("id size don't match poolValues");
            return pool;
        }

        int size = Math.min(ids.size(), poolValues.size());
        for (int i = 0; i < size; ++i) {
            pool.poolMap.put(ids.get(i), poolValues.get(i));
        }

        //pool.fillUntilFillLine();

        /*ArrayList<Long> hideIds;
        try {
            hideIds = nbtBuilder.readSimpleJackson("hide_ids", new TypeReference<ArrayList<Long>>() {});
        } catch (Exception e) {
            EzDebug.error("fail to get hide ids");
            throw new RuntimeException(e);
        }*/

        /*ArrayList<HidingShip> hidingShips = new ArrayList<>();
        nbtBuilder.readEachSimpleJackson("hide_values", HidingShip.class, hidingShips);
        if (hideIds.size() != hidingShips.size()) {
            EzDebug.warn("id size don't match hidingShips");
            return pool;
        }
        size = Math.min(hideIds.size(), hidingShips.size());
        for (int i = 0; i < size; ++i) {
            pool.hidingShips.put(hideIds.get(i), hidingShips.get(i));
        }*/
        //NbtBuilder.copy(tag)
            //.readEach("ships", PooledShip::load, pool.poolQueue)
            //.readEach("pooled_ids", nbt -> NbtBuilder.getValueOfLong(nbt, "id"), pool.pooledIds);
        /*for (Long pooledId : pool.pooledIds) {
            EventMgr.Server.serverEndTickEvent.add(event -> {
                //the level is a captured ref, may use event.server.getLevel?
                //EzDebug.light("try force chunks");

                ServerShip ship = ShipUtil.getServerShipByID(level, pooledId);
                if (ship == null) return;
                ship.getActiveChunksSet().forEach((x, y) -> {
                    level.setChunkForced(x, y, true);
                    //EzDebug.light("chunk " + x + ", " + y + " is forced.");
                });
            });
        }*/

        //need to re hide when reload ship


        return pool;
    }
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        return new NbtBuilder()
            .putSimpleJackson("pool_ids", poolMap.keySet())
            .putEachSimpleJackson("pool_values", poolMap.values())
            //.putSimpleJackson("hide_ids", hidingShips.keySet())
            //.putEachSimpleJackson("hide_values", hidingShips.values())
            .get();
    }


    @SubscribeEvent
    public static void onServerRegisterEvent(ServerStartingEvent event) {
        event.getServer().getAllLevels().forEach(l -> {
            ShipPool pool = getOrCreatePool(l);  //force load pool at first, so that event can be registered
        });
    }
    public ShipPool(ServerLevel inLevel) {
        level = inLevel;
    }

    private BlockKeepAlivePooledShip createNewPooledShip() {
        String dimId = VSGameUtilsKt.getDimensionId(level);
        ServerShip ship = VSGameUtilsKt.getShipObjectWorld(level).createNewShipAtBlock(new Vector3i(), false, 1.0, dimId);
        BlockPos shipPos = JomlUtil.bpContaining(ship.getTransform().getPositionInShip());
        WorldUtil.setBlock(level, shipPos, Blocks.IRON_BLOCK.defaultBlockState(), null);

        //todo reset should invoke in next frame so don't reset
        //but sometimes 0, 0, 0 will become the ship pool
        //DefaultReset.moveToFaraway.accept(level, ship);
        return new BlockKeepAlivePooledShip(ship.getId(), shipPos);
    }
    /*private void fillUntilFillLine() {
        while (poolMap.size() < FILL_LINE) {
            BlockKeepAlivePooledShip newPooledShip = createNewPooledShip();
            poolMap.put(newPooledShip.shipId, newPooledShip);
        }
        setDirty();
    }*/


    //since the ship may not exist, we can't get loaded ship
    public ServerShip getOrCreateEmptyShip() {
        var poolIterator = poolMap.entrySet().iterator();

        if (poolIterator.hasNext()) {
            var poolEntry = poolIterator.next();
            BlockKeepAlivePooledShip pooledShip = poolEntry.getValue();
            //todo is it thread safe?
            poolIterator.remove();

            EzDebug.highlight("ship pool has id:" + pooledShip);

            ServerShip getShip = ShipUtil.getServerShipByID(level, pooledShip.shipId);
            if (getShip == null) {
                EzDebug.warn("fail to get pooled ship with id:" + pooledShip);
                //don't save because the pool must be saved when return a no-null value
                return getOrCreateEmptyShip();
            }
            else {
                setDirty();
                level.setBlock(pooledShip.keepAliveBp.toBp(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                return getShip;
            }
        } else {
            //create a ship, do not put it in pool but directly return it.
            EzDebug.warn("no ship in pool, directly create a ship");
            setDirty();  //prev may have deleted shipId;
            return VSGameUtilsKt.getShipObjectWorld(level).createNewShipAtBlock(new Vector3i(), false, 1f, VSGameUtilsKt.getDimensionId(level));
        }
    }
    public ShipBuilder getOrCreateEmptyShipBuilder() {
        ServerShip shipToBuild = getOrCreateEmptyShip();
        return ShipBuilder.modify(level, shipToBuild);
    }
    public ShipBuilder getOrCreateEmptyShipBuilder(Consumer<ShipBuilder> initializer) {
        ShipBuilder builder = getOrCreateEmptyShipBuilder();
        if (initializer != null)
            initializer.accept(builder);
        return builder;
    }
    /*public void returnShip(PooledShip pooledShip) {
        if (pooledShip == null || pooledShip.shipId < 0) return;
        if (pooledIds.contains(pooledShip.shipId)) {
            EzDebug.error("try to return a returned ship");
            return;
        }
        if (!pooledShip.active) {
            EzDebug.error("the ship is not active while not in pool");
        }

        pooledShip.active = false;

        lock.lock();
            poolQueue.add(pooledShip);
            pooledIds.add(pooledShip.shipId);
        lock.unlock();
    }*/
    public void returnShipAndSetEmpty(ServerShip ship, BiConsumer<ServerLevel, ServerShip> shipResetter) {
        if (ship == null) return;
        if (poolMap.containsKey(ship.getId())) {
            EzDebug.warn("try to return a returned ship");
            //return;
        }

        if (poolMap.size() >= WATER_LINE) {
            EzDebug.highlight("pool size is greater than waterline, just delete the ship");
            VSGameUtilsKt.getShipObjectWorld(level).deleteShip(ship);
            return;
        }

        //todo add the bp into the center of ship
        AtomicReference<BlockPos> keepAliveBp = new AtomicReference<>();
        ShipBuilder.modify(level, ship).foreachBlock((pos, state, be) -> {
            if (keepAliveBp.get() == null) {
                keepAliveBp.set(pos);
                level.setBlockAndUpdate(pos, Blocks.IRON_BLOCK.defaultBlockState());
                return;
            }

            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());  //todo not update?
        });
        if (keepAliveBp.get() == null) {
            EzDebug.warn("try to return a empty ship");
            return;
        }

        if (shipResetter != null) {
            shipResetter.accept(level, ship);
        }

        poolMap.put(ship.getId(), new BlockKeepAlivePooledShip(ship.getId(), keepAliveBp.get()));
        setDirty();
    }


    /*public void hideShip(ServerShip ship, HideType hideType) {
        long shipId = ship.getId();

        if (hidingShips.containsKey(shipId)) {
            EzDebug.warn("now hiding a hiden ship:" + shipId);
            HidingShip prevHideShip = hidingShips.get(shipId);
            //use prev hider to show the ship, and then hide the ship use current hider
            prevHideShip.hideType.shower.accept(ship);
            /.*hideType.hider.accept(ship);

            hidingShips.put(shipId, new HidingShip(ship.getId(), hideType));
            return;*./
        }

        hideType.hider.accept(ship);
        hidingShips.put(shipId, new HidingShip(ship.getId(), hideType));
        setDirty();
    }
    public ServerShip showShip(long shipId) {
        if (shipId < 0) return null;

        HidingShip hidingShip = hidingShips.get(shipId);
        if (hidingShip == null) {
            EzDebug.warn("try show a not hiding ship!");
            return null;
        }

        ServerShip toShowShip = ShipUtil.getServerShipByID(level, shipId);
        if (toShowShip == null) {
            EzDebug.light("the ship to show is removed");
            return null;
        }

        hidingShip.hideType.shower.accept(toShowShip);
        hidingShips.remove(shipId);
        setDirty();
        return toShowShip;
    }*/
    /*public void returnShip(long shipId) {
        if (shipId < 0) return;
        if (pooledIds.contains(shipId)) {
            EzDebug.error("try to return a returned ship");
            return;
        }

        returnShip(new PooledShip(true, shipId));
    }*/
}
