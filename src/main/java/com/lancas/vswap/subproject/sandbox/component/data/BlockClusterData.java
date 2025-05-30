package com.lancas.vswap.subproject.sandbox.component.data;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.foundation.network.NetworkHandler;
import com.lancas.vswap.ship.data.IShipSchemeData;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IBlockClusterDataReader;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IBlockClusterDataWriter;
import com.lancas.vswap.subproject.sandbox.event.SandBoxEventMgr;
import com.lancas.vswap.subproject.sandbox.network.sync.BlockUpdateSyncPacket;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

//todo note no sync
public class BlockClusterData implements IComponentData<BlockClusterData>, IBlockClusterDataReader, IBlockClusterDataWriter {
    public static final AABBdc EMPTY_AABB = new AABBd();

    public static BlockClusterData EMPTY() {
        return new BlockClusterData();
    }
    public static BlockClusterData BlockAtCenter(BlockState state) {
        BlockClusterData data = new BlockClusterData();
        data.setBlock(new Vector3i(0, 0, 0), state);
        return data;
    }
    /*public static BlockClusterData fromSchemeData(@NotNull IShipSchemeData schemeData) {
        BlockClusterData data = new BlockClusterData();
        schemeData.
    }*/


    private final Object mutex = new Object();

    public final Map<Vector3ic, BlockState> blocks = new ConcurrentHashMap<>();
    private final AABBd localAABB = new AABBd();
    public volatile boolean isLocalAABBDirty = true;

    protected ISandBoxShip owner;

    public BlockClusterData() { }


    @Override
    public BlockState setBlock(Vector3ic localPos, BlockState state, boolean syncOtherSide) {
        Vector3i localPosImmutable = new Vector3i(localPos);
        /*if (state == null || state.isAir()) {
            return removeBlock(localPosImmutable);
        }*/

        BlockState oldState;
        synchronized (mutex) {
            if (state == null || state.isAir())
                oldState = blocks.remove(localPosImmutable);
            else
                oldState = blocks.put(localPosImmutable, state);
            //set isLocalAABBDirty true because the method may be freq called
            isLocalAABBDirty = true;
        }
        oldState = (oldState == null ? Blocks.AIR.defaultBlockState() : oldState);

        //only handle event and sync when it's already in ship
        if (owner != null) {
            if (!oldState.equals(state)) {
                SandBoxEventMgr.onShipBlockReplaced.invokeAll(owner, localPos, oldState, state);
            }

            if (syncOtherSide)
                syncBlockUpdate(localPos, state == null ? Blocks.AIR.defaultBlockState() : state);
        }

        return oldState;
    }

    @Override
    public void clear(boolean syncOtherSide) {
        //todo directly clean the blockData, and send a clear packet to client
        List<Vector3ic> localPoses = blocks.keySet().stream().toList();
        for (Vector3ic localPos : localPoses) {
            setBlock(localPos, Blocks.AIR.defaultBlockState(), syncOtherSide);
        }
    }

    /*public BlockState removeBlock(Vector3ic localPos, boolean syncOtherSide) {
        Vector3i localPosImmutable = new Vector3i(localPos);
        BlockState prevState;

        synchronized (mutex) {
            prevState = blocks.remove(localPosImmutable);
            isLocalAABBDirty = true;
        }

        if (syncOtherSide)
            syncBlockUpdate(localPos, Blocks.AIR.defaultBlockState());

        return prevState;
    }*/
    protected void syncBlockUpdate(Vector3ic localPos, BlockState state) {
        LogicalSide logicalSide = EffectiveSide.get();
        boolean isClient = (logicalSide == LogicalSide.CLIENT);

        EzDebug.log("logicalSide:" + logicalSide);  //this is what I need
        //EzDebug.log("current dist:" + FMLEnvironment.dist);  //ok it's physical side, I need logic side.
        if (isClient) {
            //from client to server
            EzDebug.highlight("sync from client to server");
            NetworkHandler.sendToServer(new BlockUpdateSyncPacket(true, owner.getUuid(), localPos, state));
        } else {
            //from server to all player
            EzDebug.highlight("sync from server to all player");
            NetworkHandler.sendToAllPlayers(new BlockUpdateSyncPacket(false, owner.getUuid(), localPos, state));
        }
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
            EzDebug.error("should never added a air block");
            //removeBlock(localPos);
            blocks.remove(localPos);
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
        //isServer = src.isServer;
        return this;
    }
    public BlockClusterData overwriteDataByShip(ISandBoxShip ship) {
        owner = ship;
        return this;
    }
}
