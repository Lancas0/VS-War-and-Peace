package com.lancas.vs_wap.ship.data;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.NbtBuilder;
import com.lancas.vs_wap.util.ShipUtil;
import com.lancas.vs_wap.util.StrUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Hashtable;
import java.util.function.BiConsumer;

public class RRWChunkyShipSchemeData implements IShipSchemeData, IShipSchemeRandomReader, IShipSchemeRandomWriter {
    //compount tag is nullable (no be)
    //private LinkedHashMap<BlockPos, BiTuple<BlockState, CompoundTag>> shipData = new LinkedHashMap<>();
    private final Hashtable<BiTuple.ChunkXZ, Hashtable<BlockPos, BiTuple<BlockState, CompoundTag>>> shipData = new Hashtable<>();
    private final Vector3d scale = new Vector3d(1, 1, 1);


    @Override
    public RRWChunkyShipSchemeData readShip(ServerLevel level, ServerShip ship) {
        //保证一个区块内所有方块在一个区段内
        shipData.clear();

        //todo: should I worry if the height is out the limit?
        BlockPos startBp = JomlUtil.bpContaining(ship.getTransform().getPositionInShip());
        int startChunkX = startBp.getX() >> 4;
        int startChunkZ = startBp.getZ() >> 4;

        ShipUtil.foreachSection(ship, level, (chunkX, chunkZ, i, section) -> {
            if (section.hasOnlyAir()) return;  //it's checked. but for safe

            int offsetChunkX = chunkX - startChunkX;
            int offsetChunkZ = chunkZ - startChunkZ;
            LevelChunk realChunk = level.getChunk(chunkX, chunkZ);

            //EzDebug.log("startChunkX:" + StrUtil.poslike(chunkX, i, chunkZ) + ", chunk:" + StrUtil.poslike(offsetChunkX, i, offsetChunkZ));
            var chunkXZ = new BiTuple.ChunkXZ(offsetChunkX, offsetChunkZ);
            shipData.computeIfAbsent(chunkXZ, k -> new Hashtable<>());
            var chunkyData = shipData.get(chunkXZ);

            int bottomY = i << 4;
            for (int x = 0; x <= 15; ++x)
                for (int y = 0; y <= 15; ++y)
                    for (int z = 0; z <= 15; ++z) {
                        BlockState state = section.getBlockState(x, y, z);
                        if (state.isAir()) continue;

                        int offsetX = (offsetChunkX << 4) + x;
                        int offsetY = bottomY + y;
                        int offsetZ = (offsetChunkZ << 4) + z;

                        int realX = (chunkX << 4) + x;
                        int realY = bottomY + y + level.getMinBuildHeight();
                        int realZ = (chunkZ << 4) + z;

                        //TODO memory alloc freq gc?
                        BlockPos offsetBp = new BlockPos(offsetX, offsetY, offsetZ);
                        BlockPos realBp = new BlockPos(realX, realY, realZ);
                        //BlockEntity blockEntity = level.getBlockEntity(blockPos);
                        BlockEntity be = realChunk.getBlockEntity(realBp);

                        chunkyData.put(offsetBp,
                            new BiTuple<>(state, be == null ? null : be.saveWithFullMetadata())
                        );

                        //EzDebug.log("add block:" + StrUtil.getBlockName(state) + " at " + StrUtil.getBlockPos(offsetBp) + ".at world " + );

                        EzDebug.log(
                            "chunk:" + StrUtil.poslike(chunkX, i, chunkZ) + ", offsetChunk:" + StrUtil.poslike(offsetChunkX, i, offsetChunkZ) +
                                "\nrealBp:" + realBp.toShortString() + ", offset:" + offsetBp.toShortString()
                        );

                    }
        });
        scale.set(ship.getTransform().getShipToWorldScaling());

        return this;
    }

    @Override
    public RRWChunkyShipSchemeData clear() {
        shipData.clear();
        scale.set(1, 1, 1);
        return this;
    }
    @Override
    public RRWChunkyShipSchemeData setScale(Vector3dc newScale) { scale.set(newScale); return this; }  //todo 3d scale?
    @Override
    public Vector3dc getScale() { return scale; }

    @Override
    public ServerShip createShip(ServerLevel level) {
        //todo 3d scale?
        ServerShip ship = VSGameUtilsKt.getShipObjectWorld(level).createNewShipAtBlock(new Vector3i(), false, scale.x, VSGameUtilsKt.getDimensionId(level));
        return overwriteEmptyShip(level, ship);
    }
    @Override
    public ServerShip overwriteEmptyShip(ServerLevel level, ServerShip ship) {
        BlockPos startRealBp = JomlUtil.bpContaining(ship.getTransform().getPositionInShip());
        int startChunkX = startRealBp.getX() >> 4;
        int startChunkZ = startRealBp.getZ() >> 4;

        LevelChunk cachedChunk = null;
        int cachedChunkX = 0;
        int cachedChunkZ = 0;

        EzDebug.log("place ship at chunk:" + StrUtil.poslike(startChunkX, 0, startChunkZ));

        for (var chunkData : shipData.entrySet()) {
            var chunkOffsetXZ = chunkData.getKey();
            var chunkBlocks = chunkData.getValue();

            for (var chunkBlock : chunkBlocks.entrySet()) {
                BlockPos offsetBp = chunkBlock.getKey();//dataEntry.getKey();
                BlockState curState = chunkBlock.getValue().getFirst();
                CompoundTag beNbt = chunkBlock.getValue().getSecond();

                int curRealChunkX = (offsetBp.getX() >> 4) + startChunkX;
                int curRealChunkZ = (offsetBp.getZ() >> 4) + startChunkZ;

                if (cachedChunk == null || cachedChunkX != curRealChunkX || cachedChunkZ != curRealChunkZ) {
                    cachedChunk =  level.getChunk(curRealChunkX, curRealChunkZ);
                    cachedChunkX = curRealChunkX;
                    cachedChunkZ = curRealChunkZ;
                }

                BlockPos curRealBp = startRealBp.offset(offsetBp);

                //目前无法更改履带等方块，可能需要和vmod一样做一个延时更新机制
                cachedChunk.setBlockState(curRealBp, curState, true);
                //level.setBlock(realPos, state, Block.UPDATE_ALL);
                if (beNbt != null) {
                    BlockEntity be = BlockEntity.loadStatic(curRealBp, curState, beNbt);
                    if (be != null)
                        cachedChunk.addAndRegisterBlockEntity(be);
                }
                level.getChunkSource().blockChanged(curRealBp);

                EzDebug.log("set block:" + StrUtil.getBlockName(curState) + ", at " + curRealBp.toShortString());
            }
        }
        //for (var dataEntry : shipData.entrySet()) {

            /*EzDebug.log(
                //"chunkX:" + chunkX + ", chunkZ:" + chunkZ +
                "offChunkX:" + sectionXZI.chunkXOffset + ", offChunkZ:" + sectionXZI.chunkZOffset +
                    ", lower:" + realSectionLower.toShortString() +
                    ", offset:" + offsetFromLower.toShortString() +
                    ", real:" + realPos.toShortString()
            );*/
        //}

        return ship;
    }


    @Override
    public CompoundTag saved() {
        return new NbtBuilder()
            .putMap("ship_data", shipData,
                (chunkOffsetXZ, chunkData) ->
                    new NbtBuilder()
                        .putNumber("chunk_offset_x", chunkOffsetXZ.getX())
                        .putNumber("chunk_offset_z", chunkOffsetXZ.getZ())
                        .putMap("chunk_data", chunkData, (pos, block) ->
                            NbtBuilder.tagOfBlock(pos, block.getFirst(), block.getSecond())
                        )
                        .get()
            )
            .putVector3d("scale", scale)
            .get();
    }
    @Override
    public RRWChunkyShipSchemeData load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readMapOverwrite("ship_data",
                t -> {
                    NbtBuilder tReader = NbtBuilder.modify(t);
                    int chunkOffsetX = tReader.getInt("chunk_offset_x");
                    int chunkOffsetZ = tReader.getInt("chunk_offset_z");
                    Hashtable<BlockPos, BiTuple<BlockState, CompoundTag>> chunkData = new Hashtable<>();

                    tReader.readMapOverwrite("chunk_data", blockT -> {
                        BiTuple<BlockPos, BiTuple<BlockState, CompoundTag>> blockData = new BiTuple<>();
                        NbtBuilder.blockOfTagDo(blockT, (bp, state, beNbt) -> {
                            blockData.setFirst(bp);
                            blockData.setSecond(new BiTuple<>(state, beNbt));
                        });
                        return blockData;
                    }, chunkData);

                    return new BiTuple<>(new BiTuple.ChunkXZ(chunkOffsetX, chunkOffsetZ), chunkData);
                },
                shipData
            )
            .readVector3d("scale", scale);
        return this;
    }


    @Override
    public IShipSchemeRandomReader getRandomReader() { return this; }
    @Override
    public IShipSchemeRandomReader getRandomWriter() { return this; }


    @Override
    public BlockState getBlockStateByLocalBp(BlockPos localBp) {
        BiTuple.ChunkXZ chunkXZ = new BiTuple.ChunkXZ(localBp.getX() >> 4, localBp.getZ() >> 4);
        var chunkData = shipData.get(chunkXZ);
        if (chunkData == null) return Blocks.AIR.defaultBlockState();

        var blockData = chunkData.get(localBp);
        if (blockData == null) return Blocks.AIR.defaultBlockState();

        return blockData.getFirst();
    }
    @Override
    public void foreachBlock(BiConsumer<BlockPos, BlockState> consumer) {
        shipData.forEach(((chunkXZ, chunkData) -> {
            chunkData.forEach((localBp, blockData) -> {
                consumer.accept(localBp, blockData.getFirst());
            });
        }));
    }

    @Override
    public boolean isEmpty() { return shipData.isEmpty(); }


    @Override
    public void setBlockAtLocalBp(BlockPos localBp, BlockState state, @Nullable CompoundTag beNbt) {
        var chunkXZ = new BiTuple.ChunkXZ(localBp.getX() >> 4, localBp.getZ() >> 4);

        if (!shipData.containsKey(chunkXZ)) {
            if (state.isAir()) return;
            shipData.put(chunkXZ, new Hashtable<>());
        }

        //now remove block from a existed chunk, or place a new block
        var chunkyData = shipData.get(chunkXZ);

        if (state.isAir()) {
            chunkyData.remove(localBp);
            if (chunkyData.isEmpty()) {
                shipData.remove(chunkXZ);  //remove the chunkData if chunk is empty
            }
        } else {
            chunkyData.put(localBp, new BiTuple<>(state, beNbt));
        }
    }
}
