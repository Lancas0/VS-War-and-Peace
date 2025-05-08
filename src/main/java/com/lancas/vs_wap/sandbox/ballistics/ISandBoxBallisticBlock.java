package com.lancas.vs_wap.sandbox.ballistics;

import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.sandbox.ballistics.trigger.SandBoxTriggerInfo;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.List;

public interface ISandBoxBallisticBlock {
    public default void serverTick(ServerLevel level, SandBoxServerShip ship, Vector3i localPos) {}
    public default void physTick(SandBoxServerShip ship) {}

    public default void appendTriggerInfos(ServerLevel level, Vector3ic localPos, BlockState state, SandBoxServerShip ship, List<SandBoxTriggerInfo> dest) {}

    public default void doTerminalEffect(ServerLevel level, SandBoxServerShip ship, Vector3ic localPos, BlockState state, List<SandBoxTriggerInfo> infos, Dest<Boolean> terminateByEffect) {
        terminateByEffect.set(false);
    }
}
