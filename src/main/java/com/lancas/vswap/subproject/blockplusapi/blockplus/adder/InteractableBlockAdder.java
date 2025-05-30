package com.lancas.vswap.subproject.blockplusapi.blockplus.adder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class InteractableBlockAdder implements IBlockAdder {
    /*private final QuadConsumer<Level, Player, BlockPos, BlockState> event;
    public InteractableBlockAdder(@NotNull QuadConsumer<Level, Player, BlockPos, BlockState> inEvent) {
        event = inEvent;
    }

    //todo just use "use" method
    /*@Override
    public void onInit(BlockPlus thisBlock) {
        if (event == null) return;  //in case event is null

        BlockInteractEvent.addInteractableBlock(thisBlock, event);
    }*/

    /*@Override
    public InteractionResult onInteracted(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (event == null) return InteractionResult.FAIL;
        event.apply(level, player, );
    }*/
    public abstract InteractionResult onInteracted(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit);
}
