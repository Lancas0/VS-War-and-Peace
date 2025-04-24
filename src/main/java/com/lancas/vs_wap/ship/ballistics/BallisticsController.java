package com.lancas.vs_wap.ship.ballistics;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.api.math.ForceOnPos;
import com.lancas.vs_wap.ship.ballistics.helper.BallisticsUtil;
import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.ship.ballistics.api.IPhysBehaviour;
import com.lancas.vs_wap.ship.ballistics.data.BallisticStateData;
import com.lancas.vs_wap.ship.ballistics.data.BallisticsComponentData;
import com.lancas.vs_wap.ship.ballistics.data.BallisticsShipData;
import com.lancas.vs_wap.ship.ballistics.api.TriggerInfo;
import com.lancas.vs_wap.ship.attachment.force.ProjectileReactForceInducer;
import com.lancas.vs_wap.ship.type.ProjectileWrapper;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.MathUtil;
import com.lancas.vs_wap.util.ShipUtil;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor()
public class BallisticsController implements ShipForcesInducer {  //apply on projectile ship
    @JsonIgnore
    public static final double PHYS_FRAME_TICK = 0.01667;
    /*private static final TicketType<ForgeChunkManager.TicketOwner<BallisticsController>> PROJECTILE_TICKET =
        TicketType.create("projectile_ticket", new Comparator<ForgeChunkManager.TicketOwner<BallisticsController>>() {
            @Override
            public int compare(ForgeChunkManager.TicketOwner<BallisticsController> o1, ForgeChunkManager.TicketOwner<BallisticsController> o2) {
                if (o1 == o2) return 0;
                return 1;
            }
        }, 60);*/

    /*public static class ChunkManagement {
        @JsonIgnore
        public static Map<Long, Long> id2InChunk = new ConcurrentHashMap<>();
        public static boolean isAnyProjectileInChunk(long chunkLong) {
            return id2InChunk.containsValue(chunkLong);
        }
        public static boolean isAnyProjectileAroundChunk(long chunkLong) {
            ChunkPos centerChunk = new ChunkPos(chunkLong);
            for (int x = -1; x <= 1; ++x)
                for (int z = -1; z <= 1; ++z) {
                    ChunkPos aroundChunk = new ChunkPos(centerChunk.x + x, centerChunk.z + z);
                    if (id2InChunk.containsValue(aroundChunk.toLong())) {
                        EzDebug.log("around chunk as projectile");
                        return true;
                    }

                }
            return false;
        }
        public static boolean isProjectile(long shipId) {
            return id2InChunk.containsKey(shipId);
        }
    }*/

    //save because return after re enter game we don't expect it lose action for ticks
    //private int initialWaitTick = 20;

    private BallisticsShipData shipData;
    private BallisticsComponentData componentData;
    private BallisticStateData stateData;

    private Vector3dc airDragCenterInShip;

    //@JsonSerialize(using = BallisticsTriggersSaver.TriggerDataSerializer.class)
    //@JsonDeserialize(using = BallisticsTriggersSaver.TriggerDataDeserializer.class)
    //private BallisticsTriggersData triggersData;

    //@JsonSerialize(using = TerminalDataSaver.TerminalDataSerializer.class)
    //@JsonDeserialize(using = TerminalDataSaver.TerminalDataDeserializer.class)
    //private BallisticsTerminalData terminalData;

    private boolean terminated = false;
    public boolean isTerminated() { return terminated; }
    //private double remainPropellantPower;

    //private boolean outArtillery = false;

    @JsonIgnore
    private Map<BlockPos, IPhysBehaviour> physBehaviours = null;
    @JsonIgnore
    private final Map<String, BiTuple<BlockPos, IPhysBehaviour>> tempPhyBehaviours = new ConcurrentHashMap<>();

    //sometime concurrent error
    private Queue<Vector3dc> reactionForces;  //null if breech ship is null
    private double airDragMultiplierCalInServer = 0;  //default 0, should calculate in server thread


    //private BallisticsController() {}
    public static BallisticsController apply(ServerLevel level, @NotNull ProjectileWrapper inProjectile, long propellantShipId, long artilleryShipId, double inPropellantPower) {
        if (propellantShipId < 0) {
            EzDebug.fatal("propellant ship can not be null");
            return null;
        }

        ServerShip projectileShip = inProjectile.getShip(level);
        long projectileId = inProjectile.shipId;

        /*BallisticsController controller = projectileShip.getAttachment(BallisticsController.class);
        if (controller == null) {
            controller = new BallisticsController();
            projectileShip.saveAttachment(BallisticsController.class, controller);
        }
        BallisticsController finalController = controller;*/
        BallisticsController controller = new BallisticsController();
        projectileShip.saveAttachment(BallisticsController.class, controller);  //directly overwrite the ballstic controller

        controller.shipData = new BallisticsShipData(
            inProjectile, propellantShipId, artilleryShipId
        );
        controller.stateData = new BallisticStateData(inPropellantPower);
        controller.componentData = new BallisticsComponentData(level, projectileShip);

        controller.airDragCenterInShip = BallisticsUtil.calculateAirDragCenter(level, projectileShip);
        //finalController.triggersData = new BallisticsTriggersData();
        //finalController.terminalData = new BallisticsTerminalData();
        /*ShipUtil.foreachBlock(inProjectileShip, level, (pos, state, be) -> {
            finalController.componentData.tryAcceptComponent(pos, state);
            //finalController.triggersData.tryAcceptBlock(pos, state);
            //finalController.terminalData.tryAcceptBlock(pos, state);
        });*/

        //disable collision between projectile and artillery
        if (artilleryShipId >= 0) {
            ServerShip breechShip = (ServerShip)ShipUtil.getShipByID(level, artilleryShipId);

            controller.reactionForces = new ConcurrentLinkedQueue<>();

            if (breechShip == null) {
                EzDebug.warn("can not find breechShip with id:" + artilleryShipId);
            } else {
                VSGameUtilsKt.getShipObjectWorld(level).disableCollisionBetweenBodies(projectileId, artilleryShipId);
            }
        }

        BallisticsClientManager.sendIdFromServer(projectileId);

        return controller;
    }


    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        //EzDebug.light("terminated:" + terminated + ", phy: remain tick:" + initialWaitTick + ", ship:" + shipData.getProjectileId());
        if (terminated) {  //todo maybe didn't called before remove from breechBE
            //ChunkManagement.id2InChunk.remove(shipData.projectileShipId);
            return;
        }

        //wait for seconds for stable
        /*if (initialWaitTick > 0) {
            initialWaitTick--;
            return;
        }*/

        final PhysShipImpl physShipImpl = (PhysShipImpl)physShip;
        final ShipTransform physShipTransform = physShipImpl.getTransform();
        final Matrix4dc shipToWorld = physShipTransform.getShipToWorld();
        final Matrix4dc worldToShip = physShipTransform.getWorldToShip();
        final Vector3dc velInWorld = physShipImpl.getPoseVel().getVel();
        final AABBic shipAABB = shipData.projectile.getShipAABB();
        final double sqVelLenInWorld = velInWorld.lengthSquared();
        final double shipScale = physShipTransform.getShipToWorldScaling().x();  //todo 3d scale?
        final Vector3dc worldForwardDir = JomlUtil.dWorldNormal(physShipTransform.getShipToWorld(), shipData.projectile.forwardInShip);
        final Vector3dc massWorldCenter = physShipImpl.getTransform().getPositionInWorld();


        Vector3d forceInBarrel = stateData.applyForceInBarrel(physShipImpl, shipData.getLaunchDir());
        if (forceInBarrel != null) {
            //EzDebug.log("applying in barrel force");
        }

        //physShip.applyInvariantForce(new Vector3d(0, 10, 0).mul(physShipImpl.getInertia().getShipMass()));
        /*for (var massEntry : blockMasses.entrySet()) {
            Vector3d worldCenter = JomlUtil.dWorldCenter(shipToWorld, massEntry.getKey().toBp());
            physShip.applyInvariantForceToPos(worldCenter, new Vector3d(0, -10, 0).mul(massEntry.getValue()));
        }*/
        /*if (forceInBarrel != null && reactionForces != null)
            reactionForces.add(new Vector3d(forceInBarrel).negate());*/

        AirDrag : {
            if (airDragCenterInShip == null) {
                EzDebug.warn("has no air drag center so can't be applied dir drag!");
                break AirDrag;
            }
            if (!stateData.getIsOutArtillery() || sqVelLenInWorld < 10 || airDragMultiplierCalInServer < 1E-10)
                break AirDrag;

            //Vector3d worldGeoCenter = shipData.getWorldGeoCenter(physShipTransform.getShipToWorld());
            Vector3d worldAirDragCenter = physShipTransform.getShipToWorld().transformPosition(airDragCenterInShip, new Vector3d());

            //double projectArea = shipData.getProjectArea(physShipTransform.getWorldToShip(), velInWorld);
            //double dragForceLen = 0.5 * projectArea * sqVelLenInWorld * airDragMultiplierCalInServer;
            //Vector3d airDragForce = velInWorld.normalize(-dragForceLen, new Vector3d());
            //todo remove
            Vector3d airDragForce = null;

            if (airDragForce != null && airDragForce.isFinite()) {
                Vector3d linearDrag = new Vector3d();
                Vector3d rotateDrag = new Vector3d();
                MathUtil.orthogonality(airDragForce, worldForwardDir, linearDrag, rotateDrag);

                physShipImpl.applyInvariantForce(linearDrag);

                //Vector3d moment = MathUtil.project(worldGeoCenter.sub(massWorldCenter, new Vector3d()), worldForwardDir, new Vector3d()).cross(rotateDrag);  //get the stable moment
                Vector3d moment = worldAirDragCenter.sub(massWorldCenter, new Vector3d()).cross(rotateDrag);
                physShipImpl.applyInvariantTorque(moment.mul(1));

                //NetworkHandler.sendToAllPlayers(new ForceOnPosDebugS2C(new ForceOnPos(moment, worldGeoCenter), "airDrag_toque", 125));
                //Vector3d airDragTorque =


                //NetworkHandler.sendToAllPlayers(new ForceOnPosDebugS2C(new ForceOnPos(forceAlongForward, worldGeoCenter), "airDrag_hor", 125));
                //NetworkHandler.sendToAllPlayers(new ForceOnPosDebugS2C(new ForceOnPos(forceVerticalForward, worldGeoCenter), "airDrag_vel", 375));

            }

            //空气阻力

            /*var airDrags = BallisticsMath.calculateAirDrag(shipToWorld, worldToShip, shipScale, shipAABB, velInWorld);

            for (int i = 0; i < 3; ++i) {
                ForceOnPos airDrag = (ForceOnPos)airDrags.get(i);

                boolean shouldScale = switch (shipData.projectile.forwardInShip) {
                    case EAST, WEST -> i == 0;
                    case UP, DOWN -> i == 1;
                    case SOUTH, NORTH -> i == 2;
                };

                if (shouldScale)
                    airDrag.scale(airDragMultiplierCalInServer).applyTo(physShip);
                else
                    airDrag.scale(1).applyTo(physShip);
            }*/
            /*final double blockProjectArea = BallisticsMath.getBlockProjectArea(physShipTransform.getWorldToShip(), velInWorld);

            componentData.foreachBlock(bp -> {
                Vector3d worldBlockCenter = JomlUtil.dWorldCenter(shipToWorld, bp);

                double dragForceLen = 0.05 * blockProjectArea * sqVelLenInWorld;
                Vector3d dragForce = velInWorld.normalize(-dragForceLen, new Vector3d());
                //todo some faces will be blocked

                physShipImpl.applyInvariantForceToPos(dragForce, worldBlockCenter);
            });*/
                //速度大于一定值才施加空气阻力




                //new ForceOnPos(airDragForce, worldGeoCenter)

                //NetworkHandler.sendToAllPlayers(new ForceOnPosDebugS2C(new ForceOnPos(airDragForce, worldGeoCenter), "airDrag"));
                //EzDebug.light("shipAABB:" + StrUtil.toFullString(projAABB) + ", xLen:" + JomlUtil.lengthX(projAABB) + ", lY" + JomlUtil.lengthY(projAABB) + ", lz:" + JomlUtil.lengthZ(projAABB));
                //EzDebug.light("apply airDrag:" + StrUtil.F2(airDragForce) + ", multilier:" + airDragMultiplierCalInServer + ", projArea:" + projectArea + ", sqVelInWorld:" + sqVelLenInWorld);

                //EzDebug.log("distance between massCenter and geoCenter:" + worldGeoCenter.distance(physShipImpl.getTransform().getPositionInWorld()));


        }



        //damping
        double torqueDamping = 1;
        Vector3dc omega = physShipImpl.getPoseVel().getOmega();
        Matrix3dc inertia = physShipImpl.getInertia().getMomentOfInertiaTensor();
        Vector3d dampingTorque = omega.mul(inertia, new Vector3d()).mul(-torqueDamping);

        physShipImpl.applyInvariantTorque(dampingTorque);


        if (physBehaviours != null) {
            for (Map.Entry<BlockPos, IPhysBehaviour> entry : physBehaviours.entrySet()) {
                BlockPos adderBp = entry.getKey();
                IPhysBehaviour physBehaviour = entry.getValue();

                Vector3d force = physBehaviour.getAdditionalForce(physShipImpl, adderBp, shipData);
                if (force != null && force.isFinite())
                    physShipImpl.applyInvariantForce(force);

                ForceOnPos forceOnPos = physBehaviour.getAdditionalForceOnPos(physShipImpl, adderBp, shipData);
                if (forceOnPos != null && forceOnPos.force().isFinite() && forceOnPos.pos().isFinite())
                    physShipImpl.applyInvariantForceToPos(forceOnPos.force(), forceOnPos.pos());

                Vector3d torque = physBehaviour.getAdditionalTorque(physShipImpl, adderBp, shipData);
                if (torque != null && torque.isFinite())
                    physShipImpl.applyInvariantTorque(torque);
            }
        }

        for (var entry : tempPhyBehaviours.entrySet()) {
            var bpWithPhysBehaviour = entry.getValue();
            if (bpWithPhysBehaviour != null) {
                bpWithPhysBehaviour.getSecond().applyOnShip(physShipImpl, bpWithPhysBehaviour.getFirst(), shipData);
            }
        }



        /*for (Vector3dc modifierForce : calculatedModifierForce.values()) {
            if (modifierForce == null) continue;
            if (!modifierForce.isFinite()) {
                EzDebug.warn("modifier is infinite, skip it");
                continue;
            }

            //EzDebug.Log("successfully apply force:" + StringUtil.toNormalString(modifierForce));
            physShip.applyInvariantForce(modifierForce);
        }
        for (Vector3dc modifierTorque : calculatedModifierTorque.values()) {
            if (modifierTorque == null) continue;
            if (!modifierTorque.isFinite()) {
                EzDebug.warn("modifier is infinite, skip it");
                continue;
            }

            //EzDebug.Log("successfully apply torque:" + StringUtil.toNormalString(modifierTorque));
            physShip.applyInvariantTorque(modifierTorque);
        }*/
        //todo override drag force
        /*subControls.forEach((key, value) -> {
            value.applyForces(physShip);
        });*/
    }

    public void serverTick(ServerLevel level) {
        //EzDebug.light("terminated:" + terminated + ", server: remain tick:" + initialWaitTick + ", ship:" + shipData.getProjectileId());
        if (terminated) return;

        ServerShip projectileShip = shipData.getProjectileShip(level);
        ServerShip artilleryShip = shipData.getArtilleryShip(level);
        if (projectileShip == null) {
            EzDebug.warn("projectile ship is null, ballistic controller will terminate");
            terminateAndTryReturnToPool(level);
            BallisticsClientManager.terminateIdFromServer(shipData.getProjectileId());
            return;
        }


        applyReactionForces(artilleryShip);

        stateData.updateState(level, projectileShip, artilleryShip);
        //EzDebug.log("is out artillery:" + stateData.getIsOutArtillery());
        componentData.foreachTicker(level, (tickerBp, state, ticker) -> {
            ticker.serverTicker(state, tickerBp, level, projectileShip);
        });

        airDragMultiplierCalInServer = 1;
        componentData.foreachModifier(level, (pos, state, modifier) -> {
            airDragMultiplierCalInServer *= modifier.getAirDragMultiplier(shipData.projectile, pos, state);

            modifier.modifyTempPhysBehaviour(shipData.projectile, pos, state, tempPhyBehaviours);
        });
        /*componentData.foreachModifier(level, (modifierBp, state, modifier) -> {
            var data = new IModifier.ModifierData(
                level,
                projectileShip,
                modifierBp,
                stateData.getIsOutArtillery(),
                new Vector3i(shipData.headDirInShip.getStepX(), shipData.headDirInShip.getStepY(), shipData.headDirInShip.getStepZ()),
                shipData.launchDir
            );
            Vector3dc modifierForce = modifier.calculateForceInServerTick(data);
            Vector3dc modifierTorque = modifier.calculateTorqueInServerTick(data);
            if (modifierForce == null) modifierForce = new Vector3d();
            if (modifierTorque == null) modifierTorque = new Vector3d();
            calculatedModifierForce.put(modifierBp, modifierForce);  //must put even it is null. because it is neccessy to override the last forece
            calculatedModifierTorque.put(modifierBp, modifierTorque);
        });*/
        physBehaviours = getPhysicalBehaviours(level);

        List<TriggerInfo> infos = getTriggerInfos(level);
        AtomicBoolean shouldTerminate = new AtomicBoolean(false);
        //todo avoid multi effect on a one-time effector
        tryTerminalEffect(level, infos, shouldTerminate);

        boolean doTerminate =
            shouldTerminate.get() ||
            stateData.tickStopped(projectileShip);

        if (doTerminate) {
            terminateAndTryReturnToPool(level);
            BallisticsClientManager.terminateIdFromServer(projectileShip.getId());
        }

        Vector3dc worldPos = projectileShip.getTransform().getPositionInWorld();
        ChunkPos worldChunkPos = new ChunkPos(JomlUtil.bpContaining(worldPos));
        /*EzDebug.log(
            "current chunk:" + worldChunkPos.toString() +
                ", current proj pos:" + StrUtil.toIntString(worldPos)
        );*/
        //todo enable or disable can load chunk in config
        //Vector3dc worldPos = projectileShip.getTransform().getPositionInWorld();
        //ChunkPos worldChunkPos = new ChunkPos(JomlUtil.bpContaining(worldPos));
        /*for (int x = -5; x <= 5; ++x)
            for (int z = -5; z <= 5; ++z) {
            level.setChunkForced(worldChunkPos.x + x, worldChunkPos.z + 1, true);
        }*/
    }
    private void applyReactionForces(@Nullable ServerShip artilleryShip) {
        if (artilleryShip == null) return;

        ProjectileReactForceInducer.addForcesTo(artilleryShip, reactionForces);
        reactionForces.clear();
    }
    private void terminateAndTryReturnToPool(ServerLevel level) {
        terminated = true;

        ServerShip projectile = shipData.getProjectileShip(level);
        if (projectile == null) {
            EzDebug.warn("projectile is null, fail to return to pool");
            return;
        }

        VSGameUtilsKt.getShipObjectWorld(level).deleteShip(projectile);
        //do not return ship for now: it cause unstablilize
        /*EzDebug.log("returning ship:" + shipData.getProjectileShip(level).getId());
        ShipPool.getOrCreatePool(level).returnShip(
            projectile,
            ship -> {
                ship.setTransformProvider(null);
                ship.saveAttachment(BallisticsController.class, null);
                ShipUtil.teleport(level, ship,
                    TeleportDataBuilder.noMovementOf(level, ship)
                        //.setPos(new Vector3d(0, 0, 0))
                        .defaultRotation()
                        .get()
                );
            }
        );*/
        //VSGameUtilsKt.getShipObjectWorld(level).deleteShip(projectile);
        /*EzDebug.log("returning ship:" + shipData.getProjectileShip(level).getId());
        ShipPool.getOrCreatePool(level).returnShip(
            projectile,
            ship -> {
            ship.setTransformProvider(null);
            ship.saveAttachment(BallisticsController.class, null);
            ShipUtil.teleport(level, ship,
                TeleportDataBuilder.noMovementOf(level, ship)
                    .setPos(new Vector3d(0, -2000, 0))
                    .defaultRotation()
                    .get()
            );
        });*/
    }
    /*
    private void calculateDragFactor(ServerLevel level) {
        AtomicReference<Double> dragFactor = new AtomicReference<>((double) 2);

        forEachModifier(level, (bp, state, modifier) -> {
            IModifier.ModifierData data = new IModifier.ModifierData(
                level, _projectileWrapper.getShip(level), bp, outArtillery, _projectileWrapper.getHeadDirInShip(level), launchDir
            );
            dragFactor.updateAndGet(v -> (v + modifier.getDragFactorModification(data)));
        });

        dragFactorCalculated = Math.max(0.2, dragFactor.get());
    }*/
    /*private boolean gatherShips(ServerLevel level) {
        if (_projectileWrapper == null) {
            ServerShip projectileShip = (ServerShip)ShipUtil.getShipByID(level, projectileShipId);
            Vector3dc barrelDirInShip = projectileShip.getTransform().getShipToWorldRotation().transform(launchDir, new Vector3d());
            double xAbs = Math.abs(barrelDirInShip.x());
            double yAbs = Math.abs(barrelDirInShip.y());
            double zAbs = Math.abs(barrelDirInShip.z());
            Vector3i projectileHeadDirInShip;
            if (xAbs > yAbs && xAbs > zAbs) {
                projectileHeadDirInShip = new Vector3i((int)Math.signum(xAbs), 0, 0);
            } else if (yAbs > xAbs && yAbs > zAbs) {
                projectileHeadDirInShip = new Vector3i(0, (int)Math.signum(yAbs), 0);
            } else if (zAbs > xAbs && zAbs > yAbs) {
                projectileHeadDirInShip = new Vector3i(0, 0, (int)Math.signum(zAbs));
            } else {
                //EzDebug.fatal("can't get obvious headDirInShip for projectile, barrelDirInShip:" + StringUtil.toNormalString(barrelDirInShip));
                return false;
            }

            _projectileWrapper = new BallisticShipWrapper(projectileShipId, projectileHeadDirInShip);
        }

        _breechShip =
            _breechShip != null ?
                _breechShip :
                (
                    breechShipId > 0 ?
                        (ServerShip)ShipUtil.getShipByID(level, breechShipId) :
                        null
                );

        if (!_projectileWrapper.isExist(level) || (breechShipId > 0 && _breechShip == null)) {
            EzDebug.fatal("fail to gather ships");
            return false;
        }
        return true;
    }*/
    /*
    @Deprecated
    private PriorityQueue<BallisticsHitInfo> predictCollision(ServerLevel level) {
        if (collisionDetectorPoses.isEmpty()) return null;
        ServerShip projectileShip = _projectileWrapper.getShip(level);

        Vector3dc vel = projectileShip.getVelocity();
        if (vel.lengthSquared() <= 20 * 20) return null;
        Vector3d movement = vel.mul(PREDICT_COLLISION_TIME, new Vector3d());

        //todo fatal: 有时会会变成debug方块，也许是与之前测试过后落到地面的弹头相互碰撞导致的？
        HashSet<Long> skipShipIds = new HashSet<>();
        skipShipIds.add(projectileShipId);
        skipShipIds.add(propellantShipId);
        if (breechShipId > 0) skipShipIds.add(breechShipId);  //todo ignore breech blocks that is on world

        Matrix4dc projShipToWorld = projectileShip.getShipToWorld();
        PriorityQueue<BallisticsHitInfo> hitsNearFirst = new PriorityQueue<>(BallisticsHitInfo.nearFirst());
        for (SavedBlockPos sbp : collisionDetectorPoses) {
            StopWatch sw = new StopWatch();
            sw.start();

            BlockPos cdBp = sbp.toBp();
            BlockState cdState = level.getBlockState(cdBp);

            if (!(cdState.getBlock() instanceof ICollisionDetector cd)) {
                EzDebug.fatal("block should be collision detector");
                return null;
            }

            AABBd worldBounds = cd.getWorldBounds(cdBp, cdState, projShipToWorld);

            //only to find out the ships
            AABBd worldPredictBounds = BallisticsUtil.extendAlong(cd.getWorldBounds(cdBp, cdState, projShipToWorld), movement);

            //Hashtable<Double, Iterable<ClipContext>> scale2Clips = new Hashtable<>();
            //scale2Clips.put(1.0, BallisticsUtil.raycastPlaneForBlocks(movement, worldBounds, 0.5));

            //do world
            for (var clip : /.*scale2Clips.get(1.0)*./BallisticsUtil.raycastPlaneForBlocks(movement, worldBounds, 0.5)) {
                BallisticsHitInfo info = BallisticsUtil.ballisticsClipInWorld(level, clip);
                if (info != null) {
                    hitsNearFirst.add(info);
                }
            }

            //do ships
            for (Ship ship : VSGameUtilsKt.getShipsIntersecting(level, worldPredictBounds)) {
                if (skipShipIds.contains(ship.getId())) continue;

                double shipScale = ship.getTransform().getShipToWorldScaling().x();  //todo 3d scale?
               // Iterable<ClipContext> shipClips = scale2Clips.get(shipScale);

                /.*if (shipClips == null) {
                    shipClips = BallisticsUtil.raycastPlaneForBlocks(movement, worldBounds, 0.5 * shipScale);
                    scale2Clips.put(shipScale, shipClips);
                }*./

                for (var clip : /.*shipClips*./BallisticsUtil.raycastPlaneForBlocks(movement, worldBounds, 0.5 * shipScale)) {
                    BallisticsHitInfo info = BallisticsUtil.ballisticsClipInShip(level, ship, clip, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE);
                    if (info != null) {
                        hitsNearFirst.add(info);
                    }
                }
            }

            sw.stop();
            //EzDebug.Log("a collision detection cost time:" + sw.getTime(TimeUnit.MICROSECONDS) / 1000.0 + " ms");
            //only detect one now
            return hitsNearFirst;
        }

        EzDebug.fatal("collisionDetectorPoses should be not empty so never arrive here.");
        return null;








        //test
        /*var clips = BallisticsUtil.raycastPlaneForBlocks(movement, _projectileShip.getWorldAABB(), 0.25);
        int clipSize = 0;
        HashSet<BlockPos> blockPos = new HashSet<>();

        if (breechShipId >= 0)  //idk whether a ship'id is >0 or >=0
            skipShipIds.add(breechShipId);
        for (var clip : clips) {
            clipSize++;
            //var hit = BallisticsUtil.clipIncludeShips(level, clip, true, skipShipIds);
            //test for now
            var hit = RaycastUtilsKt.clipIncludeShips(level, clip, true, projectileShipId);
            if (hit.getType() != HitResult.Type.MISS) {
                blockPos.add(hit.getBlockPos());
            }
        }

        swNoSetBlock.stop();

        BooleanProperty b01State = Block01.STATE;
        for (var bp : blockPos) {
            BlockState state = level.getBlockState(bp);
            if (state.getBlock() instanceof Block01 b01) {
                level.setBlockAndUpdate(bp, state.setValue(b01State, !state.getValue(b01State)));
            } else {
                level.setBlockAndUpdate(bp, EinherjarBlocks.BLOCK01.getDefaultState());
            }
            //sb.append(level.getBlockState(bp).getBlock().getName().getString() + "\n");
            //level.setBlockAndUpdate(blockPos, )
        }

        sw.stop();
        EzDebug.Log("a collision tick cost time:" + sw.getTime(TimeUnit.MICROSECONDS) + " microsec" + "\n" +
            "pure collision detection cost:" + swNoSetBlock.getTime(TimeUnit.MICROSECONDS) + " microsec" + "\n" +
            "total clip count:" + clipSize
            );

        //AABBd predictAABB = BallisticsUtil.extendAlong(_serverShip.getWorldAABB(), movement, new AABBd());
       // predictAABB.inter

        //detect ship collisions first
        /.*for (Ship intersectShip : VSGameUtilsKt.getShipsIntersecting(level, predictAABB)) {
            if (intersectShip.getId() == shipId) continue;


        }*./


        //_serverShip.getWorldAABB().;*./
    }*/
    private Hashtable<BlockPos, IPhysBehaviour> getPhysicalBehaviours(ServerLevel level) {
        Hashtable<BlockPos, IPhysBehaviour> behaviours = new Hashtable<>();
        componentData.foreachPhysBehaviourAdder(level, ((bp, state, adder) -> {
            behaviours.put(bp, adder.getPhysicalBehaviour(bp, state));
        }));
        return behaviours;
    }
    private List<TriggerInfo> getTriggerInfos(ServerLevel level) {
        List<TriggerInfo> infos = new ArrayList<>();
        componentData.foreachTrigger(level, (bp, state, trigger) -> {
            if (!trigger.shouldCheck(shipData, stateData))
                return;

            trigger.appendTriggerInfos(level, bp, state, shipData, stateData, infos);
        });
        return infos;
    }
    private void tryTerminalEffect(ServerLevel level, List<TriggerInfo> infos, AtomicBoolean shouldTerminate) {
        shouldTerminate.set(false);
        for (TriggerInfo info : infos) {
            componentData.foreachEffector(level, (bp, state, effector) -> {
                if (!effector.canAccept(level, bp, state, info))
                    return;

                effector.effect(level, bp, state, info);

                if (effector.shouldTerminateAfterEffecting(info))
                    shouldTerminate.set(true);
            });
        }
    }
}
