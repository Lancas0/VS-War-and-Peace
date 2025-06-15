package com.lancas.vswap.sandbox.ballistics.data;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.TriTuple;
import com.lancas.vswap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class BallisticInitialStateSubData implements IComponentData<BallisticInitialStateSubData> {
    //set default dir as SOUTH for safe
    public final Vector3d launchWorldPos = new Vector3d();
    public final Vector3i localForward = new Vector3i(0, 0, 1);  //localDir is the dir from project's tail to head.  FIXME now the localForward is always (0, 0, 1), and I'm using (0, 1, 0) as localY and (1, 0, 0) as localX
    public final Vector3d worldLaunchDir = new Vector3d(0, 0, 1);
    public double stdPropellingEnergy = 0;
    public final List<BallisticPos> fromHeadToBallisticPoses = new CopyOnWriteArrayList<>();
    //public double exitVelocity = 0;
    //public final List<BallisticPos> ballisticBlockLocPoses = new CopyOnWriteArrayList<>();

    //public final Vector3i headLocalPos = new Vector3i(0, 0, 0);

    public int ballisticLength() { return fromHeadToBallisticPoses.size(); }
    public @Nullable BallisticPos getPosFromHead(int fromHead) {
        if (fromHead < 0 || fromHead >= fromHeadToBallisticPoses.size())
            return null;
        return fromHeadToBallisticPoses.get(fromHead);
    }
    public @Nullable BallisticPos getPosFromTail(int fromTail) {
        if (fromTail < 0 || fromTail >= fromHeadToBallisticPoses.size())
            return null;
        return fromHeadToBallisticPoses.get(fromHeadToBallisticPoses.size() - fromTail - 1);
    }

    private BallisticInitialStateSubData() {}
    public static BallisticInitialStateSubData createDefault() { return new BallisticInitialStateSubData(); }
    public BallisticInitialStateSubData(Vector3dc inLaunchWorldPos, Vector3ic inLocalDirection, Vector3dc inWorldLaunchDir, double inStdPropellingEnergy) {
        launchWorldPos.set(inLaunchWorldPos);
        localForward.set(inLocalDirection);
        worldLaunchDir.set(inWorldLaunchDir);

        stdPropellingEnergy = inStdPropellingEnergy;
    }


    public void foreachBallisticBlock(SandBoxServerShip ship, TriConsumer<BallisticPos, BlockState, ISandBoxBallisticBlock> consumer) {
        fromHeadToBallisticPoses.forEach(ballisticPos -> {
            BlockState state = ship.getBlockCluster().getDataReader().getBlockState(ballisticPos.localPos());
            if (state == null || state.isAir()) {
                EzDebug.warn("state at " + ballisticPos + " is null or air!");
                return;
            }

            if (state.getBlock() instanceof ISandBoxBallisticBlock bb) {
                consumer.accept(ballisticPos, state, bb);
            }
        });
    }


    @Override
    public BallisticInitialStateSubData copyData(BallisticInitialStateSubData src) {
        localForward.set(src.localForward);
        worldLaunchDir.set(src.worldLaunchDir);
        stdPropellingEnergy = src.stdPropellingEnergy;

        fromHeadToBallisticPoses.clear();
        fromHeadToBallisticPoses.addAll(src.fromHeadToBallisticPoses);
        return this;
    }
    @Override
    public IComponentData<BallisticInitialStateSubData> overwriteDataByShip(ISandBoxShip ship) {
        //todo warning when block size is 0
        //AtomicInteger maxProjectAlongForward = new AtomicInteger(Integer.MIN_VALUE);
        AtomicInteger maxProjectAlongForward = new AtomicInteger(Integer.MIN_VALUE);  //for head
        AtomicInteger minProjectAlongForward = new AtomicInteger(Integer.MAX_VALUE);  //for tail,

        Vector3i head = new Vector3i(0, 0, 0);
        Vector3i tail = new Vector3i(0, 0, 0);
        ship.getBlockCluster().getDataReader().seekAllBlocks((localPos, state) -> {
            int projectAlongForward = JomlUtil.cross(localForward, localPos);

            if (projectAlongForward > maxProjectAlongForward.get()) {
                maxProjectAlongForward.set(projectAlongForward);
                head.set(localPos);
            }
            if (projectAlongForward < minProjectAlongForward.get()) {
                minProjectAlongForward.set(projectAlongForward);
                tail.set(localPos);
            }
        });
        EzDebug.warn("head:" + head + ", tail:" + tail);


        var blockReader = ship.getBlockCluster().getDataReader();
        BallisticPos[] ballisticPoses = new BallisticPos[blockReader.getBlockCnt()];
        ship.getBlockCluster().getDataReader().seekAllBlocks((localPos, state) -> {
            Block block = state.getBlock();
            /*if (block instanceof ISandBoxBallisticBlock) {

            }*/
            int fromHead = (int)localPos.gridDistance(head);//head.sub(localPos, new Vector3i());
            int fromTail = (int)localPos.gridDistance(tail);

            ballisticPoses[fromHead] = new BallisticPos(new Vector3i(localPos), fromHead, fromTail);
        });

        fromHeadToBallisticPoses.clear();
        fromHeadToBallisticPoses.addAll(List.of(ballisticPoses));
        return this;
    }
    @Override
    public CompoundTag saved() {
        return new NbtBuilder()
            .putVector3d("launch_world_pos", launchWorldPos)
            .putVector3i("loc_forward", localForward)
            .putVector3d("world_launch_dir", worldLaunchDir)
            .putDouble("total_propelling_energy", stdPropellingEnergy)
            //.putEach("ballistic_block_loc_poses", ballisticBlockLocPoses, NbtBuilder::tagOfVector3i)  //todo the block loc poses must be set when added to ship, maybe no need for save?
            .get();
    }
    @Override
    public IComponentData<BallisticInitialStateSubData> load(CompoundTag tag) {
        List<Vector3i> readingBlockLocPoses = new ArrayList<>();

        NbtBuilder.modify(tag)
            .readVector3d("launch_world_pos", launchWorldPos)
            .readVector3i("loc_forward", localForward)
            .readVector3d("world_launch_dir", worldLaunchDir)
            .readDoubleDo("total_propelling_energy", v -> stdPropellingEnergy = v)
            .readEachCompoundOverwrite("ballistic_block_loc_poses", NbtBuilder::vector3iOf, readingBlockLocPoses);

        //ballisticBlockLocPoses.addAll(readingBlockLocPoses);  //don't load because will be overwriteDataByShip
        return this;
    }
}
