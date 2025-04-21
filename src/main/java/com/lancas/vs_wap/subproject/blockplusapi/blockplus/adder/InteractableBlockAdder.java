package com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder;

import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.register.BlockInteractEvent;
import com.lancas.vs_wap.subproject.blockplusapi.util.QuadConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class InteractableBlockAdder implements IBlockAdder {
    private final QuadConsumer<Level, Player, BlockPos, BlockState> event;
    public InteractableBlockAdder(@NotNull QuadConsumer<Level, Player, BlockPos, BlockState> inEvent) {
        event = inEvent;
    }

    @Override
    public void onInit(BlockPlus thisBlock) {
        if (event == null) return;  //in case event is null

        BlockInteractEvent.addInteractableBlock(thisBlock, event);
    }
}
