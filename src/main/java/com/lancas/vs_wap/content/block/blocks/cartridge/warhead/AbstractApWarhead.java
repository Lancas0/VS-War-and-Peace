package com.lancas.vs_wap.content.block.blocks.cartridge.warhead;

import com.lancas.vs_wap.content.WapBlockEntites;
import com.lancas.vs_wap.content.block.blockentity.ApWarheadBlockEntity;
import com.lancas.vs_wap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vs_wap.content.info.block.WapBlockInfos;
import com.lancas.vs_wap.content.saved.IBlockRecord;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vs_wap.sandbox.ballistics.behaviour.BallisticBehaviour;
import com.lancas.vs_wap.sandbox.ballistics.trigger.SandBoxTriggerInfo;
import com.lancas.vs_wap.ship.ballistics.api.ICollisionTrigger;
import com.lancas.vs_wap.ship.ballistics.api.ITerminalEffector;
import com.lancas.vs_wap.ship.ballistics.api.TriggerInfo;
import com.lancas.vs_wap.ship.ballistics.collision.traverse.BlockTraverser;
import com.lancas.vs_wap.ship.ballistics.data.BallisticStateData;
import com.lancas.vs_wap.ship.ballistics.data.BallisticsHitInfo;
import com.lancas.vs_wap.ship.ballistics.data.BallisticsShipData;
import com.lancas.vs_wap.ship.ballistics.helper.BallisticsMath;
import com.lancas.vs_wap.ship.ballistics.helper.BallisticsUtil;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.StrUtil;
import com.lancas.vs_wap.util.WorldUtil;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.*;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.lang.Math;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public abstract class AbstractApWarhead extends BlockPlus implements ICollisionTrigger, ITerminalEffector, IBE<ApWarheadBlockEntity>, ISandBoxBallisticBlock {
    /*private static class ApWarheadRecord implements IBlockRecord {

        private ApWarheadRecord() {}
        public ApWarheadRecord(BlockPos inBp) { bp = new SavedBlockPos(inBp); }
    }*/
    @Override
    public Iterable<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            ApCoreWarhead.class,
            () -> List.of(
                new DefaultCartridgeAdder()//,
                //new RefreshBlockRecordAdder((bp, state) -> new ApWarheadRecord(bp))
            )
        );
    }
    public AbstractApWarhead(Properties p_49795_) {
        super(p_49795_);
    }

    public abstract double getRaycastStep();
    public abstract double getRaycastPredictTime();
    //@Override
    //public CollisionDetectMethod getCollisionMethod(BlockState state) { return CollisionDetectMethod.NearestFirstUnlimited(0.2, 0.5); }

    @Override
    public void appendTriggerInfos(
        ServerLevel level, BlockPos pos, BlockState state, BallisticsShipData shipData, BallisticStateData stateData, List<TriggerInfo> dest
    ) {
        ServerShip projectile = shipData.getProjectileShip(level);
        if (projectile == null) {
            EzDebug.error("the projectile ship is null");
            return;
        }
        HashSet<Long> skipShips = new HashSet<>();
        skipShips.add(projectile.getId());
        skipShips.add(shipData.propellantShipId);
        if (shipData.artilleryShipId >= 0)
            skipShips.add(shipData.artilleryShipId);

        LinkedHashMap<BlockPos, BallisticsHitInfo> traverseResults = new LinkedHashMap<>();
        //List<TriggerInfo.CollisionTriggerInfo> appends = new ArrayList<>();

        Vector3dc velocity = projectile.getVelocity();
        Vector3d movement = velocity.mul(getRaycastPredictTime(), new Vector3d());
        var clips = BallisticsUtil.raycastPlaneForBlocks(movement, getWorldBounds(pos, state, projectile.getShipToWorld()), getRaycastStep());

        for (ClipContext clipCtx : clips) {
            BlockTraverser.Ballistics.traverseAllIncludeShipAppend(level, clipCtx, skipShips, traverseResults);
        }

        EzDebug.log("vel:" + StrUtil.F2(velocity) + ", movement:" + StrUtil.F2(movement) + ", appendCnt:" + traverseResults.size());

        //todo 可能使用插入排序会更快？(数据部分有序，而且当数据量小的时候其快)
        traverseResults.values().stream().sorted(
            Comparator.comparingDouble(a -> a.sqDist)
        ).forEach(hitInfo -> {
            dest.add(new TriggerInfo.CollisionTriggerInfo(projectile, pos, state, hitInfo));
        });

        //EzDebug.log("total ballistics count:" + traverseResults.size());

        /*var hitInfos = this.getHitInfos(level, pos, state, controlData, stateData);
        for (var hitInfo : hitInfos) {
            dest.add(new TriggerInfo.CollisionTriggerInfo(
                projectileShip, pos, state, hitInfo
            ));
        }*/
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

        return true;  //only accept self-made triggerInfo
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

       // EzDebug.light("sqDist:" + collisionInfo.hitInfo.sqDist);

        apBe.onAcceptTriggerInfo(collisionInfo);
    }


    @Override
    public boolean shouldTerminateAfterEffecting(TriggerInfo info) {
        return false;
    }

    @Override
    public Class<ApWarheadBlockEntity> getBlockEntityClass() { return ApWarheadBlockEntity.class; }
    @Override
    public BlockEntityType<? extends ApWarheadBlockEntity> getBlockEntityType() { return WapBlockEntites.AP_BE.get(); }
    @Override
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<S> p_153214_) {
        return ((level, blockPos, state, be) -> ((ApWarheadBlockEntity) be).tickUpdate());
    }



    @Override
    public void appendTriggerInfos(ServerLevel level, Vector3ic localPos, BlockState state, SandBoxServerShip ship, List<SandBoxTriggerInfo> dest) {
        BallisticBehaviour bb = ship.getBehaviour(BallisticBehaviour.class);
        if (bb == null) return;

        var rigidbodyDataReader = ship.getRigidbody().getDataReader();

        Vector3d worldPos = rigidbodyDataReader.localIToWorldPos(localPos);
        Vector3dc velocity = rigidbodyDataReader.getVelocity();

        ClipContext clipCtx = new ClipContext(
            JomlUtil.v3(worldPos), JomlUtil.v3Add(velocity.mul(0.08, new Vector3d()), worldPos),
            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
            null
        );

        //todo don't create the temp HashMap
        //HashMap<BlockPos, BallisticsHitInfo> hits = new HashMap<>();
        var hits = BlockTraverser.Ballistics.traverseAllIncludeShipSorted(level, clipCtx, null);
        for (BallisticsHitInfo hit : hits) {
            dest.add(new SandBoxTriggerInfo.CollisionTriggerInfo(ship.getUuid(), localPos, state, hit));
            //EzDebug.log("hit.dist:" + hit.sqDist);
        }
    }

    @Override
    public void doTerminalEffect(ServerLevel level, SandBoxServerShip ship, Vector3ic localPos, BlockState state, List<SandBoxTriggerInfo> infos, Dest<Boolean> terminateByEffect) {
        Predicate<SandBoxTriggerInfo> validator = x ->
            x instanceof SandBoxTriggerInfo.CollisionTriggerInfo collisionInfo && collisionInfo.senderLocalPos.equals(localPos);

        AtomicBoolean bouncing = new AtomicBoolean(false);

        var rigidDataReader = ship.getRigidbody().getDataReader();
        var rigidDataWriter = ship.getRigidbody().getDataWriter();

        double mass = rigidDataReader.getMass();
        Vector3d lastHitVel = new Vector3d(rigidDataReader.getVelocity());

        infos.stream().filter(validator).forEach(info -> {
            Vector3d tempLastVel = new Vector3d(lastHitVel);
            if (bouncing.get()) return;


            var collisionInfo = (SandBoxTriggerInfo.CollisionTriggerInfo)info;

            var hitInfo = collisionInfo.hitInfo;
            //Vector3d worldPos = rigidDataReader.localIToWorldPos(localPos);
            //Vector3dc velocity = rigidDataReader.getVelocity();
            double scaleX = rigidDataReader.getScale().x();

            BlockState armourState = hitInfo.getHitBlockState(level);
            double armourScale = WorldUtil.getScaleOfShipOrWorld(level, hitInfo.hitBlockPos);

            BallisticsMath.TerminalContext terminalCtx = BallisticsMath.TerminalContext.safeContextOrNull(
                lastHitVel,
                hitInfo.worldNormal,
                WapBlockInfos.oblique_degree.valueOrDefaultOf(state),
                WapBlockInfos.hardness.valueOrDefaultOf(state),
                WapBlockInfos.hardness.valueOrDefaultOf(armourState),
                WapBlockInfos.toughness.valueOrDefaultOf(armourState),
                WapBlockInfos.getValkrienMass(state),
                WapBlockInfos.ap_area.valueOrDefaultOf(state),
                scaleX,   //todo 3d scale
                armourScale
            );
            if (terminalCtx == null)
                return;
            if (terminalCtx.isPass()) {
                EzDebug.highlight("pass by deg:" + Math.toDegrees(terminalCtx.incidenceRad));
                return;
            }

            boolean penetrate = terminalCtx.canPenetrate();
            boolean shouldBounce = terminalCtx.isBounce(null);
            if (penetrate) {
                //todo maybe add a destroy context
                level.setBlock(hitInfo.hitBlockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);  //todo armour post effect
            }

            Vector3dc postVel = terminalCtx.getPostVelocity(shouldBounce);
            lastHitVel.set(postVel);
            rigidDataWriter.setVelocity(postVel);

            if (shouldBounce) {
                bouncing.set(true);

                Quaterniondc prevRot = rigidDataReader.getRotation();
                rigidDataWriter.setRotation(terminalCtx.getBouncedRotation(prevRot));
            }

            EzDebug.highlight(
                "incDeg:" + Math.toDegrees(terminalCtx.incidenceRad) +
                    ", obliquedIncDeg:" + Math.toDegrees(terminalCtx.obliquedIncidenceRad) +
                    ", criticalDeg:" + Math.toDegrees(terminalCtx.criticalRad) +
                    ", penetrate?: " + penetrate +
                    ", bounce? :" + shouldBounce +
                    ", prevVel:" + StrUtil.F2(tempLastVel) +
                    ", postVel:" + StrUtil.F2(postVel) +
                    ", prevE(kJ):" + 0.5 * mass * tempLastVel.lengthSquared() / 1000 +
                    ", postE(kJ):" + 0.5 * mass * postVel.lengthSquared() / 1000
            );

            //todo use a constant to decide if to terminate
            terminateByEffect.set(postVel.lengthSquared() < 0.1);
        });
    }
}
