package com.lancas.vswap.content.behaviour.block;

import com.lancas.vswap.content.capacity.ListInventory;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.LazyTicks;
import com.lancas.vswap.util.ItemUtil;
import com.lancas.vswap.util.JomlUtil;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.joml.Vector3d;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ChuteLikeOutputBehaviour extends BlockEntityBehaviour {
    //amount, simulate, result
    //protected final BiFunction<Integer, Boolean, ItemStack> dropStackGetter;

    protected final Supplier<IItemHandlerModifiable> itemHandlerSup;
    protected final Supplier<Direction> extractTowards;
    protected final Predicate<BlockEntity> notInsertTo;
    protected final LazyTicks lazyTicks = new LazyTicks(10);

    public ChuteLikeOutputBehaviour(SmartBlockEntity be, Supplier<IItemHandlerModifiable> inItemHandlerSup, Supplier<Direction> inExtractTowards, Predicate<BlockEntity> inNotInsertTo) {
        super(be);
        itemHandlerSup = inItemHandlerSup;
        extractTowards = inExtractTowards;
        notInsertTo = inNotInsertTo;
    }

    public ChuteLikeOutputBehaviour setLazyTicks(int ticks) { lazyTicks.setLazyTicks(ticks); return this; }

    @Override
    public BehaviourType<?> getType() { return new BehaviourType<ChuteLikeOutputBehaviour>(); }


    @Override
    public void tick() {  //behaviour tick always called, but some SmartBlockEntity don't call beh's lazyTick (in SmartBlockEntity lazyTick has empty definition)
        super.tick();
        if (!(getWorld() instanceof ServerLevel level))
            return;

        //EzDebug.log("ChuteLikeOutput Beh tick");
        if (!lazyTicks.shouldWork())
            return;

        IItemHandlerModifiable itemHandler = itemHandlerSup.get();
        if (itemHandler == null) {
            EzDebug.warn("get itemHandler null!");
            return;
        }

        ItemStack toDropStack = ItemStack.EMPTY;//dropStackGetter.apply(false);
        int atSlot = -1;
        Objects.requireNonNull(itemHandler);
        for (int i = 0; i < itemHandler.getSlots(); ++i) {
            toDropStack = itemHandler.getStackInSlot(i);
            if (!toDropStack.isEmpty()) {
                //EzDebug.log("at slot " + i + " has stack:" + toDropStack);
                atSlot = i;
                break;
            }
        }
        if (toDropStack.isEmpty())
            return;
        if (atSlot < 0 || atSlot >= itemHandler.getSlots()) {
            EzDebug.warn("drop stack is not empty but has invalid slot:" + atSlot);
            return;
        }
        ItemStack finalToDropStack = toDropStack;  //as final
        int finalAtSlot = atSlot;  //as final


        Direction extractDir = extractTowards.get();
        BlockPos targetBp = getPos().relative(extractDir);
        BlockState targetState = level.getBlockState(targetBp);

        if (targetState.isAir()) {
            //EzDebug.log("drop side is air:" + toDropStack);
            Vector3d dropPos = JomlUtil.relativeFromCenter(getPos(), extractDir, 0.75);
            ItemUtil.dropNoRandom(level, toDropStack, dropPos);
            itemHandler.setStackInSlot(finalAtSlot, ItemStack.EMPTY);
            return;
        }

        BlockEntity targetBe = level.getBlockEntity(targetBp);
        if (targetBe == null) {  //no be, don't drop item(blocked)
            //EzDebug.log("drop side have no be, won't do anything");
            return;
        }

        targetBe.getCapability(ForgeCapabilities.ITEM_HANDLER, extractDir.getOpposite())
            .ifPresent(targetItemHandler -> {
                ItemStack remain = ItemHandlerHelper.insertItemStacked(targetItemHandler, finalToDropStack, false);
                itemHandler.setStackInSlot(finalAtSlot, remain);

                //EzDebug.log("drop side has itemHandler, remain:" + remain);
            });
    }
}
