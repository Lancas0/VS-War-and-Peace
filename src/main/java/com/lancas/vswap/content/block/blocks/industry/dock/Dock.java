package com.lancas.vswap.content.block.blocks.industry.dock;

import com.lancas.vswap.content.WapBlockEntites;
import com.lancas.vswap.content.block.api.IHoldingShipInteractableBlock;
import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.content.block.api.IDockerInteractableBlock;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.foundation.handler.multiblock.MultiContainerHandler;
import com.lancas.vswap.ship.feature.hold.ICanHoldShip;
import com.lancas.vswap.ship.feature.hold.ShipHoldSlot;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.InteractableBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.PropertyAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.ShapeByStateAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.ctx.BlockChangeContext;
import com.lancas.vswap.subproject.blockplusapi.util.Action;
import com.lancas.vswap.util.ShipUtil;
import com.lancas.vswap.util.WapColors;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Dock extends BlockPlus implements IBE<DockBe>, IDockerInteractableBlock, IHoldingShipInteractableBlock {  //todo a interface DockerInterable, handling interact logic and preview
    //public static BooleanProperty PINGPONG = BooleanProperty.create("ping_pong");
    //public static BlockState pingPongState(BlockState pre) { return pre.setValue(PINGPONG, !pre.getValue(PINGPONG)); }
    public static Action<BlockChangeContext, Void> DockOnPlaceAction = new Action<BlockChangeContext, Void>() {
        private void updateContinuousAtAxis(ServerLevel level, BlockPos pos, Direction.Axis axis, Function<DockBe, Integer> getter, BiConsumer<DockBe, Integer> setter) {
            if (!(level.getBlockEntity(pos) instanceof DockBe selfBe)) {
                EzDebug.error("after place get be at this pos is not DockeBe");
                return;
            }

            Direction posDir = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
            Direction negDir = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);
            if (level.getBlockEntity(pos.relative(posDir)) instanceof DockBe aheadBe) {
                setter.accept(selfBe, getter.apply(aheadBe) + 1);
                //selfBe.setContinuousHeight(aheadBe.getContinuousHeight() + 1);
            } else {
                //selfBe.setContinuousHeight(1);
                setter.accept(selfBe, 1);
            }

            BlockPos.MutableBlockPos curBelow = pos.relative(negDir).mutable();
            int last = getter.apply(selfBe);//selfBe.getContinuousHeight();
            while (level.getBlockEntity(curBelow) instanceof DockBe backwardBe) {
                setter.accept(backwardBe, last + 1);
                //belowBe.setContinuousHeight(lastCH + 1);
                last++;
                curBelow.move(negDir);
            }
        }
        @Override
        public Void post(BlockChangeContext ctx, Void soFar, Dest<Boolean> cancel) {
            //EzDebug.log("------onPlace post-----------" + ctx.pos.toShortString());
            //get this continuousH from all above first, then set all below
            //cz is same
            //todo check by multiblock type?
            if (!(ctx.level instanceof ServerLevel level))
                return null;
            if (!ctx.blockChanged() || ctx.isMoving)
                return null;

            //EzDebug.log("pos:" + ctx.pos.toShortString() + ", old:" + ctx.oldState + ", new:" + ctx.newState);
            //EzDebug.warn("be at pos:" + level.getBlockEntity(ctx.pos));

            updateContinuousAtAxis(level, ctx.pos, Direction.Axis.Y, DockBe::getContinuousHeight, DockBe::setContinuousHeight);
            updateContinuousAtAxis(level, ctx.pos, Direction.Axis.Z, DockBe::getContinuousZLen, DockBe::setContinuousZLen);

            new MultiContainerHandler(level).handleFrom(DockMultiContainerType.INSTANCE, ctx.pos);

            return null;
        }
    };
    public static Action<BlockChangeContext, Void> DockOnRemoveAction = new Action<BlockChangeContext, Void>() {
        private void updateContinuousAtAxis(ServerLevel level, BlockPos pos, Direction.Axis axis, BiConsumer<DockBe, Integer> setter) {
            if (!(level.getBlockEntity(pos) instanceof DockBe selfBe)) {
                EzDebug.error("after place get be at this pos is not DockeBe");
                return;
            }

            //Direction posDir = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
            Direction negDir = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);

            BlockPos.MutableBlockPos curBackward = pos.relative(negDir).mutable();
            while (level.getBlockEntity(curBackward) instanceof DockBe backBe) {
                int newContinuousVal = backBe.getContinuousZLen() - selfBe.getContinuousZLen();
                if (newContinuousVal <= 0) {
                    EzDebug.warn("invalid newContinuousVal:" + newContinuousVal);
                    newContinuousVal = 1;
                }
                setter.accept(backBe, newContinuousVal);  //get and add
                curBackward.move(negDir);
            }
        }
        @Override
        public Void pre(BlockChangeContext ctx, Void soFar, Dest<Boolean> cancel) {
            if (!(ctx.level instanceof ServerLevel level))
                return null;
            if (!ctx.blockChanged() || ctx.isMoving)
                return null;

            updateContinuousAtAxis(level, ctx.pos, Direction.Axis.Y, DockBe::setContinuousHeight);
            updateContinuousAtAxis(level, ctx.pos, Direction.Axis.Z, DockBe::setContinuousZLen);

            return null;
        }

        @Override
        public Void post(BlockChangeContext ctx, Void soFar, Dest<Boolean> cancel) {
            //EzDebug.log("------onPlace post-----------" + ctx.pos.toShortString());
            //get this continuousH from all above first, then set all below
            //cz is same
            //todo check by multiblock type?
            if (!(ctx.level instanceof ServerLevel level))
                return null;
            if (!ctx.blockChanged() || ctx.isMoving)
                return null;

            new MultiContainerHandler(level).handleFrom(DockMultiContainerType.INSTANCE, ctx.pos);

            return null;
        }
    };


    /*public static enum ConnectType {
        S, W, E, N, SW, SE, NW, NE, SWE, NWE, WSN, ESN, SWEN
    }
    public static final EnumProperty<>*/
    public static final BooleanProperty CONNECT_N = BooleanProperty.create("connect_n");
    public static final BooleanProperty CONNECT_S = BooleanProperty.create("connect_s");
    public static final BooleanProperty CONNECT_W = BooleanProperty.create("connect_w");
    public static final BooleanProperty CONNECT_E = BooleanProperty.create("connect_e");

    public Dock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public List<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(Dock.class, () -> List.of(
            new ShapeByStateAdder(s -> Shapes.block()),
            //new PropertyAdder<>(PINGPONG, false),
            new PropertyAdder<>(CONNECT_N, false),
            new PropertyAdder<>(CONNECT_S, false),
            new PropertyAdder<>(CONNECT_W, false),
            new PropertyAdder<>(CONNECT_E, false),

            new IBlockAdder() {
                @Override
                public void onNeighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
                    IBlockAdder.super.onNeighborChanged(state, level, pos, block, fromPos, isMoving);
                    BlockPos delta = fromPos.subtract(pos);
                    boolean connective = level.getBlockEntity(fromPos) instanceof DockBe;//level.getBlockState(fromPos).is(WapBlocks.Industrial.DOCK.get());

                    Direction dir = Direction.fromDelta(delta.getX(), delta.getY(), delta.getZ());
                    if (dir == null) {
                        EzDebug.warn("get null dir!");
                        return;
                    }

                    /*if (connective) {
                        level.setBlockAndUpdate(pos, pingPongState(state));  //update block to make connect texture
                    }*/
                    switch (dir) {
                        case UP, DOWN -> {
                        }
                        case NORTH -> level.setBlockAndUpdate(pos, state.setValue(CONNECT_N, connective));
                        case SOUTH -> level.setBlockAndUpdate(pos, state.setValue(CONNECT_S, connective));
                        case WEST -> level.setBlockAndUpdate(pos, state.setValue(CONNECT_W, connective));
                        case EAST -> level.setBlockAndUpdate(pos, state.setValue(CONNECT_E, connective));
                    }
                }

                /*@Override
                public InteractionResult onInteracted(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    ItemStack withStack = player.getItemInHand(hand);
                    withBlockEntityDo(level, pos, be -> {
                        ItemStack afterStack = be.onInteract(withStack);
                        player.setItemInHand(hand, afterStack);
                    });

                    return InteractionResult.PASS;
                    //return IBlockAdder.super.onInteracted(state, level, pos, player, hand, hit);
                }*/

                /*@Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    IBlockAdder.super.onRemove(state, level, pos, newState, isMoving);
                    EzDebug.log("state has be:" + state.hasBlockEntity() + ", blockChange:" + (state.getBlock() != newState.getBlock()) + ", new empty be:" + (!newState.hasBlockEntity()) + ", be is " + level.getBlockEntity(pos));
                    if (state.hasBlockEntity()) {
                        if (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity()) {
                            /.*BlockEntity be = level.getBlockEntity(pos);
                            if (!(be instanceof DockBe dockBe)) {
                                return;
                            }*./

                            //ItemHelper.dropContents(world, pos, vaultBE.inventory);
                            level.removeBlockEntity(pos);
                            ConnectivityHandler.splitMulti(dockBe);
                            EzDebug.log("ConnectivityHandler split multi");
                        }
                    }
                }*/

                @Override
                public Action<BlockChangeContext, Void> onPlace() {
                    return DockOnPlaceAction;
                }

                @Override
                public Action<BlockChangeContext, Void> onRemove() {
                    return DockOnRemoveAction;
                }
                /*@Override
                public Action<BlockChangeContext, Void> onRemove() {
                    return new Action<>() {
                        @Override
                        public Void pre(BlockChangeContext ctx, Void soFar, Dest<Boolean> cancel) {
                            if (!(ctx.level instanceof ServerLevel level))
                                return null;

                            //override the onRemove
                            cancel.set(true);
                            BlockState oldState = ctx.oldState;
                            BlockState newState = ctx.newState;
                            if (oldState.hasBlockEntity() && (oldState.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
                                if (level.getBlockEntity(ctx.pos) instanceof DockBe dockBe) {
                                    level.removeBlockEntity(ctx.pos);
                                    ConnectivityHandler.splitMulti(dockBe);
                                }
                            }

                            return null;
                        }
                    };
                }*/
            },
            new InteractableBlockAdder() {
                @Override
                public InteractionResult onInteracted(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    if (!(level instanceof ServerLevel sLevel))
                        return InteractionResult.PASS;
                    if (!player.isShiftKeyDown())  //player must shiftDown when extract ship
                        return InteractionResult.PASS;

                    ItemStack handStack = player.getItemInHand(hand);
                    if (!handStack.isEmpty())
                        return InteractionResult.PASS;

                    if (!(level.getBlockEntity(pos) instanceof DockBe be)) {
                        EzDebug.warn("fail to get dockBe at " + pos.toShortString());
                        return InteractionResult.PASS;
                    }

                    Dest<ServerShip> shipDest = new Dest<>();
                    if (be.unboundHoldingShip(false, false, shipDest) && shipDest.hasValue()) {
                        ItemStack dockerStack = Docker.stackOfVs(sLevel, shipDest.get(), false, false);  //todo use ref and don't delete ship?
                        if (!dockerStack.isEmpty()) {
                            player.setItemInHand(hand, dockerStack);
                            ShipUtil.deleteShip(sLevel, shipDest.get());
                            return InteractionResult.SUCCESS;
                        }
                    }
                    return InteractionResult.PASS;
                }
            }
        ));
    }

    /*@Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        /.*if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof DockBe)) {
                return;
            }

            DockBe dockBe = (DockBe)be;
            world.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(dockBe);
        }*./
        super.onRemove(state, world, pos, newState, isMoving);

    }*/



    @Override
    public Class<DockBe> getBlockEntityClass() { return DockBe.class; }
    @Override
    public BlockEntityType<? extends DockBe> getBlockEntityType() { return WapBlockEntites.DOCK_BE.get(); }

    @Override
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<S> p_153214_) {
        return (level, blockPos, blockState, be) -> ((DockBe)be).tick();
    }


    @Override
    public boolean mayInteract(ItemStack handDocker, Level level, Player player, BlockPos bp, BlockState state) {
        if (!level.isClientSide)
            return false;
        if (player.isShiftKeyDown())
            return false;  //when player shiftDown, don't interact

        if (!(level.getBlockEntity(bp) instanceof DockBe be)) {
            EzDebug.warn("fail to get dockBe at " + bp.toShortString());
            return false;
        }

        boolean canPutIn = be.tryPutDocker(handDocker, true);
        be.showOutline(canPutIn ? WapColors.SHOWCASE_BLUE : WapColors.DARK_RED);

        return true;  //always return true so that origin place is blocked
    }
    @Override
    public @NotNull ItemStack interact(ItemStack handDocker, Level level, Player player, BlockPos bp, BlockState state) {
        if (level.isClientSide)
            return handDocker;
        if (player.isShiftKeyDown())
            return handDocker;  //when player shiftDown, don't interact

        if (!(level.getBlockEntity(bp) instanceof DockBe be)) {
            EzDebug.warn("fail to get dockBe at " + bp.toShortString());
            return handDocker;
        }

        boolean canPutIn = be.tryPutDocker(handDocker, false);
        return canPutIn ? ItemStack.EMPTY : handDocker;
    }

    @Override
    public boolean mayInteract(@NotNull ClientShip holdingShip, ClientLevel level, Player player, BlockPos bp, BlockState state) {
        if (!(level.getBlockEntity(bp) instanceof DockBe be)) {
            EzDebug.warn("fail to get dockBe at " + bp.toShortString());
            return false;
        }

        int color = be.tryPutShip(holdingShip, true) ? WapColors.HINT_ORANGE : WapColors.DARK_RED;
        be.showOutline(color);

        return true;
    }
    @Override
    public boolean interact(@NotNull Ship holdingShip, Level level, Player player, BlockPos bp, BlockState state) {
        if (level.isClientSide)
            return false;
        if (!(level.getBlockEntity(bp) instanceof DockBe be)) {
            EzDebug.warn("fail to get dockBe at " + bp.toShortString());
            return false;
        }

        if (be.tryPutShip(holdingShip, false)) {
            ((ICanHoldShip)player).unholdShipInServer(ShipHoldSlot.MainHand, true);
            return true;
        }
        return false;
    }
}
