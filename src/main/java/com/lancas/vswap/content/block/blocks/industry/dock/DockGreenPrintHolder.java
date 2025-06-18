package com.lancas.vswap.content.block.blocks.industry.dock;

import com.lancas.vswap.content.WapBlockEntites;
import com.lancas.vswap.content.WapBlocks;
import com.lancas.vswap.content.block.blocks.blockplus.util.InteractToInsertOrExtractAdder;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.PropertyAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.ShapeByStateAdder;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;
import java.util.function.Supplier;

public class DockGreenPrintHolder extends BlockPlus implements IBE<DockGreenPrintHolderBe> {
    public static final BooleanProperty HAS = BooleanProperty.create("has");

    public static final Supplier<BlockState> HAS_STATE = () -> WapBlocks.Industrial.DOCK_GP_HOLDER.getDefaultState().setValue(HAS, true);
    public static final Supplier<BlockState> EMPTY_STATE = () -> WapBlocks.Industrial.DOCK_GP_HOLDER.getDefaultState().setValue(HAS, false);

    @Override
    public List<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(DockGreenPrintHolder.class, () -> List.of(
            new PropertyAdder<>(HAS, false),
            new PropertyAdder<>(Dock.CONNECT_N, false),
            new PropertyAdder<>(Dock.CONNECT_S, false),
            new PropertyAdder<>(Dock.CONNECT_W, false),
            new PropertyAdder<>(Dock.CONNECT_E, false),
            new ShapeByStateAdder(state -> Shapes.block()),
            Dock.DockBlockAdder,
            new InteractToInsertOrExtractAdder() {
                @Override
                public boolean canInteract(Level level, BlockPos bp, BlockEntity be) {
                    return !level.isClientSide && be instanceof DockGreenPrintHolderBe;
                }

                @Override
                public ItemStack insert(BlockEntity be, ItemStack stack) {
                    DockGreenPrintHolderBe gpHolderBe = (DockGreenPrintHolderBe)be;
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
                    DockGreenPrintHolderBe gpHolderBe = (DockGreenPrintHolderBe)be;

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

    public DockGreenPrintHolder(Properties p_49795_) {
        super(p_49795_);
    }


    @Override
    public Class<DockGreenPrintHolderBe> getBlockEntityClass() { return DockGreenPrintHolderBe.class; }
    @Override
    public BlockEntityType<? extends DockGreenPrintHolderBe> getBlockEntityType() { return WapBlockEntites.GREEN_PRINT_HOLDER_BE.get(); }
}
