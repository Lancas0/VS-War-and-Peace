package com.lancas.vs_wap.ship.ballistics.api;

import com.lancas.vs_wap.ship.ballistics.data.BallisticStateData;
import com.lancas.vs_wap.ship.ballistics.data.BallisticsShipData;
import com.lancas.vs_wap.ship.ballistics.helper.BallisticsUtil;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4dc;
import org.joml.primitives.AABBd;

public interface ICollisionTrigger extends ITrigger {
    public AABBd getLocalBound(BlockState state);
    public default AABBd getWorldBounds(BlockPos shipBp, BlockState state, Matrix4dc shipToWorld) {
        AABBd bounds = getLocalBound(state);  //local bounds
        bounds.translate(JomlUtil.dLowerCorner(shipBp));  //ship bounds
        BallisticsUtil.quickTransformAABB(shipToWorld, bounds);  //world bounds
        return bounds;
    }

    //public CollisionDetectMethod getCollisionMethod(BlockState state);


    @Override
    public default boolean shouldCheck(BallisticsShipData controlData, BallisticStateData stateData) {
        //EzDebug.Log("isOutArtillery:" + stateData.getIsOutArtillery() + ", velSqLen:" + stateData.getLastFrameVel().lengthSquared());
        if (!stateData.getIsOutArtillery()) return false;
        if (stateData.getLastFrameVel().lengthSquared() < 400) return false;  //vel must >= 20m/s todo config
        return true;
    }

    /*@Override
    public default boolean getTrigger(ServerLevel level, BallisticsControlData controlData, BallisticStateData stateData, BlockPos pos, BlockState state) {
        ServerShip projectileShip = controlData.getProjectileShip(level);

        if (!(state.getBlock() instanceof ICollisionTrigger trigger))
            return false;

        var hitInfos = trigger.getCollisionMethod(state).predictCollisions(level, projectileShip, controlData.propellantShipId, controlData.artilleryShipId, pos);
        return hitInfos != null && !hitInfos.isEmpty();
        /.*for (BallisticsHitInfo hitInfo : hitInfos) {
            detector.onCollision(level, detectorBp, detectorState, hitInfo);
        }*./
    }*/
    /*public default PriorityQueue<BallisticsHitInfo> getHitInfos(ServerLevel level, BlockPos pos, BlockState state, BallisticsShipData controlData, BallisticStateData stateData) {
        ServerShip projectileShip = controlData.getProjectileShip(level);

        if (!(state.getBlock() instanceof ICollisionTrigger trigger)) {
            EzDebug.warn("block is not collision trigger, return empty");
            return new PriorityQueue<>();
        }

        return trigger.getCollisionMethod(state).predictCollisions(level, projectileShip, controlData.propellantShipId, controlData.artilleryShipId, pos);
    }*/
    /*public default void appendTriggerInfos(ServerLevel level, BlockPos pos, BlockState state, BallisticsControlData controlData, BallisticStateData stateData, List<TriggerInfo> dest) {
        ServerShip projectileShip = controlData.getProjectileShip(level);

        if (!(state.getBlock() instanceof ICollisionTrigger trigger))
            return;

        var hitInfos = trigger.getCollisionMethod(state).predictCollisions(level, projectileShip, controlData.propellantShipId, controlData.artilleryShipId, pos);
        for (BallisticsHitInfo hitInfo : hitInfos) {
            dest.add(new Collision);
        }
        //return hitInfos != null && !hitInfos.isEmpty();
    }*/

}
