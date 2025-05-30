package com.lancas.vswap.content.saved.blockrecord;

import com.lancas.vswap.ModMain;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.event.impl.BiEventImpl;
import com.lancas.vswap.event.impl.SingleEventImpl;
import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Mod.EventBusSubscriber
public class BlockRecordRWMgr extends SavedData {
    public static class BlockRecordEvents {
        private BlockRecordEvents() {}

        public final Map<BiTuple.ChunkXZ, SingleEventImpl<ServerLevel>> chunkLoadedServerTickEvt = new ConcurrentHashMap<>();
        //event arg1: level, arg2: isChunkLoaded
        public final Map<BiTuple.ChunkXZ, BiEventImpl<ServerLevel, Boolean>> serverTickEvt = new ConcurrentHashMap<>();

        public void addChunkLoadedServerTicker(BlockPos bp, @NotNull Consumer<ServerLevel> listener) {
            chunkLoadedServerTickEvt.computeIfAbsent(BiTuple.ChunkXZ.chunkBlockIn(bp), k -> new SingleEventImpl<>())
                .addListener(listener);
        }
        public void addServerTicker(BlockPos bp, @NotNull BiConsumer<ServerLevel, Boolean> listener) {
            serverTickEvt.computeIfAbsent(BiTuple.ChunkXZ.chunkBlockIn(bp), k -> new BiEventImpl<>())
                .addListener(listener);
        }
        public void removeChunkLoadedServerTicker(BlockPos bp, @NotNull Consumer<ServerLevel> listener) {
            chunkLoadedServerTickEvt.computeIfAbsent(BiTuple.ChunkXZ.chunkBlockIn(bp), k -> new SingleEventImpl<>())
                .remove(listener);
        }
        public void removeServerTicker(BlockPos bp, @NotNull BiConsumer<ServerLevel, Boolean> listener) {
            serverTickEvt.computeIfAbsent(BiTuple.ChunkXZ.chunkBlockIn(bp), k -> new BiEventImpl<>())
                .remove(listener);
        }


        public void invokeAll(ServerLevel level) {
            try {
                ServerChunkCache chunkSrc = level.getChunkSource();
                for (var chunkLoadedTickEvt : chunkLoadedServerTickEvt.entrySet()) {
                    BiTuple.ChunkXZ chunkXZ = chunkLoadedTickEvt.getKey();
                    boolean isChunkLoaded = chunkSrc.hasChunk(chunkXZ.getX(), chunkXZ.getZ());

                    if (isChunkLoaded) chunkLoadedTickEvt.getValue().invokeAll(level);
                }

                for (var serverTickEvt : serverTickEvt.entrySet()) {
                    BiTuple.ChunkXZ chunkXZ = serverTickEvt.getKey();
                    boolean isChunkLoaded = chunkSrc.hasChunk(chunkXZ.getX(), chunkXZ.getZ());

                    serverTickEvt.getValue().invokeAll(level, isChunkLoaded);
                }
            } catch (Exception e) {
                EzDebug.log("fail to invoke evetn, exception:" + e);
                e.printStackTrace();
            }
        }
    }
    //public static Timer asyncTimer = new Timer();

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

    //private ServerLevel level;
    private final Map<BlockPos, IBlockRecord> allRecords = new ConcurrentHashMap<>();
    public final BlockRecordEvents events = new BlockRecordEvents();
    //private final Map<BiTuple.ChunkXZ, Map<BlockPos, IBlockRecord>> tickRecords = new ConcurrentHashMap<>();
    /*private final Map<BiTuple.ChunkXZ, Map<BlockPos, IBlockRecord.ChunkLoadedTicker>> chunkLoadedTickers = new ConcurrentHashMap<>();
    private final Map<BiTuple.ChunkXZ, Map<BlockPos, IBlockRecord.AlwaysTicker>> chunkAlwaysTickers = new ConcurrentHashMap<>();
    private final Map<BlockPos, TimerTask> asyncTickers = new ConcurrentHashMap<>();*/


    //public final BiEventImpl<ServerLevel, BlockPos> eternalServer
    //public final TriEventImpl<ServerLevel, BlockPos, BlockState> withBlockState


    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
        try {
            return new NbtBuilder()
                .putMap("records", allRecords,
                    (k, v) -> new NbtBuilder()
                        .putBlockPos("bp", k)
                        .putSimpleJackson("record", v)
                        .get()
                ).get();
        } catch (Exception e) {
            EzDebug.error("fail to load block data, will lose all data. exception:" + e);
            e.printStackTrace();
            return new CompoundTag();
        }
    }
    public void load(CompoundTag tag) {
        try {
            NbtBuilder nbtBuilder = NbtBuilder.copy(tag);

            nbtBuilder.readMapDo("records",
                t -> {
                    BiTuple<BlockPos, IBlockRecord> entryTuple = new BiTuple<>();
                    NbtBuilder.modify(t)
                        .readBlockPosDo("bp", entryTuple::setFirst)
                        .readSimpleJacksonDo("record", IBlockRecord.class, entryTuple::setSecond);

                    return entryTuple;
                },
                this::putRecordImpl
            );
        } catch (Exception e) {
            EzDebug.error("fail to load block data, will lose all data. exception:" + e);
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
    private void putRecordImpl(BlockPos bp, IBlockRecord record) {
        allRecords.put(bp, record);
        record.onAdded(bp, this);

        EzDebug.log("put record at " + bp.toShortString());

        /*var chunkLoadedTicker = record.chunkLoadedTicker();
        if (chunkLoadedTicker != null) {
            chunkLoadedTickers.computeIfAbsent(
                BiTuple.ChunkXZ.chunkBlockIn(bp),
                k -> new ConcurrentHashMap<>()
            ).put(bp, chunkLoadedTicker);
        }

        var alwaysTicker = record.alwaysTicker();
        if (alwaysTicker != null) {
            chunkAlwaysTickers.computeIfAbsent(
                BiTuple.ChunkXZ.chunkBlockIn(bp),
                k -> new ConcurrentHashMap<>()
            ).put(bp, alwaysTicker);
        }

        Dest<Long> intervalMs = new Dest<>();
        var asyncTicker = record.asyncTicker(intervalMs);
        if (alwaysTicker != null && intervalMs.hasValue()) {
            TimerTask newTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        asyncTicker.tick(bp);
                    } catch (Exception e) {
                        EzDebug.error("fail to async tick");
                        e.printStackTrace();
                    }
                }
            };

            TimerTask prevTask = asyncTickers.put(bp, newTask);
            if (prevTask != null)
                prevTask.cancel();

            asyncTimer.scheduleAtFixedRate(newTask, 0, intervalMs.get());
        }*/

        setDirty();
    }

    public static <T extends IBlockRecord> T removeRecord(ServerLevel level, BlockPos bp) {
        BlockRecordRWMgr mgr = getOrCreate(level);
        //IBlockRecord record = mgr.getRecordImpl(bp);
        IBlockRecord record = mgr.allRecords.remove(bp);

        if (record != null)
            record.onRemoved(bp, mgr);
        /*var chunk = BiTuple.ChunkXZ.chunkBlockIn(bp);
        var chunkTickers = mgr.chunkLoadedTickers.get(chunk);
        if (chunkTickers != null) {
            chunkTickers.remove(bp);
            if (chunkTickers.isEmpty())
                mgr.chunkLoadedTickers.remove(chunk);  //save memory
        }

        var alwaysTickers = mgr.chunkAlwaysTickers.get(chunk);
        if (alwaysTickers != null) {
            alwaysTickers.remove(bp);
            if (alwaysTickers.isEmpty())
                mgr.chunkAlwaysTickers.remove(chunk);  //save memory
        }

        TimerTask scheduled = mgr.asyncTickers.remove(bp);
        if (scheduled != null)
            scheduled.cancel();*/
        mgr.setDirty();

        EzDebug.log("remove record at " + bp.toShortString());

        try {
            return (T)record;
        } catch (Exception e) { return null; }
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
    //what's the meaning to have this method?
    //I can just get the reference and set the value.
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
        IBlockRecord data = allRecords.get(bp);
        /*if (data == null) {
            data = tickRecords.get(bp);
        }*/

        if (data == null) {
            EzDebug.warn("[BlockRecordRWMgr]can't get record at " + bp.toShortString());
            //EzDebug.logs(allRecords.keySet(), null);
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

            mgr.events.invokeAll(curLevel);

            /*for (var chunkTickers : mgr.chunkLoadedTickers.entrySet()) {
                var chunk = chunkTickers.getKey();
                if (!chunkSrc.hasChunk(chunk.getX(), chunk.getZ())) continue;  //don't tick if chunk is not loaded

                for (var chunkTicker : chunkTickers.getValue().entrySet()) {
                    BlockPos bp = chunkTicker.getKey();
                    chunkTicker.getValue().tick(curLevel, bp);
                }
            }
            for (var alwaysTickers : mgr.chunkAlwaysTickers.entrySet()) {
                var chunk = alwaysTickers.getKey();
                boolean chunkLoaded = chunkSrc.hasChunk(chunk.getX(), chunk.getZ());

                for (var chunkTicker : alwaysTickers.getValue().entrySet()) {
                    BlockPos bp = chunkTicker.getKey();
                    chunkTicker.getValue().tick(curLevel, bp, chunkLoaded);
                }
            }*/

            mgr.setDirty();
        }
    }
}
