package com.lancas.vswap.compact.create.arminteraction;

import com.lancas.vswap.content.WapItems;
import com.lancas.vswap.content.block.blocks.industry.dock.DockBe;
import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.subproject.mstandardized.MaterialStandardizedItem;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.ServerShip;

public class DockArmPoint extends ArmInteractionPoint {
    public static class Type extends ArmInteractionPointType {
        public Type(ResourceLocation id) {
            super(id);
        }

        @Override
        public boolean canCreatePoint(Level level, BlockPos bp, BlockState state) {
            return level.getBlockEntity(bp) instanceof DockBe;
        }

        @Override
        public @Nullable ArmInteractionPoint createPoint(Level level, BlockPos bp, BlockState state) {
            if (level.getBlockEntity(bp) instanceof DockBe) {
                return new DockArmPoint(this, level, bp, state);
            }
            return null;
        }
    }

    public DockArmPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    public int getSlotCount() { return 1; }

    @Override
    public ItemStack insert(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return stack;
        }

        if (!(level instanceof ServerLevel sLevel)) {
            return stack;
        }
        if (!(level.getBlockEntity(pos) instanceof DockBe dockBe)) {
            EzDebug.warn("DockArmPoint can't get dockBe");
            return stack;
        }

        //todo handle when dockBe is GreenPrintHolderBe
        /*if (dockBe instanceof GreenPrintHolderBe gpHolderBe) {
            if (GreenPrint.isGreenPrint(stack)) {
                EzDebug.log("to put green print");
                gpHolderBe.greenPrintInventory
            }
        }*/
        DockBe controllerBe = dockBe.getControllerBE();

        if (WapItems.CREATIVE_MS.isIn(stack)) {
            boolean success = controllerBe.creativeConstruct(simulate);
            return success ? stack.copyWithCount(stack.getCount() - 1) : stack;
        }

        if (stack.getItem() instanceof MaterialStandardizedItem) {  //try construct
            return controllerBe.construct(stack, simulate);//controllerBe.creativeConstruct(simulate);
        }

        if (stack.getItem() instanceof Docker) {
            boolean success = controllerBe.tryPutDocker(stack, simulate);
            if (success)
                return ItemStack.EMPTY;
        }

        //boolean success = controllerBe.tryPutDocker(stack, simulate);
        //return success ? ItemStack.EMPTY : stack;
        return stack;
    }

    @Override
    public ItemStack extract(int slot, boolean simulate) {
        return this.extract(slot, 1, simulate);
    }

    @Override
    public ItemStack extract(int slot, int amount, boolean simulate) {
        //return super.extract(slot, amount, simulate);
        //ignore amount

        if (!(level instanceof ServerLevel sLevel)) {
            return ItemStack.EMPTY;
        }
        if (!(level.getBlockEntity(pos) instanceof DockBe dockBe)) {
            EzDebug.warn("DockArmPoint can't get dockBe");
            return ItemStack.EMPTY;
        }

        DockBe controllerBe = dockBe.getControllerBE();
        Dest<ServerShip> getShip = new Dest<>();
        if (controllerBe.unboundHoldingShip(false, simulate, getShip)) {
            //can unbound
            if (simulate)
                return Docker.defaultStack();
            else {
                return Docker.stackOfVs(sLevel, getShip.get(), true, true);
            }
        }

        return ItemStack.EMPTY;
    }
}
