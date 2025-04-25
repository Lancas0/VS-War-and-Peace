package com.lancas.vs_wap.subproject.sandbox.component.data;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedBlockClusterData;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.BiConsumer;

//todo note no sync
public class SandBoxBlockClusterData implements IComponentData<SandBoxBlockClusterData>, IExposedBlockClusterData {
    public static SandBoxBlockClusterData EMPTY() {
        return new SandBoxBlockClusterData();
    }
    public static SandBoxBlockClusterData BlockAtCenter(BlockState state) {
        SandBoxBlockClusterData data = new SandBoxBlockClusterData();
        data.setBlock(new Vector3i(0, 0, 0), state);
        return data;
    }


    public final Map<Vector3i, BlockState> blocks = new Hashtable<>();

    @Nullable
    public BlockState setBlock(Vector3ic localPos, BlockState state) {
        if (state == null || state.isAir()) {
            return removeBlock(localPos);
        }

        return blocks.put(new Vector3i(localPos), state);
    }
    @Nullable
    public BlockState removeBlock(Vector3ic localPos) {
        return blocks.remove(new Vector3i(localPos));
    }

    @Override
    @Nullable
    public BlockState getBlockState(Vector3ic localPos) {
        BlockState state = blocks.get(new Vector3i(localPos));
        if (state == null) return null;
        if (state.isAir()) {
            EzDebug.warn("should never add a air block");
            removeBlock(localPos);
            return null;
        }

        return state;
    }

    @Override
    public boolean contains(Vector3ic localPos) {
        return blocks.containsKey(new Vector3i(localPos));  //todo safe check is null or air?
    }

    /*@Override
    public Iterable<Map.Entry<Vector3ic, BlockState>> allBlocks() {
        return blocks.entrySet();
    }
    @Override
    public Iterable<BlockState> getBlockStates() { return blocks.values(); }
    @Override
    public Iterable<Vector3ic> getLocalPoses() { return blocks.keySet().stream().map(localPos -> (Vector3ic)localPos).iterator(); }*/

    public boolean isEmpty() { return blocks.isEmpty(); }
    public void foreach(BiConsumer<Vector3ic, BlockState> consumer) {
        blocks.forEach(consumer);
    }


    @Override
    public CompoundTag saved() {
        NbtBuilder builder = new NbtBuilder()
            .putMap("block_data", blocks, (pos, state) ->
                new NbtBuilder().putCompound("localPos", NbtBuilder.tagOfVector3i(pos))
                    .putCompound("state", NbtUtils.writeBlockState(state))
                    .get()
            );

        return builder.get();
    }
    @Override
    public SandBoxBlockClusterData load(CompoundTag tag) {
        NbtBuilder builder = NbtBuilder.modify(tag);

        builder.readMapOverwrite("block_data", entryTag -> {
                Vector3i localPos = NbtBuilder.vector3iOf(entryTag.getCompound("localPos"));
                BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), entryTag.getCompound("state"));
                return new BiTuple<>(localPos, state);
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
