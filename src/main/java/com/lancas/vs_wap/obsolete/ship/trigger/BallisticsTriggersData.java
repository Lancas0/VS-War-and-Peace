package com.lancas.vs_wap.obsolete.ship.trigger;

/*
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.einherjar.debug.EzDebug;
import com.lancas.einherjar.foundation.data.SavedBlockPos;
import com.lancas.einherjar.ship.phys.ballistics.api.ITrigger;
import com.lancas.einherjar.ship.phys.ballistics.data.BallisticStateData;
import com.lancas.einherjar.ship.phys.ballistics.data.BallisticsShipData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BallisticsTriggersData {
    // todo use set
    private List<SavedBlockPos> triggerSbps = //new ArrayList<>();//Collections.synchronizedList(new ArrayList<>());

    public BallisticsTriggersData() {}
    public void tryAcceptBlock(BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof ITrigger) {
            triggerSbps.add(new SavedBlockPos(pos));
        }
    }

    public List<TriggerInfo> getTriggerInfos(ServerLevel level, BallisticsShipData controlData, BallisticStateData stateData) {
        List<TriggerInfo> infos = new ArrayList<>();

        for (var triggerSbp : triggerSbps) {
            BlockPos bp = triggerSbp.toBp();
            BlockState state = level.getBlockState(bp);

            if (!(state.getBlock() instanceof ITrigger trigger)) {
                EzDebug.warn("the block is not trigger. skip it");
                continue;
            }

            if (!trigger.shouldCheck(controlData, stateData)) continue;
            trigger.appendTriggerInfos(level, bp, state, controlData, stateData, infos);
        }
        return infos;
    }
}
*/