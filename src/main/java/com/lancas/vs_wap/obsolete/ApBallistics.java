package com.lancas.vs_wap.obsolete;

/*
public class ApBallistics {
    public enum BallisticsResult {
        BREAKING,
        PENETRATION,
        BOUNCE,
        STOP,
        PASS  //掠过 when it angle is >= 80 degree
    }

    //10 * 5000 * 10
    private static final double GENERAL_BLOCK_DURABLE_ENERGY = 5e5;
    private static final double BOUNCE_FACTOR = 0.8;  //vel will mul 0.8 after ricochet
    private static final double PENETRATION_FACTOR = 0.6;  //vel will mul 0.6 after penetrated

    private double criticalDeg = 50;
    private double passDeg = 80;
    private double breakingEnergy = 8e6;

    private double area = 1;
    private final BlockPos apShipPos;
    private final VoxelShape shape;

    private boolean broken = false;

    //private double currentMass;
    //private Vector3d currentVelocity;
    //private Vector3d currentOmega;
    //private double currentEnergyDensity;
    //private Vector3d currentApWorldPos;

    private final ServerShip projectile;
    private final ServerShip propellantShip;



    public ApBallistics(ServerShip inProjectile, ServerShip inPropellantShip, BlockPos inApShipPos, VoxelShape inShape) {
        projectile = inProjectile;
        propellantShip = inPropellantShip;
        apShipPos = inApShipPos;
        shape = inShape;
    }

    public void tick(ServerLevel level) {
        if (projectile == null) return;  //sometimes ship will destroy itself (explosion, or some other things...)
        if (isVelocityTooLow()) return;
        //if (broken) return;

        Vector3d apWorldPos = projectile.getShipToWorld().transformPosition(JomlUtil.dCenter(apShipPos));
        BlockState apState = level.getBlockState(apShipPos);

        boolean stopBallistics = false;
        for (BallisticsHitInfo hitInfo : BallisticsUtil.predictCollisions(level, projectile, apShipPos, apState, shape)) {
            //level.setBlock(hitInfo.pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
            BallisticsResult result =
                applyBallistics(level, apWorldPos, hitInfo);

            switch (result) {
                case BREAKING -> { broken = true; stopBallistics = true; }
                case STOP, BOUNCE -> stopBallistics = true;
                case PENETRATION, PASS -> {}

                default -> { EzDebug.Log("[Fatal] should never be called"); }
            }
            EzDebug.Log("ballistics result:" + result);

            if (stopBallistics) break;
            if (isVelocityTooLow()) return;
        }
    }
    private boolean isVelocityTooLow() {
        return projectile.getVelocity().lengthSquared() <= 25;
    }
    private double getPenetrationEnergy(BlockPos pos) {
        return GENERAL_BLOCK_DURABLE_ENERGY;  //todo
    }
    private BallisticsResult applyBallistics(ServerLevel level, Vector3d apWorldPos, BallisticsHitInfo hitInfo) {
        double mass = projectile.getInertiaData().getMass();
        Vector3dc velocity = projectile.getVelocity();
        double energyDensity = 0.5 * mass * velocity.lengthSquared() / area;

        double incidenceDeg = BallisticsMath.getIncidenceDeg(velocity, hitInfo.worldNormal);
        EzDebug.Log("hitInfo:" + hitInfo + ", vel:" + velocity + ", Angle:" + incidenceDeg);

        double penetrationEnergy = getPenetrationEnergy(hitInfo.blockPos);
        boolean canBreakTarget = (energyDensity >= penetrationEnergy);
        boolean willBreak = (energyDensity >= breakingEnergy);


        if (incidenceDeg >= passDeg) {
            postPass(level);
            return BallisticsResult.PASS;
        } else {
            if (canBreakTarget) {
                level.setBlock(hitInfo.blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
            }
            if (willBreak) {
                level.setBlock(apShipPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
                postStop(level);
                return BallisticsResult.BREAKING;
            }


            if (canBreakTarget && incidenceDeg >= criticalDeg) {
                postBounce(level, velocity, hitInfo.worldNormal);
                return BallisticsResult.BOUNCE;
            } else if (canBreakTarget) {
                postPenetration(level, hitInfo.blockPos, velocity);
                return BallisticsResult.PENETRATION;
            } else {
                postStop(level);
                return BallisticsResult.STOP;
            }
        }
    }
    private void postStop(ServerLevel level) {
        VSGameUtilsKt.getShipObjectWorld(level).teleportShip(projectile, new ShipTeleportDataImpl(
            projectile.getTransform().getPositionInWorld(),
            projectile.getTransform().getShipToWorldRotation(),
            new Vector3d(0, 0, 0),
            new Vector3d(0, 0, 0),
            VSGameUtilsKt.getDimensionId(level),
            projectile.getTransform().getShipToWorldScaling().x()
        ));
    }
    private void postPass(ServerLevel level) {
        //do nothing
    }
    private void postBounce(ServerLevel level, Vector3dc velocity, Vector3dc normal) {
        Vector3d bouncedVel = BallisticsMath.getBouncedVelocity(velocity, normal).mul(BOUNCE_FACTOR);
        Quaterniond bouncedRot = projectile.getTransform().getShipToWorldRotation().mul(
            new Quaterniond().rotateTo(velocity, bouncedVel),
            new Quaterniond()
        );

        VSGameUtilsKt.getShipObjectWorld(level).teleportShip(projectile, new ShipTeleportDataImpl(
            projectile.getTransform().getPositionInWorld(),
            bouncedRot,
            bouncedVel,
            new Vector3d(0, 0, 0),
            VSGameUtilsKt.getDimensionId(level),
            projectile.getTransform().getShipToWorldScaling().x()
        ));
    }
    /.*private void applyBreak(ServerLevel level) {
        level.setBlock(apShipPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);

        VSGameUtilsKt.getShipObjectWorld(level).teleportShip(projectile, new ShipTeleportDataImpl(
            projectile.getTransform().getPositionInWorld(),
            projectile.getTransform().getShipToWorldRotation(),
            currentVelocity.mul(penetrationFactor, new Vector3d()),
            new Vector3d(0, 0, 0),
            VSGameUtilsKt.getDimensionId(level),
            projectile.getTransform().getShipToWorldScaling().x()
        ));
    }*./
    private void postPenetration(ServerLevel level, BlockPos targetPos, Vector3dc velocity) {
        VSGameUtilsKt.getShipObjectWorld(level).teleportShip(projectile, new ShipTeleportDataImpl(
            projectile.getTransform().getPositionInWorld(),
            projectile.getTransform().getShipToWorldRotation(),
            velocity.mul(PENETRATION_FACTOR, new Vector3d()),
            new Vector3d(0, 0, 0),
            VSGameUtilsKt.getDimensionId(level),
            projectile.getTransform().getShipToWorldScaling().x()
        ));
    }




}
*/