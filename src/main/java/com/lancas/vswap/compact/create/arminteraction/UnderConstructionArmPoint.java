package com.lancas.vswap.compact.create.arminteraction;

import com.lancas.vswap.content.block.blockentity.UnderConstructionBe;
import com.lancas.vswap.content.item.items.docker.Docker;
import com.lancas.vswap.sandbox.industry.ConstructingShipBehaviour;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class UnderConstructionArmPoint extends ArmInteractionPoint {
    public UnderConstructionArmPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    public int getSlotCount() { return 1; }

    @Override
    public ItemStack insert(ItemStack stack, boolean simulate) {
        if (!(level instanceof ServerLevel sLevel)) {
            //EzDebug.log("return by client level");
            return stack;
        }
        if (stack.isEmpty()) {
            //EzDebug.log("return by empty stack, simulate:" + simulate);
            return stack;
        }
        if (!(level.getBlockEntity(pos) instanceof UnderConstructionBe be)) {
            //EzDebug.log("return by not correct be");
            return stack;
        }

        //EzDebug.log("Under construction armPoint: stack" + stack + ", simuate:" + simulate);

        ConstructingShipBehaviour beh = be.getConstructingBehaviour();
        if (beh == null || beh.isCompleted()) {
            //EzDebug.warn("return by null beh or completed beh");
            return stack;
        }

        //todo temp now: creative put
        if (!simulate)
            beh.creativePutMaterial();

        //stack.shrink(1);
        ItemStack returnStack = stack.copy();
        returnStack.shrink(1);
        return returnStack;
    }

    @Override
    public ItemStack extract(int slot, boolean simulate) {
        return this.extract(slot, 1, simulate);
    }
    @Override
    public ItemStack extract(int slot, int amount, boolean simulate) {  //amount must be 1
        if (!(level instanceof ServerLevel sLevel))
            return ItemStack.EMPTY;  //the ship deleting must be in server
        if (!(level.getBlockEntity(pos) instanceof UnderConstructionBe be))
            return ItemStack.EMPTY;

        ConstructingShipBehaviour beh = be.getConstructingBehaviour();
        if (beh == null || !beh.isCompleted())
            return ItemStack.EMPTY;

        //beh is completed
        if (simulate) {
            return Docker.defaultStack();
        }
        //EzDebug.log("return stack with data:" + beh.getSchemeData().saved());
        beh.resetConstruction();
        return Docker.stackOfData(beh.getSchemeData());
    }
}
