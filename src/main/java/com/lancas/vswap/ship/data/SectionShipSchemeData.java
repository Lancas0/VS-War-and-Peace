package com.lancas.vswap.ship.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.lancas.vs_wap.content.blocks.industry.IgnoreByScheme;
import com.lancas.vswap.content.block.blocks.industry.projector.IProjectBlock;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class SectionShipSchemeData implements IShipSchemeData {
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE
    )
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SectionXZI {
        private int chunkXOffset, chunkZOffset, sectionI;
        private SectionXZI() {}
        public SectionXZI(int inOffsetX, int inOffsetZ, int inI) { chunkXOffset = inOffsetX; chunkZOffset = inOffsetZ; sectionI = inI; }

        public int getChunkXOffset() { return chunkXOffset; }
        public int getChunkZOffset() { return chunkZOffset; }
        public int getSectionI() { return sectionI; }
        public LevelChunkSection getRealSection(Level level, int startChunkX, int startChunkZ) {
            return level.getChunk(chunkXOffset + startChunkX, chunkZOffset + startChunkZ).getSection(sectionI);
        }
        public LevelChunk getRealChunk(Level level, int startChunkX, int startChunkZ) {
            return level.getChunk(chunkXOffset + startChunkX, chunkZOffset + startChunkZ);
        }
        public int getRealBottomY(Level level) { return (sectionI << 4) + level.getMinBuildHeight(); }
        public BlockPos getRealChunkLower(Level level, int startChunkX, int startChunkZ) { return new BlockPos(
            (chunkXOffset + startChunkX) << 4, getRealBottomY(level), (chunkZOffset + startChunkZ) << 4
        ); }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            SectionXZI that = (SectionXZI) o;
            return chunkXOffset == that.chunkXOffset && chunkZOffset == that.chunkZOffset && sectionI == that.sectionI;
        }
        @Override
        public int hashCode() {
            return Objects.hash(chunkXOffset, chunkZOffset, sectionI);
        }
    }
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE
    )
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SectionData {
        public ListTag data = new ListTag();
        public void addBlock(BlockPos offsetFromLower, BlockState state, @Nullable BlockEntity be) {
            data.add(NbtBuilder.tagOfBlock(offsetFromLower, state, be));
        }
        public void foreach(@NotNull TriConsumer<BlockPos, BlockState, CompoundTag> consumer) {
            data.forEach(tag -> {
                Dest<BlockPos> offsetFromLower = new Dest<>();
                Dest<BlockState> state = new Dest<>();
                Dest<CompoundTag> beTag = new Dest<>();
                NbtBuilder.blockOfTag((CompoundTag)tag, offsetFromLower, state, beTag);

                //BlockEntity be = tagDest.hasValue() ? BlockEntity.loadStatic(offsetFromLower.get(), state.get(), tagDest.get()) : null;
                consumer.accept(offsetFromLower.get(), state.get(), beTag.get());
            });
        }

        public ListTag getDataTag() { return data; }
        public static SectionData of(ListTag tag) { var data = new SectionData(); data.data = tag; return data; }
    }

    private HashMap<SectionXZI, SectionData> shipData = new HashMap<>();
    private double scale;  //todo 3d scale


    @Override
    public SectionShipSchemeData readShip(ServerLevel level, ServerShip ship) {
        shipData.clear();

        //all data is store as centerBp is (0, 0, 0)
        //todo: should I worry if the height is out the limit?
        BlockPos startBp = JomlUtil.bpContaining(ship.getTransform().getPositionInShip());
        int startChunkX = startBp.getX() >> 4;
        int startChunkZ = startBp.getZ() >> 4;

        ShipUtil.foreachSection(ship, level, (chunkX, chunkZ, sectionI, section) -> {
            if (section.hasOnlyAir()) return;  //it's checked. but for safe

            int offsetChunkX = chunkX - startChunkX;
            int offsetChunkZ = chunkZ - startChunkZ;
            LevelChunk realChunk = level.getChunk(chunkX, chunkZ);

            SectionXZI localSectionXZI = new SectionXZI(offsetChunkX, offsetChunkZ, sectionI);
            SectionData sectionData = new SectionData();

            int realBottomY = localSectionXZI.getRealBottomY(level);
            for (int x = 0; x <= 15; ++x)
                for (int y = 0; y <= 15; ++y)
                    for (int z = 0; z <= 15; ++z) {
                        BlockState state = section.getBlockState(x, y, z);
                        if (state.getBlock() instanceof IProjectBlock projectBlock) {
                            state = projectBlock.representBlock();    //get the real state if block is projectBlock
                        }

                        if (state.isAir()) continue;

                        int localX = x;//(offsetChunkX << 4) + x;
                        int localY = /*realBottomY + */y;
                        int localZ = z;//(offsetChunkZ << 4) + z;

                        //TODO memory alloc freq gc?
                        BlockPos offsetBp = new BlockPos(localX, localY, localZ);
                        BlockPos worldBp = new BlockPos(((chunkX << 4) + x), (realBottomY + y), ((chunkZ << 4) + z));
                        //BlockEntity blockEntity = level.getBlockEntity(blockPos);
                        BlockEntity be = realChunk.getBlockEntity(worldBp);
                        sectionData.addBlock(offsetBp, state, be);

                        //EzDebug.log("add block:" + StrUtil.getBlockName(state) + " at " + StrUtil.getBlockPos(offsetBp) + ".at world " + );
                        /*
                        EzDebug.log(
                            "chunkX:" + chunkX + ", chunkZ:" + chunkZ +
                                ", offChunkX:" + offsetChunkX + ", offChunkZ:" + offsetChunkZ +
                                ", world:" + worldBp.toShortString() + ", local:" + offsetBp.toShortString()
                        );
                         */
                    }

            shipData.put(localSectionXZI, sectionData);
        });
        scale = ship.getTransform().getShipToWorldScaling().x();

        return this;
    }

    @Override
    public SectionShipSchemeData clear() {
        shipData.clear();
        return this;
    }

    @Override
    public SectionShipSchemeData setScale(Vector3dc inScale) {
        scale = inScale.x();
        return this;
    }

    /*@Override
    public JsonSectionShipSchemeData addBlock(BlockPos offset, BlockState state) {

    }

    @Override
    public JsonSectionShipSchemeData addBlockEntity(BlockPos offset, BlockState state, CompoundTag entityTag) {
        return null;
    }*/
    /*@Override
    public int getBlockCnt() { return 0; }*/

    @Override
    public Vector3dc getScale() { return new Vector3d(scale, scale, scale); }

    @Override
    public ServerShip createShip(ServerLevel level) {  //all create ship is at (0, 0, 0)
        ServerShip ship = VSGameUtilsKt.getShipObjectWorld(level).createNewShipAtBlock(new Vector3i(), false, scale, VSGameUtilsKt.getDimensionId(level));
        /*BlockPos startBpInShip = JomlUtil.bpContaining(ship.getTransform().getPositionInShip());
        int startChunkX = startBpInShip.getX() >> 4;
        int startChunkZ = startBpInShip.getZ() >> 4;

        for (var dataEntry : shipData.entrySet()) {
            SectionXZI sectionXZI = dataEntry.getKey();
            SectionData sectionData = dataEntry.getValue();

            LevelChunk realChunk = sectionXZI.getRealChunk(level, startChunkX, startChunkZ);
            LevelChunkSection realSection = sectionXZI.getRealSection(level, startChunkX, startChunkZ);
            BlockPos realSectionLower = sectionXZI.getRealChunkLower(level, startChunkX, startChunkZ);

            sectionData.foreach(level, (offsetFromLower, state, beTag) -> {
                realSection.setBlockState(offsetFromLower.getX(), offsetFromLower.getY(), offsetFromLower.getZ(), state);

                if (beTag != null) {
                    BlockEntity be = BlockEntity.loadStatic(realSectionLower.offset(offsetFromLower), state, beTag);
                    if (be != null)
                        realChunk.addAndRegisterBlockEntity(be);
                }
            });
        }*/
        //todo use a light-weight method
        //ShipBuilder.modify(level, ship).s

        return overwriteEmptyShip(level, ship);
    }

    //suppose the ship is cleaned
    @Override
    public ServerShip overwriteEmptyShip(ServerLevel level, ServerShip ship) {
        BlockPos startBpInShip = JomlUtil.bpContaining(ship.getTransform().getPositionInShip());
        int startChunkX = startBpInShip.getX() >> 4;
        int startChunkZ = startBpInShip.getZ() >> 4;

        for (var dataEntry : shipData.entrySet()) {
            SectionXZI sectionXZI = dataEntry.getKey();
            SectionData sectionData = dataEntry.getValue();

            LevelChunk realChunk = sectionXZI.getRealChunk(level, startChunkX, startChunkZ);
            //ChunkHolder chunkHolder = level.getV(ChunkPos.asLong(startChunkX + sectionXZI.chunkXOffset, startChunkZ + sectionXZI.chunkZOffset));
            //LevelChunkSection realSection = sectionXZI.getRealSection(level, startChunkX, startChunkZ);
            BlockPos realSectionLower = sectionXZI.getRealChunkLower(level, startChunkX, startChunkZ);

            sectionData.foreach((offsetFromLower, state, beTag) -> {
                //if (offsetFromLower)
                //EzDebug.log("try to add block at offset:" + StrUtil.getBlockPos(offsetFromLower));


                //EzDebug.log("set block at local:" + StrUtil.getBlockPos(offsetFromLower) + ", at world:" + realSectionLower.offset(offsetFromLower));
                /*if (beTag != null) {
                    BlockEntity be = BlockEntity.loadStatic(realSectionLower.offset(offsetFromLower), state, beTag);
                    if (be != null)
                        realChunk.addAndRegisterBlockEntity(be);
                }*/

                BlockPos realPos = realSectionLower.offset(offsetFromLower);
                realChunk.setBlockState(realPos, state, true);
                //level.setBlock(realPos, state, Block.UPDATE_ALL);
                if (beTag != null) {
                    BlockEntity be = BlockEntity.loadStatic(realPos, state, beTag);
                    if (be != null)
                        realChunk.addAndRegisterBlockEntity(be);
                }
                level.getChunkSource().blockChanged(realPos);

                //EzDebug.log("set block:" + StrUtil.getBlockName(state) + ", at " + realPos.toShortString());
                EzDebug.log(
                    //"chunkX:" + chunkX + ", chunkZ:" + chunkZ +
                        "offChunkX:" + sectionXZI.chunkXOffset + ", offChunkZ:" + sectionXZI.chunkZOffset +
                        ", lower:" + realSectionLower.toShortString() +
                        ", offset:" + offsetFromLower.toShortString() +
                        ", real:" + realPos.toShortString()
                );
            });
        }

        return ship;
    }

    @Override
    public boolean isEmpty() {
        return shipData.isEmpty();  //todo maybe there is empty entry?
    }

    @Override
    public CompoundTag saved() {
        AtomicInteger sec_data = new AtomicInteger();
        NbtBuilder nbt = new NbtBuilder();
        nbt.putEachSimpleJackson("section_locations", shipData.keySet());
        nbt.putEach("section_data", shipData.values(), (d) -> { sec_data.getAndIncrement(); return d.getDataTag(); });
        nbt.putDouble("scale", scale);

        //EzDebug.log("key:" + shipData.keySet().size() + ", val:" + shipData.values().size() + ", loaded:" + ", val:" + sec_data.get());
        //nbt.putEachSimpleJackson("section_data", shipData.values());
        return nbt.get();
    }

    @Override
    public IShipSchemeData load(CompoundTag tag) {
        if (tag == null) return null;

        List<SectionXZI> locations = new ArrayList<>();
        List<SectionData> sectionData = new ArrayList<>();
        Dest<Double> scale = new Dest<>();

        NbtBuilder nbtBuilder = NbtBuilder.copy(tag)
            .readEachSimpleJackson("section_locations", SectionXZI.class, locations)
            .readEachList("section_data", SectionData::of, sectionData)
            .readDouble("scale", scale);
        //.readEachSimpleJackson("section_data", SectionData.class, sectionData);

        if (locations.size() != sectionData.size()) {
            throw new RuntimeException("location size don't match sectionData, locations:" + locations.size() + ", sectionData:" + sectionData.size());
        }

        SectionShipSchemeData data = new SectionShipSchemeData();
        for (int i = 0; i < locations.size(); ++i) {
            data.shipData.put(locations.get(i), sectionData.get(i));
        }
        data.scale = Dest.getIfElse(scale, v -> (v != null && v > 1E-10), 1.0);

        return data;
    }

    //it's really really slow
    /*
    @Override
    public IShipSchemeRandomReader getRandom() {
        return new IShipSchemeRandomReader() {
            @Override
            public BlockState getBlockStateByLocalBp(BlockPos pos) {
                SectionXZI xzi = new SectionXZI(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
                SectionData sectionData = shipData.get(xzi);
                if (sectionData == null) return Blocks.AIR.defaultBlockState();

                final BlockState[] getState = new BlockState[] { Blocks.AIR.defaultBlockState() };
                sectionData.foreach((bp, state, beTag) -> {
                    if (pos.equals(bp))
                        getState[0] = state;
                });
                return getState[0];
            }

            @Override
            public void foreachBlock(BiConsumer<BlockPos, BlockState> consumer) {
                for (var dataEntry : shipData.entrySet()) {
                    SectionXZI sectionXZI = dataEntry.getKey();
                    SectionData sectionData = dataEntry.getValue();

                    int xLower = sectionXZI.chunkXOffset << 4;
                    int yLower = (sectionXZI.sectionI << 4);
                    int zLower = sectionXZI.chunkZOffset << 4;

                    sectionData.foreach((offsetBp, state, beTag) -> {
                        consumer.accept(
                            new BlockPos(xLower + offsetBp.getX(), yLower + offsetBp.getY(), zLower + offsetBp.getZ()),
                            state
                        );
                        EzDebug.log(
                            "foreach localPos:" + new Vector3i(xLower + offsetBp.getX(), yLower + offsetBp.getY(), zLower + offsetBp.getZ()) +
                                "xLower:" + xLower + ", yLower:" + yLower + ", zLower:" + zLower +
                                "offsetBp:" + offsetBp.toShortString()
                        );
                    });
                }
            }
        };
    }*/

    /*@Override
    public void forEach(Level level, TriConsumer<BlockPos, BlockState, CompoundTag> func) {

    }*/

}
