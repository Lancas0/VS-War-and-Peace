package com.lancas.vswap.ship.ballistics.api;

import com.lancas.vswap.ship.ballistics.data.BallisticsHitInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;

public abstract class TriggerInfo {
    public static class ActivateTriggerInfo extends TriggerInfo {
        public Vector3d targetPos;

        public ActivateTriggerInfo(ServerShip inProjectileShip, BlockPos inTriggerBlockPos, BlockState inTriggerBlockState, Vector3dc inTargetPos) {
            super(inProjectileShip, inTriggerBlockPos, inTriggerBlockState);
            targetPos = inTargetPos.get(new Vector3d());
        }

        @Override
        public String toString() {
            return "ActivateTriggerInfo{" +
                "triggerBlockPos=" + triggerBlockPos +
                ", triggerBlockState=" + triggerBlockState +
                ", targetPos=" + targetPos +
                '}';
        }
    }
    public static class CollisionTriggerInfo extends TriggerInfo {
        public BallisticsHitInfo hitInfo;

        public CollisionTriggerInfo(ServerShip inProjectileShip, BlockPos inTriggerBlockPos, BlockState inTriggerBlockState, BallisticsHitInfo inHitInfo) {
            super(inProjectileShip, inTriggerBlockPos, inTriggerBlockState);
            hitInfo = inHitInfo;
        }

        @Override
        public String toString() {
            return "CollisionTriggerInfo{" +
                "hitInfo=" + hitInfo +
                ", triggerBlockPos=" + triggerBlockPos +
                ", triggerBlockState=" + triggerBlockState +
                '}';
        }
    }

    public ServerShip projectileShip;
    public BlockPos triggerBlockPos;
    public BlockState triggerBlockState;

    //public TriggerType triggerType;

    public TriggerInfo(ServerShip inProjectileShip, BlockPos inTriggerBlockPos, BlockState inTriggerBlockState/*, TriggerType inTriggerType*/) {
        projectileShip = inProjectileShip;
        triggerBlockPos = inTriggerBlockPos;
        triggerBlockState = inTriggerBlockState;
        //triggerType = inTriggerType;
    }
}
