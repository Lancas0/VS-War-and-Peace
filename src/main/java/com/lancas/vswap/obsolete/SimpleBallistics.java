package com.lancas.vswap.obsolete;

/*
public class SimpleBallistics {

    private final ServerShip projectile;
    private final BlockPos shipPos;
    private final VoxelShape shape;

    public SimpleBallistics(ServerShip inProjectile, BlockPos inShipPos, VoxelShape inShape) {
        projectile = inProjectile;
        shipPos = inShipPos;
        shape = inShape;
        EzDebug.Log("shape:" + shape);
    }

    private Vector3d getCurrentWorldPos() {
        return projectile.getShipToWorld().transformPosition(JomlUtil.dCenter(shipPos));
    }

    /.*public void tick(ServerLevel level) {
        if (projectile == null) return;
        //suppose block pos is not null

        for (BallisticsHitInfo hitInfo : BallisticsUtil.predictCollisions(level, projectile, shipPos)) {

        }
    }*./

    public Iterable<BallisticsHitInfo> predictCollision(ServerLevel level) {
        if (projectile == null) return null;
        BlockState state = level.getBlockState(shipPos);
        return null; //todo
        //return BallisticsUtil.predictCollisions(level, projectile, shipPos, state, shape);
    }

}
*/