package com.lancas.vs_wap.subproject.sandbox.ship;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.api.ISavedLevelObject;
import com.lancas.vs_wap.util.NbtBuilder;
import com.lancas.vs_wap.util.SerializeUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ScheduleShipData implements ISavedLevelObject<ScheduleShipData> {
    //private static final Logger log = LogManager.getLogger(ScheduleShipData.class);

    @FunctionalInterface
    public interface ScheduleCallback extends Serializable {
        public void onScheduling(Level level, ISandBoxShip ship, int remainTick, AtomicBoolean canceled);
    }

    public ISandBoxShip ship;
    private final AtomicInteger remainTick = new AtomicInteger(-1);
    private ScheduleCallback callback;
    private final AtomicBoolean canceled = new AtomicBoolean(false);

    private ScheduleShipData() {}
    public ScheduleShipData(ISandBoxShip inShip, int inRemainTick, @Nullable ScheduleCallback inCallback) {
        ship = inShip;
        remainTick.set(inRemainTick);
        callback = inCallback;
    }
    public static ScheduleShipData getServerBySavedData(ServerLevel level, CompoundTag tag) {
        ScheduleShipData data = new ScheduleShipData();
        return data.load(level, tag);
    }
    //public ScheduleShipData(Level level, CompoundTag tag) { load(level, tag); }

    //it should only called in server now.
    public ScheduleShipData chaseUp(Level level) {
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
    public ScheduleShipData scheduleTick(Level level) {
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

    public ScheduleCallback getCallback() { return callback; }

    @Override
    public CompoundTag saved(ServerLevel level) {
        if (!(ship instanceof SandBoxServerShip serverShip)) {
            EzDebug.warn("a client scheduled ship shouldn't be save!");
            return new CompoundTag();
        }

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
            .putCompound("saved_ship", serverShip.saved(level))
            .putInt("remain_tick", remainTick.get())
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
