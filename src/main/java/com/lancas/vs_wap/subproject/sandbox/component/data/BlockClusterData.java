package com.lancas.vs_wap.subproject.sandbox.component.data;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vs_wap.subproject.sandbox.component.data.reader.IBlockClusterDataReader;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

//todo note no sync
public class BlockClusterData implements IComponentData<BlockClusterData>, IBlockClusterDataReader {
    public static final AABBdc EMPTY_AABB = new AABBd();

    public static BlockClusterData EMPTY() {
        return new BlockClusterData();
    }
    public static BlockClusterData BlockAtCenter(BlockState state) {
        BlockClusterData data = new BlockClusterData();
        data.setBlock(new Vector3i(0, 0, 0), state);
        return data;
    }


    private final Object mutex = new Object();

    public final Map<Vector3ic, BlockState> blocks = new ConcurrentHashMap<>();
    private final AABBd localAABB = new AABBd();
    public volatile boolean isLocalAABBDirty = true;

    public BlockState setBlock(Vector3ic localPos, BlockState state) {
        Vector3i localPosImmutable = new Vector3i(localPos);
        if (state == null || state.isAir()) {
            return removeBlock(localPosImmutable);
        }

        BlockState oldState;
        synchronized (mutex) {
            oldState = blocks.put(localPosImmutable, state);
            //set isLocalAABBDirty true because the method may be freq called
            isLocalAABBDirty = true;
        }
        return oldState;
    }
    public BlockState removeBlock(Vector3ic localPos) {
        Vector3i localPosImmutable = new Vector3i(localPos);
        BlockState prevState;

        synchronized (mutex) {
            prevState = blocks.remove(localPosImmutable);
            isLocalAABBDirty = true;
        }
        return prevState;
    }

    public void moveAll(Vector3ic movement) {
        if (blocks.isEmpty()) return;

        var prevBlocks = new HashMap<>(blocks);
        synchronized (mutex) {
            blocks.clear();
            prevBlocks.forEach((localPos, state) -> {
                blocks.put(localPos.add(movement, new Vector3i()), state);
            });
            //don't set isLocalAABBDirty but handle this in mutex.
            localAABB.translate(movement.x(), movement.y(), movement.z());
        }
    }


    @Override
    public AABBdc getLocalAABB() {
        //第一次检测，减少锁竞争
        if (isLocalAABBDirty) {
            synchronized (mutex) {
                //双重检查，确保不重复计算
                if (isLocalAABBDirty) {
                    if (blocks.isEmpty()) {
                        localAABB.set(EMPTY_AABB);
                    } else {
                        blocks.forEach((localPos, state) -> {
                            if (localAABB.equals(EMPTY_AABB)) {
                                localAABB.setMin(localPos.x() - 0.5, localPos.y() - 0.5, localPos.z() - 0.5);
                                localAABB.setMin(localPos.x() + 0.5, localPos.y() + 0.5, localPos.z() + 0.5);
                            } else {
                                localAABB.union(localPos.x() - 0.5, localPos.y() - 0.5, localPos.z() - 0.5);
                                localAABB.union(localPos.x() + 0.5, localPos.y() + 0.5, localPos.z() + 0.5);
                            }
                        });
                    }
                }
            }
        }
        return localAABB;
    }
    @Override
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
    @Override
    public int getBlockCnt() { return blocks.size(); }
    @Override
    public void seekAllBlocks(BiConsumer<Vector3ic, BlockState> consumer) { blocks.forEach(consumer); }
    @Override
    public Iterable<Vector3ic> allLocalPoses() { return blocks.keySet(); }
    @Override
    public Iterable<BlockState> allBlockStates() { return blocks.values(); }
    @Override
    public Iterable<Map.Entry<Vector3ic, BlockState>> allBlocks() { return blocks.entrySet(); }


    /*@Override
    public Iterable<Map.Entry<Vector3ic, BlockState>> allBlocks() {
        return blocks.entrySet();
    }
    @Override
    public Iterable<BlockState> getBlockStates() { return blocks.values(); }
    @Override
    public Iterable<Vector3ic> getLocalPoses() { return blocks.keySet().stream().map(localPos -> (Vector3ic)localPos).iterator(); }*/
    /*public void foreach(BiConsumer<Vector3ic, BlockState> consumer) {
        blocks.forEach(consumer);
    }*/

    //不存储或者读取local_aabb和isDirty, 默认设置dirty为true，不然可能无法维持线程安全
    @Override
    public CompoundTag saved() {
        NbtBuilder builder = new NbtBuilder()
            .putMap("block_data", blocks, (pos, state) ->
                new NbtBuilder().putCompound("localPos", NbtBuilder.tagOfVector3i(pos))
                    .putCompound("state", NbtUtils.writeBlockState(state))
                    .get()
            );
            //.putAABBd("local_aabb", localAABB);

        return builder.get();
    }
    @Override
    public BlockClusterData load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readMapOverwrite("block_data",
                entryTag -> {
                    Vector3i localPos = NbtBuilder.vector3iOf(entryTag.getCompound("localPos"));
                    BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), entryTag.getCompound("state"));
                    return new BiTuple<>(localPos, state);
                },
                blocks
            );
            //.readAABBd("local_aabb", localAABB);

        return this;
    }
    @Override
    public BlockClusterData copyData(BlockClusterData src) {
        blocks.clear();
        blocks.putAll(src.blocks);
        //localAABB.set(src.localAABB);
        isLocalAABBDirty = true;  //不存储或者读取local_aabb和isDirty, 默认设置dirty为true，不然可能无法维持线程安全
        return this;
    }
}
