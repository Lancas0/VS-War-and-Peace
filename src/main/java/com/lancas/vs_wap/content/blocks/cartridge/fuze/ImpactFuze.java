package com.lancas.vs_wap.content.blocks.cartridge.fuze;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vs_wap.sandbox.ballistics.trigger.SandBoxTriggerInfo;
import com.lancas.vs_wap.ship.ballistics.collision.traverse.BlockTraverser;
import com.lancas.vs_wap.ship.ballistics.data.BallisticsHitInfo;
import com.lancas.vs_wap.ship.ballistics.helper.BallisticsUtil;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.PropertyAdder;
import com.lancas.vs_wap.content.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vs_wap.ship.ballistics.data.BallisticStateData;
import com.lancas.vs_wap.ship.ballistics.data.BallisticsShipData;
import com.lancas.vs_wap.ship.ballistics.api.ICollisionTrigger;
import com.lancas.vs_wap.ship.ballistics.api.TriggerInfo;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.HashSet;
import java.util.List;


public class ImpactFuze extends BlockPlus implements ICollisionTrigger, ISandBoxBallisticBlock {
    public static BooleanProperty TRIGGERED = BooleanProperty.create("triggered");

    private static final List<IBlockAdder> providers = List.of(
        new DefaultCartridgeAdder(),
        new PropertyAdder<>(TRIGGERED, false)
        //,
        //EinherjarBlockInfos.mass.getOrCreateExplicit(ImpactFuze.class, state -> 5.0)
    );
    @Override
    public Iterable<IBlockAdder> getAdders() { return providers; }
    public ImpactFuze(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public void onPlace(BlockState state, Level p_60567_, BlockPos p_60568_, BlockState p_60569_, boolean p_60570_) {
        super.onPlace(state, p_60567_, p_60568_, p_60569_, p_60570_);
    }

    @Override
    public AABBd getLocalBound(BlockState state) { return ((DefaultCartridgeAdder)providers.get(0)).getLocalBound(state); }
    /*@Override
    public CollisionDetectMethod getCollisionMethod(BlockState state) {
        return CollisionDetectMethod.AnySingleCollision(0.1f, 0.5);
    }*/

    @Override
    public void appendTriggerInfos(ServerLevel level, BlockPos pos, BlockState state, BallisticsShipData shipData, BallisticStateData stateData, List<TriggerInfo> dest) {
        //EzDebug.Log("triggered:" + isTriggered(state));
        if (isTriggered(state)) return;

        ServerShip projectile = shipData.getProjectileShip(level);
        HashSet<Long> skipShips = new HashSet<>();
        skipShips.add(projectile.getId());
        skipShips.add(shipData.propellantShipId);
        if (shipData.artilleryShipId >= 0)
            skipShips.add(shipData.artilleryShipId);


        Vector3dc velocity = projectile.getVelocity();
        Vector3d movement = velocity.mul(0.06, new Vector3d());  //raycast predict time is 0.06

        var clips = BallisticsUtil.raycastPlaneForBlocks(movement, getWorldBounds(pos, state, projectile.getShipToWorld()), 0.5);  //raycast step is 0.5
        BallisticsHitInfo hitInfo = null;
        for (ClipContext clipCtx : clips) {
            hitInfo = BlockTraverser.Ballistics.traverseFirstHitIncludeShip(level, clipCtx, skipShips);
            if (hitInfo != null) break;
        }

        if (hitInfo == null) return; //append no trigger info
        dest.add(new TriggerInfo.ActivateTriggerInfo(projectile, pos, state, hitInfo.worldHitPos));
        setTriggered(state, true);
    }

    private void setTriggered(BlockState state, boolean val) { state.setValue(TRIGGERED, val); }
    private boolean isTriggered(BlockState state) { return state.getValue(TRIGGERED); }


    @Override
    public void appendTriggerInfos(ServerLevel level, Vector3i localPos, BlockState state, SandBoxServerShip ship, List<SandBoxTriggerInfo> dest) {
        if (isTriggered(state)) return;

        /*Vector3dc velocity = ship.getRigidbody().getExposedData().getVelocity();
        Vector3d movement = velocity.mul(0.06, new Vector3d());  //raycast predict time is 0.06

        Vector3dc worldPos = ship.getTransform().localToWorldPos(localPos, new Vector3d());
        AABBdc worldBounds = JomlUtil.dCenterExtended(worldPos, 1, 1, 1);

        var clips = BallisticsUtil.raycastPlaneForBlocks(movement, worldBounds, 0.5);  //raycast step is 0.5
        BallisticsHitInfo hitInfo = null;
        //EzDebug.log("testing clip count:" + clips.size());
        for (ClipContext clipCtx : clips) {
            hitInfo = BlockTraverser.Ballistics.traverseFirstHitIncludeShip(level, clipCtx, null);
            if (hitInfo != null) break;
        }

        //EzDebug.log("hitInfo is null ?" + (hitInfo == null));
        if (hitInfo == null) return; //append no trigger info
        var activateInfo = new SandBoxTriggerInfo.ActivateTriggerInfo(ship.getUuid(), localPos, state, hitInfo.worldHitPos);
        dest.add(activateInfo);
        setTriggered(state, true);
        ship.setBlock(localPos, state.setValue(TRIGGERED, true));*/

        Vector3dc worldPos = ship.getTransform().localToWorldPos(localPos, new Vector3d());
        BlockPos blockPos = JomlUtil.bpContaining(worldPos);
        BlockState findState = level.getBlockState(blockPos);

        if (!findState.isAir()) {
            var activateInfo = new SandBoxTriggerInfo.ActivateTriggerInfo(ship.getUuid(), localPos, state, worldPos);
            dest.add(activateInfo);

            ship.setBlock(localPos, state.setValue(TRIGGERED, true));
        }

        /*EzDebug.log(
            "set state:" + ship.getCluster().getBlock(localPos).getValue(TRIGGERED) +
                "\n get info:" + activateInfo
        );*/
    }

    /*@Override
    public double getMass(BlockState state) {
        return 50;
    }
     */


    //private final ShapeBuilder upShape  = new ShapeBuilder(Shapes.create(2, 0, 2, 14, 16, 14));
    /*@Override
    public ShapeBuilder getUpShape() { return upShape; }*/

    /*@Override
    public Class<ImpactFuseBE> getBlockEntityClass() { return ImpactFuseBE.class; }
    @Override
    public BlockEntityType<? extends ImpactFuseBE> getBlockEntityType() { return EinherjarBlockEntites.IMPACT_FUSE_BE.get(); }*/
    /*private static final ShapeBuilder UP_SHAPE = ShapeBuilder.createBox(4, 0, 4, 12, 12, 12);
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return UP_SHAPE.getRotated(state.getValue(FACING));
    }*/

    /*@Override
    public CollisionDetectMethod getDetectType(BlockState state) {
        return CollisionDetectMethod.AnySingleCollision(0.06, 0.5);
    }

    @Override
    public void onCollision(Level level, BlockPos detectorBp, BlockState detectorState, BallisticsHitInfo hitInfo) {
        if (level.isClientSide) return;

        if (hitInfo == null) {
            EzDebug.warn("hitInfo should not be null. anyway donothing in this method.");
            return;
        }

        EzDebug.Log("hitBlock:" + StringUtil.getBlockName(level.getBlockState(hitInfo.blockPos)) + " at " + StringUtil.getBlockPos(hitInfo.blockPos));

        level.setBlockAndUpdate(detectorBp, detectorState.setValue(POWER, 15));
        level.updateNeighborsAt(detectorBp, detectorState.getBlock());
        EzDebug.Log("impact fuse set power 15");
    }*/

}

/*
public class ImpactFuzeBlock extends FeatureAccepterBlock implements ICollisionDetector {
    private static final List<IBlockFeatureProvider> providers = List.of(
        new DefaultCartridgeFeatureProvider(),
        new RedstonePowerProvider()
    );
    @Override
    public Iterable<IBlockFeatureProvider> getProviders() {
        return providers;
    }


    public ImpactFuzeBlock(Properties p_49795_) {
        super(p_49795_);
    }


    @Override
    public boolean shouldDetect(BlockState state) {
        int power = state.getValue(RedstonePowerProvider.POWER);
        return power <= 0;
    }
    @Override
    public AABBd getLocalBound(BlockState state) {
        Direction dir = state.getValue(DirectionProvider.FACING);
        //AABB aabb = getUpShape().getRotated(dir).bounds();
        AABB aabb = new ShapeBuilder(((DirectionProvider)providers.get(0)).upShape).getRotated(dir).bounds();  //todo shape setting
        return JomlUtil.d(aabb);
        //return ((DefaultCartridgeFeatureProvider)providers.get(0)).getLocalBound(state);
    }
    @Override
    public CollisionDetectType getDetectType(BlockState state) { return CollisionDetectType.ANY_SINGLE_COLLISION; }

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
    }
}*/