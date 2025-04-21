package com.lancas.vs_wap.debug;
/*
import com.lancas.vs_wap.ModMain;
import com.lancas.vs_wap.util.ShipUtil;
import com.lancas.vs_wap.util.StrUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DebugShipPos {
    public static Set<Long> shipIds = ConcurrentHashMap.newKeySet();

    @SubscribeEvent
    public static void debugPos(TickEvent.ServerTickEvent event) {
        //EzDebug.log("debuging pos");
        ServerLevel worldLevel = event.getServer().getAllLevels().iterator().next();//VSGameUtilsKt.getLevelFromDimensionId(event.getServer(), "minecraft:overworld");


        //EzDebug.log("worldLevel:" + (VSGameUtilsKt.getDimensionId(worldLevel)));
        for (Long id : shipIds) {
            ServerShip ship = ShipUtil.getServerShipByID(worldLevel, id);
            if (ship == null) {
                EzDebug.error("fail to get ship:" + id);
                continue;
            }
            //EzDebug.log("debug pos:" + StrUtil.F2(ship.getTransform().getPositionInWorld()));
        }

    }
}
*/