package com.lancas.vswap.sandbox.ballistics;

import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.sandbox.ballistics.data.BallisticData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticFlyingContext;
import com.lancas.vswap.sandbox.ballistics.data.BallisticInitialStateSubData;
import com.lancas.vswap.sandbox.ballistics.data.BallisticPos;
import com.lancas.vswap.sandbox.ballistics.trigger.SandBoxTriggerInfo;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.List;

public interface ISandBoxBallisticBlock {
    public default void serverTick(ServerLevel level, SandBoxServerShip ship, BallisticPos ballisticPos) {}
    public default void physTick(double dt, SandBoxServerShip ship, BallisticData ballisticData) {}

    public default void onExitBarrel(ServerLevel level, SandBoxServerShip onShip, BallisticPos ballisticPos) {}

    public default void appendTriggerInfos(ServerLevel level, BallisticPos ballisticPos, BlockState state, SandBoxServerShip ship, List<SandBoxTriggerInfo> dest) {}

    public default void doTerminalEffect(ServerLevel level, SandBoxServerShip ship, BallisticPos ballisticPos, BlockState state, List<SandBoxTriggerInfo> infos, Dest<Boolean> terminateByEffect) {
        //terminateByEffect.set(false);
    }

    public default void modifyFlyingContext(ServerLevel level, SandBoxServerShip ship, BallisticData ballisticData, BallisticPos ballisticPos, BlockState state,BallisticFlyingContext ctx) {}
}