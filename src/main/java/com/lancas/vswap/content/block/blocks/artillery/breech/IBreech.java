package com.lancas.vswap.content.block.blocks.artillery.breech;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vswap.content.block.blocks.cartridge.IPrimer;
import com.lancas.vswap.content.block.blocks.cartridge.propellant.IPropellant;
import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.content.saved.blockrecord.BlockRecordRWMgr;
import com.lancas.vswap.content.saved.vs_constraint.ConstraintsMgr;
import com.lancas.vswap.content.saved.blockrecord.IBlockRecord;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.foundation.LazyTicks;
import com.lancas.vswap.foundation.TriTuple;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.foundation.data.SavedBlockPos;
import com.lancas.vswap.ship.attachment.HoldableAttachment;
import com.lancas.vswap.ship.data.RRWChunkyShipSchemeData;
import com.lancas.vswap.ship.feature.hold.ICanHoldShip;
import com.lancas.vswap.ship.feature.hold.ShipHoldSlot;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.InteractableBlockAdder;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.compact.vs.constraint.OrientationOnVsConstraint;
import com.lancas.vswap.subproject.sandbox.compact.vs.constraint.SliderOnVsConstraint;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IBlockClusterDataReader;
import com.lancas.vswap.subproject.sandbox.constraint.OrientationConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.base.IConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.base.ISliderConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.SliderConstraint;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.ShipUtil;
import com.lancas.vswap.util.StrUtil;
import com.lancas.vswap.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.primitives.AABBd;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface IBreech {
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE
    )
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BreechRecord implements IBlockRecord {  //todo eject shell when remove
        private SavedBlockPos breechBp;
        private int maxFireColdDown = 0;
        private int fireColdDown = 0;
        private int loadColdDown = 0;
        private long inShipId = -1;
        private LazyTicks serverTickLazy = new LazyTicks(1);

        //ship data, att constraint uuid, ori constraint uuid, local forward dir
        //ship data: uuid, localDir, length
        public final List<TriTuple<BiTuple<UUID, Vector3i>, UUID, UUID>> loadedShipData = new ArrayList<>();
        //public final Set<Long> loadedVsShip = new HashSet<>();  //todo remove after hold or remove or fire
        private double loadedLen = 0;  //the len that loaded ships occupied in barrel or breech

        @JsonIgnore
        private final Consumer<ServerLevel> breechServerTick = l -> {
            if (fireColdDown > 0) {
                fireColdDown--;
                //EzDebug.log("colding down");
            }
            if (loadColdDown > 0) {
                loadColdDown--;
            }

            if (!serverTickLazy.shouldWork()) return;

            if (canLoad()) {
                Dest<BlockState> stateDest = new Dest<>();
                IBreech iBreech = WorldUtil.getBlockInterface(l, breechBp.toBp(), stateDest);
                if (iBreech == null) {
                    EzDebug.warn("can't get iBreech at " + breechBp.toBp().toShortString());
                    return;
                }

                Vector3d breechWorldPos = WorldUtil.getWorldCenter(l, breechBp.toBp());
                AABBd breechInnerBound = JomlUtil.dCenterExtended(breechWorldPos, 0.35);  //todo hell breech will reload multi times?
                //CreateClient.OUTLINER.showAABB("breechInner", JomlUtil.aabb(breechInnerBound)).lineWidth(1/16f);
                for (Ship ship : VSGameUtilsKt.getShipsIntersecting(l, breechInnerBound)) {
                    ServerShip sShip = (ServerShip)ship;

                    if (ship.getId() == inShipId || ConstraintsMgr.anyLoadedConstraintWith(l, ship.getId()) || ICanHoldShip.isShipHolden(sShip))
                        continue;

                    HoldableAttachment holdable = sShip.getAttachment(HoldableAttachment.class);
                    if (holdable == null)
                        continue;

                    Vector3dc worldHoldPivotCenter = WorldUtil.getWorldCenter(sShip, holdable.holdPivotBpInShip.toBp());
                    AABBd holdPivotBound = JomlUtil.dCenterExtended(worldHoldPivotCenter, 0.5);

                    //CreateClient.OUTLINER.showAABB("holdPivotBound", JomlUtil.aabb(holdPivotBound)).lineWidth(1/16f);

                    if (breechInnerBound.intersectsAABB(holdPivotBound)) {
                        iBreech.loadMunitionShip(l, breechBp.toBp(), stateDest.get(), sShip, false);
                        //loadedVsShip.add(sShip.getId());
                    }
                }
            }
        };

        private BreechRecord() { }
        public BreechRecord(ServerLevel level, BlockPos inBreechBp, int inMaxColdDown) {
            breechBp = new SavedBlockPos(inBreechBp);
            maxFireColdDown = inMaxColdDown;

            ServerShip inShip = ShipUtil.getServerShipAt(level, inBreechBp);
            if (inShip != null)
                inShipId = inShip.getId();
        }

        public boolean isCold() { return fireColdDown <= 0; }
        public void startColdDown() { fireColdDown = maxFireColdDown; }
        public void setLoadColdDown(int cd) { loadColdDown = cd; }
        public boolean canLoad() { return loadColdDown <= 0; }

        public boolean loadShip(ServerLevel level, HoldableAttachment holdable, @Nullable ServerShip artilleryShip, BlockPos breechBp, Direction breechBlockDir) {
            BlockClusterData saShipBlockData = new BlockClusterData();

            BlockPos startPos = holdable.getPivotBpInShip();
            BlockPos.MutableBlockPos curPos = startPos.mutable();
            while (true) {
                BlockState state = level.getBlockState(curPos);
                if (state.isAir()) break;

                Vector3i localPos = JomlUtil.i(curPos.subtract(startPos));
                saShipBlockData.setBlock(localPos, state);
                curPos.move(holdable.forwardInShip);
            }

            int loadingLen = saShipBlockData.getBlockCnt();
            if (loadingLen <= 0) return false;

            //make sa ship
            RigidbodyData rigidbodyData = new RigidbodyData();
            SandBoxServerShip saMunitionShip = new SandBoxServerShip(UUID.randomUUID(), rigidbodyData, saShipBlockData);

            SandBoxServerWorld saWorld = SandBoxServerWorld.getOrCreate(level);
            SandBoxServerWorld.addShip(level, saMunitionShip, true);

            //UUID munitionUuid = saMunitionShip.getUuid();
            //IServerSandBoxShip saArtilleryShip = artilleryShip == null ? saWorld.wrapOrGetGround() : saWorld.wrapOrGetVs(artilleryShip);
            ISliderConstraint attConstraint;
            if (artilleryShip == null) {
                attConstraint = new SliderConstraint(
                    UUID.randomUUID(), saWorld.wrapOrGetGround().getUuid(), saMunitionShip.getUuid(),
                    WorldUtil.getWorldCenter(level, breechBp), new Vector3d(),
                    JomlUtil.dNormal(breechBlockDir)
                );
            } else {
                attConstraint = new SliderOnVsConstraint(
                    UUID.randomUUID(), artilleryShip.getId(), saMunitionShip.getUuid(),
                    JomlUtil.dCenter(breechBp), new Vector3d(),
                    JomlUtil.dNormal(breechBlockDir)
                );
            }
            attConstraint.setFixedDistance(0.0);
            saWorld.getConstraintSolver().addConstraint(attConstraint);

            IConstraint oriConstraint;
            if (artilleryShip == null) {
                oriConstraint = new OrientationConstraint(
                    UUID.randomUUID(), saWorld.wrapOrGetGround().getUuid(), saMunitionShip.getUuid(),
                    HoldableAttachment.rotateForwardToDirection(breechBlockDir), HoldableAttachment.rotateForwardToDirection(holdable.forwardInShip)
                );
            } else {
                oriConstraint = new OrientationOnVsConstraint(
                    UUID.randomUUID(), artilleryShip.getId(), saMunitionShip.getUuid(),
                    HoldableAttachment.rotateForwardToDirection(breechBlockDir), HoldableAttachment.rotateForwardToDirection(holdable.forwardInShip)
                );
            }
            saWorld.getConstraintSolver().addConstraint(oriConstraint);

            for (int i = loadedShipData.size() - 1; i >= 0; --i) {
                ISliderConstraint curSliderConstraint = saWorld.getConstraintSolver().getConstraint(loadedShipData.get(i).getSecond());
                if (curSliderConstraint == null) {
                    EzDebug.warn("the constraint is null, may the ship is already removed, will remove this loaded ship");
                    loadedShipData.remove(i);
                    continue;
                }

                curSliderConstraint.addFixedDistance(loadingLen);
                EzDebug.warn("add loading len:" + loadingLen);
            }
            loadedShipData.add(new TriTuple<>(
                new BiTuple<>(saMunitionShip.getUuid(), JomlUtil.iNormal(holdable.forwardInShip)),
                attConstraint.getUuid(), oriConstraint.getUuid()
            ));
            loadedLen += loadingLen;
            return true;
        }
        public boolean loadDockerShip(ServerLevel level, ItemStack stack, @Nullable ServerShip artilleryShip, BlockPos breechBp, Direction breechBlockDir) {
            if (!(stack.getItem() instanceof Docker))
                return false;

            //todo testing
            RRWChunkyShipSchemeData shipData = Docker.getShipData(stack);
            if (shipData == null) {
                EzDebug.warn("beech load docker ship fail because get null shipData");
                return false;
            }

            AtomicReference<HoldableAttachment> holdableAtt = new AtomicReference<>(null);
            //Vector3i startPos = new Vector3i();

            BlockClusterData blockData = shipData.createSaShipBlockData((bData, att, offsetBps) -> {
                if (!(att instanceof HoldableAttachment holdable)) return;

                offsetBps.findFirst().ifPresentOrElse(
                    pivot -> {
                        bData.moveAll(pivot.negate(new Vector3i()));  //move blocks so that pivot will be at (0, 0, 0)
                        //startPos.set(pivot);
                        holdableAtt.set(holdable);
                    },
                    () -> EzDebug.warn("get holdable but can't find pivot, also holdableAtt wouldn't be set")
                );
            });

            if (holdableAtt.get() == null) {
                EzDebug.warn("can't load because holdableAtt is null");
                return false;
            }

            //Vector3ic localDirV = docker.getLocalHoldForward(stack);
            //Vector3ic startPos = docker.getLocalPivot(stack);
            //todo temp
            Vector3ic startPos = new Vector3i(0, 0, 0);  //pivot is at (0, 0, 0) so startPos is (0, 0, 0)
            Direction localDir = holdableAtt.get().forwardInShip;

            BlockPos.MutableBlockPos curPos = new BlockPos.MutableBlockPos(startPos.x(), startPos.y(), startPos.z());

            shipData.foreachBlockInLocal((blockPos, state) -> {
                EzDebug.warn("bp:" + StrUtil.poslike(blockPos) + ", block:" + StrUtil.getBlockName(state));
            });

            /*while (true) {
                BlockState state = shipDataReader.getBlockStateByLocalPos(curPos);
                //EzDebug.log("curPos:" + StrUtil.poslike(curPos) + ", get state:" + StrUtil.getBlockName(state));
                if (state.isAir()) break;

                Vector3i localPos = new Vector3i(curPos.getX() - startPos.x(), curPos.getY() - startPos.y(), curPos.getZ() - startPos.z());
                saShipBlockData.setBlock(localPos, state);
                curPos.move(localDir);
            }

            int loadingLen = saShipBlockData.getBlockCnt();
            if (loadingLen <= 0) {
                EzDebug.warn("try to load a 0 length ship!");
                return false;
            }*/

            //make sa ship
            RigidbodyData rigidbodyData = new RigidbodyData();
            SandBoxServerShip saMunitionShip = new SandBoxServerShip(UUID.randomUUID(), rigidbodyData, blockData);

            SandBoxServerWorld saWorld = SandBoxServerWorld.getOrCreate(level);
            SandBoxServerWorld.addShip(level, saMunitionShip, true);

            ISliderConstraint attConstraint;
            if (artilleryShip == null) {
                attConstraint = new SliderConstraint(
                    UUID.randomUUID(), saWorld.wrapOrGetGround().getUuid(), saMunitionShip.getUuid(),
                    WorldUtil.getWorldCenter(level, breechBp), new Vector3d(),
                    JomlUtil.dNormal(breechBlockDir)
                );
            } else {
                attConstraint = new SliderOnVsConstraint(
                    UUID.randomUUID(), artilleryShip.getId(), saMunitionShip.getUuid(),
                    JomlUtil.dCenter(breechBp), new Vector3d(),
                    JomlUtil.dNormal(breechBlockDir)
                );
            }
            attConstraint.setFixedDistance(0.0);
            saWorld.getConstraintSolver().addConstraint(attConstraint);

            IConstraint oriConstraint;
            if (artilleryShip == null) {
                oriConstraint = new OrientationConstraint(
                    UUID.randomUUID(), saWorld.wrapOrGetGround().getUuid(), saMunitionShip.getUuid(),
                    HoldableAttachment.rotateForwardToDirection(breechBlockDir), HoldableAttachment.rotateForwardToDirection(localDir)
                );
            } else {
                oriConstraint = new OrientationOnVsConstraint(
                    UUID.randomUUID(), artilleryShip.getId(), saMunitionShip.getUuid(),
                    HoldableAttachment.rotateForwardToDirection(breechBlockDir), HoldableAttachment.rotateForwardToDirection(localDir)
                );
            }
            saWorld.getConstraintSolver().addConstraint(oriConstraint);

            int loadingLen = blockData.getBlockCnt();  //todo is blockCnt of blockData the len?
            for (int i = loadedShipData.size() - 1; i >= 0; --i) {
                ISliderConstraint curSliderConstraint = saWorld.getConstraintSolver().getConstraint(loadedShipData.get(i).getSecond());
                if (curSliderConstraint == null) {
                    EzDebug.warn("the constraint is null, may the ship is already removed, will remove this loaded ship");
                    loadedShipData.remove(i);
                    continue;
                }

                curSliderConstraint.addFixedDistance(loadingLen);
                EzDebug.warn("add loading len:" + loadingLen);
            }
            loadedShipData.add(new TriTuple<>(
                new BiTuple<>(saMunitionShip.getUuid(), JomlUtil.iNormal(localDir)),
                attConstraint.getUuid(), oriConstraint.getUuid()
            ));
            loadedLen += loadingLen;
            return true;
        }


        /*public ChunkLoadedTicker chunkLoadedTicker() {
            return (level, bp) ->  {
                if (coldDown > 0) coldDown--;
            };
        }*/

        @Override
        public void onAdded(BlockPos bp, BlockRecordRWMgr mgr) {
            mgr.events.addChunkLoadedServerTicker(bp, breechServerTick);
        }
        @Override
        public void onRemoved(BlockPos bp, BlockRecordRWMgr mgr) {
            mgr.events.removeChunkLoadedServerTicker(bp, breechServerTick);
        }
    }


    //public boolean getLoadedMunitionData(Level level, BlockPos breechBp, Dest<Ship> munitionShip, Dest<Boolean> isTriggered, Dest<Direction> munitionDirInShip);
    public boolean canLoadDockerNow(Level level, BlockPos breechBp, ItemStack stack);
    public void loadMunition(ServerLevel level, BlockPos breechBp, BlockState breechState, ItemStack munitionDocker);
    public void loadMunitionShip(ServerLevel level, BlockPos breechBp, BlockState breechState, ServerShip vsShip, boolean simulate);

    //public void ejectShell(Level level, BlockPos breechBp);
    //public Set<BlockPos> findBarrelWithBreechPoses(Level level, BlockPos breechPos, BlockState breechState);

    //todo make sure eject shell is success
    /*public static void ejectShell(ServerLevel level, ServerShip munitionShip, Direction munitionDirInShip, Vector3dc breechWorldPos) {
        //eject the shell
        //Direction primerBackward = primerDir.getOpposite();
        //Vector3d worldPrimerBackward = primerShip.getTransform().getShipToWorldRotation().transform(JomlUtil.dNormal(primerBackward));

        ShipBuilder.modify(level, munitionShip)
            .moveFaceTo(munitionDirInShip, breechWorldPos)
            .setLocalVelocity(JomlUtil.dNormal(munitionDirInShip, -20));
    }*/
    //public void unloadShell(ServerLevel level, BlockPos breechBp, Direction shellDirInShip, BlockPos breechBp);
    public void unloadShell(ServerLevel level, BlockPos breechBp, BlockState breechState);

    //old breech interaction apply constraint on vs ship
    /*public static InteractableBlockAdder hellBreechInteraction() {
        return new InteractableBlockAdder() {
            @Override
            public InteractionResult onInteracted(BlockState breechState, Level level, BlockPos breechBp, Player player, InteractionHand hand, BlockHitResult hit) {
                //todo sometime(in face always) repeat invoke
                if (!(level instanceof ServerLevel sLevel)) return InteractionResult.PASS;

                ICanHoldShip icanHoldShip = (ICanHoldShip)player;
                Dest<Long> holdingShipId = new Dest<>();
                icanHoldShip.getHoldingShipId(ShipHoldSlot.MainHand, holdingShipId);

                ServerShip holdingShip = ShipUtil.getServerShipByID(sLevel, holdingShipId.get());
                if (holdingShip == null) return InteractionResult.PASS;  //not holding ship
                var munitionHoldable = holdingShip.getAttachment(HoldableAttachment.class);
                if (munitionHoldable == null) return InteractionResult.PASS;  //no holdable

                AtomicBoolean shouldLoad = new AtomicBoolean(false);
                AtomicReference<BlockPos> primerBp = new AtomicReference<>(null);
                //todo not foreach ship
                ShipBuilder.modify(sLevel, holdingShip).foreachBlock((posInShip, stateInShip, blockEntity) -> {
                    if (shouldLoad.get()) return;

                    if (stateInShip.getBlock() instanceof PrimerBlock primer) {  //todo not support other primer now
                        if (!primer.isTriggered(stateInShip)) {
                            shouldLoad.set(true);
                            primerBp.set(posInShip);
                        }
                    }
                });
                if (!shouldLoad.get()) return InteractionResult.PASS;

                @Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(sLevel, breechBp);

                Direction breechDir = DirectionAdder.getDirection(breechState);  //todo use IBreech to get breech dir?
                if (breechDir == null) {
                    EzDebug.fatal("can not get direction of breech");
                    return InteractionResult.FAIL;
                }

                PrimerBlock.createConstraints(sLevel, primerBp.get(), artilleryShip, holdingShip, breechBp, breechDir, munitionHoldable);
                icanHoldShip.unholdShipInServer(ShipHoldSlot.MainHand, true, null);
                return InteractionResult.PASS;
            }
        };
    }*/
    public static InteractableBlockAdder breechInteraction() {
        return new InteractableBlockAdder() {
            @Override
            public InteractionResult onInteracted(BlockState breechState, Level level, BlockPos breechBp, Player player, InteractionHand hand, BlockHitResult hit) {
                //todo sometime(in face always) repeat invoke
                if (!(level instanceof ServerLevel sLevel)) return InteractionResult.PASS;
                if (!(breechState.getBlock() instanceof IBreech iBreech)) {
                    EzDebug.warn("the breech at " + breechBp.toShortString() + " is not a breech");
                    return InteractionResult.PASS;
                }

                ICanHoldShip icanHoldShip = (ICanHoldShip)player;
                Dest<Long> holdingShipId = new Dest<>();
                icanHoldShip.getHoldingShipId(ShipHoldSlot.MainHand, holdingShipId);

                ServerShip holdingShip = ShipUtil.getServerShipByID(sLevel, holdingShipId.get());
                if (holdingShip == null) return InteractionResult.PASS;  //not holding ship
                var munitionHoldable = holdingShip.getAttachment(HoldableAttachment.class);
                if (munitionHoldable == null) return InteractionResult.PASS;  //no holdable

                /*BlockClusterData saShipBlockData = new BlockClusterData();

                AtomicBoolean shouldLoad = new AtomicBoolean(false);
                AtomicReference<BlockPos> primerBp = new AtomicReference<>(null);
                //todo not foreach ship
                ShipBuilder.modify(sLevel, holdingShip).foreachBlock((posInShip, state, blockEntity) -> {
                    //if (shouldLoad.get()) return;

                    if (state.getBlock() instanceof PrimerBlock primer) {  //todo not support other primer now
                        if (!primer.isTriggered(state)) {
                            shouldLoad.set(true);
                            primerBp.set(posInShip);
                        }
                    }

                    saShipBlockData.setBlock(JomlUtil.i(posInShip.subtract(munitionHoldable.getPivotBpInShip())), state);
                });
                if (!shouldLoad.get()) return InteractionResult.PASS;*/

                @Nullable ServerShip artilleryShip = ShipUtil.getServerShipAt(sLevel, breechBp);

                /*Direction breechDir = DirectionAdder.getDirection(breechState);  //todo use IBreech to get breech dir?
                if (breechDir == null) {
                    EzDebug.fatal("can not get direction of breech");
                    return InteractionResult.FAIL;
                }*/
                /*Direction breechBlockDir = Objects.requireNonNull(DirectionAdder.getDirection(breechState));
                Vector3dc breechWorldDir = WorldUtil.getWorldDirection(sLevel, breechBp, breechBlockDir);

                //PrimerBlock.createConstraints(sLevel, primerBp.get(), artilleryShip, holdingShip, breechBp, breechDir, munitionHoldable);
                icanHoldShip.unholdShipInServer(ShipHoldSlot.MainHand, true, null);

                //remove the ship
                VSGameUtilsKt.getShipObjectWorld(sLevel).deleteShip(holdingShip);

                //make sa ship
                RigidbodyData rigidbodyData = new RigidbodyData();
                SandBoxServerShip saMunitionShip = new SandBoxServerShip(UUID.randomUUID(), rigidbodyData, saShipBlockData);

                SandBoxServerWorld saWorld = SandBoxServerWorld.getOrCreate(sLevel);
                SandBoxServerWorld.addShipAndSyncClient(sLevel, saMunitionShip);

                //UUID munitionUuid = saMunitionShip.getUuid();
                //IServerSandBoxShip saArtilleryShip = artilleryShip == null ? saWorld.wrapOrGetGround() : saWorld.wrapOrGetVs(artilleryShip);
                ISliderConstraint<?> attConstraint;
                if (artilleryShip == null) {
                    attConstraint = new SliderConstraint(
                        UUID.randomUUID(), saWorld.wrapOrGetGround().getUuid(), saMunitionShip.getUuid(),
                        WorldUtil.getWorldCenter(sLevel, breechBp), new Vector3d(),
                        breechWorldDir
                    );
                } else {
                    attConstraint = new SliderOnVsConstraint(
                        UUID.randomUUID(), artilleryShip.getId(), saMunitionShip.getUuid(),
                        JomlUtil.dCenter(breechBp), new Vector3d(),
                        JomlUtil.dNormal(breechBlockDir)
                    );
                }
                attConstraint.setFixedDistance(0.0);
                saWorld.getConstraintSolver().addConstraint(attConstraint);

                IConstraint<?> oriConstraint;
                if (artilleryShip == null) {
                    oriConstraint = new OrientationConstraint(
                        UUID.randomUUID(), saWorld.wrapOrGetGround().getUuid(), saMunitionShip.getUuid(),
                        HoldableAttachment.rotateForwardToDirection(breechBlockDir), HoldableAttachment.rotateForwardToDirection(munitionHoldable.forwardInShip)
                    );
                } else {
                    oriConstraint = new OrientationOnVsConstraint(
                        UUID.randomUUID(), artilleryShip.getId(), saMunitionShip.getUuid(),
                        HoldableAttachment.rotateForwardToDirection(breechBlockDir), HoldableAttachment.rotateForwardToDirection(munitionHoldable.forwardInShip)
                    );
                }
                saWorld.getConstraintSolver().addConstraint(oriConstraint);*/
                /*if (artilleryShip != null) {
                    EzDebug.log("shipToWorld:\n" + artilleryShip.getTransform().getShipToWorld() + "\nworldPos:" + artilleryShip.getTransform().getPositionInWorld() + "\nshipPos:" + artilleryShip.getTransform().getPositionInShip());
                }*/

                BreechRecord record = BlockRecordRWMgr.getRecord(sLevel, breechBp);
                if (record == null) {
                    EzDebug.warn("can't find record at " + breechBp.toShortString());
                    return InteractionResult.PASS;
                }

                boolean loaded = record.loadShip(sLevel, munitionHoldable, artilleryShip, breechBp, DirectionAdder.getDirection(breechState));
                if (loaded) {
                    icanHoldShip.unholdShipInServer(ShipHoldSlot.MainHand, true, null);
                    VSGameUtilsKt.getShipObjectWorld(sLevel).deleteShip(holdingShip);
                }
                return InteractionResult.PASS;
            }
        };
    }

    public static boolean foreachMunition(ServerLevel level, BlockPos breechBp, Vector3ic projectileAppendDir, boolean simulate, Dest<Double> totPropellingEngDest, BlockClusterData projectileBlockData) {
        BreechRecord record = BlockRecordRWMgr.getRecord(level, breechBp);
        if (record == null) {
            EzDebug.warn("[foreachMunition] can't get record at " + breechBp.toShortString());
            return false;
        }

        double totPropellingEng = 0;
        boolean seekPropellants = true;

        var saWorld = SandBoxServerWorld.getOrCreate(level);
        Vector3i curProjectileAppendLocalPos = new Vector3i();
        boolean first = true;
        Stream<BiTuple<Vector3ic, BlockState>> munitionStream = Stream.empty();
        var loadedShipData = record.loadedShipData;

        for (int i = record.loadedShipData.size() - 1; i >= 0; --i) {
            var curData = record.loadedShipData.get(i);
            var shipData = curData.getFirst();
            UUID shipUuid = shipData.getFirst();
            ISandBoxShip ship = saWorld.getShip(shipUuid);
            if (ship == null) {
                EzDebug.warn("there is a null ship inside breech, maybe removed by commands");
                loadedShipData.remove(i);
                continue;
            }

            IBlockClusterDataReader curShipBlockData = ship.getBlockCluster().getDataReader();

            Direction projectileBlockDir = JomlUtil.nearestDir(projectileAppendDir);
            Vector3ic curDir = shipData.getSecond();
            Vector3i curLocalPos = new Vector3i();

            if (first) {
                BlockState primerState = curShipBlockData.getBlockState(new Vector3i());
                if (!(primerState.getBlock() instanceof IPrimer primer)) {
                    //there is no primer
                    EzDebug.warn("fail to foreach because munition has no primer");
                    return false;
                }  else {
                    if (!simulate) {
                        ship.getBlockCluster().getDataWriter().setBlock(new Vector3i(), primer.getTriggeredState(primerState));
                    }
                }

                curLocalPos.add(curDir);  //skip first primer if first (first set false at tail of loop)
            }

            while (seekPropellants) {
                BlockState state = curShipBlockData.getBlockState(curLocalPos);
                if (!(state.getBlock() instanceof IPropellant propellant)) {
                    EzDebug.log("seeking propellant and find " + StrUtil.getBlockName(state) + " at " + StrUtil.poslike(curLocalPos));

                    if (!state.isAir()) seekPropellants = false;

                    break;
                }

                totPropellingEng += propellant.getEnergy(state);
                if (!simulate) {
                    ship.getBlockCluster().getDataWriter().setBlock(curLocalPos, propellant.getEmptyState(state));
                }
                curLocalPos.add(curDir);
            }
            //don't seeking propellants
            if (!seekPropellants) {
                while (true) {
                    BlockState state = curShipBlockData.getBlockState(curLocalPos);
                    if (state.isAir()) {
                        EzDebug.log("seeking projectile and find " + StrUtil.getBlockName(state) + " at " + StrUtil.poslike(curLocalPos));
                        break;
                    }

                    BlockState toPutState = state;
                    if (toPutState.hasProperty(DirectionAdder.FACING)) {
                        toPutState = state.setValue(DirectionAdder.FACING, projectileBlockDir);
                    }
                    projectileBlockData.setBlock(curProjectileAppendLocalPos, toPutState);
                    curProjectileAppendLocalPos.add(projectileAppendDir);
                    if (!simulate) {
                        ship.getBlockCluster().getDataWriter().setBlock(curLocalPos, Blocks.AIR.defaultBlockState());
                    }
                    curLocalPos.add(curDir);
                }
            }

            first = false;
        }
        totPropellingEngDest.set(totPropellingEng);
        if (first) return false;  //didn't iter any ship
        return true;
    }
    public static void ejectAllMunition(ServerLevel level, BlockPos breechBp, Supplier<Vector3dc> randSpawnPosGetter, Supplier<Vector3dc> randDeltaMoveGetter, boolean clear) {
        BreechRecord record = BlockRecordRWMgr.getRecord(level, breechBp);
        if (record == null) {
            EzDebug.warn("can't get record");
            return;
        }

        SandBoxServerWorld saWorld = SandBoxServerWorld.getOrCreate(level);
        record.loadedShipData.stream().map(x -> saWorld.getShip(x.getFirst().getFirst())).filter(Objects::nonNull).forEach(s -> {
            if (s.getBlockCluster().getDataReader().getBlockCnt() <= 0) return;

            ItemStack shellStack = Docker.stackOfSa(level, s);

            Vector3dc spawnPos = randSpawnPosGetter.get();
            Vector3dc deltaMove = randDeltaMoveGetter.get();

            ItemEntity itemE = new ItemEntity(level, spawnPos.x(), spawnPos.y(), spawnPos.z(), shellStack);
            itemE.setDeltaMovement(deltaMove.x(), deltaMove.y(), deltaMove.z());
            level.addFreshEntity(itemE);
        });

        if (clear)
            clearLoadedMunition(level, breechBp);
    }
    public static void clearLoadedMunition(ServerLevel level, BlockPos breechBp) {
        BreechRecord record = BlockRecordRWMgr.getRecord(level, breechBp);
        if (record == null) {
            EzDebug.warn("[clearLoadedMunition] can't get record at " + breechBp.toShortString());
            return;
        }

        var saWorld = SandBoxServerWorld.getOrCreate(level);
        record.loadedShipData.forEach(shipData -> saWorld.markShipDeleted(shipData.getFirst().getFirst()));  //constraints will auto removed by event
        record.loadedShipData.clear();
    }
}
