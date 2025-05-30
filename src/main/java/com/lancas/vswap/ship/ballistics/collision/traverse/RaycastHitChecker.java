package com.lancas.vswap.ship.ballistics.collision.traverse;

import com.lancas.vswap.ship.ballistics.data.BallisticsHitInfo;
import com.lancas.vswap.foundation.BiTuple;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Objects;
import java.util.function.Predicate;

public class RaycastHitChecker<T> {
    public final TraverseResultSupplier<T> resultSupplier;
    private final Predicate<T> hitPredicate;

    private RaycastHitChecker(TraverseResultSupplier<T> inResultSupplier, Predicate<T> inHitPredicate) {
        resultSupplier = inResultSupplier;
        hitPredicate = inHitPredicate;
    }

    public T getHitResult(Level level, ClipContext ctx, BlockPos pos) {
        BiTuple.RayTupleV3 ray = new BiTuple.RayTupleV3(ctx.getFrom(), ctx.getTo());

        // 获取方块与流体状态
        BlockState blockState = level.getBlockState(pos);
        FluidState fluidState = level.getFluidState(pos);

        // 检测方块碰撞
        VoxelShape blockShape = ctx.getBlockShape(blockState, level, pos);
        T blockHit = resultSupplier.supplyResult(level, ray, pos, blockShape);
        double blockDistSq = resultSupplier.getDistSquare(ray, blockHit);

        // 检测流体碰撞
        VoxelShape fluidShape = ctx.getFluidShape(fluidState, level, pos);
        T fluidHit = resultSupplier.supplyResult(level, ray, pos, fluidShape);
        double fluidDistDq = resultSupplier.getDistSquare(ray, fluidHit);

        return blockDistSq <= fluidDistDq ? blockHit : fluidHit;
    }
    //todo nerver used, block traverse already has one
    public boolean isHit(T result) { return hitPredicate.test(result); }

    public static final RaycastHitChecker<BlockHitResult> Vanilla = new RaycastHitChecker<>(
        TraverseResultSupplier.VanillaSupplier,
        hit -> hit != null && hit.getType() != HitResult.Type.MISS
    );

    public static final RaycastHitChecker<BlockHitResult> VanillaNoMiss = new RaycastHitChecker<>(TraverseResultSupplier.NoMissVanillaSupplier, Objects::nonNull);
    public static final RaycastHitChecker<BallisticsHitInfo> Ballistic = new RaycastHitChecker<>(TraverseResultSupplier.BallisticSupplier, Objects::nonNull);

    public static final RaycastHitChecker<BlockHitResult> VanillaMissOnEmpty = new RaycastHitChecker<>(
        TraverseResultSupplier.VanillaMissOnEmptySupplier,
        hit -> hit != null && hit.getType() != HitResult.Type.MISS
    );
    /*
    public static RaycastHitChecker<BallisticsHitInfo> ballisticsInWorldChecker = new RaycastHitChecker<BallisticsHitInfo>() {
        @Override
        public BallisticsHitInfo apply(Level level, ClipContext ctx, BlockPos pos) {
            // 获取方块与流体状态
            BlockState blockState = level.getBlockState(pos);
            FluidState fluidState = level.getFluidState(pos);

            // 检测方块碰撞
            VoxelShape blockShape = ctx.getBlockShape(blockState, level, pos);
            BlockHitResult blockHit = TraverseResultSupplier.BallisticInWorldSupplier.apply() clipAgainstShape(ctx.getFrom(), ctx.getTo(), pos, blockShape);

            // 检测流体碰撞
            VoxelShape fluidShape = context.getFluidShape(fluidState, pos);
            BlockHitResult fluidHit = clipAgainstShape(context.rayStart(), context.rayEnd(), pos, fluidShape);

            double blockHitDistSq = blockHit == null ? Double.MAX_VALUE : JomlUtil.sqDist();
            // 返回更近的碰撞结果
            return getCloserHit(context.rayStart(), blockHit, fluidHit);


            return null;
        }
    };*/
}
