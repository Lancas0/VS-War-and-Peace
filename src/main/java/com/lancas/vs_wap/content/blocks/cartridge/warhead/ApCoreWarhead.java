package com.lancas.vs_wap.content.blocks.cartridge.warhead;

//import com.lancas.einherjar.content.blockentity.ApWarheadBE;
import com.lancas.vs_wap.content.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.primitives.AABBd;

import java.util.List;

public class ApCoreWarhead extends AbstractApWarhead {
    public ApCoreWarhead(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public double getRaycastStep() { return 0.3; }
    @Override
    public double getRaycastPredictTime() { return 0.06; }

    //private static ShapeBuilder boundBuilder = ShapeBuilder.ofPrism(Direction.UP, 12);
    @Override
    public AABBd getLocalBound(BlockState state) {
        //Direction dir = state.getValue(BlockDirectionAdder.FACING);
        //return JomlUtil.dBound(boundBuilder.getRotated(dir));
        return new AABBd(0.125, 0.125, 0.125, 0.875, 0.875, 0.875);
    }

    /*@Override
    public CollisionDetectMethod getCollisionMethod(BlockState state) {
        return CollisionDetectMethod.NearestFirstUnlimited(0.2, 0.2);
    }*/

    @Override
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            ApCoreWarhead.class,
            () -> List.of(
                new DefaultCartridgeAdder()
            )
        );
    }
}
/*
public class ApWarhead extends BlockPlus implements ICollisionTrigger, ITerminalEffector, IBE<ApWarheadBlockEntity>/.* ICollisionDetector*./ {
    public static final Random randomSrc = new Random();

    private static List<IBlockAdder> adders = List.of(
        new DefaultCartridgeBlockAdder()
        //, EinherjarBlockInfos.mass.getOrCreateExplicit(ApWarheadBlock.class, state -> 275.0),
        //EinherjarBlockInfos.hardness.getOrCreateImplicit(ApWarheadBlock.class, state -> 300E3),
        //EinherjarBlockInfos.ap_head_area.getOrCreateExplicit(ApWarheadBlock.class, state -> 0.7),
        //EinherjarBlockInfos.drag_factor.getOrCreateExplicit(ApWarheadBlock.class, state -> 0.9)
    );
    @Override
    public Iterable<IBlockAdder> getAdders() { return adders; }

    public ApWarhead(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public AABBd getLocalBound(BlockState state) { return JomlUtil.dBound(state.getShape(null, null, null)); }

    @Override
    public CollisionDetectMethod getCollisionMethod(BlockState state) { return CollisionDetectMethod.NearestFirstUnlimited(0.2, 0.5); }

    @Override
    public void appendTriggerInfos(ServerLevel level, BlockPos pos, BlockState state, BallisticsShipData controlData, BallisticStateData stateData, List<TriggerInfo> dest) {
        ServerShip projectileShip = controlData.getProjectileShip(level);
        if (projectileShip == null) {
            EzDebug.error("the projectile ship is null");
            return;
        }

        var hitInfos = this.getHitInfos(level, pos, state, controlData, stateData);
        for (var hitInfo : hitInfos) {
            dest.add(new TriggerInfo.CollisionTriggerInfo(
                projectileShip, pos, state, hitInfo
            ));
        }
    }

    @Override
    public void appendDescription(Set<String> descSet) { descSet.add("penetration"); }

    @Override
    public boolean canAccept(ServerLevel level, BlockPos pos, BlockState state, TriggerInfo info) {
        if (!info.triggerBlockPos.equals(pos)) {  //only accept self-made triggerInfo
            return false;
        }
        if (!(info instanceof TriggerInfo.CollisionTriggerInfo)) {
            EzDebug.error("the self? made trigger info is not CollisionTriggerInfo!");
            return false;
        }

        ApWarheadBlockEntity be = (ApWarheadBlockEntity)level.getBlockEntity(pos);
        if (be == null) {
            EzDebug.error("ap has no block entity!");
            return false;
        }
        if (be.isBouncing()) {
            EzDebug.highlight("ap is bouncing, reject other infos.");
            return false;
        }

        return !be.isBouncing() && info.triggerBlockPos.equals(pos);  //only accept self-made triggerInfo
    }

    @Override
    public void effect(ServerLevel level, BlockPos effectorBp, BlockState effectorState, TriggerInfo info) {
        if (!(info instanceof TriggerInfo.CollisionTriggerInfo collisionInfo)) {
            EzDebug.error("ap accept a non collision info"); return;
        }
        if (!(level.getBlockEntity(info.triggerBlockPos) instanceof ApWarheadBlockEntity apBe)) {
            EzDebug.error("ap has no block entity"); return;
        }
        if (apBe.isBouncing()) {
            EzDebug.warn("ap shouldn't accept trigger info after bouncing"); return;
        }

        EzDebug.light("sqDist:" + collisionInfo.hitInfo.sqDist);

        apBe.onAcceptTriggerInfo(collisionInfo);

        /.*if (!verifyEffectSafety(level, info))
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
        }*./
    }


    @Override
    public boolean shouldTerminateAfterEffecting(TriggerInfo info) {
        return false;
    }

    @Override
    public Class<ApWarheadBlockEntity> getBlockEntityClass() { return ApWarheadBlockEntity.class; }
    @Override
    public BlockEntityType<? extends ApWarheadBlockEntity> getBlockEntityType() { return EinherjarBlockEntites.AP_BE.get(); }
    @Override
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<S> p_153214_) {
        return ((level, blockPos, state, be) -> ((ApWarheadBlockEntity)be).tickUpdate());
    }

    /.*@Override
    public Class<ApWarheadBE> getBlockEntityClass() { return ApWarheadBE.class; }
    @Override
    public BlockEntityType<? extends ApWarheadBE> getBlockEntityType() { return EinherjarBlockEntites.AP_WARHEAD_BE.get(); }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        Level level, BlockState state, BlockEntityType<T> type
    ) {
        return (lvl, pos, blockState, blockEntity) -> ((ApWarheadBE)blockEntity).tick();
    }*/
    /*private static final ShapeBuilder UP_SHAPE = ShapeBuilder.createPrism(Direction.UP, 10);
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return UP_SHAPE.getRotated(state.getValue(FACING));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (level.isClientSide) return;
        //ApWarheadBE be = (ApWarheadBE)level.getBlockEntity(pos);

        //warhead handling is in BallisticsController now
        //be.setShips(ShipUtil.getShipAt((ServerLevel)level, pos));
    }*./
    /.*@Override
    public boolean shouldDetect(BlockState state) {
        return true;
    }

    @Override
    public AABBd getLocalBound(BlockState state) {
        AABB aabb = UP_SHAPE.getRotated(state.getValue(FACING)).bounds();
        return JomlUtil.d(aabb);
    }

    @Override
    public CollisionDetectMethod getDetectType(BlockState state) {
        return CollisionDetectMethod.NearestFirstUnlimited(0.15, 0.5);
    }

    @Override
    public void onCollision(Level level, BlockPos detectorBp, BlockState detectorState, BallisticsHitInfo hitInfo) {
        if (level.isClientSide) return;
        //EzDebug.Log(hitInfo.toString());

        BooleanProperty b01State = Block01.STATE;
        BlockState state = level.getBlockState(hitInfo.blockPos);
        if (state.getBlock() instanceof Block01 b01) {
            level.setBlockAndUpdate(hitInfo.blockPos, state.setValue(b01State, !state.getValue(b01State)));
        } else {
            level.setBlockAndUpdate(hitInfo.blockPos, EinherjarBlocks.BLOCK01.getDefaultState());
        }
    }*./
}
*/