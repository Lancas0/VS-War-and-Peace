package com.lancas.vswap.content.block.blocks.artillery.breech;

import com.lancas.vswap.content.block.blocks.cartridge.primer.IPrimer;
import com.lancas.vswap.content.block.blocks.cartridge.propellant.IPropellant;
import com.lancas.vswap.content.saved.blockrecord.BlockRecordRWMgr;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IBlockClusterDataReader;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.StrUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.*;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.*;

public interface IBreech {
    public static final Direction LOADED_MUNITION_DIRECTION = Direction.SOUTH;
    public static final Vector3ic LOADED_MUNITION_FORWARD = new Vector3i(
        LOADED_MUNITION_DIRECTION.getStepX(),
        LOADED_MUNITION_DIRECTION.getStepY(),
        LOADED_MUNITION_DIRECTION.getStepZ()
    );
    public static final Vector3dc LOADED_MUNITION_FORWARD_D = new Vector3d(LOADED_MUNITION_FORWARD);
    public static final Vector3ic LOADED_MUNITION_ORIGIN = new Vector3i(0, 0, 0);
    public static final Vector3dc LOADED_MUNITION_ORIGIN_D = new Vector3d(LOADED_MUNITION_ORIGIN);


    /*@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE
    )
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record LoadedShipData(UUID shipUuid, UUID constraintUuid) {
        private LoadedShipData() { this(null, null); }

        public SandBoxServerShip getShip(ServerLevel level) { return SandBoxServerWorld.getOrCreate(level).getServerShip(shipUuid); }
        public LoadedMunitionConstraint getConstraint(ServerLevel level) { return SandBoxServerWorld.getOrCreate(level).getConstraintSolver().getConstraint(constraintUuid); }
    }

    public static class LoadedMunitionConstraint extends BiCompoundConstraint {
        private LoadedMunitionConstraint() { super(null, null, null); }
        public LoadedMunitionConstraint(UUID inSelfUuid, ISliderConstraint inC1, IOrientationConstraint inC2) {
            super(inSelfUuid, inC1, inC2);
        }
        public static LoadedMunitionConstraint onGround(ServerLevel level, UUID munitionUuid, BlockPos attachOn, Direction attachDir) {
            SandBoxServerWorld saWorld = SandBoxServerWorld.getOrCreate(level);

            SliderConstraint slider = new SliderConstraint(
                UUID.randomUUID(), saWorld.wrapOrGetGround().getUuid(), munitionUuid,
                JomlUtil.dCenter(attachOn), LOADED_MUNITION_ORIGIN_D,
                JomlUtil.dNormal(attachDir)
            );
            OrientationConstraint ori = new OrientationConstraint(
                UUID.randomUUID(), saWorld.wrapOrGetGround().getUuid(), munitionUuid,
                HoldableAttachment.rotateForwardToDirection(attachDir), HoldableAttachment.rotateForwardToDirection(LOADED_MUNITION_DIRECTION)
            );

            return new LoadedMunitionConstraint(UUID.randomUUID(), slider, ori);
        }
        public static LoadedMunitionConstraint onVsShip(ServerLevel level, long attachOnVsId, UUID munitionUuid, BlockPos attachOn, Direction attachDir) {
            SandBoxServerWorld saWorld = SandBoxServerWorld.getOrCreate(level);

            SliderOnVsConstraint slider = new SliderOnVsConstraint(
                UUID.randomUUID(), attachOnVsId, munitionUuid,
                JomlUtil.dCenter(attachOn), LOADED_MUNITION_ORIGIN_D,
                JomlUtil.dNormal(attachDir)
            );
            OrientationOnVsConstraint ori = new OrientationOnVsConstraint(
                UUID.randomUUID(), attachOnVsId, munitionUuid,
                HoldableAttachment.rotateForwardToDirection(attachDir), HoldableAttachment.rotateForwardToDirection(LOADED_MUNITION_DIRECTION)
            );

            return new LoadedMunitionConstraint(UUID.randomUUID(), slider, ori);
        }
        //public LoadedMunitionConstraint(UUID inSelfUuid, UUID attachOnUuid, UUID munitionUuid, )

        public ISliderConstraint getSliderConstraint() { return (ISliderConstraint)super.getFirst(); }
        public IOrientationConstraint getOrientationConstraint() { return (IOrientationConstraint)super.getSecond(); }
    }*/

    /*@JsonAutoDetect(
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
        public final List<LoadedShipData> loadedData = new ArrayList<>();
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

            //todo foreach for primer
            BlockPos startPos = holdable.getPivotBpInShip();

            BlockState primerState = level.getBlockState(startPos);
            if (!(primerState.getBlock() instanceof IPrimer)) {
                EzDebug.log("[should send msg]The hold pos must be primer");
                return false;
            }

            Vector3i munitionLocPos = new Vector3i(LOADED_MUNITION_ORIGIN);
            Direction primerDir = primerState.getValue(DirectionAdder.FACING);
            BlockPos.MutableBlockPos curPos = startPos.mutable();
            while (true) {
                BlockState state = level.getBlockState(curPos);
                if (state.isAir()) break;

                //Vector3i localPos = JomlUtil.i(curPos.subtract(startPos));
                saShipBlockData.setBlock(munitionLocPos, state.trySetValue(BlockStateProperties.FACING, LOADED_MUNITION_DIRECTION));
                curPos.move(primerDir);
                munitionLocPos.add(LOADED_MUNITION_FORWARD);
                //curPos.move(holdable.forwardInShip);
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
                    JomlUtil.dCenter(breechBp), LOADED_MUNITION_ORIGIN_D,
                    JomlUtil.dNormal(breechBlockDir)
                );
            } else {
                attConstraint = new SliderOnVsConstraint(
                    UUID.randomUUID(), artilleryShip.getId(), saMunitionShip.getUuid(),
                    JomlUtil.dCenter(breechBp), LOADED_MUNITION_ORIGIN_D,
                    JomlUtil.dNormal(breechBlockDir)
                );
            }
            attConstraint.setFixedDistance(0.0);
            //saWorld.getConstraintSolver().addConstraint(attConstraint);

            IOrientationConstraint oriConstraint;
            if (artilleryShip == null) {
                oriConstraint = new OrientationConstraint(
                    UUID.randomUUID(), saWorld.wrapOrGetGround().getUuid(), saMunitionShip.getUuid(),
                    HoldableAttachment.rotateForwardToDirection(breechBlockDir), HoldableAttachment.rotateForwardToDirection(LOADED_MUNITION_DIRECTION)
                    //HoldableAttachment.rotateForwardToDirection(breechBlockDir), HoldableAttachment.rotateForwardToDirection(holdable.forwardInShip)
                );
            } else {
                oriConstraint = new OrientationOnVsConstraint(
                    UUID.randomUUID(), artilleryShip.getId(), saMunitionShip.getUuid(),
                    HoldableAttachment.rotateForwardToDirection(breechBlockDir), HoldableAttachment.rotateForwardToDirection(LOADED_MUNITION_DIRECTION)
                    //HoldableAttachment.rotateForwardToDirection(breechBlockDir), HoldableAttachment.rotateForwardToDirection(holdable.forwardInShip)
                );
            }
            LoadedMunitionConstraint newLoadedConstraint = new LoadedMunitionConstraint(UUID.randomUUID(), attConstraint, oriConstraint);
            saWorld.getConstraintSolver().addConstraint(newLoadedConstraint);
            //saWorld.getConstraintSolver().addConstraint(oriConstraint);

            for (int i = loadedData.size() - 1; i >= 0; --i) {
                LoadedMunitionConstraint c = saWorld.getConstraintSolver().getConstraint(loadedData.get(i).constraintUuid());
                if (c == null) {
                    EzDebug.warn("the constraint is null, may the ship is already removed, will remove this loaded ship");
                    loadedData.remove(i);
                    continue;
                }

                c.getSliderConstraint().addFixedDistance(loadingLen);
                EzDebug.warn("add loading len:" + loadingLen);
            }


            loadedData.add(new LoadedShipData(saMunitionShip.getUuid(), newLoadedConstraint.getUuid()));
            /*loadedData.add(new TriTuple<>(
                new BiTuple<>(saMunitionShip.getUuid(), JomlUtil.iNormal(holdable.forwardInShip)),
                attConstraint.getUuid(), oriConstraint.getUuid()
            ));*./
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

            //AtomicReference<HoldableAttachment> holdableAtt = new AtomicReference<>(null);
            AtomicReference<BlockPos> savedLocalPivotBp = new AtomicReference<>(null);
            shipData.withSavedAttachmentDo(HoldableAttachment.class, (att, localPoses) -> {
                Vector3ic savedLocalPivot = att.getSavedLocalPivot(localPoses);
                if (savedLocalPivot == null)
                    return;

                savedLocalPivotBp.set(JomlUtil.bp(savedLocalPivot));
            });

            if (savedLocalPivotBp.get() == null)
                return false;

            BlockState primerState = shipData.getBlockStateByLocalPos(savedLocalPivotBp.get());
            if (!(primerState.getBlock() instanceof IPrimer primer))
                return false;

            BlockClusterData blockData = new BlockClusterData();
            Vector3i curPos = new Vector3i(LOADED_MUNITION_ORIGIN);
            BlockPos.MutableBlockPos savedCurBp = savedLocalPivotBp.get().mutable();
            Direction primerDir = primerState.getValue(DirectionAdder.FACING);
            while (true) {
                BlockState curSavedState = shipData.getBlockStateByLocalPos(savedCurBp);
                if (curSavedState.isAir())
                    break;

                blockData.setBlock(curPos, curSavedState.trySetValue(DirectionAdder.FACING, LOADED_MUNITION_DIRECTION));

                curPos.add(LOADED_MUNITION_FORWARD);
                savedCurBp.move(primerDir);
            }

            if (blockData.getBlockCnt() <= 0)
                return false;

            //Vector3i startPos = new Vector3i();

            /*BlockClusterData blockData = shipData.createSaShipBlockData((bData, att, offsetBps) -> {
                if (!(att instanceof HoldableAttachment holdable)) return;

                offsetBps.findFirst().ifPresentOrElse(
                    pivot -> {
                        bData.moveAll(pivot.negate(new Vector3i()));  //move blocks so that pivot will be at (0, 0, 0)
                        //startPos.set(pivot);
                        holdableAtt.set(holdable);
                    },
                    () -> EzDebug.warn("get holdable but can't find pivot, also holdableAtt wouldn't be set")
                );
            });*./

            //Vector3ic localDirV = docker.getLocalHoldForward(stack);
            //Vector3ic startPos = docker.getLocalPivot(stack);
            //todo temp
            /*Vector3ic startPos = new Vector3i(LOADED_MUNITION_ORIGIN);  //pivot is at (0, 0, 0) so startPos is (0, 0, 0)
            //Direction localDir = holdableAtt.get().forwardInShip;

            BlockPos.MutableBlockPos curPos = new BlockPos.MutableBlockPos(startPos.x(), startPos.y(), startPos.z());

            shipData.foreachBlockInLocal((blockPos, state) -> {
                EzDebug.warn("bp:" + StrUtil.poslike(blockPos) + ", block:" + StrUtil.getBlockName(state));
            });*./

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
            }*./

            //make sa ship
            //FIXME this region is tempory removed
            RigidbodyData rigidbodyData = new RigidbodyData();
            SandBoxServerShip saMunitionShip = new SandBoxServerShip(UUID.randomUUID(), rigidbodyData, blockData);

            SandBoxServerWorld saWorld = SandBoxServerWorld.getOrCreate(level);
            SandBoxServerWorld.addShip(level, saMunitionShip, true);

            ISliderConstraint attConstraint;
            if (artilleryShip == null) {
                attConstraint = new SliderConstraint(
                    UUID.randomUUID(), saWorld.wrapOrGetGround().getUuid(), saMunitionShip.getUuid(),
                    JomlUtil.dCenter(breechBp), LOADED_MUNITION_ORIGIN_D,
                    JomlUtil.dNormal(breechBlockDir)
                );
            } else {
                attConstraint = new SliderOnVsConstraint(
                    UUID.randomUUID(), artilleryShip.getId(), saMunitionShip.getUuid(),
                    JomlUtil.dCenter(breechBp), LOADED_MUNITION_ORIGIN_D,
                    JomlUtil.dNormal(breechBlockDir)
                );
            }
            attConstraint.setFixedDistance(0.0);
            //saWorld.getConstraintSolver().addConstraint(attConstraint);

            IOrientationConstraint oriConstraint;
            if (artilleryShip == null) {
                oriConstraint = new OrientationConstraint(
                    UUID.randomUUID(), saWorld.wrapOrGetGround().getUuid(), saMunitionShip.getUuid(),
                    HoldableAttachment.rotateForwardToDirection(breechBlockDir), HoldableAttachment.rotateForwardToDirection(LOADED_MUNITION_DIRECTION)
                );
            } else {
                oriConstraint = new OrientationOnVsConstraint(
                    UUID.randomUUID(), artilleryShip.getId(), saMunitionShip.getUuid(),
                    HoldableAttachment.rotateForwardToDirection(breechBlockDir), HoldableAttachment.rotateForwardToDirection(LOADED_MUNITION_DIRECTION)
                );
            }
            //saWorld.getConstraintSolver().addConstraint(oriConstraint);
            LoadedMunitionConstraint newConstraint = new LoadedMunitionConstraint(UUID.randomUUID(), attConstraint, oriConstraint);
            saWorld.getConstraintSolver().addConstraint(newConstraint);

            int loadingLen = blockData.getBlockCnt();  //todo is blockCnt of blockData the len?
            for (int i = loadedData.size() - 1; i >= 0; --i) {
                LoadedMunitionConstraint loadedConstraint = loadedData.get(i).getConstraint(level);
                if (loadedConstraint == null) {
                    EzDebug.warn("the constraint is null, may the ship is already removed, will remove this loaded ship");
                    loadedData.remove(i);
                    continue;
                }

                loadedConstraint.getSliderConstraint().addFixedDistance(loadingLen);
                EzDebug.warn("add loading len:" + loadingLen);
            }
            loadedData.add(new LoadedShipData(saMunitionShip.getUuid(), newConstraint.getUuid()));
            loadedLen += loadingLen;
            return true;
        }


        /.*public ChunkLoadedTicker chunkLoadedTicker() {
            return (level, bp) ->  {
                if (coldDown > 0) coldDown--;
            };
        }*./

        @Override
        public void onAdded(BlockPos bp, BlockRecordRWMgr mgr) {
            mgr.events.addChunkLoadedServerTicker(bp, breechServerTick);
        }
        @Override
        public void onRemoved(BlockPos bp, BlockRecordRWMgr mgr) {
            mgr.events.removeChunkLoadedServerTicker(bp, breechServerTick);
        }
    }*/


    //public boolean getLoadedMunitionData(Level level, BlockPos breechBp, Dest<Ship> munitionShip, Dest<Boolean> isTriggered, Dest<Direction> munitionDirInShip);
    public boolean canArmLoadDockerNow(ServerLevel level, BlockPos breechBp, ItemStack stack);
    public boolean loadMunition(ServerLevel level, BlockPos breechBp, BlockState breechState, ItemStack munitionDocker);
    public void loadMunitionShip(ServerLevel level, BlockPos breechBp, BlockState breechState, ServerShip vsShip, boolean simulate);

    //public void unloadShell(ServerLevel level, BlockPos breechBp, Direction shellDirInShip, BlockPos breechBp);
    public void unloadShell(ServerLevel level, BlockPos breechBp, BlockState breechState);


    /*public static boolean foreachMunition(ServerLevel level, BlockPos breechBp, Vector3ic projectileAppendDir, boolean simulate, Dest<Double> totPropellingEngDest, BlockClusterData projectileBlockData) {
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
        //Stream<BiTuple<Vector3ic, BlockState>> munitionStream = Stream.empty();
        var loadedShipData = record.loadedData;

        for (int i = record.loadedData.size() - 1; i >= 0; --i) {
            //var curData = record.loadedData.get(i);
            //var shipData = curData.getFirst();
            UUID shipUuid = record.loadedData.get(i).shipUuid();
            ISandBoxShip ship = saWorld.getShip(shipUuid);
            if (ship == null) {
                EzDebug.warn("there is a null ship inside breech, maybe removed by commands");
                loadedShipData.remove(i);
                continue;
            }

            IBlockClusterDataReader curShipBlockData = ship.getBlockCluster().getDataReader();

            Direction projectileBlockDir = JomlUtil.nearestDir(projectileAppendDir);
            //Vector3ic curDir = shipData.getSecond();
            Vector3i curLocalPos = new Vector3i(IBreech.LOADED_MUNITION_ORIGIN);

            if (first) {
                BlockState primerState = curShipBlockData.getBlockState(IBreech.LOADED_MUNITION_ORIGIN);
                if (!(primerState.getBlock() instanceof IPrimer primer)) {
                    //there is no primer
                    EzDebug.warn("fail to foreach because munition has no primer");
                    return false;
                }  else {
                    if (!simulate) {
                        ship.getBlockCluster().getDataWriter().setBlock(new Vector3i(), primer.getTriggeredState(primerState));
                    }
                }

                //curLocalPos.add(curDir);  //skip first primer if first (first set false at tail of loop)
                curLocalPos.add(IBreech.LOADED_MUNITION_FORWARD);
            }

            while (seekPropellants) {
                BlockState state = curShipBlockData.getBlockState(curLocalPos);
                if (!(state.getBlock() instanceof IPropellant propellant)) {
                    EzDebug.log("seeking propellant and find " + StrUtil.getBlockName(state) + " at " + StrUtil.poslike(curLocalPos));

                    if (!state.isAir()) seekPropellants = false;

                    break;
                }

                totPropellingEng += propellant.getSPE(state);
                if (!simulate) {
                    ship.getBlockCluster().getDataWriter().setBlock(curLocalPos, propellant.getEmptyState(state));
                }
                curLocalPos.add(IBreech.LOADED_MUNITION_FORWARD);
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
                    curLocalPos.add(IBreech.LOADED_MUNITION_FORWARD);
                }
            }

            first = false;
        }
        totPropellingEngDest.set(totPropellingEng);
        if (first) return false;  //didn't iter any ship
        return true;
    }*/

    /*public static void clearLoadedMunition(ServerLevel level, BlockPos breechBp) {
        BreechRecord record = BlockRecordRWMgr.getRecord(level, breechBp);
        if (record == null) {
            EzDebug.warn("[clearLoadedMunition] can't get record at " + breechBp.toShortString());
            return;
        }

        var saWorld = SandBoxServerWorld.getOrCreate(level);
        record.loadedData.forEach(shipData -> saWorld.markShipDeleted(shipData.shipUuid())); //constraints will auto removed by event
        //record.loadedData.forEach(shipData -> saWorld.markShipDeleted(shipData.getFirst().getFirst()));  //constraints will auto removed by event
        record.loadedData.clear();
    }*/
}
