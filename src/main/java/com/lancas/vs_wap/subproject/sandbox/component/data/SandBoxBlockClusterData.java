package com.lancas.vs_wap.subproject.sandbox.component.data;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.Map;

//todo note no sync
public class SandBoxBlockClusterData implements IComponentData<SandBoxBlockClusterData>, IExposedBlockClusterData {
    public static SandBoxBlockClusterData EMPTY() {
        return new SandBoxBlockClusterData();
    }
    public static SandBoxBlockClusterData BlockAtCenter(BlockState state) {
        SandBoxBlockClusterData data = new SandBoxBlockClusterData();
        data.setBlock(new BlockPos(0, 0, 0), state);
        return data;
    }


    public final Map<BlockPos, BlockState> blocks = new Hashtable<>();

    @Nullable
    public BlockState setBlock(BlockPos localPos, BlockState state) {
        if (state == null || state.isAir()) {
            return removeBlock(localPos);
        }

        return blocks.put(localPos, state);
    }
    @Nullable
    public BlockState removeBlock(BlockPos localPos) {
        return blocks.remove(localPos);
    }

    @Override
    @Nullable
    public BlockState getBlockState(BlockPos localPos) {
        BlockState state = blocks.get(localPos);
        if (state == null) return null;
        if (state.isAir()) {
            EzDebug.warn("should never add a air block");
            removeBlock(localPos);
            return null;
        }

        return state;
    }
    @Override
    public boolean contains(BlockPos localPos) {
        return blocks.containsKey(localPos);  //todo safe check is null or air?
    }
    @Override
    public Iterable<Map.Entry<BlockPos, BlockState>> allBlocks() { return blocks.entrySet(); }
    @Override
    public Iterable<BlockState> getBlockStates() { return blocks.values(); }
    @Override
    public Iterable<BlockPos> getLocalPoses() { return blocks.keySet(); }


    public boolean isEmpty() { return blocks.isEmpty(); }


    @Override
    public CompoundTag saved() {
        NbtBuilder builder = new NbtBuilder()
            .putMap("block_data", blocks, (pos, state) ->
                new NbtBuilder().putCompound("pos", NbtUtils.writeBlockPos(pos))
                    .putCompound("state", NbtUtils.writeBlockState(state))
                    .get()
            );

        return builder.get();
    }
    @Override
    public SandBoxBlockClusterData load(CompoundTag tag) {
        NbtBuilder builder = NbtBuilder.modify(tag);

        builder.readMapOverwrite("block_data", entryTag -> {
                BlockPos pos = NbtUtils.readBlockPos(entryTag.getCompound("pos"));
                BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), entryTag.getCompound("state"));
                return new BiTuple<>(pos, state);
            },
            blocks
        );

        return this;
    }

    @Override
    public SandBoxBlockClusterData copyData(SandBoxBlockClusterData src) {
        blocks.clear();
        blocks.putAll(src.blocks);
        return this;
    }
}
