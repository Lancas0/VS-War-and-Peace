package com.lancas.vswap.obsolete.mixin;

import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ClientChunkCache.class)
public class MixinClientChunkCache {
    @Shadow
    volatile ClientChunkCache.Storage storage;
    @Shadow
    @Final
    ClientLevel level;


    @Inject(method = "replaceWithPacketData", at = @At("HEAD"), cancellable = true)
    private void preLoadChunkFromPacket(final int x, final int z,
                                        final FriendlyByteBuf buf,
                                        final CompoundTag tag,
                                        final Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> consumer, final CallbackInfoReturnable<LevelChunk> cir) {
        /*final ClientChunkCacheStorageAccessor clientChunkMapAccessor =
            ClientChunkCacheStorageAccessor.class.cast(storage);
        if (!clientChunkMapAccessor.callInRange(x, z)) {
            if (VSGameUtilsKt.isChunkInShipyard(level, x, z)) {
                final long chunkPosLong = ChunkPos.asLong(x, z);

                final LevelChunk oldChunk = vs$shipChunks.get(chunkPosLong);
                final LevelChunk worldChunk;
                if (oldChunk != null) {
                    worldChunk = oldChunk;
                    oldChunk.replaceWithPacketData(buf, tag, consumer);
                } else {
                    worldChunk = new LevelChunk(this.level, new ChunkPos(x, z));
                    worldChunk.replaceWithPacketData(buf, tag, consumer);
                    vs$shipChunks.put(chunkPosLong, worldChunk);
                }

                this.level.onChunkLoaded(new ChunkPos(x, z));
                SodiumCompat.onChunkAdded(this.level, x, z);
                cir.setReturnValue(worldChunk);
            }
        }*/
        //EzDebug.Log("[MixinClientChunk]called, x:" + x + ", z:" + z);
        //EzDebug.logs(BallisticsController.ChunkManagement.id2InChunk, null);

        /*
        final ClientChunkCacheStorageAccessor clientChunkMapAccessor =
            ClientChunkCacheStorageAccessor.class.cast(storage);
        if (!clientChunkMapAccessor.callInRange(x, z)) {
            final long chunkPosLong = ChunkPos.asLong(x, z);
            if (BallisticsController.ChunkManagement.isAnyProjectileAroundChunk(chunkPosLong)) {
                LevelChunk chunk = new LevelChunk(this.level, new ChunkPos(x, z));
                chunk.replaceWithPacketData(buf, tag, consumer);
                this.level.onChunkLoaded(new ChunkPos(x, z));
                cir.setReturnValue(chunk);
            }
        }*/
    }
}
