package com.lancas.vs_wap.content.saved;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lancas.vs_wap.ModMain;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.data.SavedBlockPos;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

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
    private Map<SavedBlockPos, IBlockRecord> records = new ConcurrentHashMap<>();

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        try {
            return new NbtBuilder()
                .putSimpleJackson("block_poses", records.keySet())
                .putEachSimpleJackson("record_values", records.values())
                .get();

        } catch (Exception e) {
            EzDebug.error("fail to load block data, will lose all data. exception:" + e.toString());
        }
        return new CompoundTag();
    }
    public void load(CompoundTag tag) {
        try {
            NbtBuilder nbtBuilder = NbtBuilder.copy(tag);
            List<SavedBlockPos> bps = nbtBuilder.readSimpleJackson("block_poses", new TypeReference<ArrayList<SavedBlockPos>>() {});
            List<IBlockRecord> dataValues = new ArrayList<>();
            nbtBuilder.readEachSimpleJackson("record_values", IBlockRecord.class, dataValues);

            if (bps.size() != dataValues.size()) {
                EzDebug.error("block pos count don't match dataVals size");
                return;
            }

            for (int i = 0; i < bps.size(); ++i) {
                records.put(bps.get(i), dataValues.get(i));
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
        records.put(new SavedBlockPos(bp), data);
        setDirty();
    }

    public static <T extends IBlockRecord> T removeRecord(ServerLevel level, BlockPos bp) {
        BlockRecordRWMgr mgr = getOrCreate(level);
        T record = mgr.getRecordImpl(bp);
        mgr.records.remove(new SavedBlockPos(bp));
        mgr.setDirty();
        return record;
    }
    @Nullable
    public static <T extends IBlockRecord> T getRecord(ServerLevel level, BlockPos bp) {
        BlockRecordRWMgr mgr = getOrCreate(level);
        return mgr.getRecordImpl(bp);
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
        IBlockRecord data = records.get(new SavedBlockPos(bp));

        if (data == null) {
            EzDebug.warn("can't get record at " + bp.toShortString() + ", hasKey?:" + records.containsKey(new SavedBlockPos(bp)));
            EzDebug.logs(records.keySet(), null);
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



}
