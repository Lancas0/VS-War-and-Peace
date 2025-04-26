package com.lancas.vs_wap.subproject.sandbox.component.data;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedBlockClusterData;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.BiConsumer;

//todo note no sync
public class SandBoxBlockClusterData implements IComponentData<SandBoxBlockClusterData>, IExposedBlockClusterData {
    public static final AABBdc ZERO_AABB = new AABBd();

    public static SandBoxBlockClusterData EMPTY() {
        return new SandBoxBlockClusterData();
    }
    public static SandBoxBlockClusterData BlockAtCenter(BlockState state) {
        SandBoxBlockClusterData data = new SandBoxBlockClusterData();
        data.setBlock(new Vector3i(0, 0, 0), state);
        return data;
    }


    public final Map<Vector3i, BlockState> blocks = new Hashtable<>();
    public final AABBd localAABB = new AABBd();

    @Nullable
    public BlockState setBlock(Vector3ic localPos, BlockState state) {
        if (state == null || state.isAir()) {
            return removeBlock(localPos);
        }

        BlockState oldState = blocks.put(new Vector3i(localPos), state);
        localAABBUnionOne(localPos);
        return oldState;
    }
    @Nullable
    public BlockState removeBlock(Vector3ic localPos) {
        BlockState prevState = blocks.remove(new Vector3i(localPos));
        recalculateLocalAABB();
        return prevState;
    }

    //todo 不要遍历，但是目前blocks不会太多，先遍历着
    private void recalculateLocalAABB() {
        if (blocks.isEmpty()) {
            localAABB.set(ZERO_AABB);
            return;
        }

        blocks.forEach((localPos, state) -> {
            localAABBUnionOne(localPos);
        });
    }
    private void localAABBUnionOne(Vector3ic localPos) {
        if (localAABB.equals(ZERO_AABB)) {
            localAABB.setMin(localPos.x() - 0.5, localPos.y() - 0.5, localPos.z() - 0.5);
            localAABB.setMin(localPos.x() + 0.5, localPos.y() + 0.5, localPos.z() + 0.5);
        } else {
            localAABB.union(localPos.x() - 0.5, localPos.y() - 0.5, localPos.z() - 0.5);
            localAABB.union(localPos.x() + 0.5, localPos.y() + 0.5, localPos.z() + 0.5);
        }
    }


    @Override
    @NotNull
    public BlockState getBlockState(Vector3ic localPos) {
        BlockState state = blocks.get(new Vector3i(localPos));
        if (state == null) return Blocks.AIR.defaultBlockState();
        if (state.isAir()) {
            EzDebug.warn("should never add a air block");
            removeBlock(localPos);
            return Blocks.AIR.defaultBlockState();
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
            )
            .putAABBd("local_aabb", localAABB);

        return builder.get();
    }
    @Override
    public SandBoxBlockClusterData load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readMapOverwrite("block_data",
                entryTag -> {
                    Vector3i localPos = NbtBuilder.vector3iOf(entryTag.getCompound("localPos"));
                    BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), entryTag.getCompound("state"));
                    return new BiTuple<>(localPos, state);
                },
                blocks
            )
            .readAABBd("local_aabb", localAABB);

        return this;
    }
    @Override
    public SandBoxBlockClusterData copyData(SandBoxBlockClusterData src) {
        blocks.clear();
        blocks.putAll(src.blocks);
        localAABB.set(src.localAABB);
        return this;
    }
}
