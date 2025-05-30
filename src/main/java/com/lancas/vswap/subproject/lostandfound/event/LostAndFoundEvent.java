package com.lancas.vswap.subproject.lostandfound.event;

import com.lancas.vswap.event.impl.QuadEventImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LostAndFoundEvent {
    //bp, oldState, newState
    public static QuadEventImpl<Level, BlockPos, BlockState, BlockState> preBlockChangeEvt = new QuadEventImpl<>();

}
