package com.lancas.vswap.ship.ballistics;

import com.lancas.vswap.ModMain;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.ship.ballistics.data.BallisticData;
import com.lancas.vswap.ship.ballistics.force.BallisticForceInducer;
import com.lancas.vswap.ship.ballistics.handler.BallisticsUpdateHandler;
import com.lancas.vswap.ship.feature.pool.ShipPool;
import com.lancas.vswap.ship.type.ProjectileWrapper;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BallisticsServerMgr extends SavedData {
    public static BallisticsServerMgr getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            nbt -> {
                BallisticsServerMgr mgr = new BallisticsServerMgr(level);
                return mgr.load(nbt);
            },
            () -> new BallisticsServerMgr(level),
            ModMain.MODID + "_ballistics"
        );
    }

    private ServerLevel level;
    private Queue<BallisticData> ballistics = new ConcurrentLinkedQueue<>();
    //private List<Long> toAddBallistics = new ArrayList<>();

    private BallisticsServerMgr(ServerLevel inLevel) {
        level = inLevel;
    }

    public static void addBallistics(ServerLevel level, ProjectileWrapper projectile, ServerShip propellantShip, @Nullable ServerShip artilleryShip, double propellantEnergy) {
        BallisticsServerMgr mgr = getOrCreate(level);

        long artilleryShipId = artilleryShip == null ? -1 : artilleryShip.getId();
        mgr.addBallisticsImpl(projectile, propellantShip.getId(), artilleryShipId, propellantEnergy);

        BallisticsClientManager.sendIdFromServer(projectile.shipId);
    }
    private void addBallisticsImpl(ProjectileWrapper projectile, long propellantShipId, long artilleryShipId, double propellantEnergy) {
        BallisticData ballisticData = new BallisticData(level, projectile, propellantShipId, artilleryShipId, propellantEnergy);
        ballistics.add(ballisticData);
        BallisticForceInducer.apply(level, projectile, propellantEnergy, ballisticData);
        setDirty();
    }
    private void updateAll() {
        Iterator<BallisticData> dataIt = ballistics.iterator();
        while (dataIt.hasNext()) {
            BallisticData data = dataIt.next();

            Dest<Boolean> shouldTerminate = new Dest<>(data.isTerminated());
            if (!data.isTerminated()) {
                BallisticsUpdateHandler.update(level, data, shouldTerminate);
            }

            if (shouldTerminate.get()) {
                ServerShip projectileShip = data.shipData.getProjectileShip(level);
                EzDebug.highlight("terminated:" + projectileShip.getId());
                returnShipToPool(projectileShip);
                BallisticsClientManager.terminateIdFromServer(projectileShip.getId());
                dataIt.remove();
                setDirty();
            }
        }
    }

    private void returnShipToPool(ServerShip inShip) {
        BallisticForceInducer.clear(level, inShip);
        ShipPool.getOrCreatePool(level).returnShipAndSetEmpty(
            inShip,
            ShipPool.ResetAndSet.farawayAndNoConstraint
        );
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (ServerLevel curLevel : event.getServer().getAllLevels()) {
            BallisticsServerMgr mgr = BallisticsServerMgr.getOrCreate(curLevel);
            mgr.updateAll();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        return new NbtBuilder()
            .putEachSimpleJackson("ballistics_data", ballistics)
            .get();
    }
    public BallisticsServerMgr load(CompoundTag tag) {
        ballistics.clear();
        NbtBuilder.copy(tag)
            .readEachSimpleJackson("ballistics_data", BallisticData.class, ballistics);
        return this;
    }
}
