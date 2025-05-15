package com.lancas.vs_wap.content.block.blocks.cartridge;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vs_wap.content.block.blocks.blockplus.RefreshBlockRecordAdder;
import com.lancas.vs_wap.content.saved.BlockRecordRWMgr;
import com.lancas.vs_wap.content.saved.ConstraintsMgr;
import com.lancas.vs_wap.content.saved.IBlockRecord;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.event.EventMgr;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.ship.attachment.HoldableAttachment;
import com.lancas.vs_wap.ship.feature.hold.ICanHoldShip;
import com.lancas.vs_wap.ship.feature.hold.ShipHoldSlot;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.InteractableBlockAdder;
import com.lancas.vs_wap.subproject.blockplusapi.util.QuadConsumer;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShapeBuilder;
import com.lancas.vs_wap.util.ShipUtil;
import com.lancas.vs_wap.util.StrUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;

import java.util.List;
import java.util.Objects;

public class ShellFrame extends BlockPlus/* implements IBE<ShellFrameBE>*/ {
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
    )
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ShellFrameRecord implements IBlockRecord {
        public String attConstraintKey = "";
        public String oriConstraintKey = "";
        public long lockingShipId = -1;
        //public long lastLockTime = 0;

        @JsonIgnore
        public TriConsumer<ServerLevel, ServerPlayer, Long> unholdListener;
    }

    private static final List<IBlockAdder> adders = List.of(
        new DirectionAdder(true, true, ShapeBuilder.cubicRing(0, 0, 0, 2, 16)),
        new RefreshBlockRecordAdder((level, bp, state) -> new ShellFrameRecord()),
        new InteractableBlockAdder() {
            @Override
            public InteractionResult onInteracted(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                //todo remove constraint when redstone

                //hold/unhold ship must be in server
                if (!(level instanceof ServerLevel sLevel)) return InteractionResult.PASS;
            /*if (!(level.getBlockEntity(bp) instanceof ShellFrameBE be)) {
                EzDebug.error("no ShellFrameBe on a shell frame be block? the block:" + StrUtil.getBlockName(state));
                return;
            }*/
                ICanHoldShip icanHoldShip = (ICanHoldShip)player;

                Dest<Long> prevHoldShipId = new Dest<>();
                icanHoldShip.unholdShipInServer(ShipHoldSlot.MainHand, true, prevHoldShipId);
                ServerShip holdenShip = ShipUtil.getServerShipByID(sLevel, prevHoldShipId.get());
                if (holdenShip == null) return InteractionResult.PASS;

                lockShip(sLevel, holdenShip, pos);
                return InteractionResult.PASS;

            /*HoldableAttachment holdable = holdenShip.getAttachment(HoldableAttachment.class);
            if (holdable == null) {
                EzDebug.fatal("It should never happen when unholdShip return a shipId with no holdable");
                return;
            }

            BlockPos holdBpInShip = holdable.holdPivotBpInShip.toBp();

            Direction forwardInHoldenShip = holdable.forwardInShip;

            addConstraints(sLevel, bp, holdenShip, holdBpInShip, frameDir, forwardInHoldenShip);
            //addAttachConstraint(sLevel, bp, holdenShip, holdBpInShip);
            //addFixedOrientationConstraint(sLevel, bp, holdenShip, frameDir, forwardInHoldenShip);
            /*Direction frameDir = DirectionBlockAdder.getDirection(state);
            if (frameDir == null) {
                EzDebug.fatal("fail to get frameDir");
                return;
            }

            var orientationConstraint = new VSFixedOrientationConstraint(
                frameShipId, holdenShipId,
                1e-10, HoldableAttachment.rotateForwardToDirection(frameDir), HoldableAttachment.rotateForwardToDirection(holdable.forwardInShip),
                1e20
            );
            shipObjWorld.createNewConstraint(orientationConstraint);*/
            }
        }
    );
    @Override
    public Iterable<IBlockAdder> getAdders() { return adders; }

    public ShellFrame(Properties p_49795_) {
        super(p_49795_);
    }




    /*@Override
    public Class<ShellFrameBE> getBlockEntityClass() { return ShellFrameBE.class; }
    @Override
    public BlockEntityType<? extends ShellFrameBE> getBlockEntityType() { return WapBlockEntites.SHELL_FRAME_BE.get(); }
    @Override
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<S> p_153214_) {
        return ((level, blockPos, state, be) -> ((ShellFrameBE)be).tickColdDown());
    }*/
    public static long releaseShip(ServerLevel level, BlockPos frameBp) {
        ShellFrameRecord record = BlockRecordRWMgr.getRecord(level, frameBp);
        if (record == null) {
            EzDebug.warn("fail to get shell frame record at " + frameBp.toShortString());
            return -1;
        }
        if (record.lockingShipId < 0) return -1;

        if (StrUtil.isNotEmpty(record.attConstraintKey)) {
            ConstraintsMgr.removeInLevelConstraint(level, record.attConstraintKey);
            record.attConstraintKey = "";
            //EzDebug.light("remove att key:" + attConstraintKey);
        }
        if (StrUtil.isNotEmpty(record.oriConstraintKey)) {
            ConstraintsMgr.removeInLevelConstraint(level, record.oriConstraintKey);
            record.oriConstraintKey = "";
            //EzDebug.light("remove ori key:" + oriConstraintKey);
        }

        long prevShipId = record.lockingShipId;
        record.lockingShipId = -1;

        //todo can i only save one?
        //or to lazy save only when game exit?
        BlockRecordRWMgr.setDirtyOf(level);
        return prevShipId;
    }
    public static boolean canLockShip(ServerShip ship, @Nullable Dest<HoldableAttachment> holdableDest) {
        var holdable = ship.getAttachment(HoldableAttachment.class);
        if (holdable == null) return false;

        Dest.setIfExistDest(holdableDest, holdable);
        return true;
    }
    public static void lockShip(ServerLevel level, ServerShip ship, BlockPos frameBp) {
        Dest<HoldableAttachment> holdable = new Dest<>();
        if (!canLockShip(ship, holdable)) return;

        ShellFrameRecord record = BlockRecordRWMgr.getRecord(level, frameBp);
        if (record == null) {
            EzDebug.warn("fail to get record, recreate one.");
            record = new ShellFrameRecord();
            BlockRecordRWMgr.putRecord(level, frameBp, record);
        }
        BlockState shellFrameState = level.getBlockState(frameBp);

        @Nullable ServerShip inShip = ShipUtil.getServerShipAt(level, frameBp);
        Direction frameDirInShipOrWorld = DirectionAdder.getDirection(shellFrameState);


        Dest<String> attachmentId = new Dest<>();
        Dest<String> orientationId = new Dest<>();
        generateKeys(frameBp, attachmentId, orientationId);

        ConstraintsMgr.addAttachment(
            level, attachmentId.get(),
            inShip, ship,
            1e-10,
            JomlUtil.dCenter(frameBp), JomlUtil.dCenter(holdable.get().getPivotBpInShip()),
            1e10, 0
        );
        ConstraintsMgr.addFixedOrientation(
            level, orientationId.get(),
            inShip, ship,
            1e-10,
            HoldableAttachment.rotateForwardToDirection(frameDirInShipOrWorld), HoldableAttachment.rotateForwardToDirection(holdable.get().forwardInShip),
            1e10
        );
        //todo set pos and rotation so that minialize the force applied
        /*ShipUtil.teleport(sLevel, ship,
            TeleportDataBuilder.noMovementOf(sLevel, ship)
                .withPos()
        );*/

        record.lockingShipId = ship.getId();
        record.attConstraintKey = attachmentId.get();
        record.oriConstraintKey = orientationId.get();

        //todo can i only save one?
        //or to lazy save only when game exit?
        BlockRecordRWMgr.setDirtyOf(level);
    }
    private static long getOtherShipId(ServerLevel sLevel, BlockPos frameBp, long shipId0, long shipId1) {
        long thisShipOrGroundId = ShipUtil.getShipOrGroundIdAt(sLevel, frameBp);
        if (shipId0 == thisShipOrGroundId)
            return shipId1;
        if (shipId1 == thisShipOrGroundId)
            return shipId0;

        EzDebug.fatal("none ids of ships is this id");
        throw new RuntimeException("fail to get other ship's id");
    }
    public static void generateKeys(BlockPos frameBp, Dest<String> attachmentKey, Dest<String> orientationKey) {
        attachmentKey.set("shell_frame|attachment|" + frameBp.getX() + "|" + frameBp.getY() + "|" + frameBp.getZ());
        orientationKey.set("shell_frame|orientation|" + frameBp.getX() + "|" + frameBp.getY() + "|" + frameBp.getZ());
    }
    private static boolean verifyKey(String key, Dest<Boolean> isAttachment, Dest<Boolean> isOrientation, Dest<BlockPos> frameBp) {
        //EzDebug.light("verify key:" + key);
        String[] split = key.split("\\|");

        verify : {
            if (split.length == 0) break verify;
            if (!split[0].equals("shell_frame")) break verify;

            if (split[1].equals("attachment")) {
                isAttachment.set(true);
                isOrientation.set(false);
            } else if (split[1].equals("orientation")) {
                isOrientation.set(true);
                isAttachment.set(false);
            } else break verify;

            frameBp.set(new BlockPos(Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4])));
            return true;
        }

        isAttachment.set(false);
        isOrientation.set(false);
        return false;
    }



    public static QuadConsumer<ServerLevel, String, VSConstraint, Integer> onConstraintAdd() {
        return (level, key, constraint, constraintId) -> {
            Dest<Boolean> isAttachment = new Dest<>();
            Dest<Boolean> isOrientation = new Dest<>();
            Dest<BlockPos> frameBp = new Dest<>();

            if (!verifyKey(key, isAttachment, isOrientation, frameBp)) return;
            ShellFrameRecord record = BlockRecordRWMgr.getRecord(level, frameBp.get());
            if (record == null) {
                EzDebug.error("fail to get record at:" + frameBp.get().toShortString());
                return;
            }
            /*if (!(level.getBlockEntity(frameBp.get()) instanceof ShellFrameBE be)) {
                EzDebug.warn("verified key but can't find ShellFrameBe at " + frameBp.get().toShortString());
                return;
            }*/

            if (isAttachment.get()) {
                record.attConstraintKey = key;
                //EzDebug.light("set att id to " + constraintId);
            } else if (isOrientation.get()) {
                record.oriConstraintKey = key;
                //EzDebug.light("set ori id to " + constraintId);
            }

            long otherShipId = getOtherShipId(level, frameBp.get(), constraint.getShipId0(), constraint.getShipId1());
            record.lockingShipId = otherShipId;
            //EzDebug.light("other ship id:" + otherShipId);

            //add event if related to other ship
            //add when both attachment event is triggered
            if (StrUtil.isNotEmpty(record.attConstraintKey) && StrUtil.isNotEmpty(record.oriConstraintKey)) {
                //remove prevListener
                if (record.unholdListener != null) {
                    EventMgr.Server.holdShipEvent.remove(record.unholdListener);
                }
                //register an event expire when successfully run
                record.unholdListener = (lisLevel, player, shipId) -> {
                    //EzDebug.light("shipId:" + shipId + ", lockingShipId:" + be.lockingShipId);
                    if (shipId != record.lockingShipId) return;
                    //ConstraintsMgr.removeConstraint(sLevel, be.attConstraintKey);
                    //ConstraintsMgr.removeConstraint(sLevel, be.oriConstraintKey);
                    releaseShip(lisLevel, frameBp.get());
                    EzDebug.highlight("remove constraint by hold event");
                    EventMgr.Server.holdShipEvent.remove(record.unholdListener);
                };
                EventMgr.Server.holdShipEvent.addListener(record.unholdListener);
            }
            //todo can i only save one?
            //or to lazy save only when game exit?
            BlockRecordRWMgr.setDirtyOf(level);
        };
    }
    public static TriConsumer<ServerLevel, String, VSConstraint> onConstraintRemove() {
        return (level, key, constraint) -> {
            //EzDebug.light("shell frame be on constraint remove, key:" + key);

            Dest<Boolean> isAttachment = new Dest<>();
            Dest<Boolean> isOrientation = new Dest<>();
            Dest<BlockPos> frameBp = new Dest<>();

            if (!verifyKey(key, isAttachment, isOrientation, frameBp)) return;
            ShellFrameRecord record = Objects.requireNonNull(BlockRecordRWMgr.getRecord(level, frameBp.get()));
            /*if (!(level.getBlockEntity(frameBp.get()) instanceof ShellFrameBE be)) {
                EzDebug.warn("verified key but can't find ShellFrameBe at " + frameBp.get().toShortString());
                return;
            }*/

            if (isAttachment.get()) {
                record.attConstraintKey = "";
                //EzDebug.light("set att id to " + -1);
            } else if (isOrientation.get()) {
                record.oriConstraintKey = "";
                //EzDebug.light("set ori id to " + -1);
            }
            long otherShipId = getOtherShipId(level, frameBp.get(), constraint.getShipId0(), constraint.getShipId1());
            record.lockingShipId = otherShipId;
            //EzDebug.light("remove other ship id:" + otherShipId);
            //todo can i only save one?
            //or to lazy save only when game exit?
            BlockRecordRWMgr.setDirtyOf(level);
        };
    }
}
