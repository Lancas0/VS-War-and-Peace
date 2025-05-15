package com.lancas.vs_wap.register;

import com.lancas.vs_wap.content.item.items.docker.RefWithFallbackDocker;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.LazyTicks;
import com.lancas.vs_wap.util.WorldUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.valkyrienskies.core.api.ships.ServerShip;

@Mod.EventBusSubscriber
public class ReleaseRefShipEvt {
    @SubscribeEvent
    public static void onItemEntityDestroyed(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof ItemEntity itemEntity && event.getLevel() instanceof ServerLevel sLevel) {
            ItemStack stack = itemEntity.getItem();

            tryRemoveRefShip(sLevel, stack);
        }
    }

    @SubscribeEvent
    public static void onPlayerDestroyItem(PlayerDestroyItemEvent event) {
        if (event.getEntity().level() instanceof ServerLevel level) {
            ItemStack stack = event.getOriginal();
            tryRemoveRefShip(level, stack);
        }
    }

    //the last line of clear ref ship
    private static final int LAZY_TICKS = 60 * 20;  //1minus
    private static final LazyTicks lazy = new LazyTicks(LAZY_TICKS);  //5 sec lazy
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (!lazy.shouldWork())
            return;

        //EzDebug.log("[onReleaseRefShip] onServerTick");
        //EzDebug.logs(RefWithFallbackDocker.countingDown, null);

        for (ServerLevel level : event.getServer().getAllLevels()) {
            var countingDownIt = RefWithFallbackDocker.countingDown.entrySet().iterator();
            while (countingDownIt.hasNext()) {
                var entry = countingDownIt.next();
                long shipId = entry.getKey();
                var value = entry.getValue();

                if (value.getFirst() != level) continue;

                if (value.getSecond() < LAZY_TICKS) {
                    var vsWorld = WorldUtil.shipWorldOf(level);
                    ServerShip ship = vsWorld.getAllShips().getById(shipId);
                    if (ship != null)
                        vsWorld.deleteShip(ship);

                    countingDownIt.remove();
                    continue;
                }

                value.setSecond(value.getSecond() - LAZY_TICKS);
                EzDebug.log("new life:" + value.getSecond());
            }
        }
    }

    private static boolean tryRemoveRefShip(ServerLevel level, ItemStack stack) {
        if (stack.getItem() instanceof RefWithFallbackDocker docker) {
            var vsWorld = WorldUtil.shipWorldOf(level);

            ServerShip ship = vsWorld.getAllShips().getById(docker.getVsShipId(stack));
            if (ship != null) {
                vsWorld.deleteShip(ship);
                EzDebug.log("delete refed ship by onItemEntityDestroyed");
                return true;
            }
        }
        return false;
    }
}
