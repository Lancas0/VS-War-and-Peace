package com.lancas.vswap.content.saved.refship;

import com.lancas.vswap.VsWap;
import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.event.EventMgr;
import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.foundation.LazyTicks;
import com.lancas.vswap.util.NbtBuilder;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber
public class RefShipMgr extends SavedData {

    private static final int COUNTING_DOWN = 3 * 60 * 20 - 1;//3 minus
    public static RefShipMgr getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            t -> new RefShipMgr(level).load(t),
            () -> new RefShipMgr(level),
            VsWap.MODID + "_ref_ship_mgr"
        );
    }

    public ConcurrentHashMap<Long, Integer> countingDown = new ConcurrentHashMap<>();
    private final ServerLevel level;
    private RefShipMgr(ServerLevel inLevel) {
        level = inLevel;
        EventMgr.Server.onVsShipUnloaded.addListener(id -> {  //todo can't handle all situation
            countingDown.remove(id);
            setDirty();
        });
    }

    public static void addCountingRef(ServerLevel level, long shipId) {
        getOrCreate(level).addCountingRef(shipId);
    }
    public void addCountingRef(long shipId) {
        countingDown.put(shipId, COUNTING_DOWN);
        setDirty();
    }
    public static void releaseRef(ServerLevel level, long shipId) {
        getOrCreate(level).releaseRef(shipId);//countingDown.remove(shipId);
    }
    public void releaseRef(long shipId) {
        countingDown.remove(shipId);
        setDirty();
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        return new NbtBuilder()
            .putMap("counting_down", countingDown, (k, v) -> {
                return new NbtBuilder()
                    .putLong("id", k)
                    .putInt("count_down", v)
                    .get();
            }).get();
    }
    public RefShipMgr load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readMapOverwrite("counting_down", t -> {
                BiTuple<Long, Integer> element = new BiTuple<>();
                NbtBuilder.modify(t)
                    .readLongDo("id", element::setFirst)
                    .readIntDo("count_down", element::setSecond);
                return element;
            }, countingDown);
        return this;
    }


    private void deleteCountingRefShipOf(ItemStack stack) {
        Long refShipId = Docker.getReferencedShipId(level, stack);

        if (refShipId != null && countingDown.containsKey(refShipId)) {
            ShipUtil.deleteShipById(level, refShipId);
        }
    }
    @SubscribeEvent
    public static void onItemEntityDestroyed(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof ItemEntity itemEntity && event.getLevel() instanceof ServerLevel level) {
            RefShipMgr mgr = getOrCreate(level);
            ItemStack stack = itemEntity.getItem();

            mgr.deleteCountingRefShipOf(stack);
        }
    }

    @SubscribeEvent
    public static void onPlayerDestroyItem(PlayerDestroyItemEvent event) {
        if (event.getEntity().level() instanceof ServerLevel level) {
            RefShipMgr mgr = getOrCreate(level);
            ItemStack stack = event.getOriginal();

            mgr.deleteCountingRefShipOf(stack);
        }
    }

    //the last line of clear ref ship
    private static final int LAZY_TICKS = 20 * 40; //40s //5s temp//60 * 20;  //1minus
    private static final LazyTicks lazy = new LazyTicks(LAZY_TICKS);  //5 sec lazy
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (!lazy.shouldWork())
            return;

        //EzDebug.log("[onReleaseRefShip] onServerTick");
        //EzDebug.logs(RefWithFallbackDocker.countingDown, null);

        for (ServerLevel level : event.getServer().getAllLevels()) {
            RefShipMgr mgr = getOrCreate(level);
            var countingDownIt = mgr.countingDown.entrySet().iterator();

            while (countingDownIt.hasNext()) {
                var entry = countingDownIt.next();
                long shipId = entry.getKey();
                int countDown = entry.getValue();

                if (countDown < LAZY_TICKS) {
                    ShipUtil.deleteShipById(level, shipId);
                    countingDownIt.remove();
                    mgr.setDirty();
                    continue;
                }

                entry.setValue(countDown - LAZY_TICKS);
                mgr.setDirty();
                EzDebug.log("new life:" + entry.getValue());
            }
        }
    }

    /*private static boolean tryRemoveRefShip(ServerLevel level, ItemStack stack) {
        if (stack.getItem() instanceof Docker) {
            var vsWorld = WorldUtil.shipWorldOf(level);

            ServerShip refShip = Docker.getReferencedShip(level, stack);
            if (refShip != null) {
                vsWorld.deleteShip(refShip);
                EzDebug.log("delete refed ship by onItemEntityDestroyed");
                return true;
            }
        }
        return false;
    }*/
}
