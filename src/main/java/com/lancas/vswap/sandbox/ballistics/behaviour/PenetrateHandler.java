package com.lancas.vswap.sandbox.ballistics.behaviour;

import com.lancas.vswap.WapConfig;
import com.lancas.vswap.content.info.block.WapBlockInfos;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.foundation.math.WapBallisticMath;
import com.lancas.vswap.sandbox.ballistics.data.BallisticData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticPos;
import com.lancas.vswap.ship.ballistics.data.BallisticsHitInfo;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.RandUtil;
import com.lancas.vswap.util.StrUtil;
import com.lancas.vswap.util.WorldUtil;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class PenetrateHandler {

    public static class PMath {

    }

    public static void handle(ServerLevel level, SandBoxServerShip ship, BallisticData ballisticData, AtomicBoolean terminate) {
        BallisticPos headBPos = ballisticData.initialStateData.getPosFromHead(0);
        if (headBPos == null) {
            EzDebug.warn("fail to get projectile head");
            return;
        }
        var blockReader = ship.getBlockCluster().getDataReader();
        BlockState warhead = blockReader.getBlockState(headBPos.localPos());

        var rigidReader = ship.getRigidbody().getDataReader();

        Vector3dc pos = rigidReader.getPosition();
        Vector3d hfScale = rigidReader.getScale().mul(0.5, new Vector3d());

        Vector3dc initialVel = rigidReader.getVelocity();
        Vector3d refVel = rigidReader.getVelocity(new Vector3d());
        Vector3d rayWithLen = refVel.mul(0.075, new Vector3d());

        HashMap<BlockPos, BallisticsHitInfo> hitInfos = new HashMap<>();

        /*BallisticsUtil.raycastPlaneForBlocks(rayWithLen, JomlUtil.dCenterExtended(pos, hfScale), WapCommonConfig.projectileCollisionStep).forEach(c -> {
            BlockTraverser.Ballistics.traverseAllIncludeShipSorted(level, c, null).forEach(x -> hitInfos.put(x.hitBlockPos, x));
        });*/
        Vec3 headWorldPos = JomlUtil.v3(rigidReader.localIToWorldPos(headBPos.localPos()));

        ClipContext clip = new ClipContext(
            headWorldPos,
            headWorldPos.add(JomlUtil.v3(rayWithLen)),
            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
            null
        );
        Dest<Boolean> bouncing = new Dest<>(false);

        for (int i = 0; i < WapConfig.maxDestroyCnt; ++i) {  //loop until miss hit or i>=MAX_DESTROY_CNT
            BallisticsHitInfo hitInfo = BallisticsHitInfo.clipIncludeShip(level, clip);
            if (hitInfo == null)  //not hit
                break;

            double mass = rigidReader.getMass();  //todo rigidData's mass effect by scale
            //Vector3d lastHitVel = new Vector3d(rigidDataReader.getVelocity());
            double warheadScale = rigidReader.getScale().x();  //todo 3d scale?  todo scale effects

            //Vector3d tempLastVel = new Vector3d(lastHitVel);
            if (bouncing.get()) break;
            if (refVel.lengthSquared() < 0.1 || !Double.isFinite(refVel.lengthSquared())) break;

            //todo limit max destroy range
            breakAroundBlocks(level, ship, ballisticData, warhead, headBPos, refVel, hitInfo, bouncing, terminate);
        }

        /*hitInfos.values().forEach(info -> {
            //Predicate<SandBoxTriggerInfo> validator = x ->
            //    x instanceof SandBoxTriggerInfo.CollisionTriggerInfo collisionInfo && collisionInfo.senderLocalPos.equals(ballisticPos.localPos());
            Dest<Boolean> bouncing = new Dest<>(false);

            double mass = rigidDataReader.getMass();  //rigidData's mass effect by scale
            //Vector3d lastHitVel = new Vector3d(rigidDataReader.getVelocity());
            double warheadScale = rigidDataReader.getScale().x();  //todo 3d scale?  todo scale effects


            //Vector3d tempLastVel = new Vector3d(lastHitVel);
            if (bouncing.get()) return;
            if (refVel.lengthSquared() < 0.1 || !Double.isFinite(refVel.lengthSquared())) return;

            //todo limit max destroy range
            breakAroundBlocks(level, ship, ballisticData, warhead, headBPos, refVel, info, bouncing, terminate);
        });*/

        IRigidbodyDataWriter rigidWriter = ship.getRigidbody().getDataWriter();

        double consumeEnergy = initialVel.lengthSquared() - refVel.lengthSquared();
        rigidWriter.setVelocity(refVel);
        //rigidWriter.applyWork(-consumeEnergy);

    }

    public void breakSingle(BlockState warhead, BlockState armour, Vector3d refHitVel) {

    }

    /*public void doTerminalEffect(ServerLevel level, SandBoxServerShip ship, BallisticPos ballisticPos, BlockState state, List<SandBoxTriggerInfo> infos, Dest<Boolean> terminateByEffect) {
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
        });

    }*/
    private static void breakAroundBlocks(ServerLevel level, SandBoxServerShip ship, BallisticData data, BlockState warhead, BallisticPos ballisticPos, Vector3d refHitVel, BallisticsHitInfo hitInfo, Dest<Boolean> bouncing, AtomicBoolean terminate) {
        if (terminate.get() || bouncing.get()) return;
        if (refHitVel.lengthSquared() < 0.1 || !Double.isFinite(refHitVel.lengthSquared())) return;

        //var hitInfo = hitInfo.hitInfo;
        //BlockState hitBlockState = hitInfo.getHitBlockState(level);
        //BlockState warheadState = hitInfo.senderState;

        IRigidbodyDataReader rigidReader = ship.getRigidbody().getDataReader();


        double mass = rigidReader.getMass();
        AtomicReference<Double> stdPE = new AtomicReference<>(0.5 * mass * refHitVel.lengthSquared() / PropellingForceHandler.STD_PROPELLANT_ENERGY);


        double pMul = WapBlockInfos.PenetrationMultiplier.valueOrDefaultOf(warhead);
        double incidenceRad = WapBallisticMath.RAD.calIncidenceRad(refHitVel, hitInfo.worldNormal);
        double ricochetProb = WapBallisticMath.RAD.calRicochetPob(warhead, incidenceRad);

        WapBallisticMath.calDestructionBlocksIncludeShip(level, hitInfo.worldHitPos, stdPE.get(), warhead, WapConfig.maxDestroyRadius).forEach(bp -> {
            if (bouncing.get() || terminate.get() || refHitVel.lengthSquared() < 0.1 || !Double.isFinite(refHitVel.lengthSquared()))
                return;

            BlockState curArmour = level.getBlockState(bp);
            if (curArmour.isAir())
                return;

            double armourScale = WorldUtil.getScaleOfShipOrWorld(level, bp);

            if (incidenceRad >= WapBallisticMath.PASS_RAD)
                return;

            double rhea = armourScale * WapBallisticMath.RAD.calAfterNormalizationRhae(curArmour, warhead, incidenceRad) / pMul;

            if (stdPE.get() < rhea) {  //not penetrate
                refHitVel.set(0, 0, 0);
                terminate.set(true);
                //stuck in block
                // Vector3d worldStuckDir = WorldUtil.getWorldDirection(level, bp, )hitInfo.worldNormal
                ClipContext struckToBlockClip = new ClipContext(
                    JomlUtil.v3(rigidReader.getPosition()),
                    JomlUtil.v3(rigidReader.predictFurtherPos(0.2, new Vector3d())),  //predict for longer time
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    null
                );
                BlockHitResult stuckHit = RaycastUtilsKt.clipIncludeShips(level, struckToBlockClip, true);

                if (stuckHit.getType() != HitResult.Type.MISS) {
                    //ship.getRigidbody().getDataWriter().moveLocalPosToWorld(ballisticPos.localPos(), JomlUtil.d(stuckHit.getLocation()));
                    EzDebug.log("stuckHit:" + stuckHit.getLocation());
                    data.stuckHitPos = JomlUtil.d(stuckHit.getLocation());
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
            data.destroyedCnt++;
            if (data.destroyedCnt >= WapConfig.maxDestroyCnt)
                terminate.set(true);

            double afterEnergy = stdPE.get() - WapBallisticMath.RAD.getAbsorbStdPP(curArmour, warhead, incidenceRad) / pMul;
            /*if (afterEnergy <= 0) {
                refHitVel.set(0, 0, 0);
                terminate.set(true);
                return;
            }*/

            Vector3d newVelDir;// = lastHitVel.normalize(new Vector3d());
            double newVelLength = Math.sqrt(2 * PropellingForceHandler.STD_PROPELLANT_ENERGY * afterEnergy / mass);

            boolean isFatal = (stdPE.get() >= armourScale * WapBallisticMath.RAD.calFatalSpe(curArmour, incidenceRad));
            if (!isFatal && RandUtil.nextBool(ricochetProb)) {  //ricochet
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
            //rigidWriter.setVelocity(postVel);

            EzDebug.highlight(
                "incDeg:" + Math.toDegrees(incidenceRad) + "\n" +
                    ", pos:" + bp.toShortString() +
                    ", block:" + StrUtil.getBlockName(curArmour) + "\n" +
                    ", preStdPE:" + StrUtil.F4(stdPE.get()) +
                    ", postStdPE:" + StrUtil.F4(0.5 * mass * postVel.lengthSquared() / PropellingForceHandler.STD_PROPELLANT_ENERGY)
                    //", prevVel:" + StrUtil.F2(tempLastVel) +
                    //", postVel:" + StrUtil.F2(postVel) +
                    //", prevE(kJ):" + 0.5 * mass * tempLastVel.lengthSquared() / 1000 +
                    //", postE(kJ):" + 0.5 * mass * postVel.lengthSquared() / 1000
            );

            stdPE.set(afterEnergy);

            //todo use a constant to decide if to terminate
            //terminate.set(postVel.lengthSquared() < 0.1);
        });
    }
}
