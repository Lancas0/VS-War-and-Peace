package com.lancas.vs_wap.obsolete.be;

/*
public class ImpactFuseBE extends BlockEntity {
    //todo configurable min speed
    public ImpactFuseBE(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    private SimpleBallistics ballistics;
    private ServerShip ship;

    private int redstone = 0;
    public int getRedstone() { EzDebug.Log("getredstone:" + redstone); return redstone; }


    public void tick() {
        if (redstone > 0) return;  //already activated

        if (level == null || level.isClientSide) return;
        if (ship == null) {
            ship = ShipUtil.getShipAt((ServerLevel)level, worldPosition);
            if (ship == null) return;
        }
        if (ballistics == null) {
            ballistics = new SimpleBallistics(ship, worldPosition, getBlockState().getShape(level, worldPosition));
        }

        if (ship.getVelocity().lengthSquared() < 64) return;  //do not check when speed is low (< 10m/s

        for (var hitInfo : ballistics.predictCollision((ServerLevel)level)) {
            //any collision will trigger the fuse
            EzDebug.Log("set redstone to 15");
            redstone = 15;
            setChanged();
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
            return;
        }
    }
}
*/