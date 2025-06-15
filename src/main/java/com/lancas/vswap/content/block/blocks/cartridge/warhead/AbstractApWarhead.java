package com.lancas.vswap.content.block.blocks.cartridge.warhead;

import com.lancas.vswap.WapConfig;
import com.lancas.vswap.content.WapBlockEntites;
import com.lancas.vswap.content.block.blockentity.ApWarheadBlockEntity;
import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.content.info.block.WapBlockInfos;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.foundation.math.WapBallisticMath;
import com.lancas.vswap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vswap.sandbox.ballistics.behaviour.BallisticBehaviour;
import com.lancas.vswap.sandbox.ballistics.behaviour.PropellingForceHandler;
import com.lancas.vswap.sandbox.ballistics.data.BallisticPos;
import com.lancas.vswap.sandbox.ballistics.trigger.SandBoxTriggerInfo;
import com.lancas.vswap.ship.ballistics.api.ICollisionTrigger;
import com.lancas.vswap.ship.ballistics.api.ITerminalEffector;
import com.lancas.vswap.ship.ballistics.api.TriggerInfo;
import com.lancas.vswap.ship.ballistics.collision.traverse.BlockTraverser;
import com.lancas.vswap.ship.ballistics.data.BallisticStateData;
import com.lancas.vswap.ship.ballistics.data.BallisticsHitInfo;
import com.lancas.vswap.ship.ballistics.data.BallisticsShipData;
import com.lancas.vswap.ship.ballistics.helper.BallisticsUtil;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.RandUtil;
import com.lancas.vswap.util.StrUtil;
import com.lancas.vswap.util.WorldUtil;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.*;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;

import java.lang.Math;
import java.util.*;
import java.util.function.Predicate;

public abstract class AbstractApWarhead extends BlockPlus implements ICollisionTrigger, ITerminalEffector, IBE<ApWarheadBlockEntity>, ISandBoxBallisticBlock {
    /*private static class ApWarheadRecord implements IBlockRecord {

        private ApWarheadRecord() {}
        public ApWarheadRecord(BlockPos inBp) { bp = new SavedBlockPos(inBp); }
    }*/
    @Override
    public List<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(
            WarheadAPCR.class,
            () -> List.of(
                new DefaultCartridgeAdder(true)//,
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
    public void appendTriggerInfos(ServerLevel level, BallisticPos ballisticPos, BlockState state, SandBoxServerShip ship, List<SandBoxTriggerInfo> dest) {
        if (true) return;  //todo temp

        if (ballisticPos.fromHead() != 0) {
            return;  //only ap warhead at head effects.
        }

        BallisticBehaviour bb = ship.getBehaviour(BallisticBehaviour.class);
        if (bb == null) return;

        var rigidbodyDataReader = ship.getRigidbody().getDataReader();

        Vector3d worldPos = rigidbodyDataReader.localIToWorldPos(ballisticPos.localPos());
        Vector3dc velocity = rigidbodyDataReader.getVelocity();

        ClipContext clipCtx = new ClipContext(
            JomlUtil.v3(worldPos), JomlUtil.v3Add(velocity.mul(0.1, new Vector3d()), worldPos),
            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
            null
        );

        //todo don't create the temp HashMap
        //HashMap<BlockPos, BallisticsHitInfo> hits = new HashMap<>();
        var hits = BlockTraverser.Ballistics.traverseAllIncludeShipSorted(level, clipCtx, null);
        for (BallisticsHitInfo hit : hits) {
            dest.add(new SandBoxTriggerInfo.CollisionTriggerInfo(ship.getUuid(), ballisticPos.localPos(), state, hit));
            //EzDebug.log("hit.dist:" + hit.sqDist);
        }
    }

    @Override
    public void doTerminalEffect(ServerLevel level, SandBoxServerShip ship, BallisticPos ballisticPos, BlockState state, List<SandBoxTriggerInfo> infos, Dest<Boolean> terminateByEffect) {
        if (ballisticPos.fromHead() != 0) {
            return;  //only ap warhead at head effects.
        }

        Predicate<SandBoxTriggerInfo> validator = x ->
            x instanceof SandBoxTriggerInfo.CollisionTriggerInfo collisionInfo && collisionInfo.senderLocalPos.equals(ballisticPos.localPos());

        Dest<Boolean> bouncing = new Dest<>(false);

        var rigidDataReader = ship.getRigidbody().getDataReader();
        var rigidDataWriter = ship.getRigidbody().getDataWriter();

        double mass = rigidDataReader.getMass();  //rigidData's mass effect by scale
        Vector3d lastHitVel = new Vector3d(rigidDataReader.getVelocity());
        double warheadScale = rigidDataReader.getScale().x();  //todo 3d scale?  todo scale effects

        infos.stream().filter(validator).map(x -> (SandBoxTriggerInfo.CollisionTriggerInfo)x).forEach(info -> {
            //Vector3d tempLastVel = new Vector3d(lastHitVel);
            if (bouncing.get()) return;
            if (lastHitVel.lengthSquared() < 0.1 || !Double.isFinite(lastHitVel.lengthSquared())) return;

            breakAroundBlocks(level, ship, ballisticPos, lastHitVel, info, bouncing, terminateByEffect);


            /*var collisionInfo = (SandBoxTriggerInfo.CollisionTriggerInfo)info;
            var hitInfo = collisionInfo.hitInfo;
            BlockState armourState = hitInfo.getHitBlockState(level);
            BlockState warheadState = collisionInfo.senderState;
            //Vector3d worldPos = rigidDataReader.localIToWorldPos(localPos);
            //Vector3dc velocity = rigidDataReader.getVelocity();
            //double scaleX = rigidDataReader.getScale().x();


            double armourScale = WorldUtil.getScaleOfShipOrWorld(level, hitInfo.hitBlockPos);
            double stdPropellantEnergy = 0.5 * mass * lastHitVel.lengthSquared() / PropellingForceHandler.STD_PROPELLANT_ENERGY;
            double incidenceRad = WapBallisticMath.RAD.calIncidenceRad(lastHitVel, hitInfo.worldNormal);

            if (incidenceRad >= WapBallisticMath.PASS_RAD)
                return;


            double ricochetProb = WapBallisticMath.RAD.calRicochetPob(warheadState, incidenceRad);

            double rhea = WapBallisticMath.RAD.calAfterNormalizationRhae(armourState, warheadState, incidenceRad);

            if (stdPropellantEnergy < rhea) {  //not penetrate
                lastHitVel.set(0, 0, 0);
                terminateByEffect.set(true);
                return;
            }

            //level.setBlock(hitInfo.hitBlockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
            BlockHelper.destroyBlock(level, hitInfo.hitBlockPos, 1f);

            double afterEnergy = stdPropellantEnergy - WapBallisticMath.RAD.getAbsorbStdPP(armourState, warheadState, incidenceRad);
            if (afterEnergy <= 0) {
                lastHitVel.set(0, 0, 0);
                terminateByEffect.set(true);
                return;
            }

            Vector3d newVelDir;// = lastHitVel.normalize(new Vector3d());
            double newVelLength = Math.sqrt(2 * PropellingForceHandler.STD_PROPELLANT_ENERGY * afterEnergy / mass);

            if (RandUtil.nextBool(ricochetProb)) {  //ricochet
                newVelDir = WapBallisticMath.RAD.calBouncedVelDir(lastHitVel.normalize(new Vector3d()), hitInfo.worldNormal);
                bouncing.set(true);

                Quaterniondc prevRot = rigidDataReader.getRotation();
                rigidDataWriter.setRotation(WapBallisticMath.RAD.calBouncedRotation(lastHitVel, hitInfo.worldNormal, prevRot));//  terminalCtx.getBouncedRotation(prevRot));
            } else {
                newVelDir = lastHitVel.normalize(new Vector3d());
            }


            Vector3dc postVel = newVelDir.normalize(newVelLength);
            lastHitVel.set(postVel);
            rigidDataWriter.setVelocity(postVel);

            /*if (shouldBounce) {
                bouncing.set(true);

                Quaterniondc prevRot = rigidDataReader.getRotation();
                rigidDataWriter.setRotation(terminalCtx.getBouncedRotation(prevRot));
            }*./

            EzDebug.highlight(
                "incDeg:" + Math.toDegrees(incidenceRad) +
                    //", obliquedIncDeg:" + Math.toDegrees(terminalCtx.obliquedIncidenceRad) +
                    //", criticalDeg:" + Math.toDegrees(terminalCtx.criticalRad) +
                    //", penetrate?: " + penetrate +
                    //", bounce? :" + shouldBounce +
                    ", rhea:" + rhea +
                    ", preStdPE:" + stdPropellantEnergy +
                    ", postStdPE:" + (0.5 * mass * postVel.lengthSquared() / PropellingForceHandler.STD_PROPELLANT_ENERGY) +
                    ", prevVel:" + StrUtil.F2(tempLastVel) +
                    ", postVel:" + StrUtil.F2(postVel) +
                    ", prevE(kJ):" + 0.5 * mass * tempLastVel.lengthSquared() / 1000 +
                    ", postE(kJ):" + 0.5 * mass * postVel.lengthSquared() / 1000
            );

            //todo use a constant to decide if to terminate
            terminateByEffect.set(postVel.lengthSquared() < 0.1);*/

            //BallisticsMath.TerminalContext
            /*BallisticsMath.TerminalContext terminalCtx = BallisticsMath.TerminalContext.safeContextOrNull(
                lastHitVel,
                hitInfo.worldNormal,
                WapBlockInfos.WarheadNormalization.valueOrDefaultOf(state),
                WapBlockInfos.ArmourRhae.valueOrDefaultOf(state),
                WapBlockInfos.ArmourRhae.valueOrDefaultOf(armourState),
                WapBlockInfos.ArmourAbsorbRatio.valueOrDefaultOf(armourState),
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
            terminateByEffect.set(postVel.lengthSquared() < 0.1);*/
        });

    }
    private void breakAroundBlocks(ServerLevel level, SandBoxServerShip ship, BallisticPos ballisticPos, Vector3d refHitVel, SandBoxTriggerInfo.CollisionTriggerInfo collisionInfo, Dest<Boolean> bouncing, Dest<Boolean> terminate) {
        if (true) return;

        /*if (terminate.get() || bouncing.get()) return;
        if (refHitVel.lengthSquared() < 0.1 || !Double.isFinite(refHitVel.lengthSquared())) return;

        var hitInfo = collisionInfo.hitInfo;
        //BlockState hitBlockState = hitInfo.getHitBlockState(level);
        BlockState warheadState = collisionInfo.senderState;

        IRigidbodyDataReader rigidReader = ship.getRigidbody().getDataReader();
        IRigidbodyDataWriter rigidWriter = ship.getRigidbody().getDataWriter();

        double mass = rigidReader.getMass();
        double stdPE = 0.5 * mass * refHitVel.lengthSquared() / PropellingForceHandler.STD_PROPELLANT_ENERGY;


        double pMul = WapBlockInfos.PenetrationMultiplier.valueOrDefaultOf(warheadState);
        double incidenceRad = WapBallisticMath.RAD.calIncidenceRad(refHitVel, hitInfo.worldNormal);
        double ricochetProb = WapBallisticMath.RAD.calRicochetPob(warheadState, incidenceRad);

        WapBallisticMath.calDestructionBlocksIncludeShip(hitInfo.hitBlockPos, stdPE, warheadState, WapConfig.maxDestroyRadius).forEach(bp -> {
            if (bouncing.get() || terminate.get() || refHitVel.lengthSquared() < 0.1 || !Double.isFinite(refHitVel.lengthSquared()))
                return;

            BlockState curArmour = level.getBlockState(bp);
            if (curArmour.isAir())
                return;

            double armourScale = WorldUtil.getScaleOfShipOrWorld(level, bp);

            if (incidenceRad >= WapBallisticMath.PASS_RAD)
                return;

            double rhea = armourScale * WapBallisticMath.RAD.calAfterNormalizationRhae(curArmour, warheadState, incidenceRad) / pMul;

            if (stdPE < rhea) {  //not penetrate
                refHitVel.set(0, 0, 0);
                terminate.set(true);
                //stuck in block
               // Vector3d worldStuckDir = WorldUtil.getWorldDirection(level, bp, )hitInfo.worldNormal
                ClipContext struckToBlockClip = new ClipContext(
                    JomlUtil.v3(rigidReader.getPosition()),
                    JomlUtil.v3(rigidReader.predictFurtherPos(1, new Vector3d())),  //predict for longer time
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    null
                );
                BlockHitResult stuckHit = RaycastUtilsKt.clipIncludeShips(level, struckToBlockClip, true);

                if (stuckHit.getType() != HitResult.Type.MISS) {
                    ship.getRigidbody().getDataWriter().moveLocalPosToWorld(ballisticPos.localPos(), JomlUtil.d(stuckHit.getLocation()));
                } else {
                    EzDebug.warn("fail to get stuck pos, will remove projectile");
                    SandBoxServerWorld.markShipDeleted(level, ship.getUuid());
                }
                //Vector3d hitFaceCenter = WorldUtil.getWorldFaceCenter(level, bp, hitInfo.face);
                //EzDebug.warn("set pos:" + StrUtil.F2(hitFaceCenter));

                return;
            }

            //level.setBlock(hitInfo.hitBlockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
            BlockHelper.destroyBlock(level, bp, 1f);

            double afterEnergy = stdPE - WapBallisticMath.RAD.getAbsorbStdPP(curArmour, warheadState, incidenceRad) / pMul;
            /.*if (afterEnergy <= 0) {
                refHitVel.set(0, 0, 0);
                terminate.set(true);
                return;
            }*./

            Vector3d newVelDir;// = lastHitVel.normalize(new Vector3d());
            double newVelLength = Math.sqrt(2 * PropellingForceHandler.STD_PROPELLANT_ENERGY * afterEnergy / mass);

            if (RandUtil.nextBool(ricochetProb)) {  //ricochet
                newVelDir = WapBallisticMath.RAD.calBouncedVelDir(refHitVel.normalize(new Vector3d()), hitInfo.worldNormal);
                bouncing.set(true);

                //no need to set rotation because rot dir is to vel
                //Quaterniondc prevRot = rigidReader.getRotation();
                //rigidWriter.setRotation(WapBallisticMath.RAD.calBouncedRotation(refHitVel, hitInfo.worldNormal, prevRot));//  terminalCtx.getBouncedRotation(prevRot));
            } else {
                newVelDir = refHitVel.normalize(new Vector3d());
            }


            Vector3dc postVel = newVelDir.normalize(newVelLength);
            refHitVel.set(postVel);
            rigidWriter.setVelocity(postVel);

            EzDebug.highlight(
                "incDeg:" + Math.toDegrees(incidenceRad) +
                    ", rhea:" + rhea +
                    ", preStdPE:" + stdPE +
                    ", postStdPE:" + (0.5 * mass * postVel.lengthSquared() / PropellingForceHandler.STD_PROPELLANT_ENERGY) +
                    //", prevVel:" + StrUtil.F2(tempLastVel) +
                    ", postVel:" + StrUtil.F2(postVel) +
                    //", prevE(kJ):" + 0.5 * mass * tempLastVel.lengthSquared() / 1000 +
                    ", postE(kJ):" + 0.5 * mass * postVel.lengthSquared() / 1000
            );

            //todo use a constant to decide if to terminate
            //terminate.set(postVel.lengthSquared() < 0.1);
        });*/
    }
}
