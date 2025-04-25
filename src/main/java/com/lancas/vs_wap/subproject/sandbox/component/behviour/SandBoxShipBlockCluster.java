package com.lancas.vs_wap.subproject.sandbox.component.behviour;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxBlockClusterData;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedBlockClusterData;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedComponentData;
import com.lancas.vs_wap.subproject.sandbox.event.SandBoxEventMgr;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

//todo sync
public class SandBoxShipBlockCluster extends AbstractComponentBehaviour<SandBoxBlockClusterData> {
    // 方块数据（局部坐标系，Vector3i为相对位置，(0,0,0)表示中心）
    //private final Map<BlockPos, BlockState> blocks = new ConcurrentHashMap<>();
    //private final SandBoxBlockClusterData data = new SandBoxBlockClusterData();
    private AABBi localAABB = null;

    public BlockState setBlock(Vector3ic localPos, BlockState state) {
        if (state == null || state.isAir()) {
            return removeBlock(localPos);
        }

        BlockState prevState = data.setBlock(localPos, state);
        //if (state.equals(prevState))  todo 不好确定方块是否实际更新了，先不检测
        SandBoxEventMgr.onShipBlockReplaced.invokeAll(ship, localPos, prevState, state);

        if (prevState != null) return prevState;  //there is a prev state in this pos, no need to update localAABB

        if (localAABB == null)
            localAABB = new AABBi(localPos, localPos);
        else localAABB.union(localPos);

        return prevState;
    }
    public BlockState removeBlock(Vector3ic localPos) {
        BlockState oldState = data.removeBlock(localPos);

        if (oldState != null)  //if fail to remove(prestate is already null), don't invoke
            SandBoxEventMgr.onShipBlockReplaced.invokeAll(ship, localPos, oldState, null);

        if (data.isEmpty()) {
            localAABB = null;
            return oldState;
        }

        //没有方块需要调整，直接返回
        if (oldState == null) return null;

        //todo 不要遍历，但是目前blocks不会太多，先遍历着
        recalculateLocalAABB();

        return oldState;
    }

    private void recalculateLocalAABB() {
        if (data.isEmpty()) {
            localAABB = null;
            return;
        }

        if (localAABB == null) localAABB = new AABBi();
        AtomicInteger ix = new AtomicInteger();
        data.foreach((localPos, state) -> {
            if (ix.get() == 0) {
                localAABB.setMin(localPos);
                localAABB.setMax(localPos);
            } else {
                localAABB.union(localPos);
            }

            ix.getAndIncrement();
        });
    }

    public BlockState getBlockOrNull(Vector3ic localPos) {
        BlockState state = data.getBlockState(localPos);
        if (state == null) return null;
        if (state.isAir()) {
            EzDebug.warn("should never add a air block");
            removeBlock(localPos);
            return null;
        }

        return state;
    }
    public BlockState getBlockOrAir(Vector3ic localPos) {
        BlockState state = getBlockOrNull(localPos);
        return state == null ? Blocks.AIR.defaultBlockState() : state;
    }
    public Iterable<BiTuple<Vector3ic, BlockState>> allBlocks() {
        return () -> data.blocks.entrySet().stream().map(
            entry -> new BiTuple<Vector3ic, BlockState>(entry.getKey(), entry.getValue())
        ).iterator();
    }
    public void foreach(@NotNull BiConsumer<Vector3ic, BlockState> consumer) {
        data.foreach(consumer);
    }
    public Iterable<BlockState> allBlockStates() {
        return data.blocks.values();
    }
    public int blockCount() { return data.blocks.size(); }

    @Nullable
    public AABBic getLocalAABB() { return localAABB; }

    /*@Override
    public CompoundTag saved() {
        NbtBuilder builder = new NbtBuilder().putCompound("data", data.saved());

        if (localAABB != null)
            builder.putAABBi("local_aabb", localAABB);

        return builder.get();
    }
    @Override
    public void load(CompoundTag tag) {
        NbtBuilder builder = NbtBuilder.modify(tag)
            .readCompoundDo("data", data::load);

        if (builder.contains("local_aabb")) {
            localAABB = new AABBi();
            builder.readAABBi("local_aabb", localAABB);
        }
    }*/

    @Override
    protected SandBoxBlockClusterData makeData() { return new SandBoxBlockClusterData(); }

    @Override
    public void loadData(SandBoxServerShip ship, SandBoxBlockClusterData src) {
        super.loadData(ship, src);
        recalculateLocalAABB();
    }

    @Override
    public IExposedBlockClusterData getExposedData() { return data; }
}
