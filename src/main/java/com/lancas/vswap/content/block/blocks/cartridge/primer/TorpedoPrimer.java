package com.lancas.vswap.content.block.blocks.cartridge.primer;

import com.lancas.vswap.content.block.blocks.artillery.breech.IBreech;
import com.lancas.vswap.content.block.blocks.artillery.breech.helper.LoadedMunitionData;
import com.lancas.vswap.content.block.blocks.blockplus.DefaultCartridgeAdder;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.sandbox.ballistics.ISandBoxBallisticBlock;
import com.lancas.vswap.sandbox.ballistics.data.BallisticData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticFlyingContext;
import com.lancas.vswap.sandbox.ballistics.data.BallisticPos;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.PropertyAdder;
import com.lancas.vswap.subproject.blockplusapi.util.QuadFunction;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.StrUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.List;

public class TorpedoPrimer extends BlockPlus implements IPrimer, ISandBoxBallisticBlock {
    /*@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE
    )
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PrimerRecord implements IBlockRecord {
        public String attConstraintKey;
        public String oriConstraintKey;
        private PrimerRecord() {}
        public PrimerRecord(String attKey, String oriKey) { attConstraintKey = attKey; oriConstraintKey = oriKey; }
    }*/
    public static double VelLenToForwardForce(double velLen) {
        return 0;
    }
    public static final double TORPEDO_PRIMER_LAUNCH_SPE = 0.1;


    public static final List<IBlockAdder> adders = List.of(
        new DefaultCartridgeAdder(false),
        new PropertyAdder<>(TRIGGERED, false)
        //, EinherjarBlockInfos.mass.getOrCreateExplicit(PrimerBlock.class, state -> 5.0)
    );
    @Override
    public List<IBlockAdder> getAdders() { return adders; }

    public TorpedoPrimer(Properties p_49795_) { super(p_49795_); }

    /*@Override
    public int getPixelLength() {
        return 8;
    }*/

    //todo frequencely called
    @Override
    public boolean isTriggered(BlockState state) {
        return state.getValue(TRIGGERED);
    }
    @Override
    public BlockState getTriggeredState(BlockState prevState) {
        //level.setBlockAndUpdate(pos, prevState.setValue(TRIGGERED, true));
        //removeConstraint(level, pos);
        return prevState.setValue(TRIGGERED, true);
    }


    public static final QuadFunction<ServerLevel, Class<? extends IPrimer>, List<LoadedMunitionData>, List<ItemStack>, @Nullable BlockClusterData> TorpedoPrimerLikeFire =
        (level, expectedPrimerType, loaded, munitionRemains) -> {
            //speDest.set(0.0);
            munitionRemains.clear();
            if (loaded.isEmpty())
                return null;

            //get last loaded(in breech round)
            BlockState primerState = loaded.get(loaded.size() - 1)
                .getShip(level)
                .getBlockCluster()
                .getDataReader()
                .getBlockState(IBreech.LOADED_MUNITION_ORIGIN);

            if (!primerState.getBlock().getClass().equals(expectedPrimerType)) {
                EzDebug.warn("can't get " + expectedPrimerType.getSimpleName() + " but " + StrUtil.getBlockName(primerState));
                return null;
            }

            BlockClusterData blockData = new BlockClusterData();
            Vector3i curPos = new Vector3i(IBreech.LOADED_MUNITION_ORIGIN);
            IPrimer.foreachMunition(level, loaded, expectedPrimerType, (ship, locPos, state) -> {
                blockData.setBlock(curPos, state, false);
                curPos.add(IBreech.LOADED_MUNITION_FORWARD);
            });

            if (blockData.getBlockCnt() <= 0) {
                EzDebug.warn("get torpedo primer but blockData has 0 blocks");
                return null;
            }

            //speDest.set(TORPEDO_PRIMER_LAUNCH_SPE);
            return blockData;
        };
    @Override
    public @Nullable BlockClusterData fire(ServerLevel level, List<LoadedMunitionData> loaded, Dest<Double> speDest, List<ItemStack> munitionRemains) {
        /*munitionRemains.clear();
        if (loaded.isEmpty())
            return null;

        //get last loaded(in breech round)
        BlockState primerState = loaded.get(loaded.size() - 1)
            .getShip(level)
            .getBlockCluster()
            .getDataReader()
            .getBlockState(IBreech.LOADED_MUNITION_ORIGIN);

        if (!(primerState.getBlock() instanceof TorpedoPrimer)) {
            EzDebug.warn("can't get torpedoPrimer but " + StrUtil.getBlockName(primerState));
            return null;
        }

        BlockClusterData blockData = new BlockClusterData();
        Vector3i curPos = new Vector3i(IBreech.LOADED_MUNITION_ORIGIN);
        IPrimer.foreachMunition(level, loaded, TorpedoPrimer.class, (ship, locPos, state) -> {
            blockData.setBlock(curPos, state, false);
            curPos.add(IBreech.LOADED_MUNITION_FORWARD);
        });

        if (blockData.getBlockCnt() <= 0) {
            EzDebug.warn("get torpedo primer but blockData has 0 blocks");
            return null;
        }*/
        speDest.set(0.0);
        BlockClusterData blockData = TorpedoPrimerLikeFire.apply(level, TorpedoPrimer.class, loaded, munitionRemains);

        if (blockData != null)
            speDest.set(TORPEDO_PRIMER_LAUNCH_SPE);

        return blockData;
    }

    @Override
    public void modifyFlyingContext(ServerLevel level, SandBoxServerShip ship, BallisticData ballisticData, BallisticPos ballisticPos, BlockState state, BallisticFlyingContext ctx) {
        if (!level.getFluidState(JomlUtil.bpContaining(ship.getRigidbody().getDataReader().getPosition())).isEmpty()) {

        }
    }

    //public boolean foreachMunition(ServerLevel level, Stream<SandBoxServerShip> loaded, BlockPos breechBp, Vector3ic projectileAppendDir, boolean simulate, Dest<Double> totPropellingEngDest, BlockClusterData projectileBlockData) {
        /*IBreech.BreechRecord record = BlockRecordRWMgr.getRecord(level, breechBp);
        if (record == null) {
            EzDebug.warn("[foreachMunition] can't get record at " + breechBp.toShortString());
            return false;
        }*/
/*
        double totPropellingEng = 0;
        boolean seekPropellants = true;

        var saWorld = SandBoxServerWorld.getOrCreate(level);
        Vector3i curProjectileAppendLocalPos = new Vector3i();
        AtomicBoolean first = new AtomicBoolean(true);
        Stream<BiTuple<Vector3ic, BlockState>> munitionStream = Stream.empty();

        loaded.forEach(s -> {

 */
            /*var curData = record.loadedShipData.get(i);
            var shipData = curData.getFirst();
            UUID shipUuid = shipData.getFirst();
            ISandBoxShip ship = saWorld.getShip(shipUuid);
            if (ship == null) {
                EzDebug.warn("there is a null ship inside breech, maybe removed by commands");
                loadedShipData.remove(i);
                continue;
            }*/
            //IBlockClusterDataReader curShipBlockData = s.getBlockCluster().getDataReader();

            //Direction projectileBlockDir = JomlUtil.nearestDir(projectileAppendDir);
            //Vector3ic curDir = shipData.getSecond();
            /*Vector3i curLocalPos = new Vector3i();

            if (first.get()) {
                BlockState primerState = curShipBlockData.getBlockState(new Vector3i());
                if (!(primerState.getBlock() == this)) {
                    //there is no primer
                    EzDebug.warn("fail to foreach because get unexpected primer");
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

            first.set(false);
        });

        var loadedShipData = record.loadedShipData;

        for (int i = record.loadedShipData.size() - 1; i >= 0; --i) {

        }
        totPropellingEngDest.set(totPropellingEng);
        if (first) return false;  //didn't iter any ship
        return true;
    }*/


    /*public static void createConstraints(ServerLevel level, BlockPos primerBp, @Nullable ServerShip artilleryShip, ServerShip munitionShip, BlockPos breechPos/.*, Direction primerDir, double primerShapeLen*./, Direction breechDirInShipOrWorld, HoldableAttachment munitionHoldable) {
        Dest<String> attKey = new Dest<>();
        Dest<String> oriKey = new Dest<>();
        generateKeys(primerBp, attKey, oriKey);

        Vector3d attPos0 = JomlUtil.dCenter(breechPos);//JomlUtil.dFaceCenter(breech.getSecond(), primerDir.getOpposite());  //breech
        Vector3d attPos1 = JomlUtil.dCenter(primerBp);//.add(JomlUtil.dNormal(primerDir, 1.0 - primerShapeLen));//JomlUtil.dFaceCenter(worldPosition, primerDir);

        //I suppose both constraint are successfully added
        BlockRecordRWMgr.putRecord(level, primerBp, new PrimerRecord(attKey.get(), oriKey.get()));

        ConstraintsMgr.addAttachment(level, attKey.get(),
            artilleryShip, munitionShip,
            1e-10, attPos0, attPos1,
            1e10, 0
        );
        ConstraintsMgr.addFixedOrientation(level, oriKey.get(),
            artilleryShip, munitionShip,
            1e-10,
            HoldableAttachment.rotateForwardToDirection(breechDirInShipOrWorld), HoldableAttachment.rotateForwardToDirection(munitionHoldable.forwardInShip),
            1e10
        );
    }
    public static void removeConstraint(ServerLevel level, BlockPos primerBp) {
        PrimerRecord record = BlockRecordRWMgr.removeRecord(level, primerBp);
        if (record == null) {
            EzDebug.warn("[PrimerBlock.removeConstraint] fail to get record at " + primerBp.toShortString());
            return;
        }

        if (StrUtil.isNotEmpty(record.attConstraintKey)) {
            boolean s = ConstraintsMgr.removeInLevelConstraint(level, record.attConstraintKey);
            EzDebug.highlight("successfully remove primer att constraint?:" + s);
        }
        if (StrUtil.isNotEmpty(record.oriConstraintKey)) {
            boolean s = ConstraintsMgr.removeInLevelConstraint(level, record.oriConstraintKey);
            EzDebug.highlight("successfully remove primer ori constraint?:" + s);
        }
    }
    private static void generateKeys(BlockPos pos, Dest<String> attKeyDest, Dest<String> oriKeyDest) {
        attKeyDest.set("primer|att|" + pos.getX() + "|" + pos.getY() + "|" + pos.getZ());
        oriKeyDest.set("primer|ori|" + pos.getX() + "|" + pos.getY() + "|" + pos.getZ());
    }
    private static boolean verifyKey(String key, Dest<Boolean> isAttachment, Dest<Boolean> isOrientation, Dest<BlockPos> primerBpDest) {
        String[] split = key.split("\\|");
        if (split.length == 0) return false;

        if (!split[0].equals("primer")) return false;

        if (split[1].equals("att")) {
            isAttachment.set(true);
            isOrientation.set(false);
        } else if (split[1].equals("ori")) {
            isAttachment.set(false);
            isOrientation.set(true);
        } else return false;

        primerBpDest.set(
            new BlockPos(Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]))
        );
        return true;
    }
    public static QuadConsumer<ServerLevel, String, VSConstraint, Integer> onConstraintAdd =
        (sLevel, key, constraint, id) -> {
            Dest<Boolean> isAttachment = new Dest<>();
            Dest<Boolean> isOrientation = new Dest<>();
            Dest<BlockPos> primerBp = new Dest<>();
            if (!verifyKey(key, isAttachment, isOrientation, primerBp)) return;

            //if (!(sLevel.getBlockState()))
            /.*if (!(sLevel.getBlockEntity(primerBp.get()) instanceof PrimerBE be)) {
                EzDebug.warn("can't get primerBp at " + primerBp.get().toShortString());
                return;
            }*./

            //constraint mgr may remove record by onConstraintRemove event when remove prev constraint
            PrimerRecord prevRecord = BlockRecordRWMgr.getRecord(sLevel, primerBp.get());
            if (prevRecord == null) {
                prevRecord = new PrimerRecord(null, null);
                BlockRecordRWMgr.putRecord(sLevel, primerBp.get(), prevRecord);
            }

            if (isAttachment.get()) {
                /.*BlockRecordRWMgr.changeIfExist(sLevel, primerBp.get(),
                    (Function<PrimerRecord, PrimerRecord>)record -> new PrimerRecord(key, record.oriConstraintKey)
                );*./
                prevRecord.attConstraintKey = key;
            }
            if (isOrientation.get()) {
                /.*BlockRecordRWMgr.changeIfExist(sLevel, primerBp.get(),
                    (Function<PrimerRecord, PrimerRecord>)record -> new PrimerRecord(record.attConstraintKey, key)
                );*./
                prevRecord.oriConstraintKey = key;
            }
            if (!isAttachment.get() && !isOrientation.get()) {
                EzDebug.warn("verified key but no constraint is matched, isAtt:" + isAttachment.get() + ", isOri:" + isOrientation.get() + ", primerBp:" + primerBp.get());
            }

            /.*EventMgr.Server.holdShipEvent.addListener(new TriRemoveAfterSuccessListener<ServerLevel, ServerPlayer, Long>() {
                private boolean success = false;
                @Override
                public void accept(ServerLevel level, ServerPlayer player, Long shipId) {

                }
                @Override
                public boolean isSuccess() { EzDebug.log("get success:" + success); return success; }
            });
            EzDebug.highlight("holdShip event added");*./
        };
    public static TriConsumer<ServerLevel, String, VSConstraint> onConstraintRemove =
        (sLevel, key, constraint) -> {
            Dest<Boolean> isAttachment = new Dest<>();
            Dest<Boolean> isOrientation = new Dest<>();
            Dest<BlockPos> primerBp = new Dest<>();
            if (!verifyKey(key, isAttachment, isOrientation, primerBp)) return;


            PrimerRecord prevRecord = BlockRecordRWMgr.getRecord(sLevel, primerBp.get());
            if (prevRecord == null)
                return;

            if (isAttachment.get())
                prevRecord.attConstraintKey = null;
            if (isOrientation.get())
                prevRecord.oriConstraintKey = null;

            if (prevRecord.oriConstraintKey == null && prevRecord.attConstraintKey == null)
                BlockRecordRWMgr.removeRecord(sLevel, primerBp.get());
        };

    /*public static TriConsumer<ServerLevel, ServerPlayer, Long> onUnholdShip =
        (level, player, shipId) -> {
            //todo don't foreach ship

            Dest<ServerShip> breechShip = new Dest<>();
            Dest<BlockPos> breechBp = new Dest<>();
            findBreech(inShip, primerDir, inShip, breechShip, breechBp);
            if (!breechBp.hasValue()) return;

            BlockState breechState = level.getBlockState(breechBp.get());
            Direction breechDir = DirectionAdder.getDirection(breechState);  //todo use IBreech to get breech dir?
            if (breechDir == null) {
                EzDebug.fatal("can not get direction of breech");
                return;
            }

            createConstraints(breechShip.get(), inShip, breechBp.get(), /.*primerDir, primer.getPixelLength(),*./ breechDir, primerHoldable);
        };*/
    /*private static void findBreech(ServerLevel level, BlockPos primerBp, Ship shipPrimerIn, Direction primerDir, ServerShip primerInShip, Dest<ServerShip> breechShip, Dest<BlockPos> breechBp) {
        //EzDebug.light("start find breech");

        Matrix4dc shipToWorld = shipPrimerIn.getShipToWorld();

        Vector3d forwardFaceWorldCenter = JomlUtil.dWorldFaceCenter(shipToWorld, primerBp, primerDir);
        Vector3d backwardFaceWorldCenter = JomlUtil.dWorldFaceCenter(shipToWorld, primerBp, primerDir.getOpposite());

        BlockPos forwardWorldBreechBp = JomlUtil.bpContaining(forwardFaceWorldCenter);
        if (level.getBlockState(forwardWorldBreechBp).getBlock() instanceof IBreech) {
            //EzDebug.Log("find world breech");
            breechShip.set(null);
            breechBp.set(forwardWorldBreechBp);
            return;
        }
        //EzDebug.light("forward:block at " + forwardWorldBreechBp.toShortString() + ", not breech and is:" + StrUtil.getBlockName(level.getBlockState(forwardWorldBreechBp)));
        BlockPos backWorldBreechBp = JomlUtil.bpContaining(backwardFaceWorldCenter);
        if (level.getBlockState(backWorldBreechBp).getBlock() instanceof IBreech) {
            //EzDebug.Log("find world breech");
            breechShip.set(null);
            breechBp.set(backWorldBreechBp);
            return;
        }
        //EzDebug.light("backward:block at " + backWorldBreechBp.toShortString() + ", not breech and is:" + StrUtil.getBlockName(level.getBlockState(backWorldBreechBp)));

        Vector3d centerWorldCenter = JomlUtil.dWorldCenter(shipToWorld, primerBp);
        AABBd detectAABB = JomlUtil.dBoundCubic(centerWorldCenter, 1);
        for (Ship intersectShip : VSGameUtilsKt.getShipsIntersecting(level, detectAABB)) {
            if (intersectShip.getId() == primerInShip.getId()) continue;
            //different scale don't fit
            if (!intersectShip.getTransform().getShipToWorldScaling().equals(shipPrimerIn.getTransform().getShipToWorldScaling()))
                continue;

            Vector3d shipBreechPos = intersectShip.getWorldToShip().transformPosition(centerWorldCenter);
            BlockPos shipBreechBp = JomlUtil.bpContaining(shipBreechPos);
            if (level.getBlockState(shipBreechBp).getBlock() instanceof IBreech) {
                breechShip.set((ServerShip)intersectShip);
                breechBp.set(shipBreechBp);
                return;
            }
        }
    }*/
    /*@Override
    public Class<PrimerBE> getBlockEntityClass() { return PrimerBE.class; }
    @Override
    public BlockEntityType<? extends PrimerBE> getBlockEntityType() { return EinherjarBlockEntites.PRIMER_BE.get(); }
    @Override
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<S> p_153214_) {
        return ((level, blockPos, state, be) -> ((PrimerBE)be).tick());
    }*/
    /*
    @Override
    public TriTuple<Double, Vector3dc, Long> trigger(ServerLevel level, long artilleryBreechId, BlockPos primerBp, BlockState primerState, Vector3i projectileStartPosDest) {
        if (isTriggered(primerState)) return null;

        ServerShip shipPrimerIn = ShipUtil.getServerShipAt(level, primerBp);
        if (shipPrimerIn == null) {
            EzDebug.fatal("primer should be triggered on a ship");
            return null;
        }

        //只要开始trigger了，就一定需要设置triggered为true
        //相当于就算子弹哑火，底火也不能再用了
        level.setBlockAndUpdate(primerBp, primerState.setValue(TRIGGERED, true));


        Direction primerDir = primerState.getValue(DirectionBlockAdder.FACING);
        //BlockPos curPos = primerBp.relative(primerDir);
        /.*double totalEnergy = 0;

        //The first block must be a filled propellant
        BlockState startState = level.getBlockState(curPos);
        if (!(startState.getBlock() instanceof IPropellant startPropellant) || startPropellant.isEmpty(startState)) {
            EzDebug.Log("start is " + startState.getBlock().getName().getString() + " and trigger fail");
            return null;
        } else {
            totalEnergy += startPropellant.getEnergy();
            startPropellant.setAsEmpty(level, curPos, startState);
        }

        while (true) {
            curPos = curPos.relative(dir);
            BlockState curState = level.getBlockState(curPos);

            //make sure after the loop ends, the curPos is the first block that is not a propellant
            if (curState.getBlock() instanceof IPropellant propellant) {
                if (!propellant.isEmpty(curState)) {
                    totalEnergy += propellant.getEnergy();
                    propellant.setAsEmpty(level, curPos, curState);
                }
            } else {
                break;
            }
        }

        EzDebug.Log("totalEnergy is " + totalEnergy);*./
        //double totalEnergy = BallisticsCalculation.calculateEnergyFromPrimer(level, primerBp, primerDir);
        //just ensure
        double totalEnergy = 0;
        //EzDebug.Log("has one:" + new PropellantIterable(level, primerBp).hasNext());
        for (var tuple : new PropellantIterable(level, primerBp)) {
            BlockPos propellantBp = tuple.getFirst();
            BlockState propellantState = tuple.getSecond();

            if (!(propellantState.getBlock() instanceof IPropellant propellant)) {
                EzDebug.warn("the block is not propellant, skip it.");
                continue;
            }

            EzDebug.log("bp:" + propellantBp + ", block:" + propellantState.getBlock().getName().getString() + ", power:" + propellant.getEnergy());

            if (!propellant.isEmpty(propellantState)) {
                totalEnergy += propellant.getEnergy();
                propellant.setAsEmpty(level, propellantBp, propellantState);
            }
        }

        if (totalEnergy < 1E-20)
            return null;

        //curPos is the first block that is not a propellant
        BlockPos ballisticHeadBp = BallisticsCalculation.calculateBallisticHead(level, primerBp, primerDir);
        //BlockState ballisticHeadState = level.getBlockState(ballisticHeadBp); //projStartState = level.getBlockState(curPos);
        //EzDebug.Log("projStartState:" + ballisticHeadBp.getBlock().getName().getString() + ", spliiter:" + level.getBlockState(curPos.relative(primerDir.getOpposite())).getBlock().getName().getString());
        //assert !(projStartState instanceof IPropellant);
        BlockPos splitBp = ballisticHeadBp.relative(primerDir.getOpposite());
        //ShipBuilder projectile = DirectionalSplitHandler.trySplit(level, splitBp, primerDir);
        ShipBuilder shipBuilder = ShipPool.getOrCreatePool(level).getOrCreateShipBuilder();
        shipBuilder.resetBlocks();
        DirectionalSplitHandler.trySplit(level, splitBp, primerDir, shipBuilder);

        if (shipBuilder.isEmpty()) {
            EzDebug.warn("fail to split projectile part");
            return null;
        }

        //splitSB.setLocalVelocity(dir.getStepX() * 40.0, dir.getStepY() * 40.0, dir.getStepZ() * 40.0);
        Vector3dc launchDir = shipPrimerIn.getTransform().getShipToWorldRotation().transform(JomlUtil.dNormal(primerDir));

        projectileStartPosDest.set(JomlUtil.i(shipBuilder.getInitialBP()));
        //EzDebug.log("dirNormal:" + StrUtil.toNormalString(JomlUtil.dNormal(primerDir)) + "launchDir:" + StrUtil.toNormalString(launchDir));
        EzDebug.highlight("new projectile id:" + shipBuilder.getId());

        VSGameUtilsKt.getShipObjectWorld(level).enableCollisionBetweenBodies(shipPrimerIn.getId(), artilleryBreechId);

        return new TriTuple<>(
            totalEnergy,
            launchDir,
            shipBuilder.getId()
        );
    }



    /*@Override
    public void setTriggered(BlockState state, boolean val) {
        state.setValue(TRIGGERED, val);
        //todo block update?
    }*/
}
