package com.lancas.vswap.ship.ballistics.collision;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.ship.ballistics.api.ICollisionTrigger;
import com.lancas.vswap.util.StrUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.HashSet;

public class CollisionDetectData {
    public ServerLevel level;
    public ServerShip projectileShip;
    public HashSet<Long> skipShipIds;
    public BlockPos detectorBpInShip;

    public CollisionDetectData(ServerLevel inLevel, ServerShip inProjectileShip, BlockPos inDetectorBpInShip, long propellantShipId, long breechShipId) {
        level = inLevel;
        projectileShip = inProjectileShip;
        detectorBpInShip = inDetectorBpInShip;

        skipShipIds = new HashSet<>();
        skipShipIds.add(inProjectileShip.getId());
        if (propellantShipId < 0) {
            EzDebug.warn("propellantShipId is: " + propellantShipId + ", ignore the id and do not add it to skipShipIds");
        } else {
            skipShipIds.add(propellantShipId);
        }

        if (breechShipId >= 0)
            skipShipIds.add(breechShipId);
    }

    private Vector3d movement = null;
    private AABBd worldBounds = null;
    public Vector3d getMovement(double predictTime) {
        return projectileShip.getVelocity().mul(predictTime, new Vector3d());
    }
    public AABBdc getWorldBounds() {
        if (worldBounds != null)
            return worldBounds;

        BlockState detectState = level.getBlockState(detectorBpInShip);
        if (!(detectState.getBlock() instanceof ICollisionTrigger trigger)) {
            EzDebug.fatal("block:" + StrUtil.getBlockName(detectState) + " should be a collision trigger");
            return new AABBd();
        }
        worldBounds = trigger.getWorldBounds(detectorBpInShip, detectState, projectileShip.getShipToWorld());
        return worldBounds;
    }
}