package com.lancas.vs_wap.sandbox.ballistics.data;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentDataReader;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.util.TriConsumer;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BallisticInitialStateSubData implements IComponentData<BallisticInitialStateSubData> {


    //set default dir as SOUTH for safe
    public final Vector3d launchWorldPos = new Vector3d();
    public final Vector3i localForward = new Vector3i(0, 0, 1);  //localDir is the dir from project's tail to head.
    public final Vector3d worldLaunchDir = new Vector3d(0, 0, 1);
    public double totalPropellingEnergy = 0;
    //public double exitVelocity = 0;
    public final List<Vector3i> ballisticBlockLocPoses = new CopyOnWriteArrayList<>();

    private BallisticInitialStateSubData() {}
    public static BallisticInitialStateSubData createDefault() { return new BallisticInitialStateSubData(); }
    public BallisticInitialStateSubData(Vector3dc inLaunchWorldPos, Vector3ic inLocalDirection, Vector3dc inWorldLaunchDir, double inPropellingEnergy) {
        launchWorldPos.set(inLaunchWorldPos);
        localForward.set(inLocalDirection);
        worldLaunchDir.set(inWorldLaunchDir);

        totalPropellingEnergy = inPropellingEnergy;
    }


    public void foreachBallisticBlock(SandBoxServerShip ship, TriConsumer<Vector3i, BlockState, ISandBoxBallisticBlock> consumer) {
        ballisticBlockLocPoses.forEach(localPos -> {
            BlockState state = ship.getBlockCluster().getDataReader().getBlockState(localPos);
            if (state == null || state.isAir() || !(state.getBlock() instanceof ISandBoxBallisticBlock bb)) {
                EzDebug.warn("fail to get ballistic state at " + localPos);
                return;
            }
            consumer.accept(localPos, state, bb);
        });
    }


    @Override
    public BallisticInitialStateSubData copyData(BallisticInitialStateSubData src) {
        localForward.set(src.localForward);
        worldLaunchDir.set(src.worldLaunchDir);
        totalPropellingEnergy = src.totalPropellingEnergy;
        ballisticBlockLocPoses.addAll(src.ballisticBlockLocPoses);
        return this;
    }
    @Override
    public IComponentData<BallisticInitialStateSubData> overwriteDataByShip(ISandBoxShip ship) {
        List<Vector3i> ballisticBlocks = new ArrayList<>();
        ship.getBlockCluster().getDataReader().seekAllBlocks((localPos, state) -> {
            Block block = state.getBlock();
            if (block instanceof ISandBoxBallisticBlock)
                ballisticBlocks.add(new Vector3i(localPos));
        });
        ballisticBlockLocPoses.addAll(ballisticBlocks);
        return this;
    }
    @Override
    public CompoundTag saved() {
        return new NbtBuilder()
            .putVector3d("launch_world_pos", launchWorldPos)
            .putVector3i("loc_forward", localForward)
            .putVector3d("world_launch_dir", worldLaunchDir)
            .putNumber("total_propelling_energy", totalPropellingEnergy)
            .putEach("ballistic_block_loc_poses", ballisticBlockLocPoses, NbtBuilder::tagOfVector3i)  //todo the block loc poses must be set when added to ship, maybe no need for save?
            .get();
    }
    @Override
    public IComponentData<BallisticInitialStateSubData> load(CompoundTag tag) {
        List<Vector3i> readingBlockLocPoses = new ArrayList<>();

        NbtBuilder.modify(tag)
            .readVector3d("launch_world_pos", launchWorldPos)
            .readVector3i("loc_forward", localForward)
            .readVector3d("world_launch_dir", worldLaunchDir)
            .readDoubleDo("total_propelling_energy", v -> totalPropellingEnergy = v)
            .readEachCompoundOverwrite("ballistic_block_loc_poses", NbtBuilder::vector3iOf, readingBlockLocPoses);

        ballisticBlockLocPoses.addAll(readingBlockLocPoses);
        return this;
    }
}
