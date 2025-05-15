package com.lancas.vs_wap.content.block.blocks.cartridge;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vs_wap.content.saved.BlockRecordRWMgr;
import com.lancas.vs_wap.content.saved.ConstraintsMgr;
import com.lancas.vs_wap.content.saved.IBlockRecord;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.ship.attachment.HoldableAttachment;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.PropertyAdder;
import com.lancas.vs_wap.subproject.blockplusapi.util.QuadConsumer;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShapeBuilder;
import com.lancas.vs_wap.util.StrUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;

import java.util.List;

public class PrimerBlock extends BlockPlus implements IPrimer/*, IBE<PrimerBE>*/  {
    /*public static class BallisticHeadIterable implements Iterable<BiTuple<BlockPos, BlockState>>, Iterator<BiTuple<BlockPos, BlockState>> {
        private final Level level;
        private final BlockPos primerBp;
        private final Direction primerDir;

        private BlockPos curBp;

        public BallisticHeadIterable(Level inLevel, BlockPos inPrimerBp) {
            level = inLevel;
            primerBp = inPrimerBp;

            primerDir = level.getBlockState(primerBp).getValue(DirectionBlockAdder.FACING);

            curBp = primerBp.relative(primerDir);
            while (level.getBlockState(curBp) instanceof IPropellant) {
                curBp = primerBp.relative(primerDir);
            }
            //curBp is the first head.
        }
        @Override
        public @NotNull Iterator<BiTuple<BlockPos, BlockState>> iterator() { return this; }

        @Override
        public boolean hasNext() {
            return !level.getBlockState(curBp).isAir();
        }

        @Override
        public BiTuple<BlockPos, BlockState> next() {
            BlockPos result = curBp;
            curBp = curBp.relative(primerDir);
            return new BiTuple<>(result, level.getBlockState(result));
        }
    }*/
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE
    )
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PrimerRecord implements IBlockRecord {
        public String attConstraintKey;
        public String oriConstraintKey;
        private PrimerRecord() {}
        public PrimerRecord(String attKey, String oriKey) { attConstraintKey = attKey; oriConstraintKey = oriKey; }
    }

    public static final BooleanProperty TRIGGERED = BooleanProperty.create("triggered");
    public static final List<IBlockAdder> adders = List.of(
        new DirectionAdder(
            false,
            true,
            ShapeBuilder.ofBox(2, 5, 2, 14, 11, 14)
                .append(box(3, 11, 3, 13, 16, 13))
                .get()
        ),
        new PropertyAdder<>(TRIGGERED, false)
        //, EinherjarBlockInfos.mass.getOrCreateExplicit(PrimerBlock.class, state -> 5.0)
    );
    @Override
    public Iterable<IBlockAdder> getAdders() { return adders; }

    public PrimerBlock(Properties p_49795_) { super(p_49795_); }

    @Override
    public int getPixelLength() {
        return 8;
    }

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

    public static void createConstraints(ServerLevel level, BlockPos primerBp, @Nullable ServerShip artilleryShip, ServerShip munitionShip, BlockPos breechPos/*, Direction primerDir, double primerShapeLen*/, Direction breechDirInShipOrWorld, HoldableAttachment munitionHoldable) {
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
            /*if (!(sLevel.getBlockEntity(primerBp.get()) instanceof PrimerBE be)) {
                EzDebug.warn("can't get primerBp at " + primerBp.get().toShortString());
                return;
            }*/

            //constraint mgr may remove record by onConstraintRemove event when remove prev constraint
            PrimerRecord prevRecord = BlockRecordRWMgr.getRecord(sLevel, primerBp.get());
            if (prevRecord == null) {
                prevRecord = new PrimerRecord(null, null);
                BlockRecordRWMgr.putRecord(sLevel, primerBp.get(), prevRecord);
            }

            if (isAttachment.get()) {
                /*BlockRecordRWMgr.changeIfExist(sLevel, primerBp.get(),
                    (Function<PrimerRecord, PrimerRecord>)record -> new PrimerRecord(key, record.oriConstraintKey)
                );*/
                prevRecord.attConstraintKey = key;
            }
            if (isOrientation.get()) {
                /*BlockRecordRWMgr.changeIfExist(sLevel, primerBp.get(),
                    (Function<PrimerRecord, PrimerRecord>)record -> new PrimerRecord(record.attConstraintKey, key)
                );*/
                prevRecord.oriConstraintKey = key;
            }
            if (!isAttachment.get() && !isOrientation.get()) {
                EzDebug.warn("verified key but no constraint is matched, isAtt:" + isAttachment.get() + ", isOri:" + isOrientation.get() + ", primerBp:" + primerBp.get());
            }

            /*EventMgr.Server.holdShipEvent.addListener(new TriRemoveAfterSuccessListener<ServerLevel, ServerPlayer, Long>() {
                private boolean success = false;
                @Override
                public void accept(ServerLevel level, ServerPlayer player, Long shipId) {

                }
                @Override
                public boolean isSuccess() { EzDebug.log("get success:" + success); return success; }
            });
            EzDebug.highlight("holdShip event added");*/
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
