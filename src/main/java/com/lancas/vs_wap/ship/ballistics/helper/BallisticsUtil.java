package com.lancas.vs_wap.ship.ballistics.helper;

import com.lancas.vs_wap.content.info.block.WapBlockInfos;
import com.lancas.vs_wap.ship.ballistics.data.BallisticsHitInfo;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.*;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class BallisticsUtil {
    private static final double PREDICT_TIME = 0.2;
    public static final double MIN_STEP = 0.01;

    /*private static final double SHRINK = 0.1;  //to avoid ghost collide
    private static final double INV_SHRINK = 1 - SHRINK;
    private static final Vec3[] shipOffsets = new Vec3[] {
        new Vec3(SHRINK, SHRINK, SHRINK),              //000
        new Vec3(SHRINK, SHRINK, INV_SHRINK),          //001
        new Vec3(SHRINK, INV_SHRINK, SHRINK),          //010
        new Vec3(SHRINK, INV_SHRINK,  INV_SHRINK),     //011
        new Vec3(INV_SHRINK, SHRINK, SHRINK),          //100
        new Vec3(INV_SHRINK, SHRINK, INV_SHRINK),      //101
        new Vec3(INV_SHRINK, INV_SHRINK, SHRINK),      //110
        new Vec3(INV_SHRINK, INV_SHRINK, INV_SHRINK),  //111
    };*/

    /*private static final Method vsClip;

    static {
        try {
            vsClip = RaycastUtilsKt.class.getDeclaredMethod("clip", Level.class, ClipContext.class, Vec3.class, Vec3.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }*/

    /*public static AABBd predictCollisionAABB(ServerShip ship, BlockPos warheadShipPos) {
        AABBd aabb = new AABBd();
        Matrix4dc ship2World = ship.getShipToWorld();
        Vector3d tickMovement = ship.getVelocity().mul(PREDICT_TIME, new Vector3d());


        for (double dx = SHRINK; dx <= 1.0 - SHRINK; dx += 1.0 - 2 * SHRINK)
            for (double dy = SHRINK; dy <= 1.0 - SHRINK; dy += 1.0 - 2 * SHRINK)
                for (double dz = SHRINK; dz <= 1.0 - SHRINK; dz += 1.0 - 2 * SHRINK) {
                    Vec3 corner = Vec3.atLowerCornerWithOffset(warheadShipPos, dx, dy, dz);
                    Vector3d worldCorner = ship2World.transformPosition(JomlUtil.d(corner));

                    aabb.union(worldCorner);
                    aabb.union(worldCorner.add(tickMovement));  //include the movement
                }

        return aabb;
    }
    public static Iterable<BlockPos> predictCollideBPs(ServerShip ship, BlockPos warheadShipPos) {
        AABBd predictAABB = predictCollisionAABB(ship, warheadShipPos);
        //double强制转换为int会保留整数而不是向下取整，所以需要用floor
        return BlockPos.betweenClosed(
            (int)Math.floor(predictAABB.minX),
            (int)Math.floor(predictAABB.minY),
            (int)Math.floor(predictAABB.minZ),
            (int)Math.floor(predictAABB.maxX),
            (int)Math.floor(predictAABB.maxY),
            (int)Math.floor(predictAABB.maxZ)
        );
    }*/

    /*public static Iterable<BallisticsHitInfo> predictCollisions(ServerLevel level, ServerShip ship, BlockPos warheadShipPos) {
        Matrix4dc ship2World = ship.getShipToWorld();
        Vec3 sweep = JomlUtil.v3(ship.getVelocity().mul(PREDICT_TIME, new Vector3d()));

        List<BallisticsHitInfo> infoList = new ArrayList<>();

        for (Vec3 shipOffset : shipOffsets) {
            Vec3 shipCorner = Vec3.atLowerCornerWithOffset(warheadShipPos, shipOffset.x, shipOffset.y, shipOffset.z);
            Vec3 worldCorner = JomlUtil.v3(ship2World.transformPosition(JomlUtil.d(shipCorner)));

            //EzDebug.Log("worldC" + worldCorner + ", sweeped:" + worldCorner.add(sweep));

            ClipContext clipCtx = new ClipContext(
                worldCorner,
                worldCorner.add(sweep),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null
            );
            //skip ship Id
            BlockHitResult hit = RaycastUtilsKt.clipIncludeShips(level, clipCtx, true, ship.getId());//level.clip(clipCtx);

            if (hit.isInside()) {  //碰撞上了
                infoList.add(new BallisticsHitInfo(
                    hit.getBlockPos(),
                    hit.getDirection()
                ));
                EzDebug.Log(
                    "[add info] wcorner:" + worldCorner +
                        ", addSwp:" + worldCorner.add(sweep) +
                        ", pos:" + hit.getBlockPos() +
                        ", state:" + level.getBlockState(hit.getBlockPos()) +
                        ", dir:" + hit.getDirection() +
                        ", vel:" + ship.getVelocity() +
                        ", sweep:" + sweep
                );
            }
        }

        return infoList;
    }*/
    /*public static Iterable<BallisticsHitInfo> predictCollisions(ServerLevel level, ServerShip ballisticShip, BlockPos warheadShipPos, BlockState warheadState, VoxelShape shape) {
        Vector3d predictMovement = ballisticShip.getVelocity().mul(PREDICT_TIME, new Vector3d());
        AABBd predictAABB = getPredictWorldAABB(ballisticShip.getShipToWorld(), warheadShipPos, shape, predictMovement);

        //Matrix4dc ship2World = ship.getShipToWorld();
        //Vector3d predictMovement = ship.getVelocity().mul(PREDICT_TIME, new Vector3d());
        Vector3d warheadWorldPos = ballisticShip.getShipToWorld().transformPosition(JomlUtil.dCenter(warheadShipPos));
        Vector3dc shipVel = ballisticShip.getVelocity();

        Map<BlockPos, BallisticsHitInfo> infoList = new LinkedHashMap<>();

        /.*for (Vec3 shipOffset : shipOffsets) {
            shape.forAllBoxes();
            Vec3 shipCorner = Vec3.atLowerCornerWithOffset(warheadShipPos, shipOffset.x, shipOffset.y, shipOffset.z);
            Vector3d worldCorner = ship2World.transformPosition(JomlUtil.d(shipCorner));

            predictAABB.union(worldCorner);
            predictAABB.union(worldCorner.add(predictMovement));  //include the movement
        }*./
        //EzDebug.Log("-----------------------------");


        //todo predict include ship
        /.*BlockPos.betweenClosed(
            (int)Math.floor(predictAABB.minX),
            (int)Math.floor(predictAABB.minY),
            (int)Math.floor(predictAABB.minZ),
            (int)Math.floor(predictAABB.maxX),
            (int)Math.floor(predictAABB.maxY),
            (int)Math.floor(predictAABB.maxZ)
        )*./
        if (!(warheadState.getBlock() instanceof IDirectionalBlock dirBlock)) {
            return new ArrayList<>();
        }

        Direction warheadDir = dirBlock.getDirection(warheadState);
        ShapeBuilder sb = new ShapeBuilder(shape).rotated(warheadDir);
        CollisionUtil.getCoveredWorldBlocks(ballisticShip.getShipToWorld(), warheadShipPos, sb.getDBounds())
        .forEach(pos -> {
            BlockState state = level.getBlockState(pos);
            if (state.isAir()) return;

            //todo 获取predictAABB对速度方向的投影，获取所有方块然后检测射线
            //predictAABB对速度的投影即为原AABB对速度方向的投影

            /.*Vector3d clipStart = JomlUtil.dCenter(pos).sub(shipVel.mul(PREDICT_TIME, new Vector3d()));
            Vector3d clipStartFromWarhead = clipStart.sub(warheadWorldPos, new Vector3d());
            double dot = clipStartFromWarhead.dot(shipVel);
            if (dot < 0) {  //开始点在warheadWorldPos后面，不应该裁剪
                EzDebug.Log("clipStart:" + clipStart + ", warhead:" + warheadWorldPos + ", dot:" + dot);
                return;
            }*./

            ClipContext clipCtx = new ClipContext(
                pos.getCenter(),//pos.getCenter().subtract(JomlUtil.v3(ship.getVelocity().mul(PREDICT_TIME, new Vector3d()))),  //ensure clip dir is velDir
                //warheadWorldPos,  //todo temp
                JomlUtil.v3(JomlUtil.dCenter(pos).add(predictMovement)),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null
            );
            //todo shouldTransformHitPos is true?
            HashSet<Long> skipShipIds = new HashSet<>();
            skipShipIds.add(ballisticShip.getId());
            //skipShipIds.add(propellantShip.getId());
            BlockHitResult hit = clipIncludeShips(
                level,
                clipCtx,
                true,
                skipShipIds
            );//level.clip(clipCtx);
            if (hit.getType() == HitResult.Type.BLOCK && !infoList.containsKey(hit.getBlockPos())) {
                ServerShip hitShip = ShipUtil.getShipAt(level, hit.getBlockPos());
                if (hitShip != null) {
                    BallisticsHitInfo info = new BallisticsHitInfo(
                        hit.getBlockPos(),
                        JomlUtil.d(hit.getLocation()),
                        hit.getDirection(),
                        hitShip.getTransform().getShipToWorldRotation().transform(JomlUtil.dNormal(hit.getDirection())),
                        JomlUtil.sqDist(warheadWorldPos, hit.getLocation()),
                        hitShip.getId()
                    );
                    //hit on ship
                    infoList.put(
                        hit.getBlockPos(),
                        info
                    );
                    EzDebug.Log("hit ship:" + hitShip.getId() + ", ctx:" + clipCtx + ", info:" + info);
                } else {  //hit on world
                    infoList.put(
                        hit.getBlockPos(),
                        new BallisticsHitInfo(
                            hit.getBlockPos(),
                            JomlUtil.d(hit.getLocation()),
                            hit.getDirection(),
                            JomlUtil.dNormal(hit.getDirection()),
                            JomlUtil.sqDist(hit.getLocation(), warheadWorldPos),
                            -1
                        )
                    );
                }

            }
        });
        return infoList.values();
    }*/

    /*public static AABBd getPredictWorldAABB(Matrix4dc shipToWorld, BlockPos warheadShipPos, VoxelShape shape, Vector3dc movement) {
        AABBd predictAABB = new AABBd();
        AABBd bound = JomlUtil.d(shape.bounds());

        for (double x = bound.minX; x <= bound.maxX; x += bound.maxX - bound.minX)
            for (double y = bound.minY; y <= bound.maxY; y += bound.maxY - bound.minY)
                for (double z = bound.minZ; z <= bound.maxZ; z += bound.maxZ - bound.minZ) {
                    Vec3 shipCorner = Vec3.atLowerCornerWithOffset(warheadShipPos, x, y, z);
                    Vector3d worldCorner = shipToWorld.transformPosition(JomlUtil.d(shipCorner));

                    predictAABB.union(worldCorner);
                    predictAABB.union(worldCorner.add(movement));  //include the movement
                }

        /.*int loops = 10000;
        StopWatch sw = new StopWatch();
        sw.start();
        for (int i = 0; i < loops; ++i) {
            predictAABB = transformAABB(shipToWorld, bound);
        }
        sw.stop();
        EzDebug.Log("transformAABB for " + loops + "times, use time:" + sw.getTime(TimeUnit.MICROSECONDS) + " micros");

        sw.reset();
        sw.start();
        for (int i = 0; i < loops; ++i) {
            quickPredictAABB = quickTransformAABB(shipToWorld, bound);
        }
        sw.stop();
        EzDebug.Log("quickTransformAABB for " + loops + "times, use time:" + sw.getTime(TimeUnit.MICROSECONDS) + " micros");

        EzDebug.Log("normal:" + StringUtil.toNormalString(predictAABB) + ", quick:" + StringUtil.toNormalString(quickPredictAABB) + ", origin:" + StringUtil.toNormalString(originAABBRes));
        *./
        /.*AABBd boundInShip = bound.translate(JomlUtil.d(warheadShipPos), new AABBd());

        AABBd testInner = new AABBd();
        AABBd testQuick = new AABBd();

        int loops = 10000;
        StopWatch sw = new StopWatch();
        sw.start();
        for (int i = 0; i < loops; ++i) {
             boundInShip.transform(shipToWorld, testInner);
        }
        sw.stop();
        EzDebug.Log("transformAABB for " + loops + "times, use time:" + sw.getTime(TimeUnit.MICROSECONDS) + " micros");

        sw.reset();
        sw.start();
        for (int i = 0; i < loops; ++i) {
            quickTransformAABB(shipToWorld, boundInShip, testQuick);
        }
        sw.stop();
        EzDebug.Log("quickTransformAABB for " + loops + "times, use time:" + sw.getTime(TimeUnit.MICROSECONDS) + " micros");

        EzDebug.Log("inner:" + StringUtil.toNormalString(testInner) + ", quick:" + StringUtil.toNormalString(testQuick) + ", origin:" + StringUtil.toNormalString(predictAABB));
        *./



        return predictAABB;
    }*/





    /*public static BlockHitResult clipIncludeShips(Level level, ClipContext ctx, boolean shouldTransformHitPos, Set<Long> skipShips) {
        BlockHitResult vanillaHit = RaycastUtilsKt.vanillaClip(level, ctx);

        BlockHitResult closestHit = vanillaHit;
        Vector3d closestHitPos = JomlUtil.d(vanillaHit.getLocation());
        double closestHitSqDist = closestHitPos.distanceSquared(JomlUtil.d(ctx.getFrom()));

        AABBdc clipAABB = new AABBd(JomlUtil.d(ctx.getFrom()), JomlUtil.d(ctx.getTo())).correctBounds();

        // Iterate every ship, find do the raycast in ship space,
        // choose the raycast with the lowest distance to the start position.
        for (Ship ship : VSGameUtilsKt.getShipsIntersecting(level, clipAABB)) {
            EzDebug.Log("skips:" + skipShips + ", try checking ship:" + ship.getId());
            // Skip skipShip
            if (skipShips.contains(ship.getId())) {
                EzDebug.Log("skip ship:" + ship.getId());
                continue;
            }

            Matrix4dc worldToShip;
            Matrix4dc shipToWorld;
            if (ship instanceof ClientShip cShip) {
                worldToShip = cShip.getRenderTransform().getWorldToShip();
                shipToWorld = cShip.getRenderTransform().getShipToWorld();
            } else {
                worldToShip = ship.getWorldToShip();
                shipToWorld = ship.getShipToWorld();
            }

            Vector3d shipStart = worldToShip.transformPosition(JomlUtil.d(ctx.getFrom()));
            Vector3d shipEnd = worldToShip.transformPosition(JomlUtil.d(ctx.getTo()));

            BlockHitResult shipHit = clip(level, ctx, JomlUtil.v3(shipStart), JomlUtil.v3(shipEnd));
            Vector3d shipHitPos = shipToWorld.transformPosition(JomlUtil.d(shipHit.getLocation()));
            double shipHitSqDist = shipHitPos.distanceSquared(JomlUtil.d(ctx.getFrom()));

            EzDebug.Log(
                "newCloest?:" + (shipHitSqDist < closestHitSqDist) + ", shipHitType:" + shipHit.getType() + "\n" +
                    ", closestHitSqDist:" + closestHitSqDist + ", shitHitSqDist:" + shipHitSqDist);
            if (shipHitSqDist < closestHitSqDist && shipHit.getType() != HitResult.Type.MISS) {
                EzDebug.Log("set closestHit to current ship hit");
                closestHit = shipHit;
                closestHitPos = shipHitPos;
                closestHitSqDist = shipHitSqDist;
            }
        }

        Vec3 finalLocation = closestHit.getLocation();
        if (shouldTransformHitPos) {
            finalLocation = JomlUtil.v3(closestHitPos);
        }

        if (closestHit.getType() == HitResult.Type.MISS) {
            closestHit = BlockHitResult.miss(
                finalLocation,
                closestHit.getDirection(),
                closestHit.getBlockPos()
            );
        } else {
            closestHit = new BlockHitResult(
                finalLocation,
                closestHit.getDirection(),
                closestHit.getBlockPos(),
                closestHit.isInside()
            );
        }
        return closestHit;
    }*/


    /*private static BlockHitResult VsClip(Level level, ClipContext ctx, Vector3dc start, Vector3dc end) {
        try {
            BlockHitResult hit = (BlockHitResult)vsClip.invoke(null, level, ctx, JomlUtil.v3(start), JomlUtil.v3(end));
            return hit;
        } catch (Exception e) {
            EzDebug.error("fail to call vsClip, exception:" + e.toString());
            //todo correct the miss return value
            return BlockHitResult.miss(new Vec3(0, 0, 0), Direction.UP, new BlockPos(0, 0, 0));
        }

    }*/
    //copy from vs
    /*private static BlockHitResult clip(Level level, ClipContext context, Vec3 realStart, Vec3 realEnd) {
        return clip(
            realStart, realEnd, context,
            (ClipContext raycastContext, BlockPos blockPos) -> {
                BlockState blockState = level.getBlockState(blockPos);
                FluidState fluidState = level.getFluidState(blockPos);
                Vec3 vec3d = realStart;
                Vec3 vec3d2 = realEnd;
                VoxelShape voxelShape = raycastContext.getBlockShape(blockState, level, blockPos);
                BlockHitResult blockHitResult =
                    level.clipWithInteractionOverride(vec3d, vec3d2, blockPos, voxelShape, blockState);

                VoxelShape voxelShape2 = raycastContext.getFluidShape(fluidState, level, blockPos);
                BlockHitResult blockHitResult2 = voxelShape2.clip(vec3d, vec3d2, blockPos);

                double d, e;
                if (blockHitResult == null) {
                    d = Double.MAX_VALUE;
                } else {
                    d = realStart.distanceToSqr(blockHitResult.getLocation());
                }
                if (blockHitResult2 == null)
                    e = Double.MAX_VALUE;
                else
                    e = realEnd.distanceToSqr(blockHitResult2.getLocation());

                if (d <= e)
                    return blockHitResult;
                else
                    return blockHitResult2;
            },
            (ClipContext raycastContext) -> {
                Vec3 vec3d = realStart.subtract(realEnd);
                return BlockHitResult.miss(realEnd, Direction.getNearest(vec3d.x, vec3d.y, vec3d.z), BlockPos.containing(realEnd));
            }
        );
    }

    private static <T> T clip(
        Vec3 realStart,
        Vec3 realEnd,
        ClipContext raycastContext,
        BiFunction<ClipContext, BlockPos, T> context,
        Function<ClipContext, T> blockRaycaster
    ) {
        Vec3 vec3d = realStart;
        Vec3 vec3d2 = realEnd;

        if (vec3d.equals(vec3d2)) {
            return blockRaycaster.apply(raycastContext);
        } else {
            double d = lerp(-1.0E-7, vec3d2.x, vec3d.x);
            double e = lerp(-1.0E-7, vec3d2.y, vec3d.y);
            double f = lerp(-1.0E-7, vec3d2.z, vec3d.z);
            double g = lerp(-1.0E-7, vec3d.x, vec3d2.x);
            double h = lerp(-1.0E-7, vec3d.y, vec3d2.y);
            double i = lerp(-1.0E-7, vec3d.z, vec3d2.z);
            double j = floor(g);
            double k = floor(h);
            double l = floor(i);

            var mutable = new BlockPos.MutableBlockPos(j, k, l);
            T obj = context.apply(raycastContext, mutable);
            if (obj != null)
                return obj;
             else {
                double m = d - g;
                double n = e - h;
                double o = f - i;
                double p = sign(m);
                double q = sign(n);
                double r = sign(o);
                double s = p == 0 ? Double.MAX_VALUE : p / m;
                double t = q == 0 ? Double.MAX_VALUE : q / n;
                double u = r == 0 ? Double.MAX_VALUE : r / o;
                var v = s * (p > 0 ? (1.0 - Mth.frac(g)) : Mth.frac(g));
                var w = t * (q > 0 ? (1.0 - Mth.frac(h)) : Mth.frac(h));
                var x = u * (r > 0 ? (1.0 - Mth.frac(i)) : Mth.frac(i));

                T obj2;
                do {
                    if (v > 1.0 && w > 1.0 && x > 1.0) {
                        return blockRaycaster.apply(raycastContext);
                    }
                    if (v < w) {
                        if (v < x) {
                            j += p;
                            v += s;
                        } else {
                            l += r;
                            x += u;
                        }
                    } else if (w < x) {
                        k += q;
                        w += t;
                    } else {
                        l += r;
                        x += u;
                    }
                    obj2 = context.apply(raycastContext, mutable.set(j, k, l));
                } while (obj2 == null);
                return obj2;
            }
        }
    }*/

    /*
    public static AABBd transformAABB(Matrix4dc transformer, AABBdc aabb) {
        AABBd transformed = new AABBd();

        /.*for (double x : new double[]{aabb.minX(), aabb.maxX()})
            for (double y : new double[]{aabb.minY(), aabb.maxY()})
                for (double z : new double[]{aabb.minZ(), aabb.maxZ()}) {
                    Vector3d transformedPos = transformer.transformPosition(x, y, z, new Vector3d());
                    transformed.union(transformedPos);
                }*./
        for (double x = aabb.minX(); x <= aabb.maxX(); x += aabb.maxX() - aabb.minX())
            for (double y = aabb.minY(); y <= aabb.maxY(); y += aabb.maxY() - aabb.minY())
                for (double z = aabb.minZ(); z <= aabb.maxZ(); z += aabb.maxZ() - aabb.minZ()) {
                    Vector3d transformedPos = transformer.transformPosition(x, y, z, new Vector3d());
                    transformed.union(transformedPos);
                }
        return transformed;
    }*/

    public static HashSet<Vector3d> sweepPointsOnPlaneInBounds(Planed plane, AABBdc bounds, double step) {
        if (step < MIN_STEP) return new HashSet<>();  //too much points

        HashSet<Vector3d> points = new HashSet<>();

        double ax = Math.abs(plane.a);
        double ay = Math.abs(plane.b);
        double az = Math.abs(plane.c);
        if (ax >= ay && ax >= az) {
            // 主导轴为X轴，遍历Y和Z
            for (double y = bounds.minY(); y <= bounds.maxY(); y += step) {
                for (double z = bounds.minZ(); z <= bounds.maxZ(); z += step) {
                    double xVal = (-plane.d - plane.b * y - plane.c * z) / plane.a;
                    points.add(new Vector3d(xVal, y, z));
                }
            }
        } else if (ay >= ax && ay >= az) {
            // 主导轴为Y轴，遍历X和Z
            for (double x = bounds.minX(); x <= bounds.maxX(); x += step) {
                for (double z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                    double yVal = (-plane.d - plane.a * x - plane.c * z) / plane.b;
                    points.add(new Vector3d(x, yVal, z));
                }
            }
        } else {
            // 主导轴为Z轴，遍历X和Y
            for (double x = bounds.minX(); x <= bounds.maxX(); x += step) {
                for (double y = bounds.minY(); y <= bounds.maxY(); y += step) {
                    double zVal = (-plane.d - plane.a * x - plane.b * y) / plane.c;
                    points.add(new Vector3d(x, y, zVal));
                }
            }
        }

        return points;
    }
    public static Iterable<ClipContext> raycastPlaneForBlocks(Vector3dc rayWithLength, AABBdc worldBounds, double step) {
        if (step < MIN_STEP) return new ArrayList<>();

        Planed plane = new Planed(worldBounds.center(new Vector3d()), rayWithLength.normalize(new Vector3d()));
        HashSet<Vector3d> points = sweepPointsOnPlaneInBounds(plane, worldBounds, step);
        List<ClipContext> clips = new ArrayList<>();
        for (Vector3dc point : points) {
            clips.add(new ClipContext(
                JomlUtil.v3(point),
                JomlUtil.v3Add(point, rayWithLength),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null
            ));
        }
        return clips;
    }

    public static @Nullable BallisticsHitInfo ballisticsClipInWorld(Level level, ClipContext ctx/*, Vector3dc hitVelocity*/) {
        BlockHitResult hit = RaycastUtilsKt.vanillaClip(level, ctx);
        if (hit.getType() != HitResult.Type.BLOCK)  //todo entity?
            return null;

        return BallisticsHitInfo.inWorld(ctx.getFrom(), hit.getBlockPos(), hit.getLocation(), hit.getDirection()/*, hitVelocity*/); //todo determine it is face or dir
    }
    public static BallisticsHitInfo ballisticsClipInShip(Level level, Ship shipHit, ClipContext ctx, ClipContext.Block blockGetter, ClipContext.Fluid fluidGetter/*, Vector3dc hitVelocity*/) {
        return ballisticsClipInShip(level, JomlUtil.d(ctx.getFrom()), JomlUtil.d(ctx.getTo()), blockGetter, fluidGetter, shipHit/*, hitVelocity*/);
    }
    public static BallisticsHitInfo ballisticsClipInShip(
        Level level,
        Vector3d worldFrom, Vector3dc worldTo,
        ClipContext.Block blockGetter, ClipContext.Fluid fluidGetter,
        Ship shipHit
        //,
        //Vector3dc hitVelocity
    ) {
        Matrix4dc worldToShip =
            (shipHit instanceof ClientShip cShip) ?
                cShip.getRenderTransform().getWorldToShip() :
                shipHit.getWorldToShip();

        Vec3 fromInShip = JomlUtil.transformPosV3(worldToShip, worldFrom);

        ClipContext ctx = new ClipContext(
            fromInShip,
            JomlUtil.transformPosV3(worldToShip, worldTo),
            blockGetter,
            fluidGetter,
            null
        );

        BlockHitResult hit = RaycastUtilsKt.vanillaClip(level, ctx);
        if (hit.getType() != HitResult.Type.BLOCK)  //todo entity?
            return null;

        return BallisticsHitInfo.inShip(shipHit, fromInShip, hit.getBlockPos(), hit.getLocation(), hit.getDirection()/*, hitVelocity*/); //todo determine it is face or dir
    }


    public static AABBd quickTransformAABB(Matrix4dc transformer, AABBdc aabb, AABBd dest) {
        /*double w = transformer.get(new Matrix4d()).get(3, 3);

        Vector3d col0 = transformer.getColumn(0, new Vector3d());
        Vector3d col1 = transformer.getColumn(1, new Vector3d());
        Vector3d col2 = transformer.getColumn(2, new Vector3d());
        Vector3d col3 = transformer.getColumn(3, new Vector3d());

        Vector3d xa = col0.mul(aabb.minX(), new Vector3d());
        Vector3d xb = col0.mul(aabb.maxX(), new Vector3d());

        Vector3d ya = col1.mul(aabb.minY(), new Vector3d());
        Vector3d yb = col1.mul(aabb.maxY(), new Vector3d());

        Vector3d za = col2.mul(aabb.minZ(), new Vector3d());
        Vector3d zb = col2.mul(aabb.maxZ(), new Vector3d());

        Vector3d min = xa.min(xb, new Vector3d()).add(ya.min(yb, new Vector3d())).add(za.min(zb, new Vector3d())).add(col3);
        Vector3d max = xa.max(xb, new Vector3d()).add(ya.max(yb, new Vector3d())).add(za.max(zb, new Vector3d())).add(col3);

        return dest.setMin(min.div(w)).setMax(max.div(w));*/
        return aabb.transform(transformer, dest);
    }
    public static AABBd quickTransformAABB(Matrix4dc transformer, AABBd aabb) {
        return quickTransformAABB(transformer, aabb, aabb);
    }
    public static AABBd quickTransformAABB(Matrix4dc transformer, AABBic aabb, AABBd dest) {
        double w = transformer.get(3, 3);

        Vector3d col0 = transformer.getColumn(0, new Vector3d());
        Vector3d col1 = transformer.getColumn(1, new Vector3d());
        Vector3d col2 = transformer.getColumn(2, new Vector3d());
        Vector3d col3 = transformer.getColumn(3, new Vector3d());

        Vector3d xa = col0.mul(aabb.minX(), new Vector3d());
        Vector3d xb = col0.mul(aabb.maxX(), new Vector3d());

        Vector3d ya = col1.mul(aabb.minY(), new Vector3d());
        Vector3d yb = col1.mul(aabb.maxY(), new Vector3d());

        Vector3d za = col2.mul(aabb.minZ(), new Vector3d());
        Vector3d zb = col2.mul(aabb.maxZ(), new Vector3d());

        Vector3d min = xa.min(xb, new Vector3d()).add(ya.min(yb, new Vector3d())).add(za.min(zb, new Vector3d())).add(col3);
        Vector3d max = xa.max(xb, new Vector3d()).add(ya.max(yb, new Vector3d())).add(za.max(zb, new Vector3d())).add(col3);

        return dest.setMin(min.div(w)).setMax(max.div(w));
    }

    public static AABBd extendAlong(AABBdc aabb, Vector3dc movement, AABBd dest) {
        Vector3d min = getMin(aabb);
        Vector3d max = getMax(aabb);

        Vector3d movedMin = min.add(movement, new Vector3d());
        Vector3d movedMax = max.add(movement, new Vector3d());

        dest.union(min.min(movedMin));
        dest.union(max.max(movedMax));
        return dest;
    }
    public static AABBd extendAlong(AABBd aabb, Vector3dc movement) {
        Vector3d min = getMin(aabb);
        Vector3d max = getMax(aabb);

        Vector3d movedMin = min.add(movement, new Vector3d());
        Vector3d movedMax = max.add(movement, new Vector3d());

        aabb.union(min.min(movedMin));
        aabb.union(max.max(movedMax));
        return aabb;
    }
    public static Vector3d getMin(AABBdc aabb) { return new Vector3d(aabb.minX(), aabb.minY(), aabb.minZ()); }
    public static Vector3d getMax(AABBdc aabb) { return new Vector3d(aabb.maxX(), aabb.maxY(), aabb.maxZ()); }

    public static Vector3d calculateAirDragCenter(ServerLevel level, ServerShip projectile) {
        //todo will excess the max?
        AtomicReference<Double> totalWeight = new AtomicReference<>((double) 0);
        Vector3d sumCenter = new Vector3d();
        ShipUtil.foreachBlock(projectile, level, (pos, state, be) -> {
            if (state.isAir()) return;

            double curDragFactor = WapBlockInfos.drag_factor.valueOrDefaultOf(state);
            totalWeight.updateAndGet(v -> v + curDragFactor);
            sumCenter.add(JomlUtil.dCenter(pos).mul(curDragFactor));
        });
        return sumCenter.div(totalWeight.get());
    }
}
