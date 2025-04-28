package com.lancas.vs_wap.subproject.sandbox.ship;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.api.ISavedLevelObject;
import com.lancas.vs_wap.util.NbtBuilder;
import com.lancas.vs_wap.util.SerializeUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ScheduleShipData implements ISavedLevelObject<ScheduleShipData> {
    private static final Logger log = LogManager.getLogger(ScheduleShipData.class);

    @FunctionalInterface
    public interface ScheduleCallback extends Serializable {
        public void onScheduling(ServerLevel level, SandBoxServerShip ship, int remainTick, AtomicBoolean canceled);
    }

    public SandBoxServerShip ship;
    private final AtomicInteger remainTick = new AtomicInteger(-1);
    private ScheduleCallback callback;
    private final AtomicBoolean canceled = new AtomicBoolean(false);

    public ScheduleShipData(SandBoxServerShip inShip, int inRemainTick, @Nullable ScheduleCallback inCallback) {
        ship = inShip;
        remainTick.set(inRemainTick);
        callback = inCallback;
    }
    public ScheduleShipData(ServerLevel level, CompoundTag tag) { load(level, tag); }

    public ScheduleShipData chaseUp(ServerLevel level) {
        if (callback == null || canceled.get()) {
            remainTick.set(-1);
            return this;
        }

        while (remainTick.get() >= 0) {
            callback.onScheduling(level, ship, remainTick.get(), canceled);
            if (canceled.get()) {
                return this;
            }

            remainTick.decrementAndGet();
        }
        return this;
    }
    public ScheduleShipData scheduleTick(ServerLevel level) {
        if (canceled.get()) return this;

        if (callback != null) {
            callback.onScheduling(level, ship, remainTick.get(), canceled);
        }
        remainTick.decrementAndGet();

        return this;
    }


    public int getRemainTick()   { return remainTick.get(); }
    public void remainTickDown() { remainTick.decrementAndGet(); }
    public boolean shouldSpawn() { return (remainTick.get() < 0) && (!canceled.get()); }
    public boolean isCanceled() { return canceled.get(); }

    @Override
    public CompoundTag saved(ServerLevel level) {
        NbtBuilder nbt = new NbtBuilder();

        //handle schedulingCb
        if (callback != null) {
            byte[] callbackBytes = SerializeUtil.safeSerialize(callback);
            if (callbackBytes == null || callbackBytes.length == 0) {
                EzDebug.warn("can't save scheduling callback, will chase up callback");
                chaseUp(level);  //chase up and don't serialize callback
            } else {  //simply save callback
                nbt.putBytes("schedule_callback", callbackBytes);
            }
        }

        return nbt
            .putCompound("saved_ship", ship.saved(level))
            .putNumber("remain_tick", remainTick.get())
            .putBoolean("canceled", canceled.get())
            .get();
    }

    @Override
    public ScheduleShipData load(ServerLevel level, CompoundTag tag) {
        NbtBuilder tReader = NbtBuilder.modify(tag);

        if (tReader.contains("schedule_callback")) {
            callback = SerializeUtil.safeDeserialize(tReader.getBytes("scheduling_callback"));
            if (callback == null) {
                EzDebug.warn("fail to read schedule callback");
            }
        }

        ship = new SandBoxServerShip(level, tReader.getCompound("saved_ship"));
        remainTick.set(tReader.getInt("remain_tick"));
        canceled.set(tReader.getBoolean("canceled"));

        return this;
    }
}
