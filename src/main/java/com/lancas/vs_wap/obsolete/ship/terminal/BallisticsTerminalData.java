package com.lancas.vs_wap.obsolete.ship.terminal;
/*
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.einherjar.debug.EzDebug;
import com.lancas.einherjar.foundation.data.SavedBlockPos;
import com.lancas.einherjar.ship.phys.ballistics.api.ITerminalEffector;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class BallisticsTerminalData {
    //may be used in physical and server thread
    public List<SavedBlockPos> terminalEffectorBps = //new CopyOnWriteArrayList<>();//Collections.synchronizedList(new ArrayList<>());

    /.*public void effect(ServerLevel level, Vector3d targetPos) {
        for (var effectorSbp : terminalEffectorBps) {
            BlockPos effectorBp = effectorSbp.toBp();
            BlockState state = level.getBlockState(effectorBp);

            if (!(state.getBlock() instanceof ITriggerConsumer effector)) {
                EzDebug.warn("the block is not TerminalEffector, skip it");
                continue;
            }

            effector.effect(effectorBp, state, targetPos);
        }
    }*./
    public void tryAcceptBlock(BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof ITerminalEffector) {
            terminalEffectorBps.add(new SavedBlockPos(pos));
        }
    }

    public void forEachEffector(ServerLevel level, TriConsumer<BlockPos, BlockState, ITerminalEffector> consumer) {
        for (var effectorSbp : terminalEffectorBps) {
            BlockPos effectorBp = effectorSbp.toBp();
            BlockState state = level.getBlockState(effectorBp);

            if (!(state.getBlock() instanceof ITerminalEffector effector)) {
                EzDebug.warn("the block is not TerminalEffector, skip it");
                continue;
            }

            //effector.effect(effectorBp, state, targetPos);
            consumer.accept(effectorBp, state, effector);
        }
    }
}
*/