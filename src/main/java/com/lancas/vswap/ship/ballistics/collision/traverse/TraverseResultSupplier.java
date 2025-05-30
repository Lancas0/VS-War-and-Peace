package com.lancas.vswap.ship.ballistics.collision.traverse;

import com.lancas.vswap.ship.ballistics.data.BallisticsHitInfo;
import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.valkyrienskies.core.api.ships.Ship;

public abstract class TraverseResultSupplier<T> {
    //don't check interact shape. I suppose the shape and interact shape are same, or interact shape is empty.

    public abstract double getDistSquare(BiTuple.RayTupleV3 ray, T result);
    public abstract T supplyResult(Level level, BiTuple.RayTupleV3 ray, BlockPos pos, VoxelShape shape);

    public static final TraverseResultSupplier<BlockHitResult> VanillaSupplier =
        new TraverseResultSupplier<BlockHitResult>() {
            @Override
            public BlockHitResult supplyResult(Level level, BiTuple.RayTupleV3 ray, BlockPos pos, VoxelShape shape) {
                //boolean isFluid = !(blockTuple.getBlockState().getFluidState().isEmpty());
                return shape.clip(ray.getFrom(), ray.getTo(), pos);
            }

            @Override
            public double getDistSquare(BiTuple.RayTupleV3 ray, BlockHitResult result) {
                //todo when miss,  get the dist between center and from?
                if (result == null || result.getType() == HitResult.Type.MISS)
                    return Double.MAX_VALUE;

                return JomlUtil.sqDist(result.getLocation(), ray.getFrom());
            }
        };

    public static final TraverseResultSupplier<BlockHitResult> VanillaMissOnEmptySupplier =
        new TraverseResultSupplier<BlockHitResult>() {
            @Override
            public double getDistSquare(BiTuple.RayTupleV3 ray, BlockHitResult result) {
                if (result == null)
                    return Double.MAX_VALUE;

                return JomlUtil.sqDist(result.getLocation(), ray.getFrom());
            }

            @Override
            public BlockHitResult supplyResult(Level level, BiTuple.RayTupleV3 ray, BlockPos pos, VoxelShape shape) {
                BlockHitResult hit = shape.clip(ray.getFrom(), ray.getTo(), pos);
                if (hit == null)
                    return BlockHitResult.miss(pos.getCenter(), Direction.UP, pos);
                return hit;
            }
    };

    public static final TraverseResultSupplier<BlockHitResult> NoMissVanillaSupplier =
        new TraverseResultSupplier<BlockHitResult>() {
            @Override
            public BlockHitResult supplyResult(Level level, BiTuple.RayTupleV3 ray, BlockPos pos, VoxelShape shape) {
                //boolean isFluid = !(blockTuple.getBlockState().getFluidState().isEmpty());
                BlockHitResult hit = shape.clip(ray.getFrom(), ray.getTo(), pos);
                if (hit == null || hit.getType() == HitResult.Type.MISS)
                    return null;
                return hit;
            }

            @Override
            public double getDistSquare(BiTuple.RayTupleV3 ray, BlockHitResult result) {
                if (result == null || result.getType() == HitResult.Type.MISS)
                    return Double.MAX_VALUE;

                return JomlUtil.sqDist(result.getLocation(), ray.getFrom());
            }
        };

    public static final TraverseResultSupplier<BallisticsHitInfo> BallisticSupplier =
        new TraverseResultSupplier<BallisticsHitInfo>() {
            @Override
            public BallisticsHitInfo supplyResult(Level level, BiTuple.RayTupleV3 ray, BlockPos pos, VoxelShape shape) {
                BlockHitResult hit = shape.clip(ray.getFrom(), ray.getTo(), pos);
                if (hit == null || hit.getType() != HitResult.Type.BLOCK)
                    return null;

                //since it must hit a block, we can use getShipAt to get the ship
                Ship shipBeHit = ShipUtil.getShipAt(level, pos);
                if (shipBeHit == null) {
                    return BallisticsHitInfo.inWorld(ray.getFrom(), pos, hit.getLocation(), hit.getDirection());
                }

                return BallisticsHitInfo.inShip(shipBeHit, ray.getFrom(), pos, hit.getLocation(), hit.getDirection());
            }

            @Override
            public double getDistSquare(BiTuple.RayTupleV3 ray, BallisticsHitInfo result) {
                if (result == null)
                    return Double.MAX_VALUE;

                return result.sqDist;
            }
        };
}
