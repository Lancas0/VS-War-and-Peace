package com.lancas.vswap.content.block.blocks.industry.dock;

import com.lancas.vswap.content.WapBlockEntites;
import com.lancas.vswap.content.WapBlocks;
import com.lancas.vswap.content.block.blockentity.VSProjectorBE;
import com.lancas.vswap.content.block.blocks.blockplus.util.InteractToInsertOrExtractAdder;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.PropertyAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.ShapeByStateAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.ctx.BlockChangeContext;
import com.lancas.vswap.subproject.blockplusapi.util.Action;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;
import java.util.function.Supplier;

public class GreenPrintHolder extends BlockPlus implements IBE<GreenPrintHolderBe> {
    public static final BooleanProperty HAS = BooleanProperty.create("has");

    public static final Supplier<BlockState> HAS_STATE = () -> WapBlocks.Industrial.GREEN_PRINT_HOLDER.getDefaultState().setValue(HAS, true);
    public static final Supplier<BlockState> EMPTY_STATE = () -> WapBlocks.Industrial.GREEN_PRINT_HOLDER.getDefaultState().setValue(HAS, false);

    @Override
    public List<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(GreenPrintHolder.class, () -> List.of(
            new PropertyAdder<>(HAS, false),
            new PropertyAdder<>(Dock.CONNECT_N, false),
            new PropertyAdder<>(Dock.CONNECT_S, false),
            new PropertyAdder<>(Dock.CONNECT_W, false),
            new PropertyAdder<>(Dock.CONNECT_E, false),
            new ShapeByStateAdder(state -> Shapes.block()),
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
                        case UP, DOWN -> {}
                        case NORTH -> level.setBlockAndUpdate(pos, state.setValue(Dock.CONNECT_N, connective));
                        case SOUTH -> level.setBlockAndUpdate(pos, state.setValue(Dock.CONNECT_S, connective));
                        case WEST -> level.setBlockAndUpdate(pos, state.setValue(Dock.CONNECT_W, connective));
                        case EAST -> level.setBlockAndUpdate(pos, state.setValue(Dock.CONNECT_E, connective));
                    }
                }

                @Override
                public Action<BlockChangeContext, Void> onPlace() {
                    return Dock.DockOnPlaceAction;
                }

                @Override
                public Action<BlockChangeContext, Void> onRemove() {
                    return Dock.DockOnRemoveAction;
                }
            },
            new InteractToInsertOrExtractAdder() {
                @Override
                public boolean canInteract(Level level, BlockPos bp, BlockEntity be) {
                    return !level.isClientSide && be instanceof GreenPrintHolderBe;
                }

                @Override
                public ItemStack insert(BlockEntity be, ItemStack stack) {
                    GreenPrintHolderBe gpHolderBe = (GreenPrintHolderBe)be;
                    if (!gpHolderBe.gpInventory.getStackInSlot(0).isEmpty())  //can't insert when it's not empty
                        return stack;

                    ItemStack remain = gpHolderBe.gpInventory.insertItem(0, stack, false);
                    if (!remain.equals(stack, true)) {
                        if (be.getLevel() != null) {
                            be.getLevel().setBlockAndUpdate(be.getBlockPos(), HAS_STATE.get());
                        }
                        gpHolderBe.notifyUpdate();
                        EzDebug.highlight("successfully insert");
                    }
                    return remain;
                }

                @Override
                public ItemStack extract(BlockEntity be) {
                    GreenPrintHolderBe gpHolderBe = (GreenPrintHolderBe)be;

                    ItemStack extract = gpHolderBe.gpInventory.extractItem(0, 1, false);
                    if (!extract.isEmpty()) {
                        if (be.getLevel() != null) {
                            be.getLevel().setBlockAndUpdate(be.getBlockPos(), EMPTY_STATE.get());
                        }
                        //gpHolderBe.itemHandler.setStackInSlot(0, ItemStack.EMPTY);
                        gpHolderBe.notifyUpdate();
                       // gpHolderBe.afterUpdateGreenPrint();
                    }
                    return extract;
                }
            }
        ));
    }

    public GreenPrintHolder(Properties p_49795_) {
        super(p_49795_);
    }


    @Override
    public Class<GreenPrintHolderBe> getBlockEntityClass() { return GreenPrintHolderBe.class; }
    @Override
    public BlockEntityType<? extends GreenPrintHolderBe> getBlockEntityType() { return WapBlockEntites.GREEN_PRINT_HOLDER_BE.get(); }
}
