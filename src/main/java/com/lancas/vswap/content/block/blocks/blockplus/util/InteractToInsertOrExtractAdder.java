package com.lancas.vswap.content.block.blocks.blockplus.util;

import com.lancas.vswap.content.block.blockentity.VSProjectorBE;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.InteractableBlockAdder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public abstract class InteractToInsertOrExtractAdder extends InteractableBlockAdder {
    public boolean extractWhenShiftDown() { return true; }

    //public abstract @Nullable T getInventory(Level level, BlockPos bp, BlockEntity be);
    public abstract boolean canInteract(Level level, BlockPos bp, BlockEntity be);
    public abstract ItemStack insert(BlockEntity be, ItemStack stack);
    public abstract ItemStack extract(BlockEntity be);

    @Override
    public InteractionResult onInteracted(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!canInteract(level, pos, be))
            return InteractionResult.PASS;

        ItemStack handStack = player.getItemInHand(hand);
        boolean toExtract = extractWhenShiftDown() ? player.isShiftKeyDown() : !player.isShiftKeyDown();

        if (toExtract) {  //to extract
            if (!handStack.isEmpty()) {
                return InteractionResult.PASS;  //can't extract when holding stuff
            }

            ItemStack extract = extract(be);
            if (!extract.isEmpty()) {
                player.setItemInHand(hand, extract);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;

        } else {  //to insert
            if (handStack.isEmpty()) {
                return InteractionResult.PASS;  //can't insert empty stack
            }

            ItemStack remain = insert(be, handStack);
            if (!remain.equals(handStack, true)) {
                player.setItemInHand(hand, remain);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
    }
}
