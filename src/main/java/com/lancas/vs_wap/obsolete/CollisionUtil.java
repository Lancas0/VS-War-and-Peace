package com.lancas.vs_wap.obsolete;

/*
public class CollisionUtil {
    private static final double PREDICT_TIME = 0.2;

    /.public Iterable<ClipContext> getPredictCollisionClips(ServerLevel level, ServerShip ship, BlockPos warheadShipPos, VoxelShape shape) {
        Vector3d predictMovement = ship.getVelocity().mul(PREDICT_TIME, new Vector3d());


    }*./
    public static Iterable<BlockPos> getCoveredWorldBlocks(Matrix4dc shipToWorld, BlockPos warheadShipPos, AABBdc localDirectedBounds) {
        AABBd shipBounds = localDirectedBounds.translate(JomlUtil.dLowerCorner(warheadShipPos), new AABBd());
        AABBd worldBounds = BallisticsUtil.quickTransformAABB(shipToWorld, shipBounds, new AABBd());

        return BlockPos.betweenClosed(
            (int)Math.floor(worldBounds.minX),
            (int)Math.floor(worldBounds.minY),
            (int)Math.floor(worldBounds.minZ),
            (int)Math.floor(worldBounds.maxX),
            (int)Math.floor(worldBounds.maxX),
            (int)Math.floor(worldBounds.maxX)
        );
    }
}
*/