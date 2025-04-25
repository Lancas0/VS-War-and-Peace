package com.lancas.vs_wap.sandbox.component.ballistic;

import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.ship.ballistics.api.TriggerInfo;
import com.lancas.vs_wap.ship.ballistics.data.BallisticStateData;
import com.lancas.vs_wap.ship.ballistics.data.BallisticsShipData;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3i;

import java.util.List;

public interface ISandBoxBallisticBlock {
    public default void serverTick(ServerLevel level, SandBoxServerShip ship) {}
    public default void physTick(SandBoxServerShip ship) {}

    public default void appendTriggerInfos(Vector3i localPos, BlockState state, SandBoxServerShip ship, List<TriggerInfo> dest) {}

    public default void doTerminalEffect(Vector3i localPos, BlockState state, TriggerInfo info, Dest<Boolean> terminateByEffect) {
        terminateByEffect.set(false);
    }
}
