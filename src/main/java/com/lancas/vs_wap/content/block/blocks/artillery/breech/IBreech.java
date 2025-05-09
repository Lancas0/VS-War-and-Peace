package com.lancas.vs_wap.content.block.blocks.artillery.breech;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vs_wap.content.block.blocks.cartridge.IPrimer;
import com.lancas.vs_wap.content.block.blocks.cartridge.propellant.IPropellant;
import com.lancas.vs_wap.content.saved.BlockRecordRWMgr;
import com.lancas.vs_wap.content.saved.IBlockRecord;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.foundation.TriTuple;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.ship.attachment.HoldableAttachment;
import com.lancas.vs_wap.ship.feature.hold.ICanHoldShip;
import com.lancas.vs_wap.ship.feature.hold.ShipHoldSlot;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.InteractableBlockAdder;
import com.lancas.vs_wap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vs_wap.subproject.sandbox.compact.vs.constraint.OrientationOnVsConstraint;
import com.lancas.vs_wap.subproject.sandbox.compact.vs.constraint.SliderOnVsConstraint;
import com.lancas.vs_wap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vs_wap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vs_wap.subproject.sandbox.component.data.reader.IBlockClusterDataReader;
import com.lancas.vs_wap.subproject.sandbox.constraint.OrientationConstraint;
import com.lancas.vs_wap.subproject.sandbox.constraint.base.IConstraint;
import com.lancas.vs_wap.subproject.sandbox.constraint.base.ISliderConstraint;
import com.lancas.vs_wap.subproject.sandbox.constraint.SliderConstraint;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShipUtil;
import com.lancas.vs_wap.util.StrUtil;
import com.lancas.vs_wap.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface IBreech {
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE
    )
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BreechRecord implements IBlockRecord {
        //private BlockPos breechPos;
        private int maxColdDown = 0;
        private int coldDown = 0;

        //ship data, att constraint uuid, ori constraint uuid, local forward dir
        //ship data: uuid, localDir, length
        public final List<TriTuple<BiTuple<UUID, Vector3ic>, UUID, UUID>> loadedShipData = new ArrayList<>();
        private double loadedLen = 0;  //the len that loaded ships occupied in barrel or breech

        @JsonIgnore
        private final Consumer<ServerLevel> coldDownTicker = l -> {
            if (coldDown > 0) {
                coldDown--;
                EzDebug.log("colding down");
            }
        };

        private BreechRecord() { }
        public BreechRecord(int inMaxColdDown) { /*breechPos = inBreechPos;*/ maxColdDown = inMaxColdDown; }

        public boolean isCold() { return coldDown <= 0; }
        public void startColdDown() { coldDown = maxColdDown; }
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
            SandBoxServerWorld.addShipAndSyncClient(level, saMunitionShip);

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


        /*public ChunkLoadedTicker chunkLoadedTicker() {
            return (level, bp) ->  {
                if (coldDown > 0) coldDown--;
            };
        }*/

        @Override
        public void onAdded(BlockPos bp, BlockRecordRWMgr mgr) {
            mgr.events.addChunkLoadedServerTicker(bp, coldDownTicker);
        }
        @Override
        public void onRemoved(BlockPos bp, BlockRecordRWMgr mgr) {
            mgr.events.removeChunkLoadedServerTicker(bp, coldDownTicker);
        }
    }


    public boolean getLoadedMunitionData(Level level, BlockPos breechBp, Dest<Ship> munitionShip, Dest<Boolean> isTriggered, Dest<Direction> munitionDirInShip);
    public boolean isDockerLoadable(Level level, BlockPos breechBp, ItemStack stack);
    public void loadMunition(ServerLevel level, BlockPos breechBp, BlockState breechState, ItemStack munitionDocker);

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
    public void unloadShell(ServerLevel level, ServerShip shellShip, Direction shellDirInShip, BlockPos breechBp);

    //old breech interaction apply constraint on vs ship
    /*public static InteractableBlockAdder breechInteraction() {
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
            EzDebug.warn("can't get record at " + breechBp.toShortString());
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
                        ship.getBlockCluster().setBlock(new Vector3i(), primer.getTriggeredState(primerState));
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
                    ship.getBlockCluster().setBlock(curLocalPos, propellant.getEmptyState(state));
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

                    projectileBlockData.setBlock(curProjectileAppendLocalPos, state);
                    curProjectileAppendLocalPos.add(projectileAppendDir);
                    if (!simulate) {
                        ship.getBlockCluster().setBlock(curLocalPos, Blocks.AIR.defaultBlockState());
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
    public static void clearLoadedMunition(ServerLevel level, BlockPos breechBp) {
        BreechRecord record = BlockRecordRWMgr.getRecord(level, breechBp);
        if (record == null) {
            EzDebug.warn("can't get record at " + breechBp.toShortString());
            return;
        }

        var saWorld = SandBoxServerWorld.getOrCreate(level);
        record.loadedShipData.forEach(shipData -> saWorld.markShipDeleted(shipData.getFirst().getFirst()));  //constraints will auto removed by event
        record.loadedShipData.clear();
    }
}
