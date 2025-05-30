package com.lancas.vswap.ship.ballistics.collision;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.ship.ballistics.data.BallisticsHitInfo;
import com.lancas.vswap.ship.ballistics.helper.BallisticsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import org.joml.primitives.AABBd;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Function;

public class CollisionDetectMethod {
    public static CollisionDetectMethod AnySingleCollision(double predictTime, double raycastStep) {
        return new CollisionDetectMethod(
            //predictTime,
            //planeRaycastPlaneInWorld,
            data -> {
                PriorityQueue<BallisticsHitInfo> hitInfos = new PriorityQueue<>(BallisticsHitInfo.nearFirst());
                appendPredictWorldAndShipsCollisions(predictTime, raycastStep, data, 1, hitInfos);
                return hitInfos;
            }
        );
    }
    public static CollisionDetectMethod NearestFirstUnlimited(double predictTime, double raycastStep) {
        return new CollisionDetectMethod(
            //predictTime,
            //planeRaycastPlaneInWorld,
            data -> {
                PriorityQueue<BallisticsHitInfo> hitInfos = new PriorityQueue<>(BallisticsHitInfo.nearFirst());
                appendPredictWorldAndShipsCollisions(predictTime, raycastStep, data, Integer.MAX_VALUE, hitInfos);
                return hitInfos;
            }
        );
    }

    //private final double predictTime;
    //private final double raycastStep;
    private Function<CollisionDetectData, PriorityQueue<BallisticsHitInfo>> detectFun;
    CollisionDetectMethod(/*double inPredictTime, double inRaycastStep, */Function<CollisionDetectData, PriorityQueue<BallisticsHitInfo>> inDetectFun) {
        //predictTime = inPredictTime;
        //raycastStep = inRaycastStep;

        if (inDetectFun == null) {
            EzDebug.fatal("inDetectFun is null");
            detectFun = data -> new PriorityQueue<>();
            return;
        }

        detectFun = inDetectFun;
    }

    public PriorityQueue<BallisticsHitInfo> predictCollisions(ServerLevel level, ServerShip projectileShip, long propellantShipId, long breechShipId, BlockPos shipBp) {
        CollisionDetectData data = new CollisionDetectData(level, projectileShip, shipBp, propellantShipId, breechShipId);
        return detectFun.apply(data);
    }

    private static PriorityQueue<BallisticsHitInfo> appendPredictWorldCollisions(double predictTime, double raycastStep, CollisionDetectData data, int maxCollisionCnt, PriorityQueue<BallisticsHitInfo> dest) {
        if (maxCollisionCnt <= 0) {
            EzDebug.warn("It is no sense to detect " + maxCollisionCnt + " collisions. Anyway it return the original queue");
            return dest;
        }

        int collisionCnt = 0;
        //do world
        for (var clip : BallisticsUtil.raycastPlaneForBlocks(data.getMovement(predictTime), data.getWorldBounds(), raycastStep)) {
            BallisticsHitInfo info = BallisticsUtil.ballisticsClipInWorld(data.level, clip/*, data.projectileShip.getVelocity()*/);
            if (info == null) continue;

            dest.add(info);
            collisionCnt++;

            if (collisionCnt >= maxCollisionCnt) {
                return dest;
            }
        }

        return dest;
    }
    private static PriorityQueue<BallisticsHitInfo> appendPredictShipCollisions(double predictTime, double raycastStep, CollisionDetectData data, ServerShip shipCollided, int maxCollisionCnt, PriorityQueue<BallisticsHitInfo> dest) {
        if (maxCollisionCnt <= 0) {
            EzDebug.warn("It is no sense to detect " + maxCollisionCnt + " collisions. Anyway it return the origin queue.");
            return dest;
        }

        double shipScale = shipCollided.getTransform().getShipToWorldScaling().x();  //todo 3d scale?
        int collisionCnt = 0;

        for (var clip : BallisticsUtil.raycastPlaneForBlocks(data.getMovement(predictTime), data.getWorldBounds(), raycastStep * shipScale)) {
            BallisticsHitInfo info = BallisticsUtil.ballisticsClipInShip(data.level, shipCollided, clip, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE/*, data.projectileShip.getVelocity()*/);
            if (info == null) continue;

            dest.add(info);
            collisionCnt++;

            if (collisionCnt >= maxCollisionCnt) {
                return dest;
            }
        }
        return dest;
    }
    private static Iterable<ServerShip> getShipsAround(double predictTime, CollisionDetectData data) {
        AABBd worldPredictBounds = BallisticsUtil.extendAlong(data.getWorldBounds(), data.getMovement(predictTime), new AABBd());
        List<ServerShip> ships = new ArrayList<>();
        for (Ship ship : VSGameUtilsKt.getShipsIntersecting(data.level, worldPredictBounds)) {
            if (data.skipShipIds.contains(ship.getId())) continue;
            if (!(ship instanceof ServerShip sShip)) {
                EzDebug.warn("level is ServeLevel but get a ship that is not serverShip. skip it.");
                continue;
            }

            ships.add(sShip);
        }
        return ships;
    }
    private static PriorityQueue<BallisticsHitInfo> appendPredictShipsAroundCollisions(double predictTime, double raycastStep, CollisionDetectData data, int maxCollisionCnt, PriorityQueue<BallisticsHitInfo> dest) {
        if (maxCollisionCnt <= 0) {
            EzDebug.warn("It is no sense to detect " + maxCollisionCnt + " collisions. Anyway it return the origin queue.");
            return dest;
        }

        int remainCollisionCnt = maxCollisionCnt;
        for (ServerShip shipAround : getShipsAround(predictTime, data)) {
            appendPredictShipCollisions(predictTime, raycastStep, data, shipAround, remainCollisionCnt, dest);
            remainCollisionCnt = maxCollisionCnt - dest.size();

            if (remainCollisionCnt <= 0) {
                if (remainCollisionCnt < 0) EzDebug.warn("remainCollisionCnt:" + remainCollisionCnt + ", that means get more collisions than needed.");
                return dest;
            }
        }
        return dest;
    }
    private static PriorityQueue<BallisticsHitInfo> appendPredictWorldAndShipsCollisions(double predictTime, double raycastStep, CollisionDetectData data, int maxCollisionCnt, PriorityQueue<BallisticsHitInfo> dest) {
        if (maxCollisionCnt <= 0) {
            EzDebug.warn("It is no sense to detect " + maxCollisionCnt + " collisions. Anyway it return the original queue");
            return dest;
        }

        int remainCollisionCnt = maxCollisionCnt;
        appendPredictWorldCollisions(predictTime, raycastStep, data, remainCollisionCnt, dest);
        remainCollisionCnt = maxCollisionCnt - dest.size();

        if (remainCollisionCnt <= 0) {
            if (remainCollisionCnt < 0) EzDebug.warn("remainCollisionCnt:" + remainCollisionCnt + ", that means get more collisions than needed.");
            return dest;
        }

        appendPredictShipsAroundCollisions(predictTime, raycastStep, data, remainCollisionCnt, dest);
        remainCollisionCnt = maxCollisionCnt - dest.size();
        if (remainCollisionCnt < 0)
            EzDebug.warn("remainCollisionCnt:" + remainCollisionCnt + ", that means get more collisions than needed.");

        return dest;
    }
        /*private PriorityQueue<BallisticsHitInfo> predictCollision(ServerLevel level, ServerShip projectileShip, HashSet<Long> skipShipIds, BlockPos shipBp) {
            Vector3dc vel = projectileShip.getVelocity();
            if (vel.lengthSquared() <= minSqSpeed) return null;
            Vector3d movement = vel.mul(PREDICT_TIME, new Vector3d());

            if (!skipShipIds.contains(projectileShip.getId())) {
                EzDebug.warn("skipShipIds have no projectileShipId:" + projectileShip.getId() + ", auto added");
                skipShipIds.add(projectileShip.getId());
            }

            //todo fatal: 有时会会变成debug方块，也许是与之前测试过后落到地面的弹头相互碰撞导致的？
            Matrix4dc projShipToWorld = projectileShip.getShipToWorld();
            PriorityQueue<BallisticsHitInfo> hitsNearFirst = new PriorityQueue<>(BallisticsHitInfo.nearFirst());


            BlockState state = level.getBlockState(shipBp);
            ICollisionDetector collisionDetector = (ICollisionDetector)state.getBlock();
            AABBd worldBounds = collisionDetector.getWorldBounds(shipBp, state, projShipToWorld);

            //only to find out the ships
            AABBd worldPredictBounds = BallisticsUtil.extendAlong(worldBounds, movement, new AABBd());
            //Hashtable<Double, Iterable<ClipContext>> scale2Clips = new Hashtable<>();
            //scale2Clips.put(1.0, BallisticsUtil.raycastPlaneForBlocks(movement, worldBounds, 0.5));

            //do world
            /.*for (var clip : /.*scale2Clips.get(1.0)*./BallisticsUtil.raycastPlaneForBlocks(movement, worldBounds, PLANE_RAYCAST_STEP_WORLD)) {
                BallisticsHitInfo info = BallisticsUtil.ballisticsClipInWorld(level, clip);
                if (info != null) {
                    hitsNearFirst.add(info);
                }
            }

            //do ships
            for (Ship ship : VSGameUtilsKt.getShipsIntersecting(level, worldPredictBounds)) {
                if (skipShipIds.contains(ship.getId())) continue;

                double shipScale = ship.getTransform().getShipToWorldScaling().x();  //todo 3d scale?

                for (var clip : BallisticsUtil.raycastPlaneForBlocks(movement, worldBounds, PLANE_RAYCAST_STEP_WORLD * shipScale)) {
                    BallisticsHitInfo info = BallisticsUtil.ballisticsClipInShip(level, ship, clip, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE);
                    if (info != null) {
                        hitsNearFirst.add(info);
                    }
                }
            }*./

            //EzDebug.Log("a collision detection cost time:" + sw.getTime(TimeUnit.MICROSECONDS) / 1000.0 + " ms");
            //only detect one now
            return hitsNearFirst;
        }*/
}
