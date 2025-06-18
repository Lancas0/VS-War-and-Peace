package com.lancas.vswap.subproject.sandbox.ship;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SandBoxPonderShip extends SandBoxClientShip {

    private Map<Vector3ic, Integer> breakProgressions = new ConcurrentHashMap<>();

    /*private final Deque<PonderShipInitialState> stateStack = new ArrayDeque<>();*/

    public SandBoxPonderShip(UUID inId, RigidbodyData rigidbodyData, BlockClusterData clusterData) {
        super(inId, rigidbodyData, clusterData);
    }

    /*public void pushState() {
        stateStack.add(new PonderShipInitialState(this));
    }
    public void popState() {
        if (stateStack.isEmpty()) {
            EzDebug.warn("Ponder ship has empty stateStack, fail to pop!");
            return;
        }
        stateStack.pop().setState(this);
    }*/

    public void addBreakProgress(Vector3ic localPos, boolean canBreak, @Nullable Consumer<BlockState> breakCallback) {
        //todo check to ensure don't try to break air
        Vector3i key = new Vector3i(localPos);
        int prev = breakProgressions.computeIfAbsent(key, k -> 0);

        if (prev >= 9) {  //prev >= 9, cur >= 10, 10 for broken
            if (canBreak) {
                BlockState prevState = getBlockCluster().getDataWriter().setBlock(key, Blocks.AIR.defaultBlockState(), false);
                if (breakCallback != null)
                    breakCallback.accept(prevState);
                breakProgressions.remove(key);
            }
        } else {
            breakProgressions.put(key, Math.min(10, prev + 1));  //10 for break, so limit to 9
        }
    }
    public @Nullable Integer getBreakProgress(Vector3ic localPos) {
        Integer progress = breakProgressions.get(localPos);
        if (progress == null)
            return null;

        return Math.min(9, progress);
    }

    public List<Vector3ic> selectExistBlocks(int x0, int y0, int z0, int x1, int y1, int z1) {
        List<Vector3ic> poses = new ArrayList<>();
        var blockReader = blockCluster.getDataReader();

        for (int x = x0; x <= x1; ++x)
            for (int y = y0; y <= y1; ++y)
                for (int z = z0; z <= z1; ++z) {
                    Vector3i cur = new Vector3i(x, y, z);
                    if (blockReader.contains(cur))
                        poses.add(cur);
                }
        return poses;
    }

}
