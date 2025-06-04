package com.lancas.vswap.register;

import com.lancas.vswap.VsWap;
import com.lancas.vswap.debug.EzDebug;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = VsWap.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerShipEvent {
    public static List<Runnable> delayedShipEvents = Collections.synchronizedList(new ArrayList<>());
    public static List<Runnable> shipEvents = Collections.synchronizedList(new ArrayList<>());
    private final static HashSet<Runnable> ranEvents = new HashSet<>();

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (Runnable action : shipEvents) {
            if (action == null) continue;
            try {
                action.run();
            } catch (Exception e) {
                EzDebug.error(e.toString());
            }
            ranEvents.add(action);
        }
        for (Runnable ran : ranEvents) {
            shipEvents.remove(ran);
        }
        ranEvents.clear();


        for (Runnable delayedAction : delayedShipEvents) {
            if (delayedAction == null) continue;
            shipEvents.add(delayedAction);
        }
        for (Runnable action : shipEvents) {
            delayedShipEvents.remove(action);
        }
    }
}
