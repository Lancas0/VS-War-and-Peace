package com.lancas.vs_wap.subproject.sandbox.component.behviour;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxBlockClusterData;
import com.lancas.vs_wap.subproject.sandbox.event.SandBoxEventMgr;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3ic;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;

import java.util.Map;

//todo sync
public class SandBoxShipBlockCluster extends AbstractComponentBehaviour<SandBoxBlockClusterData> {
    // 方块数据（局部坐标系，Vector3i为相对位置，(0,0,0)表示中心）
    //private final Map<BlockPos, BlockState> blocks = new ConcurrentHashMap<>();
    //private final SandBoxBlockClusterData data = new SandBoxBlockClusterData();
    private AABBi localAABB = null;

    public BlockState setBlock(BlockPos localPos, BlockState state) {
        if (state == null || state.isAir()) {
            return removeBlock(localPos);
        }

        BlockState prevState = data.setBlock(localPos, state);
        //if (state.equals(prevState))  todo 不好确定方块是否实际更新了，先不检测
        SandBoxEventMgr.onShipBlockReplaced.invokeAll(ship, localPos, prevState, state);

        if (prevState != null) return prevState;  //there is a prev state in this pos, no need to update localAABB

        Vector3ic localPosI = JomlUtil.i(localPos);
        if (localAABB == null)
            localAABB = new AABBi(localPosI, localPosI);
        else localAABB.union(localPosI);

        return prevState;
    }
    public BlockState removeBlock(BlockPos localPos) {
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
        int ix = 0;
        for (var blockEntry : data.allBlocks()) {
            Vector3ic curLocPosI = JomlUtil.i(blockEntry.getKey());
            if (ix == 0) {
                localAABB.setMin(curLocPosI);
                localAABB.setMax(curLocPosI);
            } else {
                localAABB.union(curLocPosI);
            }

            ix++;
        }
    }

    public BlockState getBlockOrNull(BlockPos localPos) {
        BlockState state = data.getBlockState(localPos);
        if (state == null) return null;
        if (state.isAir()) {
            EzDebug.warn("should never add a air block");
            removeBlock(localPos);
            return null;
        }

        return state;
    }
    public BlockState getBlockOrAir(BlockPos localPos) {
        BlockState state = getBlockOrNull(localPos);
        return state == null ? Blocks.AIR.defaultBlockState() : state;
    }

    public Iterable<Map.Entry<BlockPos, BlockState>> allBlocks() {
        return data.allBlocks();
    }
    public Iterable<BlockState> allBlockStates() {
        return data.blocks.values();
    }

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
}
