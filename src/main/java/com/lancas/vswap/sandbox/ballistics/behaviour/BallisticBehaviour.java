package com.lancas.vswap.sandbox.ballistics.behaviour;

import com.lancas.vswap.WapConfig;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vswap.sandbox.ballistics.data.BallisticData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticFlyingContext;
import com.lancas.vswap.sandbox.ballistics.data.BallisticPos;
import com.lancas.vswap.sandbox.ballistics.trigger.SandBoxTriggerInfo;
import com.lancas.vswap.ship.ballistics.data.BallisticsHitInfo;
import com.lancas.vswap.subproject.sandbox.component.behviour.SandBoxExpireTicker;
import com.lancas.vswap.subproject.sandbox.component.behviour.abs.ServerOnlyBehaviour;
import com.lancas.vswap.subproject.sandbox.component.data.ExpireTickerData;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.RandUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BallisticBehaviour extends ServerOnlyBehaviour<BallisticData> {
    @Override
    protected BallisticData makeInitialData() { return BallisticData.makeDefault(); }

    @Override
    public Class<BallisticData> getDataType() { return BallisticData.class; }

    @Override
    public synchronized void physTick(double dt) {
        if (data.terminated) return;

        var rigidbody = ship.getRigidbody();
        var rigidReader = ship.getRigidbody().getDataReader();
        var rigidWriter = ship.getRigidbody().getDataWriter();

        PropellingForceHandler.applyPropellingForceIfShould(ship, data);
        //AirDragHandler.applyAirDragIfShould(ship, data);


        //damping
        /*double torqueDamping = 1.5;
        Vector3dc omega = rigidbody.getDataReader().getOmega();
        Matrix3dc inertia = rigidbody.getDataReader().getLocalInertia();
        Vector3d dampingTorque = omega.mul(inertia, new Vector3d()).mul(-torqueDamping);  //τ=I⋅α
        rigidbody.getDataWriter().applyWorldTorque(dampingTorque);*/
        //Vector3d worldForward = rigidReader.localIToWorldNoScaleDir(data.initialStateData.localForward);
        //BallisticFlyingContext flyingCtx = BallisticFlyingContext.getDefault();

        data.initialStateData.foreachBallisticBlock(ship, (locPos, state, bb) -> {
            bb.physTick(dt, ship, data);
        });

        //apply linear air drag
        Vector3dc vel = rigidReader.getVelocity();
        rigidWriter.applyWorldForce(vel.mul(-0.5 * WapConfig.airDragFactor * vel.length(), new Vector3d()));


        if (vel.isFinite() && vel.lengthSquared() > 100 && WapConfig.projectileRandomDisplacement > 1E-10) {
            Quaterniond displacement = RandUtil.nextQuaterniond(0, WapConfig.projectileRandomDisplacement);
            Vector3d displacedVel = displacement.transform(vel, new Vector3d());

            Vector3d euler = new Quaterniond().rotateTo(JomlUtil.d(data.initialStateData.localForward), displacedVel).getEulerAnglesXYZ(new Vector3d());
            Quaterniond q = new Quaterniond().rotateXYZ(euler.x, euler.y, 0);
            rigidWriter.updateVelocity(v -> displacement.transform(vel, new Vector3d()));
            //rigidWriter.setRotation(q);
        }

        /*synchronized (data.initialStateData.ballisticBlockLocPoses) {

            //EzDebug.log("try phys ticks for all block");
        }*/
    }

    @Override
    public synchronized void serverTick(ServerLevel level) {
        if (data.firstTick) {
            ship.addBehaviour(new SandBoxExpireTicker(), new ExpireTickerData(WapConfig.flyingProjectileLifeSpan));
            data.firstTick = false;
        }

        IRigidbodyDataReader rigidReader = ship.getRigidbody().getDataReader();
        IRigidbodyDataWriter rigidWriter = ship.getRigidbody().getDataWriter();

        //EzDebug.log("ballstic ticking");
        if (data.terminated) {
            rigidWriter  //avoid some component still try to set vel/gravity after terminated.
                .setNoGravity()
                .setVelocity(0, 0, 0);

            BallisticPos headPos = data.initialStateData.getPosFromHead(0);
            if (headPos != null && data.stuckHitPos != null)
                rigidWriter.moveLocalPosToWorld(headPos.localPos(), data.stuckHitPos);
            return;
        } else {
            ship.getRigidbody().getDataWriter().setEarthGravity();
        }

        //todo reaction force
        //if (data.barrelCtx.appliedHighPressureStage)  //only after apply power can update barrel ctx
         //   BarrelCtxUpdateHandler.updateBarrelCtx(level, ship, data);

        //EzDebug.log("barrel ctx updated");


        /*if (!data.barrelCtx.isAbsoluteExitBarrel()) {
            ship.getRigidbody().getDataWriter().setNoGravity();
            return;
        }*/ /*else {
            ship.getRigidbody().getDataWriter().setEarthGravity();
        }*/


        //EzDebug.log("handling SandBoxTriggerInfo");
        BallisticFlyingContext flyingCtx = BallisticFlyingContext.getDefault();
        List<SandBoxTriggerInfo> infos = new ArrayList<>();

        /*synchronized (data.initialStateData.ballisticBlockLocPoses) {
            //warn: may change during foreach
        }*/
        data.initialStateData.foreachBallisticBlock(ship, (ballisticPos, state, bb) -> {
            bb.serverTick(level, ship, ballisticPos);
            bb.appendTriggerInfos(level, ballisticPos, state, ship, infos);
            bb.modifyFlyingContext(level, ship, data, ballisticPos, state, flyingCtx);
        });

        rigidWriter.setGravity(flyingCtx.gravity);
        Vector3dc vel = rigidReader.getVelocity();

        //set rotation so that head point to forward
        //Vector3d euler = new Quaterniond().rotateTo(rigidReader.localToWorldNoScaleDir(JomlUtil.d(data.initialStateData.localForward)), vel).getEulerAnglesXYZ(new Vector3d());
        /*Quaterniond q = new Quaterniond().rotateTo(JomlUtil.d(data.initialStateData.localForward), vel);
        q.lookAlong()
        Vector3d euler = new Quaterniond().rotateTo(JomlUtil.d(data.initialStateData.localForward), vel).getEulerAnglesXYZ(new Vector3d());
        Quaterniond q = new Quaterniond().rotateXYZ(euler.x, euler.y, 0);
        rigidWriter.setRotation(q);*/
        //Vector3d euler = new Quaterniond().rotateTo(JomlUtil.d(data.initialStateData.localForward), vel).getEulerAnglesYXZ(new Vector3d());
        //Quaterniond q = new Quaterniond().rotationYXZ(euler.y, euler.x, 0);
        rigidWriter.setRotation(JomlUtil.swingYXRotateTo(JomlUtil.d(data.initialStateData.localForward), vel, new Quaterniond()));
        /*Vector3d up = rigidReader.localToWorldNoScaleDir(new Vector3d(0, 1, 0));
        rigidWriter.setRotation(new Quaterniond().lookAlong(vel, up));*/

        if (vel.isFinite() && vel.lengthSquared() > 100 && flyingCtx.displacementIntensity > 1E-10) {
            Quaterniond displacement = RandUtil.nextQuaterniond(0, flyingCtx.displacementIntensity);
            Vector3d displacedVel = displacement.transform(vel, new Vector3d());
            rigidWriter.updateVelocity(v -> displacement.transform(v, new Vector3d()));
        }

        //Quaterniond q = new Quaterniond().look
        //rigidWriter.setRotation(JomlUtil.swingRotateTo(new Vector3d(0, 0, 1), vel, new Quaterniond()));

        //Vector3dc initialVel = rigidReader.getVelocity();
        Vector3d refVel = rigidReader.getVelocity(new Vector3d());
        Vector3d rayWithLen = refVel.mul(0.05, new Vector3d());

        BallisticPos headBPos = data.initialStateData.getPosFromHead(0);
        Vec3 headWorldPos = JomlUtil.v3(rigidReader.localIToWorldPos(headBPos.localPos()));

        ClipContext clip = new ClipContext(
            headWorldPos,
            headWorldPos.add(JomlUtil.v3(rayWithLen)),
            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
            null
        );
        Dest<Boolean> bouncing = new Dest<>(false);
        AtomicBoolean needTerminate = new AtomicBoolean(false);
        BlockState warhead = ship.getBlockCluster().getDataReader().getBlockState(headBPos.localPos());
        for (int i = 0; i < WapConfig.maxDestroyCnt; ++i) {  //loop until miss hit or i>=MAX_DESTROY_CNT
            if (bouncing.get()) break;
            if (refVel.lengthSquared() < 0.1 || !Double.isFinite(refVel.lengthSquared())) break;
            if (needTerminate.get())
                break;

            BallisticsHitInfo hitInfo = BallisticsHitInfo.clipIncludeShip(level, clip, data.vsShipFirer);
            if (hitInfo == null)  //not hit
                break;

            data.initialStateData.fromHeadToBallisticPoses.stream()
                //.map(x -> ship.getBlockCluster().getDataReader().getBlockState(x.localPos()))
                //.filter(x -> x.getBlock() instanceof ISandBoxBallisticBlock)
                //.map(x -> (ISandBoxBallisticBlock)(x.getBlock()))
                .forEach(x -> {
                    BlockState s = ship.getBlockCluster().getDataReader().getBlockState(x.localPos());
                    if (s.getBlock() instanceof ISandBoxBallisticBlock bb) {
                        bb.onClip(level, x, hitInfo, s, ship, infos);
                    }
                });

            data.initialStateData.foreachBallisticBlock(ship, (locPos, state, bb) -> {
                Dest<Boolean> curNeedTerminate = new Dest<>(false);
                bb.doTerminalEffect(level, ship, locPos, state, infos, curNeedTerminate);

                if (curNeedTerminate.get())
                    needTerminate.set(true);
            });

            if (needTerminate.get())
                break;

            double mass = rigidReader.getMass();  //todo rigidData's mass effect by scale
            //Vector3d lastHitVel = new Vector3d(rigidDataReader.getVelocity());
            double warheadScale = rigidReader.getScale().x();  //todo 3d scale?  todo scale effects

            //Vector3d tempLastVel = new Vector3d(lastHitVel);


            //todo limit max destroy range
            PenetrateHandler.breakAroundBlocks(level, ship, data, warhead, headBPos, refVel, hitInfo, bouncing, needTerminate);
        }






        /*AtomicBoolean penetrateTerminate = new AtomicBoolean(false);
        PenetrateHandler.handle(level, ship, data, penetrateTerminate);

        if (penetrateTerminate.get())
            needTerminate.set(true);*/


        /*for (SandBoxTriggerInfo info : infos) {
            Dest<Boolean> curNeedTerminate = new Dest<>(false);

            data.initialStateData.foreachBallisticBlock(ship, (locPos, state, bb) -> {

                //EzDebug.log("try doTerminalEffect");
            });

            if (curNeedTerminate.get())
                needTerminate.set(true);
        }*/


        data.elapsedTime += 0.05;  //todo provided by method arg;
        //if (data.elapsedTime > BallisticData.TIME_OUT_SECONDS)
        //    needTerminate.set(true);
        if (ship.getRigidbody().getDataReader().getTransform().getPosition().y() < BallisticData.RANGE_OUT_LOWER_Y)
            needTerminate.set(true);

        //needTerminate || stopped for too long?
        if (needTerminate.get()) {
            data.terminated = true;
            ship.getRigidbody().getDataWriter()
                .setNoGravity()
                .setVelocity(0, 0, 0);  //disable gravity
            ship.addBehaviour(new SandBoxExpireTicker(), new ExpireTickerData(WapConfig.stoppedProjectileLifeSpan));
            //SandBoxServerWorld.getOrCreate(level).markShipDeleted(ship.getUuid());
            EzDebug.highlight("terminate this ship");
        }
    }
}
