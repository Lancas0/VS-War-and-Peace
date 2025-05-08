package com.lancas.vs_wap.subproject.sandbox.constraint.base;

import com.lancas.vs_wap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractBiConstraint implements IConstraint {
    protected UUID selfUuid;
    protected UUID aUuid, bUuid;

    public AbstractBiConstraint(UUID inSelfUuid, UUID inAUuid, UUID inBUuid) {
        selfUuid = inSelfUuid;
        aUuid = inAUuid;
        bUuid = inBUuid;
    }

    @Override
    public UUID getUuid() { return selfUuid; }
    @Override
    public boolean involveShip(UUID shipUuid) { return aUuid.equals(shipUuid) || bUuid.equals(shipUuid); }
    @Override
    public boolean involveVsShip(long inVsShipId) { return false; }

    protected <TS extends ISandBoxShip> TS getA(ISandBoxWorld<TS> world) { return world.getShipIncludeVSAndGround(aUuid); }
    protected <TS extends ISandBoxShip> TS getB(ISandBoxWorld<TS> world) { return world.getShipIncludeVSAndGround(bUuid); }
}
