package com.lancas.vs_wap.subproject.sandbox.component.behviour;

import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentDataReader;
import com.lancas.vs_wap.subproject.sandbox.api.component.IDataReadableBehaviour;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.abs.BothSideBehaviour;
import com.lancas.vs_wap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vs_wap.subproject.sandbox.component.data.reader.IBlockClusterDataReader;
import com.lancas.vs_wap.subproject.sandbox.event.SandBoxEventMgr;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3ic;
import org.joml.primitives.AABBdc;

import java.util.function.BiConsumer;

//todo sync
public class SandBoxShipBlockCluster extends BothSideBehaviour<BlockClusterData>
    implements IDataReadableBehaviour<BlockClusterData> {

    public BlockState setBlock(Vector3ic localPos, BlockState state) {
        if (state == null || state.isAir()) {
            return removeBlock(localPos);
        }

        BlockState prevState = data.setBlock(localPos, state);
        //if (state.equals(prevState))  todo 不好确定方块是否实际更新了，先不检测
        SandBoxEventMgr.onShipBlockReplaced.invokeAll(ship, localPos, prevState, state);

        return prevState == null ? Blocks.AIR.defaultBlockState() : prevState;
    }
    public BlockState removeBlock(Vector3ic localPos) {
        BlockState oldState = data.removeBlock(localPos);

        if (oldState != null)  //if fail to remove(prestate is already null), don't invoke
            SandBoxEventMgr.onShipBlockReplaced.invokeAll(ship, localPos, oldState, null);

        return oldState == null ? Blocks.AIR.defaultBlockState() : oldState;
    }


    /*public BlockState getBlockOrNull(Vector3ic localPos) {
        BlockState state = data.getBlockState(localPos);
        if (state == null) return null;
        if (state.isAir()) {
            EzDebug.warn("should never add a air block");
            removeBlock(localPos);
            return null;
        }

        return state;
    }*/
    /*
    public BlockState getBlock(Vector3ic localPos) {  //todo exposed data
        return data.getBlockState(localPos);
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
    }*/
    //public int blockCount() { return data.blocks.size(); }
    //public AABBdc getLocalAABB() { return data.getLocalAABB(); }

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
    protected BlockClusterData makeInitialData() { return new BlockClusterData(); }

    @Override
    public IBlockClusterDataReader getDataReader() { return data; }

    @Override
    public Class<BlockClusterData> getDataType() { return BlockClusterData.class; }



    @Override
    public synchronized void clientTick(ClientLevel level) { }
    @Override
    public synchronized void serverTick(ServerLevel level) { }
}
