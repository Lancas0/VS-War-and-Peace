package com.lancas.vswap.ship.feature.spilt;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.ship.helper.builder.ShipBuilder;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.util.VectorConversionsKt;

import java.text.NumberFormat;
import java.util.*;

public class DirectionalSplitHandler {
    private static final Vector3i[] offsets = new Vector3i[] {
        new Vector3i(0, 0, -1),
        new Vector3i(0, 0, 1),
        new Vector3i(1, 0, 0),
        new Vector3i(-1, 0, 0),
        new Vector3i(0, 1, 0),
        new Vector3i(0, -1, 0)
    };
    /*public static class SplitRule {
        public boolean splitByBlockDir;
        public boolean keepBlockInOriginShip;  //true to keep in origin, false to keep
    }*/
    //todo constructor or set it a static class

    //protected final ConcurrentMap<PairKey<BlockState, BlockState>, SplitRule> splitRules = new ConcurrentHashMap<>();

    public static ShipBuilder trySplit(ServerLevel level, BlockPos splitterPos, Direction splitDir, ShipBuilder dest) {
        ServerShip ship = ShipUtil.getServerShipAt(level, splitterPos);
        if (ship == null) {
            EzDebug.warn("fail to split because ship is null");
            return dest;
        }
        AABBic shipAABB = ship.getShipAABB();
        if (shipAABB == null) return dest;

        //do not care whether the split start is air: ccc turns to air after trigger.
        BlockPos splitStart = splitterPos.relative(splitDir);
        /*if (level.getBlockState(splitStart).isAir()) {
            EzDebug.warn("fail to split because split start is air");
            return dest;
        }*/

        //EzDebug.log("--------BFS---------");

        //get splittable shipyard poses by bfs
        HashSet<Vector3i> splitPoses = bfsGetSplitShipPoses(level, shipAABB, splitterPos, splitDir);
        if (splitPoses == null || splitPoses.isEmpty()) return dest;

        //EzDebug.log("------copying block-------");

        //copy blocks to build the ship
        //ShipBuilder builder = new ShipBuilder(splitStart, level, ship.getTransform().getShipToWorldScaling().x(), false);
        for (Vector3i posToSplit : splitPoses) {
            //todo is it right to use  newShipPos - JomlUtil.i(splitStart) as offset?
            Vector3i offset = posToSplit.sub(JomlUtil.i(splitStart), new Vector3i());
            dest.copyBlock(JomlUtil.bp(posToSplit), JomlUtil.bp(offset), true);
            //EzDebug.log("copy block:" + level.getBlockState(JomlUtil.bp(posToSplit)).getBlock().getName().getString());
        }


        //move the split ship to original position
        Vector3d splitStartWorldPos = ship.getTransform().getShipToWorld().transformPosition(JomlUtil.dCenter(splitStart));
        dest.rotate(ship.getTransform().getShipToWorldRotation())
            .moveLocalPosToWorldPos(new Vector3d(), splitStartWorldPos);

        return dest;
    }
    public static @Nullable ShipBuilder trySplit(ServerLevel level, BlockPos splitterPos, Direction splitDir) {
        ServerShip ship = ShipUtil.getServerShipAt(level, splitterPos);
        if (ship == null) return null;
        AABBic shipAABB = ship.getShipAABB();
        if (shipAABB == null) return null;

        BlockPos splitStart = splitterPos.relative(splitDir);
        if (level.getBlockState(splitStart).isAir()) return null;

        //EzDebug.log("--------BFS---------");

        //get splittable shipyard poses by bfs
        HashSet<Vector3i> splitPoses = bfsGetSplitShipPoses(level, shipAABB, splitterPos, splitDir);
        if (splitPoses == null || splitPoses.isEmpty()) return null;

        //EzDebug.log("------copying block-------");

        //copy blocks to build the ship
        ShipBuilder builder = new ShipBuilder(splitStart, level, ship.getTransform().getShipToWorldScaling().x(), false);
        for (Vector3i newShipPos : splitPoses) {
            Vector3i offset = newShipPos.sub(JomlUtil.i(splitStart), new Vector3i());
            builder.copyBlock(JomlUtil.bp(newShipPos), JomlUtil.bp(offset), true);
            //EzDebug.log("copy block:" + level.getBlockState(JomlUtil.bp(newShipPos)).getBlock().getName().getString());
        }


        //move the split ship to original position
        Vector3d splitStartWorldPos = ship.getTransform().getShipToWorld().transformPosition(JomlUtil.dCenter(splitStart));
        builder.rotate(ship.getTransform().getShipToWorldRotation())
            .moveLocalPosToWorldPos(new Vector3d(), splitStartWorldPos);

        return builder;
    }
    public static NumberFormat format() {
        NumberFormat df;
        df = NumberFormat.getNumberInstance(Locale.ENGLISH);
        df.setGroupingUsed(false);
        return df;
    }

    protected static HashSet<Vector3i> bfsGetSplitShipPoses(ServerLevel level, AABBic shipAABB, BlockPos splitterPos, Direction splitDir) {
        HashSet<Vector3i> visited = new HashSet<>();
        Queue<Vector3i> open = new ArrayDeque<>();

        AABBi containAllShipAABB = VectorConversionsKt.expand(shipAABB, 1, new AABBi());
        Vector3i start = JomlUtil.i(splitterPos.relative(splitDir));
        BlockState startState = level.getBlockState(JomlUtil.bp(start));

        //EzDebug.log("start state is " + startState.getBlock().getName().getString());
        //EzDebug.Log("shipAABB is " + shipAABB + ", contain:" + shipAABB.containsPoint(start) + ", is air:" + startState.isAir());
        //do not check whether the block in AABB because some blocks' colliders are not considererd as ship collider
        if (/*!containAllShipAABB.containsPoint(start) || */startState.isAir()) return null;  //start is not in ship or start is air

        visited.add(start);
        open.add(start);

        while (!open.isEmpty()) {
            Vector3i curPos = open.poll();

            for (Vector3i offset : offsets) {
                Vector3i neighbor = curPos.add(offset, new Vector3i());
                BlockState neighborState = level.getBlockState(JomlUtil.bp(neighbor));

                //EzDebug.log("checking neighbor at " + JomlUtil.bp(neighbor) + ", state:" + neighborState.getBlock().getName().getString());

                //todo currently do not check AABB because ship ignore some non-full block
                if (visited.contains(neighbor) /*|| !containAllShipAABB.containsPoint(neighbor)*/ || neighborState.isAir())
                    continue;

                boolean neighborIsSplitter = neighbor.equals(splitterPos.getX(), splitterPos.getY(), splitterPos.getZ());

                if (neighborIsSplitter) {
                    //the neighbor is equals to splitor pos.
                    //If a not start pos has a neighbor pos equals to splitor, then can not split.
                    //EzDebug.log("neighbor is splitter, and curPos is:" + curPos);
                    if (!curPos.equals(start)) return null;
                }

                if (!neighborIsSplitter) {
                    //EzDebug.log("add " + neighborState.getBlock().getName().getString());
                    visited.add(neighbor);
                    open.add(neighbor);
                }
            }
        }

        return visited;
    }
}
