package com.lancas.vswap.ship.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lancas.vswap.content.block.blocks.industry.projector.IProjectBlock;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.foundation.TriTuple;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class RRWChunkyShipSchemeData implements IShipSchemeData, IShipSchemeRandomReader, IShipSchemeRandomWriter {
    public static BlockPos getOriginInShipForScheme(Level level, Ship ship) {  //is the lower corner of start chunk
        BlockPos center = ShipUtil.getCenterShipBP(level, ship);
        return new BlockPos(
            (center.getX() >> 4) << 4,  //to chunk and then to chunk lower
            level.getMinBuildHeight() + center.getY(),  //real Y
            (center.getZ() >> 4) << 4
        );
    }

    public static final AABBic EMPTY_AABB = new AABBi();

    //compount tag is nullable (no be)
    //private LinkedHashMap<BlockPos, BiTuple<BlockState, CompoundTag>> shipData = new LinkedHashMap<>();
    //offset chunk, map<bpInLocalSpace, blockData>
    //localBp is offset to lower of startChunk, not the lower of keyChunk
    private final LinkedHashMap<BiTuple.ChunkXZ, Hashtable<BlockPos, BiTuple<BlockState, CompoundTag>>> shipData = new LinkedHashMap<>();
    private final Vector3d scale = new Vector3d(1, 1, 1);
    private final AABBi localAABB = new AABBi();

    private final Hashtable<Class<? extends ISavableAttachment>, BiTuple<List<TriTuple.ChunkXZOffsetTuple>, String>> savedAttachments = new Hashtable<>();


    @Override
    public RRWChunkyShipSchemeData readShip(ServerLevel level, ServerShip ship) {
        localAABB.set(EMPTY_AABB);
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
            //BlockPos offsetSectionLower = new BlockPos(offsetChunkX << 4, i << 4, offsetChunkZ << 4);
            for (int x = 0; x <= 15; ++x)
                for (int y = 0; y <= 15; ++y)
                    for (int z = 0; z <= 15; ++z) {
                        BlockState state = section.getBlockState(x, y, z);
                        if (state.getBlock() instanceof IProjectBlock projectBlock) {
                            state = projectBlock.representBlock();  //get the real state if block is projectBlock
                        }
                        if (state.isAir()) continue;

                        //note that offset pos is offset from the start chunk lower corner
                        int offsetX = (offsetChunkX << 4) + x;
                        int offsetY = bottomY + y + level.getMinBuildHeight() - startBp.getY();
                        int offsetZ = (offsetChunkZ << 4) + z;
                        //EzDebug.log("offset bp:" + StrUtil.poslike(offsetX, offsetY, offsetZ));

                        /*int realX = (chunkX << 4) + x;
                        int realY = bottomY + y + level.getMinBuildHeight();
                        int realZ = (chunkZ << 4) + z;*/
                        BlockPos realBp = new BlockPos((chunkX << 4) + x, bottomY + y + level.getMinBuildHeight(), (chunkZ << 4) + z);


                        //TODO memory alloc freq gc?
                        //BlockPos offsetBp = new BlockPos(x, bottomY + y + level.getMinBuildHeight() - startBp.getY(), z);
                        BlockPos offsetBp = new BlockPos(offsetX, offsetY, offsetZ);
                        //BlockPos realBp = new BlockPos(realX, realY, realZ);
                        //BlockEntity blockEntity = level.getBlockEntity(blockPos);
                        //BlockPos offsetBp = worldToSavedBp(realBp, startChunkX, startChunkZ);
                        BlockEntity be = realChunk.getBlockEntity(realBp);

                        chunkyData.put(offsetBp,
                            new BiTuple<>(state, be == null ? null : be.saveWithFullMetadata())
                        );
                        localAABB.union(offsetBp.getX(), offsetBp.getY(), offsetBp.getZ());
                        //EzDebug.log("aabb union " + StrUtil.poslike(offsetX, offsetY, offsetZ) + ", aabb:" + localAABB);

                        //EzDebug.log("add block:" + StrUtil.getBlockName(state) + " at " + StrUtil.getBlockPos(offsetBp) + ".at world " + );
                        /*EzDebug.log(
                            "chunk:" + StrUtil.poslike(chunkX, i, chunkZ) + ", offsetChunk:" + StrUtil.poslike(offsetChunkX, i, offsetChunkZ) +
                                "\nrealBp:" + realBp.toShortString() + ", offset:" + offsetBp.toShortString()
                        );*/
                    }
        });
        scale.set(ship.getTransform().getShipToWorldScaling());

        ObjectMapper mapper = new ObjectMapper();
        for (var type : ISavableAttachment.allToSave) {
            ISavableAttachment att = ship.getAttachment(type);
            if (att == null) continue;

            List<TriTuple.ChunkXZOffsetTuple> savedBps = new ArrayList<>();
            att.getAllBpInShipToSave().forEach(bp -> {
                savedBps.add(worldToSavedBp(bp, startBp/*startChunkX, startChunkZ*/));
            });

            savedAttachments.put(type, new BiTuple<>(savedBps, NbtBuilder.jacksonWriteAsStringRethrown(att, mapper)));
        }

        return this;
    }
    private TriTuple.ChunkXZOffsetTuple worldToSavedBp(BlockPos worldBp, BlockPos startBp) {
        int startWorldChunkX = startBp.getX() >> 4;
        int startWorldChunkZ = startBp.getZ() >> 4;

        int chunkX = worldBp.getX() >> 4;
        int chunkZ = worldBp.getZ() >> 4;

        int offsetChunkX = chunkX - startWorldChunkX;
        int offsetChunkZ = chunkZ - startWorldChunkZ;

        BlockPos chunkLower = new BlockPos(chunkX << 4, startBp.getY(), chunkZ << 4);

        EzDebug.log("worldToSavedBp, worldBp:" + worldBp.toShortString() + ", offsetChunkX:" + offsetChunkX + ", offsetChunkZ:" + offsetChunkZ + ", chunkLower:" + chunkLower.toShortString() + ", saved:" + worldBp.subtract(chunkLower).toShortString());

        return new TriTuple.ChunkXZOffsetTuple(offsetChunkX, offsetChunkZ, worldBp.subtract(chunkLower));
    }
    private BlockPos savedBpToWorldBp(TriTuple.ChunkXZOffsetTuple savedBp, BlockPos startBp) {
        int startWorldChunkX = startBp.getX() >> 4;
        int startWorldChunkZ = startBp.getZ() >> 4;

        int offsetChunkX = savedBp.getChunkX();
        int offsetChunkZ = savedBp.getChunkZ();
        BlockPos offset = savedBp.getOffset();

        int worldChunkX = startWorldChunkX + offsetChunkX;
        int worldChunkZ = startWorldChunkZ + offsetChunkZ;

        EzDebug.log("savedBpToWorldBp, offset:" + offset.toShortString() + ", offsetChunkX:" + offsetChunkX + ", offsetChunkZ:" + offsetChunkZ + ", worldChunkX:" + worldChunkX + ", worldChunkZ:" + worldChunkZ + ", loaded:" + new BlockPos((worldChunkX << 4) + offset.getX(), offset.getY(), (worldChunkZ << 4) + offset.getZ()).toShortString());


        return new BlockPos((worldChunkX << 4) + offset.getX(), offset.getY() + startBp.getY(), (worldChunkZ << 4) + offset.getZ());
    }

    /*private void saveOneBlock(Hashtable<BlockPos, BiTuple<BlockState, CompoundTag>> chunkData, LevelChunkSection section, LevelChunk chunk,
        int xInSection, int yInSection, int zInSection,
        BlockPos offsetSectionLower
    ) {
        BlockState state = section.getBlockState(xInSection, yInSection, zInSection);
        if (state.getBlock() instanceof IProjectBlock projectBlock) {
            state = projectBlock.representBlock();  //get the real state if block is projectBlock
        }
        if (state.isAir()) return;

        BlockPos offset = offsetSectionLower.offset(xInSection, yInSection, zInSection);
        //note that offset pos is offset from the start chunk lower corner
        /.*int offsetX = (offsetChunkX << 4) + x;
        int offsetY = bottomY + y;
        int offsetZ = (offsetChunkZ << 4) + z;*./
        //EzDebug.log("offset bp:" + StrUtil.poslike(offsetX, offsetY, offsetZ));

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

    }*/

    @Override
    public RRWChunkyShipSchemeData clear() {
        shipData.clear();
        scale.set(1, 1, 1);
        localAABB.set(EMPTY_AABB);
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
        int localToWorldAddY = WorldUtil.midY(level) - (int)localAABB.center(new Vector3d()).y;
        BlockPos startBp = JomlUtil.bpContaining(ship.getTransform().getPositionInShip()).atY(localToWorldAddY);
        int startChunkX = startBp.getX() >> 4;
        int startChunkZ = startBp.getZ() >> 4;
        //int startY = WorldUtil.midY(level);

        //offset pos is offset from the start chunk lower corner
        //BlockPos startChunkLowerCorner = new BlockPos(startChunkX << 4, level.getMinBuildHeight(), startChunkZ << 4);
        //BlockPos startBp = new BlockPos(startChunkX << 4, level.getMinBuildHeight(), startChunkZ << 4);

        //LevelChunk cachedChunk = null;
        //int cachedChunkX = 0;
        //int cachedChunkZ = 0;

        //EzDebug.log("place ship at chunk:" + StrUtil.poslike(startChunkX, 0, startChunkZ));

        for (var chunkData : shipData.entrySet()) {
            var chunkOffsetXZ = chunkData.getKey();
            var chunkBlocks = chunkData.getValue();

            int curRealChunkX = startChunkX + chunkOffsetXZ.getX();
            int curRealChunkZ = startChunkZ + chunkOffsetXZ.getZ();

            LevelChunk curChunk = level.getChunk(curRealChunkX, curRealChunkZ);

            //EzDebug.log("curChunkLowerCorner:" + StrUtil.poslike(curChunkLowerCorner));

            for (var chunkBlock : chunkBlocks.entrySet()) {
                BlockPos offsetBp = chunkBlock.getKey();//dataEntry.getKey();
                BlockState curState = chunkBlock.getValue().getFirst();
                CompoundTag beNbt = chunkBlock.getValue().getSecond();

                //int curRealChunkX = (offsetBp.getX() >> 4) + startChunkX;
                //int curRealChunkZ = (offsetBp.getZ() >> 4) + startChunkZ;

                /*if (cachedChunk == null || cachedChunkX != curRealChunkX || cachedChunkZ != curRealChunkZ) {
                    cachedChunk =  level.getChunk(curRealChunkX, curRealChunkZ);
                    cachedChunkX = curRealChunkX;
                    cachedChunkZ = curRealChunkZ;
                }*/

                BlockPos curRealBp = new BlockPos(offsetBp.getX() + (startChunkX << 4), offsetBp.getY() + startBp.getY(), offsetBp.getZ() + (startChunkZ << 4));//startChunkLowerCorner.offset(offsetBp);//startRealBp.offset(offsetBp);
                //EzDebug.log("offset bp:" + offsetBp.toShortString() + ", curRealBp:" + curRealBp.toShortString());

                //目前无法更改履带等方块，可能需要和vmod一样做一个延时更新机制
                curChunk.setBlockState(curRealBp, curState, true);
                //level.setBlock(realPos, state, Block.UPDATE_ALL);
                if (beNbt != null) {
                    BlockEntity be = BlockEntity.loadStatic(curRealBp, curState, beNbt);
                    if (be != null)
                        curChunk.addAndRegisterBlockEntity(be);
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
        ObjectMapper mapper = new ObjectMapper();
        for (var savedAtt : savedAttachments.entrySet()) {
            var type = savedAtt.getKey();
            var savedData = savedAtt.getValue();

            ISavableAttachment att = NbtBuilder.jacksonReadRethrown(savedData.getSecond(), type, mapper);

            List<BlockPos> inShipLoadedBps = new ArrayList<>();
            savedData.getFirst().forEach(saved -> {
                BlockPos inShipLoadedBp = savedBpToWorldBp(saved, startBp/*startChunkX, startChunkZ, */);
                inShipLoadedBps.add(inShipLoadedBp);
            });
            att.loadAllBp(inShipLoadedBps);

            ship.saveAttachment(type, att.getActual());
        }

        return ship;
    }
    public void placeAsBlocks(ServerLevel level, BlockPos localToWorld) {
        AtomicReference<Integer> worldChunkX = new AtomicReference<>(null);
        AtomicReference<Integer> worldChunkZ = new AtomicReference<>(null);
        AtomicReference<LevelChunk> worldChunk = new AtomicReference<>(null);

        allBlocksInLocal().forEach(b -> {
            BlockPos localBp = b.getBlockPos();
            BlockState state = b.getBlockState();
            CompoundTag beNbt = b.getBeNbt();

            BlockPos worldBp = localBp.offset(localToWorld);
            int curWorldChunkX = (worldBp.getX() >> 4);
            int curWorldChunkZ = (worldBp.getZ() >> 4);

            if (worldChunk.get() == null || curWorldChunkX != worldChunkX.get() || curWorldChunkZ != worldChunkZ.get()) {
                worldChunkX.set(curWorldChunkX);
                worldChunkZ.set(curWorldChunkZ);
                worldChunk.set(level.getChunk(curWorldChunkX, curWorldChunkZ));
            }

            worldChunk.get().setBlockState(worldBp, state, true);
            //level.setBlock(realPos, state, Block.UPDATE_ALL);
            if (beNbt != null) {
                BlockEntity be = BlockEntity.loadStatic(worldBp, state, beNbt);
                if (be != null)
                    worldChunk.get().addAndRegisterBlockEntity(be);
            }
            level.getChunkSource().blockChanged(worldBp);
        });
    }

    public BlockClusterData createSaShipBlockData(@Nullable TriConsumer<BlockClusterData, ISavableAttachment, Stream<Vector3ic>> attachmentCallback) {
        BlockClusterData blockData = new BlockClusterData();

        for (var chunkData : shipData.entrySet()) {
            var chunkOffsetXZ = chunkData.getKey();
            var chunkBlocks = chunkData.getValue();

            for (var chunkBlock : chunkBlocks.entrySet()) {
                Vector3i localPos = new Vector3i(
                    /*(chunkOffsetXZ.getX() >> 4) + */chunkBlock.getKey().getX(),
                    chunkBlock.getKey().getY(),
                    /*(chunkOffsetXZ.getZ() >> 4) + */chunkBlock.getKey().getZ()
                );
                BlockState curState = chunkBlock.getValue().getFirst();

                EzDebug.log(
                    "state:" + StrUtil.getBlockName(curState) +
                        ",chunkOffsetXZ:" + chunkOffsetXZ +
                        ", offset:" + chunkBlock.getKey().toShortString() +
                        ", localPos:" + StrUtil.poslike(localPos)
                );

                blockData.setBlock(localPos, curState);
            }
        }

        if (attachmentCallback != null) {
            //ObjectMapper mapper = new ObjectMapper();
            for (var savedAtt : savedAttachments.entrySet()) {
                var type = savedAtt.getKey();
                var savedData = savedAtt.getValue();

                ISavableAttachment att = NbtBuilder.jacksonReadRethrown(savedData.getSecond(), type, NbtBuilder.SIMPLE_MAPPER);

                attachmentCallback.accept(blockData,
                    att, savedAtt.getValue().getFirst().stream().map(
                        x -> JomlUtil.i(x.toRealBp())
                    )
                );
            }
        }

        return blockData;
    }

    public <T extends ISavableAttachment> boolean withSavedAttachmentDo(Class<T> attachmentType, BiConsumer<T, Stream<Vector3ic>> callback) {
        var savedAttachment = savedAttachments.get(attachmentType);
        if (savedAttachment == null)
            return false;

        T att = NbtBuilder.jacksonReadRethrown(savedAttachment.getSecond(), attachmentType, NbtBuilder.SIMPLE_MAPPER);
        if (att == null)
            return false;

        callback.accept(att, savedAttachment.getFirst().stream().map(x -> JomlUtil.i(x.toRealBp())));
        return true;
    }


    public static boolean isSavedEmpty(CompoundTag saved) {
        AABBi aabb = NbtBuilder.modify(saved).getAABBi("local_aabb");
        return !aabb.isValid();
    }


    @Override
    public CompoundTag saved() {
        return new NbtBuilder()
            .putMap("ship_data", shipData,
                (chunkOffsetXZ, chunkData) ->
                    new NbtBuilder()
                        .putInt("chunk_offset_x", chunkOffsetXZ.getX())
                        .putInt("chunk_offset_z", chunkOffsetXZ.getZ())
                        .putMap("chunk_data", chunkData, (pos, block) ->
                            NbtBuilder.tagOfBlock(pos, block.getFirst(), block.getSecond())
                        )
                        .get()
            )
            .putMap("saved_att", savedAttachments,
                (type, data) ->
                    new NbtBuilder()
                        .putString("type", type.getName())
                        .putEach("saved_bps", data.getFirst(), x ->
                            new NbtBuilder()
                                .putInt("chunk_offset_x", x.getChunkX())
                                .putInt("chunk_offset_z", x.getChunkZ())
                                .putBlockPos("offset", x.getOffset())
                                .get()
                        )
                        //.putEachSimpleJackson("saved_bps", data.getFirst())
                        .putString("saved_data", data.getSecond())
                        .get()
            )
            .putVector3d("scale", scale)
            .putAABBi("local_aabb", localAABB)
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
            .readMapOverwrite("saved_att", t -> {
                NbtBuilder reader = NbtBuilder.modify(t);
                BiTuple<Class<? extends ISavableAttachment>, BiTuple<List<TriTuple.ChunkXZOffsetTuple>, String>> entry = new BiTuple<>();
                try {
                    entry.setFirst((Class<? extends ISavableAttachment>)Class.forName(reader.getString("type")));
                    List<TriTuple.ChunkXZOffsetTuple> savedBps = new ArrayList<>();
                    reader.readEachCompoundOverwrite("saved_bps", savedBpsT -> {
                        TriTuple.ChunkXZOffsetTuple tuple = new TriTuple.ChunkXZOffsetTuple(0, 0, null);
                        NbtBuilder.modify(savedBpsT)
                            .readIntDo("chunk_offset_x", tuple::setFirst)
                            .readIntDo("chunk_offset_z", tuple::setSecond)
                            .readBlockPosDo("offset", tuple::setThird);
                        return tuple;
                    }, savedBps);
                    String savedData = reader.getString("saved_data");

                    entry.setSecond(new BiTuple<>(savedBps, savedData));

                } catch (Exception e) {
                    EzDebug.error("fail to load saved att");
                    e.printStackTrace();
                    //throw new RuntimeException(e);
                }

                return entry;
            }, savedAttachments)
            .readVector3d("scale", scale)
            .readAABBi("local_aabb", localAABB);

        return this;
    }


    @Override
    public IShipSchemeRandomReader getRandomReader() { return this; }

    @Override
    public @NotNull AABBic getLocalAabbContainsCoordinate() { return localAABB; }
    @Override
    public @NotNull AABBic getLocalAabbContainsShape() { return JomlUtil.extentFromLower(localAABB, 1, 1, 1, new AABBi()); }

    @Override
    public IShipSchemeRandomReader getRandomWriter() { return this; }


    @Override
    public BlockState getBlockStateByLocalPos(BlockPos localBp) {
        BiTuple.ChunkXZ chunkXZ = new BiTuple.ChunkXZ(localBp.getX() >> 4, localBp.getZ() >> 4);
        var chunkData = shipData.get(chunkXZ);
        if (chunkData == null) return Blocks.AIR.defaultBlockState();

        var blockData = chunkData.get(localBp);
        if (blockData == null) return Blocks.AIR.defaultBlockState();

        return blockData.getFirst();
    }
    @Override
    public void foreachBlockInLocal(BiConsumer<BlockPos, BlockState> consumer) {
        shipData.forEach(((chunkXZ, chunkData) -> {
            chunkData.forEach((localBp, blockData) -> {
                consumer.accept(localBp, blockData.getFirst());
            });
        }));
    }

    public Stream<BlockPos> allLocalBps() {
        return shipData.values().stream().
            flatMap(x -> x.keySet().stream());
    }
    public Stream<TriTuple.SavedBlockTuple> allBlocksInLocal() {
        return shipData.values().stream().
            flatMap(x ->
                x.entrySet().stream()
                    .map(y -> new TriTuple.SavedBlockTuple(
                        y.getKey(),
                        y.getValue().getFirst(),
                        y.getValue().getSecond())
                    )
            );
    }

    public void foreachBlockInLocal(TriConsumer<BlockPos, BlockState, CompoundTag> consumer) {
        shipData.forEach(((chunkXZ, chunkData) -> {
            chunkData.forEach((localBp, blockData) -> {
                consumer.accept(localBp, blockData.getFirst(), blockData.getSecond());
            });
        }));
    }

    @Override
    public List<TriTuple<BlockPos, BlockState, CompoundTag>> getCopyOfAllBlocksOverwrite(List<TriTuple<BlockPos, BlockState, CompoundTag>> dest) {
        dest.clear();

        dest.addAll(shipData.values().stream()
            .flatMap(x -> x.entrySet().stream())
            .map(x -> new TriTuple<>(x.getKey(), x.getValue().getFirst(), x.getValue().getSecond()))
            .toList()
        );
        return dest;
        /*return new Iterator<TriTuple<BlockPos, BlockState, CompoundTag>>() {
            private int ix = -1;
            private List<TriTuple<BlockPos, BlockState, CompoundTag>> allBlocks = null;

            @Override
            public boolean hasNext() {
                if (allBlocks == null) {
                    allBlocks = shipData.values().stream()
                        .flatMap(x -> x.entrySet().stream())
                        .map(x -> new TriTuple<>(x.getKey(), x.getValue().getFirst(), x.getValue().getSecond()))
                        .toList();
                }

                if (ix + 1 >= allBlocks.size())
                    return false;

                return true;
            }

            @Override
            public TriTuple<BlockPos, BlockState, CompoundTag> next() { return allBlocks.get(++ix); }

            @Override
            public void remove() {
                if (ix < 0) {
                    throw new IllegalStateException("try to remove element with ix:" + ix);
                }

                allBlocks.remove(ix--);
            }

            public void reset() { ix = -1; }
            public boolean isEmpty() { return allBlocks.isEmpty(); }
        };*/
    }

    @Override
    public boolean isEmpty() {
        for (var chunkData : shipData.values()) {
            if (!chunkData.isEmpty()) return false;
        }
        return true;
        //EzDebug.highlight("aabb:" + localAABB + ", shipData cnt:" + shipData.size());
        //return shipData.isEmpty();
    }  //may have empty map as value?


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
            //recalculate localAABB
            recalculateAABB();
        } else {
            chunkyData.put(localBp, new BiTuple<>(state, beNbt));
            //increasment update localAABB
            localAABB.union(JomlUtil.i(localBp));
        }
    }

    private void recalculateAABB() {
        localAABB.set(EMPTY_AABB);
        shipData.values().forEach(
            x -> x.keySet().forEach(
                p ->
                    localAABB.union(JomlUtil.i(p))
            )
        );
    }

    /*

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RRWChunkyShipSchemeData that = (RRWChunkyShipSchemeData) o;
        return Objects.equals(shipData, that.shipData) && Objects.equals(scale, that.scale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shipData, scale);
    }*/
}
