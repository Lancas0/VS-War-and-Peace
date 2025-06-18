package com.lancas.vswap.content.item.items.vsmotion;

import com.lancas.vswap.content.item.items.base.ShipInteractableItem;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.*;

@Mod.EventBusSubscriber
public class MotionRecorder extends ShipInteractableItem {
    public MotionRecorder(Properties p_41383_) {
        super(p_41383_);
    }

    public static Set<Long> recordingShips = new HashSet<>();
    public static HashMap<Long, IWriteOnlySavableMotion> records = new HashMap<>();
    public static boolean started = false;

    @Override
    public InteractionResult onItemUseOnShip(ItemStack stack, @NotNull Ship ship, @NotNull Level level, @NotNull Player player, UseOnContext ctx) {
        if (level.isClientSide)
            return InteractionResult.PASS;

        recordingShips.add(ship.getId());
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult onItemNotUseOnShip(ItemStack stack, Level level, Player player, UseOnContext ctx) {
        if (level.isClientSide)
            return InteractionResult.PASS;

        if (!started) {
            started = true;
            return InteractionResult.SUCCESS;
        } else {
            started = false;
            for (IWriteOnlySavableMotion motion : records.values()) {
                boolean exported = MotionRecord.saveRecord("Motion", false, motion);
                EzDebug.log("Exported:" + (exported ? "Succeed" : "Failed"));
            }
            recordingShips.clear();
            records.clear();
            return InteractionResult.SUCCESS;
        }
    }

    private static int lastTickCnt = -1;
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        if (!started || recordingShips.isEmpty())
            return;
        if (lastTickCnt == event.getServer().getTickCount()) {
            EzDebug.warn("skip same tick");
            return;
        }

        lastTickCnt = event.getServer().getTickCount();

        event.getServer().getAllLevels().forEach(l -> {
            recordingShips.stream().map(x -> ShipUtil.getServerShipByID(l, x))
                .filter(Objects::nonNull)
                .forEach(s -> {
                    IWriteOnlySavableMotion motion = records.computeIfAbsent(s.getId(), k -> new MotionRecord());
                    motion.addFrame(TransformPrimitive.fromVsTransform(s.getTransform()));
                });
        });
    }
}
