package com.lancas.vswap.ship.ballistics;

import com.lancas.vswap.VsWap;
import com.lancas.vswap.foundation.network.NetworkHandler;
import com.lancas.vswap.ship.ballistics.network.BallisticIdSyncPacketS2C;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = VsWap.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BallisticsClientManager {

    private static final List<Long> shipIds = Collections.synchronizedList(new ArrayList<>());

    //it's better to make sure whether the ship is null before send
    public static void sendIdFromServer(long shipId) {
        if (shipId >= 0)
            NetworkHandler.sendToAllPlayers(BallisticIdSyncPacketS2C.newId(shipId));
    }
    public static void terminateIdFromServer(long shipId) {
        if (shipId >= 0)
            NetworkHandler.sendToAllPlayers(BallisticIdSyncPacketS2C.terminateId(shipId));
    }

    public static void handleSyncPacketInClient(BallisticIdSyncPacketS2C packet) {
        if (packet.shipId < 0) return;

        if (packet.newOrTerminate)
            shipIds.add(packet.shipId);
        else
            shipIds.remove(packet.shipId);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        synchronized (shipIds) {
            var idsIterator = shipIds.iterator();
            while (idsIterator.hasNext()) {
                long id = idsIterator.next();
                Ship ship = ShipUtil.getShipByID(level, id);

                //we assume that sometimes ship cost times to spawn in client world,
                //and all ship will
                if (ship == null) { continue; }

                Vector3dc shipWorldPos = ship.getTransform().getPositionInWorld();
                //ClearAPI.Particle.addParticle(level, ParticleTypes.POOF, shipWorldPos, new Vector3d());
                level.addParticle(ParticleTypes.POOF, shipWorldPos.x(), shipWorldPos.y(), shipWorldPos.z(), 0, 0, 0);
            }
        }
    }
}
