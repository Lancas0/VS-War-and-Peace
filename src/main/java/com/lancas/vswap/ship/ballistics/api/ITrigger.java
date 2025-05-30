package com.lancas.vswap.ship.ballistics.api;

import com.lancas.vswap.ship.ballistics.data.BallisticStateData;
import com.lancas.vswap.ship.ballistics.data.BallisticsShipData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface ITrigger {
    public boolean shouldCheck(BallisticsShipData controlData, BallisticStateData stateData);
    public void appendTriggerInfos(ServerLevel level, BlockPos pos, BlockState state, BallisticsShipData controlData, BallisticStateData stateData, List<TriggerInfo> dest);
}
