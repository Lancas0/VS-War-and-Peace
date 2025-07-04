package com.lancas.vswap.obsolete.chunkloading;

/*
import com.lancas.einherjar.ModMain;
import com.lancas.einherjar.debug.EzDebug;
import com.lancas.einherjar.ship.phys.ballistics.BallisticsController;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Map;

public class ChunkManagement extends SavedData {
    public static ChunkManagement getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            ChunkManagement::load,
            ChunkManagement::new,
            ModMain.MODID + "_chunkloading"
        );
    }

    private final LongOpenHashSet chunks;
    private final LongArrayFIFOQueue queue = new LongArrayFIFOQueue();
    private final LongOpenHashSet inQueue;
    private final Long2IntOpenHashMap loaded = new Long2IntOpenHashMap();

    public ChunkManagement() { this(new LongOpenHashSet()); }
    public ChunkManagement(LongOpenHashSet inChunks) {
        this.chunks = inChunks;
        this.inQueue = new LongOpenHashSet(this.chunks);
        for (long packedPos : this.chunks)
            this.queue.enqueue(packedPos);
    }

    public static ChunkManagement load(CompoundTag tag) {
        long[] arr = tag.getLongArray("LoadedChunks");
        LongOpenHashSet chunks = new LongOpenHashSet(arr);
        return new ChunkManagement(chunks);
    }
    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putLongArray("LoadedChunks", this.chunks.toLongArray());
        return compoundTag;
    }

    //@Deprecated
    public void queueForceLoad(ChunkPos pos) {
        EzDebug.log("force chunk:" + pos);
        long packedPos = pos.toLong();
        if (this.inQueue.add(packedPos)) {
            this.queue.enqueue(packedPos);
            this.chunks.add(packedPos);
            this.setDirty();
        }
    }

    public void tick(ServerLevel level) {
        //todo max count in config
        //now just use max int value
        int maxForcedChunkCnt = Integer.MAX_VALUE;
        int maxChunkProssed = Integer.MAX_VALUE;

        LongSet vanillaForcedChunks = level.getForcedChunks();
        int defaultLifespan = 200;
        int timeoutLifespan = 400;
        //int MAX_CHUNKS_PROCESSED = RPLConfigs.server().maxChunksLoadedEachTick.get();
        //int DEFAULT_AGE = RPLConfigs.server().projectileChunkAge.get();
        //int ENTITY_LOAD_TIMEOUT_AGE = -RPLConfigs.server().entityLoadTimeout.get() - 1;

        LongOpenHashSet expired = new LongOpenHashSet();
        for (Map.Entry<Long, Integer> entry : this.loaded.long2IntEntrySet()) {
            long packedPos = entry.getKey();
            int age = entry.getValue();
            if (age <= -1) {
                // Inefficient but less accessor boilerplate
                ChunkPos cpos = new ChunkPos(packedPos);
                BlockPos bpos = new BlockPos(SectionPos.sectionToBlockCoord(cpos.x), 0, SectionPos.sectionToBlockCoord(cpos.z));

                if (BallisticsController.ChunkManagement.isAnyProjectileAroundChunk(cpos.toLong())) {
                    EzDebug.log("any projectile around chunk");
                    age = defaultLifespan;
                }
                //if (level.isPositionEntityTicking(bpos))
                //    age = defaultLifespan;
            }
            int newAge = age - 1;
            entry.setValue(newAge);
            if (newAge == 0 || newAge <= timeoutLifespan)
                expired.add(packedPos);
        }

        int freeSlots = Math.max(0, maxForcedChunkCnt - this.loaded.size());
        int pollCount = Math.min(maxChunkProssed, freeSlots + expired.size());
        pollCount = Math.min(pollCount, this.queue.size());
        for (int i = 0; i < pollCount; ++i) {
            if (this.queue.isEmpty())
                break;
            long packedPos = this.queue.dequeueLong();
            this.inQueue.remove(packedPos);
            ChunkPos chunkPos = new ChunkPos(packedPos);
            if (this.loaded.containsKey(packedPos) && this.loaded.get(packedPos) > -1) {
                this.loaded.put(packedPos, defaultLifespan);
                expired.remove(packedPos);
            } else if (!vanillaForcedChunks.contains(packedPos) && loadChunkNoGenerate(level, chunkPos)) {
                this.loaded.put(packedPos, -1);
                level.getChunkSource().updateChunkForced(chunkPos, true);
            } else {
                this.chunks.remove(packedPos);
            }
        }

        for (long packedPos : expired) {
            level.getChunkSource().updateChunkForced(new ChunkPos(packedPos), false);
            this.loaded.remove(packedPos);
            if (!this.inQueue.contains(packedPos))
                this.chunks.remove(packedPos);
        }
        this.loaded.trim();
        this.inQueue.trim();
        this.queue.trim();
        this.chunks.trim();
        this.setDirty();
    }

    // Largely modeled after CraftBukkit World#loadChunk

    private static boolean loadChunkNoGenerate(ServerLevel level, ChunkPos cpos) {
        ServerChunkCache source = level.getChunkSource();
        ChunkAccess immediate = source.getChunkNow(cpos.x, cpos.z);
        if (immediate != null)
            return true;
        ChunkAccess access = source.getChunk(cpos.x, cpos.z, ChunkStatus.EMPTY, true);
        if (access instanceof ProtoChunk) {
            source.removeRegionTicket(TicketType.UNKNOWN, cpos, -11, cpos);
            access = source.getChunk(cpos.x, cpos.z, ChunkStatus.FULL, true);
        }
        return access instanceof LevelChunk;
    }

}*/