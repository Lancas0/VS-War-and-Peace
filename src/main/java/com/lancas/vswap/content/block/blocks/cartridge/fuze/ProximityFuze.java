package com.lancas.vswap.content.block.blocks.cartridge.fuze;

/*
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.RedstonePowerAdder;
import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;

import java.util.List;

public class ProximityFuze extends BlockPlus /.*implements ICollisionDetector*./ {
    private static final List<IBlockAdder> providers = List.of(
        new DefaultCartridgeAdder(true),
        new RedstonePowerAdder()
    );

    @Override
    public List<IBlockAdder> getAdders() {
        return providers;
    }

    public ProximityFuze(Properties p_49795_) {
        super(p_49795_);
    }



    /.*@Override
    public boolean shouldDetect(BlockState state) {
        int redstone = state.getValue(RedstonePowerProvider.POWER);
        return redstone <= 0;
    }
    @Override
    public AABBd getLocalBound(BlockState state) { return null; }
    @Override
    public AABBd getWorldBounds(BlockPos shipBp, BlockState state, Matrix4dc shipToWorld) {
        Vector3d worldPos = shipToWorld.transformPosition(JomlUtil.dCenter(shipBp));
        return JomlUtil.centerExtended(worldPos, 2.5);
    }


    @Override
    public CollisionDetectMethod getDetectType(BlockState state) { return CollisionDetectMethod.AnySingleCollision(0.15, 0.5); }
    @Override
    public void onCollision(Level level, BlockPos detectorBp, BlockState detectorState, BallisticsHitInfo hitInfo) {
        if (level.isClientSide) return;

        if (hitInfo == null) {
            EzDebug.warn("hitInfo should not be null. anyway donothing in this method.");
            return;
        }

        EzDebug.Log("hitBlock:" + StringUtil.getBlockName(level.getBlockState(hitInfo.blockPos)) + " at " + StringUtil.getBlockPos(hitInfo.blockPos));

        level.setBlockAndUpdate(detectorBp, detectorState.setValue(RedstonePowerProvider.POWER, 15));
        level.updateNeighborsAt(detectorBp, detectorState.getBlock());
    }*./
}
*/