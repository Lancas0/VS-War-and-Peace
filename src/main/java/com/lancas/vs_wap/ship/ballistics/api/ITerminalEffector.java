package com.lancas.vs_wap.ship.ballistics.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public interface ITerminalEffector {
    //public JSONObject serialize();
    //public void deserializeOverwrite(JSONObject json);
    public void appendDescription(Set<String> descSet);

    public boolean canAccept(ServerLevel level, BlockPos pos, BlockState state, TriggerInfo info);
    public void effect(ServerLevel level, BlockPos effectorBp, BlockState effectorState, TriggerInfo info);
    public boolean shouldTerminateAfterEffecting(TriggerInfo info);
}
