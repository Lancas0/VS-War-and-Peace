package com.lancas.vswap.sandbox.ballistics.trigger;

import com.lancas.vswap.ship.ballistics.data.BallisticsHitInfo;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.UUID;

public class SandBoxTriggerInfo {
    public static class ActivateTriggerInfo extends SandBoxTriggerInfo {
        public Vector3d activatePos;

        public ActivateTriggerInfo(UUID inProjectileUuid, Vector3ic inSenderLocalPos, BlockState inSenderState, Vector3dc inTargetPos) {
            super(inProjectileUuid, inSenderLocalPos, inSenderState);
            activatePos = inTargetPos.get(new Vector3d());
        }

        @Override
        public String toString() {
            return "ActivateTriggerInfo{" +
                "triggerBlockPos=" + senderLocalPos +
                ", triggerBlockState=" + senderState +
                ", targetPos=" + activatePos +
                '}';
        }
    }
    public static class CollisionTriggerInfo extends SandBoxTriggerInfo {
        public BallisticsHitInfo hitInfo;

        public CollisionTriggerInfo(UUID inProjectileUuid, Vector3ic inSenderLocalPos, BlockState inSenderState, BallisticsHitInfo inHitInfo) {
            super(inProjectileUuid, inSenderLocalPos, inSenderState);
            hitInfo = inHitInfo;
        }

        @Override
        public String toString() {
            return "CollisionTriggerInfo{" +
                "hitInfo=" + hitInfo +
                ", triggerBlockPos=" + senderLocalPos +
                ", triggerBlockState=" + senderState +
                '}';
        }
    }

    public UUID projectileUuid;
    public Vector3ic senderLocalPos;
    public BlockState senderState;  //sender state is the state just prev triggered. any change after trigger will not included.

    //public TriggerType triggerType;

    public SandBoxTriggerInfo(UUID inProjectileUuid, Vector3ic inSenderLocalPos, BlockState inSenderState/*, TriggerType inTriggerType*/) {
        projectileUuid = inProjectileUuid;
        senderLocalPos = new Vector3i(inSenderLocalPos);
        senderState = inSenderState;
        //triggerType = inTriggerType;
    }
}
