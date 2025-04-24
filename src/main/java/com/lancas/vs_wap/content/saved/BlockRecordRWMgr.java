package com.lancas.vs_wap.content.saved;

import com.lancas.vs_wap.ModMain;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Mod.EventBusSubscriber
public class BlockRecordRWMgr extends SavedData {
    public static BlockRecordRWMgr getOrCreate(ServerLevel inLevel) {
        return inLevel.getDataStorage().computeIfAbsent(
            nbt -> {
                BlockRecordRWMgr mgr = new BlockRecordRWMgr();
                mgr.load(nbt);
                return mgr;
            },
            BlockRecordRWMgr::new,
            ModMain.MODID + "_block_record"
        );
    }

    private ServerLevel level;
    private Map<BlockPos, IBlockRecord> rwRecords = new ConcurrentHashMap<>();
    private Map<BlockPos, IBlockRecord> tickRecords = new ConcurrentHashMap<>();

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        try {
            return new NbtBuilder()
                .putCompound("tick_records",
                    new NbtBuilder().putEach("block_poses", tickRecords.keySet(), NbtBuilder::ofBlockPos)
                        .putEachSimpleJackson("record_values", tickRecords.values())
                        .get()
                )
                .putCompound("rw_records",
                    new NbtBuilder().putEach("block_poses", rwRecords.keySet(), NbtBuilder::ofBlockPos)
                        .putEachSimpleJackson("record_values", rwRecords.values())
                        .get()
                )
                .get();

        } catch (Exception e) {
            EzDebug.error("fail to load block data, will lose all data. exception:" + e.toString());
        }
        return new CompoundTag();
    }
    public void load(CompoundTag tag) {
        try {
            NbtBuilder nbtBuilder = NbtBuilder.copy(tag);
            List<BlockPos> bps = new ArrayList<>();
            List<IBlockRecord> records = new ArrayList<>();
            Dest<CompoundTag> recordsDest = new Dest<>();

            nbtBuilder.readCompound("tick_records", recordsDest);
            NbtBuilder.copy(recordsDest.get())
                .readEachAsCompound("block_poses", NbtBuilder::blockPosValueOf, bps)
                .readEachSimpleJackson("record_values", IBlockRecord.class, records);

            if (bps.size() != records.size()) { EzDebug.error("block pos count don't match dataVals size"); return; }
            else {
                for (int i = 0; i < bps.size(); ++i) {
                    tickRecords.put(bps.get(i), records.get(i));
                }
            }

            bps.clear();
            records.clear();
            recordsDest.set(null);

            nbtBuilder.readCompound("rw_records", recordsDest);
            NbtBuilder.copy(recordsDest.get())
                .readEachAsCompound("block_poses", NbtBuilder::blockPosValueOf, bps)
                .readEachSimpleJackson("record_values", IBlockRecord.class, records);

            if (bps.size() != records.size()) { EzDebug.error("block pos count don't match dataVals size"); return; }
            else {
                for (int i = 0; i < bps.size(); ++i) {
                    rwRecords.put(bps.get(i), records.get(i));
                }
            }

        } catch (Exception e) {
            EzDebug.error("fail to load block data, will lose all data. exception:" + e.toString());
        }
    }

    public static void putRecord(ServerLevel level, BlockPos bp, IBlockRecord record) {
        if (record == null) {
            EzDebug.warn("don't expect a null record!");
            return;
        }

        BlockRecordRWMgr mgr = getOrCreate(level);
        mgr.putRecordImpl(bp, record);
    }
    private void putRecordImpl(BlockPos bp, IBlockRecord data) {
        if (data.shouldTick()) {
            tickRecords.put(bp, data);
            setDirty();
            return;
        }

        rwRecords.put(bp, data);
        setDirty();
    }

    public static <T extends IBlockRecord> T removeRecord(ServerLevel level, BlockPos bp) {
        BlockRecordRWMgr mgr = getOrCreate(level);
        T record = mgr.getRecordImpl(bp);
        mgr.rwRecords.remove(bp);
        mgr.tickRecords.remove(bp);
        mgr.setDirty();
        return record;
    }
    @Nullable
    public static <T extends IBlockRecord> T getRecord(ServerLevel level, BlockPos bp) {
        BlockRecordRWMgr mgr = getOrCreate(level);
        return mgr.getRecordImpl(bp);
    }
    @NotNull
    public static <T extends IBlockRecord> T getRecordNonNull(ServerLevel level, BlockPos bp) {
        BlockRecordRWMgr mgr = getOrCreate(level);
        return Objects.requireNonNull(mgr.getRecordImpl(bp));
    }
    public static <T extends IBlockRecord> void changeIfExist(ServerLevel level, BlockPos bp, Function<T, T> changer) {
        BlockRecordRWMgr mgr = getOrCreate(level);

        T record = mgr.getRecordImpl(bp);
        if (record == null) return;

        T newRecord = changer.apply(record);
        if (newRecord == null) return;

        mgr.putRecordImpl(bp, newRecord);
    }

    @Nullable
    private <T extends IBlockRecord> T getRecordImpl(BlockPos bp) {
        IBlockRecord data = rwRecords.get(bp);
        if (data == null) {
            data = tickRecords.get(bp);
        }

        if (data == null) {
            EzDebug.warn("can't get record at " + bp.toShortString() + ", hasRWKey?:" + rwRecords.containsKey(bp) + ", hasTickKey?:" + tickRecords.containsKey(bp));
            EzDebug.logs(rwRecords.keySet(), null);
            return null;
        }

        try {
            return (T)data;
        } catch (Exception e) {
            EzDebug.warn("fail to convert from type:" + (data == null ? "null" : data.getClass().toString()));
            return null;
        }
    }

    public static void setDirtyOf(ServerLevel level) {
        BlockRecordRWMgr mgr = getOrCreate(level);
        mgr.setDirty();
    }


    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (ServerLevel curLevel : event.getServer().getAllLevels()) {
            BlockRecordRWMgr mgr = BlockRecordRWMgr.getOrCreate(curLevel);

            for (var tickRecord : mgr.tickRecords.entrySet()) {
                BlockPos bp = tickRecord.getKey();
                IBlockRecord record = tickRecord.getValue();

                if (!record.shouldTick()) {
                    EzDebug.warn("there is a record in should tick records is shouldn't tick! Maybe it's tempory disabled for effecient?");
                    continue;
                }

                record.onTick(bp);
                mgr.setDirty();
            }
        }
    }
}
