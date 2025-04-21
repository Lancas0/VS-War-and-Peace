package com.lancas.vs_wap.obsolete;


import com.lancas.vs_wap.debug.EzDebug;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;

public class DebugPosAtt implements ShipForcesInducer {
    public static final boolean doDebug = true;

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        if (doDebug)
            EzDebug.log("pos:" + physShip.getTransform().getPositionInWorld());
    }
}
