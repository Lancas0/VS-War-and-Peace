package com.lancas.vswap.obsolete;

/*
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;

public class ShipSchemeDataAsTag implements IShipSchemeData {
    public static final String BLOCKS_DATA_TAG = "blocks_data";
    public static final String SCALE_X_TAG = "scale_x";
    public static final String SCALE_Y_TAG = "scale_y";
    public static final String SCALE_Z_TAG = "scale_z";


    private CompoundTag tagData;
    private ListTag blockTags;

    public ShipSchemeDataAsTag() {
        tagData = new CompoundTag();
        blockTags = new ListTag();
        tagData.put(BLOCKS_DATA_TAG, blockTags);
    }

    public ShipSchemeDataAsTag(CompoundTag tag) {
        tagData = tag;
        if (tag.contains(BLOCKS_DATA_TAG)) {
            blockTags = tag.getList(BLOCKS_DATA_TAG, Tag.TAG_COMPOUND);
        } else {
            blockTags = new ListTag();
            tagData.put(BLOCKS_DATA_TAG, blockTags);
        }

        if (!tag.contains(SCALE_X_TAG)) {
            tagData.putDouble(SCALE_X_TAG, 1.0);
            tagData.putDouble(SCALE_Y_TAG, 1.0);
            tagData.putDouble(SCALE_Z_TAG, 1.0);
        }
    }

    public ShipSchemeDataAsTag fromTag(CompoundTag tag) {
        tagData = tag;
        if (tag.contains(BLOCKS_DATA_TAG)) {
            blockTags = tag.getList(BLOCKS_DATA_TAG, Tag.TAG_COMPOUND);
        } else {
            blockTags = new ListTag();
            tagData.put(BLOCKS_DATA_TAG, blockTags);
        }

        if (!tag.contains(SCALE_X_TAG)) {
            tagData.putDouble(SCALE_X_TAG, 1.0);
            tagData.putDouble(SCALE_Y_TAG, 1.0);
            tagData.putDouble(SCALE_Z_TAG, 1.0);
        }

        return this;
    }

    public CompoundTag toTag() {
        return tagData;
    }

    @Override
    public ShipSchemeDataAsTag readShip(ServerLevel level, ServerShip ship) {
        clear();

        BlockPos shipyardCenter = ShipUtil.getCenterShipBP(ship);

        ship.getActiveChunksSet().forEach((chunkX, chunkZ) -> {
            LevelChunk chunk = level.getChunk(chunkX, chunkZ);

            for (int i = chunk.getSections().length - 1; i >= 0; --i) {
                LevelChunkSection section = chunk.getSection(i);

                if (section.hasOnlyAir()) continue;

                int bottomY = i << 4;

                for (int x = 0; x <= 15; ++x)
                    for (int y = 0; y <= 15; ++y)
                        for (int z = 0; z <= 15; ++z) {
                            BlockState state = section.getBlockState(x, y, z);
                            //ignore air block
                            if (state.isAir()) continue;

                            int realX = (chunkX << 4) + x;
                            int realY = bottomY + y + level.getMinBuildHeight();
                            int realZ = (chunkZ << 4) + z;

                            //TODO memory alloc freq gc?
                            BlockPos curBlockPos = new BlockPos(realX, realY, realZ);
                            BlockPos offset = curBlockPos.subtract(shipyardCenter);
                            BlockEntity blockEntity = level.getBlockEntity(curBlockPos);

                            if (blockEntity == null) {
                                addBlock(offset, state);
                                EzDebug.log("add " + state.getBlock().getName().getString() + " at " + offset.toShortString());
                            } else {
                                addBlockEntity(offset, state, blockEntity.saveWithFullMetadata());
                                EzDebug.log("add " + state.getBlock().getName().getString() + " at " + offset.toShortString());
                            }
                        }
            }
        });
        Vector3dc scale = ship.getTransform().getShipToWorldScaling();
        tagData.putDouble(SCALE_X_TAG, scale.x());
        tagData.putDouble(SCALE_Y_TAG, scale.y());
        tagData.putDouble(SCALE_Z_TAG, scale.z());

        EzDebug.log("origin ship: posWorld = " + ship.getTransform().getPositionInWorld() + ", posShip = " + ship.getTransform().getPositionInShip());

        return this;
    }

    @Override
    public ShipSchemeDataAsTag clear() {
        blockTags.clear();
        tagData.putDouble(SCALE_X_TAG, 1.0);
        tagData.putDouble(SCALE_Y_TAG, 1.0);
        tagData.putDouble(SCALE_Z_TAG, 1.0);

        return this;
    }

    @Override
    public ShipSchemeDataAsTag setScale(Vector3dc scale) {
        tagData.putDouble(SCALE_X_TAG, scale.x());
        tagData.putDouble(SCALE_Y_TAG, scale.y());
        tagData.putDouble(SCALE_Z_TAG, scale.z());

        return this;
    }
}
*/
    /*@Override
    public ShipSchemeDataAsTag addBlock(BlockPos offset, BlockState state) {
        CompoundTag newTag = new CompoundTag();
        newTag.putInt("x", offset.getX());
        newTag.putInt("y", offset.getY());
        newTag.putInt("z", offset.getZ());

        newTag.put("state", NbtUtils.writeBlockState(state));

        blockTags.add(newTag);

        return this;
    }
    @Override
    public ShipSchemeDataAsTag addBlockEntity(BlockPos offset, BlockState state, CompoundTag entityTag) {
        CompoundTag newTag = new CompoundTag();
        newTag.putInt("x", offset.getX());
        newTag.putInt("y", offset.getY());
        newTag.putInt("z", offset.getZ());

        newTag.put("state", NbtUtils.writeBlockState(state));
        newTag.put("block_entity", entityTag);

        blockTags.add(newTag);

        return this;
    }

    @Override
    public int getBlockCnt() {
        return blockTags.size();
    }

    @Override
    public Vector3dc getScale() {
        return new Vector3d(
            tagData.getDouble(SCALE_X_TAG),
            tagData.getDouble(SCALE_Y_TAG),
            tagData.getDouble(SCALE_Z_TAG)
        );
    }


    @Override
    public void forEach(Level level, TriConsumer<BlockPos, BlockState, CompoundTag> func) {
        for (Tag current : blockTags) {
            CompoundTag curTag = (CompoundTag)current;

            //相对坐标
            int x = curTag.getInt("x");
            int y = curTag.getInt("y");
            int z = curTag.getInt("z");

            // 方块状态
            BlockState state = NbtUtils.readBlockState(
                    level.holderLookup(Registries.BLOCK),
                    curTag.getCompound("state")
            );

            //方块实体数据
            CompoundTag entityTag = curTag.getCompound("block_entity");

            func.accept(new BlockPos(x, y, z), state, entityTag);
        }
    }
}
*/