package com.lancas.vswap.ship.ballistics.collision.traverse;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.mixins.accessor.ClipCtxAccessor;
import com.lancas.vswap.ship.ballistics.data.BallisticsHitInfo;
import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.util.JomlUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.joml.primitives.Planed;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.lancas.vswap.ship.ballistics.helper.BallisticsUtil.sweepPointsOnPlaneInBounds;

public class BlockTraverser<T> {
    private static final double OVERLAP_SQ_APPROXIMATE = 1E-8;
    private static final double OVERLAP_APPROXIMATE = 1E-4;
    private static final double MIN_STEP = 1E-2;

    private static boolean isApproxOverlap(Vec3 from, Vec3 to) {
        return JomlUtil.sqDist(from, to) < OVERLAP_SQ_APPROXIMATE;
    }
    private static boolean isApproxOverlap(double a, double b) {
        return Math.abs(a - b) < OVERLAP_APPROXIMATE;
    }
    private static boolean isApproxZero(double a) {
        return -OVERLAP_APPROXIMATE < a && a < OVERLAP_APPROXIMATE;
    }

    private static void traverse(Vec3 from, Vec3 to, Predicate<BlockPos> traverseNext) {
        if (JomlUtil.sqDist(from, to) < OVERLAP_SQ_APPROXIMATE) {
            traverseNext.test(BlockPos.containing(from));
            return;
        }

        Vec3 safeFrom = JomlUtil.lerpV3(from, to, -1e-7);
        Vec3 safeTo = JomlUtil.lerpV3(to, from, -1e-7);
        Vec3 rayDir = safeTo.subtract(safeFrom);

        // 步进方向（-1或+1）
        int stepX = Mth.sign(rayDir.x);
        int stepY = Mth.sign(rayDir.y);
        int stepZ = Mth.sign(rayDir.z);

        // 各轴向的单位步进时间（避免除零）
        double deltaX = isApproxZero(rayDir.x) ? Double.MAX_VALUE : stepX / rayDir.x;
        double deltaY = isApproxZero(rayDir.y) ? Double.MAX_VALUE : stepY / rayDir.y;
        double deltaZ = isApproxZero(rayDir.z) ? Double.MAX_VALUE : stepZ / rayDir.z;

        // 计算初始步进阈值
        double tMaxX = deltaX * (stepX > 0 ? 1 - Mth.frac(safeFrom.x) : Mth.frac(safeFrom.x));
        double tMaxY = deltaY * (stepY > 0 ? 1 - Mth.frac(safeFrom.y) : Mth.frac(safeFrom.y));
        double tMaxZ = deltaZ * (stepX > 0 ? 1 - Mth.frac(safeFrom.z) : Mth.frac(safeFrom.z));

        BlockPos.MutableBlockPos curPos = BlockPos.containing(from).mutable();
        while (true) {
            if (!traverseNext.test(curPos.immutable())) {
                return;
            }

            // 判断下一个步进方向
            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) { // X轴最先到达边界
                    tMaxX += deltaX;
                    curPos.move(stepX, 0, 0);
                } else { // Z轴最先到达边界
                    tMaxZ += deltaZ;
                    curPos.move(0, 0, stepZ);
                }
            } else {
                if (tMaxY < tMaxZ) { // Y轴最先到达边界
                    tMaxY += deltaY;
                    curPos.move(0, stepY, 0);
                } else { // Z轴最先到达边界
                    tMaxZ += deltaZ;
                    curPos.move(0, 0, stepZ);
                }
            }

            // 检查是否超出遍历范围
            if (tMaxX > 1.0 && tMaxY > 1.0 && tMaxZ > 1.0) {
                //vanilla this is a failure fall back
                traverseNext.test(curPos.immutable());
                return;
            }
        }
    }
    private static void traverseBound(AABBdc worldBounds, Vector3dc rayDirWithLength, double step, Predicate<BlockPos> traverseNext) {
        if (step < MIN_STEP) return;

        Planed plane = new Planed(worldBounds.center(new Vector3d()), rayDirWithLength.normalize(new Vector3d()));
        HashSet<Vector3d> points = sweepPointsOnPlaneInBounds(plane, worldBounds, step);

        for (Vector3dc point : points) {
            Vec3 from = JomlUtil.v3(point);
            Vec3 to = JomlUtil.v3Add(point, rayDirWithLength);

            traverse(from, to, traverseNext);
        }
    }

    private final RaycastHitChecker<T> raycastHitChecker;
    private final Predicate<T> isHit;
    private BlockTraverser(RaycastHitChecker<T> inChecker, Predicate<T> inIsHit) {
        raycastHitChecker = inChecker;
        isHit = inIsHit;
    }

    private <TMap extends Map<BlockPos, T>> TMap traverseAllInWorldAppend(Level level, ClipContext ctx, TMap dest) {
        Vec3 from = ctx.getFrom();
        Vec3 to = ctx.getTo();

        traverse(
            from, to,
            pos -> {
                dest.computeIfAbsent(pos, p -> raycastHitChecker.getHitResult(level, ctx, p));
                return true;
            }
        );
        return dest;
    }
    /*private <TMap extends Map<BlockPos, T>> TMap traverseBoundAllInWorldAppend(Level level, AABBdc worldBound, Vector3dc dirWithLength, double step, TMap dest) {
        if (step < MIN_STEP) return dest;

        traverseBound(
            worldBound,
            dirWithLength,
            step,
            pos -> {
                dest.computeIfAbsent(pos, p -> raycastHitChecker.getHitResult(level, ctx, p));
                return true;
            }
        );
        return dest;
    }*/


    @NotNull
    public LinkedHashMap<BlockPos, T> traverseAllWorld(Level level, ClipContext ctx) {
        LinkedHashMap<BlockPos, T> results = new LinkedHashMap<>();
        traverseAllInWorldAppend(level, ctx, results);
        return results;
    }
    @NotNull
    public LinkedHashMap<BlockPos, T> traverseAllIncludeShip(
        Level level, ClipContext ctx, @Nullable Set<Long> skipShips
    ) {
        return traverseAllIncludeShipAppend(level, ctx, skipShips, new LinkedHashMap<>());
    }

    @NotNull
    public <TMap extends Map<BlockPos, T>> TMap traverseAllIncludeShipAppend(
        Level level, ClipContext ctx, @Nullable Set<Long> skipShips, TMap dest
    ) {
        if (!(ctx instanceof ClipCtxAccessor ctxAccessor)) {
            EzDebug.error("unable to access clip accessor");
            return dest;
        }

        ClipContext ctxInWorld = new ClipContext(
            ctx.getFrom(), ctx.getTo(), ctxAccessor.getBlock(), ctxAccessor.getFluid(), null
        );
        traverseAllInWorldAppend(level, ctxInWorld, dest);

        AABBd clipAABB = JomlUtil.correctAABBd(ctx.getFrom(), ctx.getTo());
        for (Ship ship : VSGameUtilsKt.getShipObjectWorld(level).getAllShips().getIntersecting(clipAABB)) {
            if (ship == null) continue;  //just for safe
            if (skipShips != null && skipShips.contains(ship.getId())) continue;

            Matrix4dc worldToShip;
            Matrix4dc shipToWorld;
            if (ship instanceof ClientShip cShip) {
                worldToShip = cShip.getRenderTransform().getWorldToShip();
                shipToWorld = cShip.getRenderTransform().getShipToWorld();
            } else {
                worldToShip = ship.getTransform().getWorldToShip();
                shipToWorld = ship.getTransform().getShipToWorld();
            }

            Vec3 fromInShip = JomlUtil.transformPosV3(worldToShip, ctx.getFrom());
            Vec3 toInShip = JomlUtil.transformPosV3(worldToShip, ctx.getTo());

            ClipContext ctxInShip = new ClipContext(
                fromInShip, toInShip, ctxAccessor.getBlock(), ctxAccessor.getFluid(), null
            );
            traverseAllInWorldAppend(level, ctxInShip, dest);
        }

        return dest;
    }

    /*
    @NotNull
    public <TMap extends Map<BlockPos, T>> TMap traverseVoxelAllIncludeShipAppend(
        Level level, AABBdc worldBound, Vector3dc dirWithLength, ClipContext.Block blockShapeGetter, ClipContext.Fluid fluidShapeGetter, @Nullable Set<Long> skipShips, TMap dest
    ) {
        ClipContext ctxInWorld = new ClipContext(
            ray.getFrom(), ray.getTo(), blockShapeGetter, fluidShapeGetter, null
        );
        traverseAllInWorldAppend(level, ctxInWorld, dest);

        AABBd clipAABB = JomlUtil.correctAABBd(ray.getFrom(), ray.getTo());
        for (Ship ship : VSGameUtilsKt.getShipObjectWorld(level).getAllShips().getIntersecting(clipAABB)) {
            if (ship == null) continue;  //just for safe
            if (skipShips != null && skipShips.contains(ship.getId())) continue;

            Matrix4dc worldToShip;
            Matrix4dc shipToWorld;
            if (ship instanceof ClientShip cShip) {
                worldToShip = cShip.getRenderTransform().getWorldToShip();
                shipToWorld = cShip.getRenderTransform().getShipToWorld();
            } else {
                worldToShip = ship.getTransform().getWorldToShip();
                shipToWorld = ship.getTransform().getShipToWorld();
            }

            Vec3 fromInShip = JomlUtil.transformPosV3(worldToShip, ray.getFrom());
            Vec3 toInShip = JomlUtil.transformPosV3(worldToShip, ray.getTo());

            ClipContext ctxInShip = new ClipContext(
                fromInShip, toInShip, blockShapeGetter, fluidShapeGetter, null
            );
            traverseAllInWorldAppend(level, ctxInShip, dest);
        }

        return dest;
    }*/

    public List<T> traverseAllIncludeShipSorted(
        Level level, ClipContext ctx, @Nullable Set<Long> skipShips
    ) {
        var unsorted = traverseAllIncludeShip(level, ctx, skipShips);
        return unsorted.values().stream().sorted(
            (a, b) -> {
                double sqDistA = raycastHitChecker.resultSupplier.getDistSquare(new BiTuple.RayTupleV3(ctx.getFrom(), ctx.getTo()), a);
                double sqDistB = raycastHitChecker.resultSupplier.getDistSquare(new BiTuple.RayTupleV3(ctx.getFrom(), ctx.getTo()), b);
                return Double.compare(sqDistA, sqDistB);
            }
        ).toList();
    }

    public T traverseFirstHitInWorld(Level level, ClipContext ctx) {
        Vec3 from = ctx.getFrom();
        Vec3 to = ctx.getTo();
        LinkedHashMap<BlockPos, T> results = new LinkedHashMap<>();
        final AtomicReference<T> hitResult = new AtomicReference<>(null);

        traverse(
            from, to,
            pos -> {
                if (hitResult.get() != null)
                    return false;

                T curResult = results.computeIfAbsent(pos, p -> raycastHitChecker.getHitResult(level, ctx, p));
                if (isHit.test(curResult)) {
                    hitResult.set(curResult);
                    return false;
                }

                return true;
            }
        );

        return hitResult.get();
    }
    public T traverseFirstHitIncludeShip(Level level, ClipContext ctxInWorld, @Nullable Set<Long> skipShips) {
        final AtomicReference<T> hitResult = new AtomicReference<>(null);

        hitResult.set(traverseFirstHitInWorld(level, ctxInWorld));
        if (isHit.test(hitResult.get())) return hitResult.get();

        ClipCtxAccessor worldCtxAccessor = (ClipCtxAccessor)ctxInWorld;
        foreachNearbyShips(level, ctxInWorld, skipShips,
            ship -> {
                Matrix4dc worldToShip = (ship instanceof ClientShip cShip) ? cShip.getRenderTransform().getWorldToShip() : ship.getTransform().getWorldToShip();

                Vec3 fromInShip = JomlUtil.transformPosV3(worldToShip, ctxInWorld.getFrom());
                Vec3 toInShip = JomlUtil.transformPosV3(worldToShip, ctxInWorld.getTo());

                ClipContext ctxInShip = new ClipContext(
                    fromInShip, toInShip, worldCtxAccessor.getBlock(), worldCtxAccessor.getFluid(), null
                );

                traverse(ctxInShip.getFrom(), ctxInShip.getTo(),
                    pos -> {
                        if (hitResult.get() != null)
                            return false;

                        T curResult = raycastHitChecker.getHitResult(level, ctxInShip, pos);
                        if (isHit.test(curResult)) {
                            hitResult.set(curResult);
                            return false;
                        }

                        return true;
                    }
                );
        });
        return hitResult.get();
    }
    private void foreachNearbyShips(Level level, ClipContext ctxInWorld, @Nullable Set<Long> skipShips, Consumer<Ship> consumer) {
        AABBd clipAABB = JomlUtil.correctAABBd(ctxInWorld.getFrom(), ctxInWorld.getTo());
        for (Ship ship : VSGameUtilsKt.getShipObjectWorld(level).getAllShips().getIntersecting(clipAABB)) {
            if (ship == null) continue;  //just for safe
            if (skipShips != null && skipShips.contains(ship.getId())) continue;

           consumer.accept(ship);
        }
    }

    public static final BlockTraverser<BlockHitResult> Vanilla = new BlockTraverser<>(RaycastHitChecker.Vanilla, hit -> hit != null && hit.getType() != HitResult.Type.MISS);
    public static final BlockTraverser<BlockHitResult> VanillaMissOnEmpty = new BlockTraverser<>(RaycastHitChecker.VanillaMissOnEmpty, hit -> hit != null && hit.getType() != HitResult.Type.MISS);
    public static final BlockTraverser<BlockHitResult> VanillaNoMiss = new BlockTraverser<>(RaycastHitChecker.VanillaNoMiss, hit -> hit != null && hit.getType() != HitResult.Type.MISS);
    public static final BlockTraverser<BallisticsHitInfo> Ballistics = new BlockTraverser<>(RaycastHitChecker.Ballistic, hit -> hit != null);
}
