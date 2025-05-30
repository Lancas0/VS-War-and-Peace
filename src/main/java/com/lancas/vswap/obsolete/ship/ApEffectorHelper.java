package com.lancas.vswap.obsolete.ship;

/*
public class ApEffectorHelper {


    private Consumer<BallisticsMath.TerminalContext, BlockPos>

    public void SelfTriggerApEffectorHelper(
        ServerLevel level,
        TriggerInfo.CollisionTriggerInfo triggerInfo,

        Consumer<BallisticsMath.TerminalContext> onBounce,
        Consumer<BallisticsMath.TerminalContext> onPenetrate,
        Consumer<BallisticsMath.TerminalContext> onStop  //temp name

    ) {
        if (!verifyEffectSafety(level, info))
            return;

        TriggerInfo.CollisionTriggerInfo collisionInfo = (TriggerInfo.CollisionTriggerInfo)info;

        BallisticsMath.TerminalContext terminalCtx = BallisticsMath.TerminalContext.selfCollisionTrigger(level, collisionInfo);
        boolean isPass = terminalCtx.isPass();

        if (isPass) {
            return;
        }

        ServerShip projectile = info.projectileShip;
        LoadedServerShip hitShip = ShipUtil.getLoadedServerByID(level, collisionInfo.hitInfo.hitShipId);
        BallisticsHitInfo hitInfo = collisionInfo.hitInfo;

        double mass = projectile.getInertiaData().getMass();
        Vector3d prevVel = projectile.getVelocity().get(new Vector3d());

        boolean penetrate = terminalCtx.canPenetrate();
        boolean bounce = terminalCtx.isBounce(randomSrc);
        if (penetrate) {
            //todo maybe add a destroy context
            level.setBlockAndUpdate(hitInfo.hitBlockPos, Blocks.AIR.defaultBlockState());  //todo armour post effect
        }

        Vector3d postVel = bounce ? terminalCtx.getBouncedVelocity(penetrate) : terminalCtx.getPenetratedVel();
        ShipUtil.teleport(level, projectile,
            TeleportDataBuilder.copy(level, projectile)
                .withVel(postVel)
        );
        EzDebug.highlight(
            "incDeg:" + Math.toDegrees(terminalCtx.incidenceRad) +
                ", criticalDeg:" + Math.toDegrees(terminalCtx.criticalRad) +
                ", bounce? :" + bounce +
                ", prevVel:" + StrUtil.F2(prevVel) +
                ", postVel:" + StrUtil.F2(postVel) +
                ", prevE(kJ):" + 0.5 * mass * prevVel.lengthSquared() / 1000 +
                ", postE(kJ):" + 0.5 * mass * postVel.lengthSquared() / 1000
        );

        if (hitShip != null) {
            ForcesInducer.apply(hitShip, terminalCtx.getImpactForce());
        }
    }


}
*/