package com.lancas.vs_wap.subproject.sandbox.compact.vs.constraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vs_wap.subproject.sandbox.constraint.base.IConstraint;
import com.lancas.vs_wap.subproject.sandbox.constraint.SandBoxConstraintSolver;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.UUID;

public abstract class AbstractSaOnVsConstraint implements IConstraint {
    protected volatile UUID selfUuid;
    protected volatile long vsShipId;
    protected volatile UUID saShipUuid;

    @JsonIgnore
    @Nullable private volatile Ship vsShipCache;

    public long getVsShipId() { return vsShipId; }

    //protected AbstractOnVsConstraint() {}
    public AbstractSaOnVsConstraint(UUID inSelfUuid, long inVsShipId, UUID inSaShipUuid) {
        selfUuid = inSelfUuid;
        vsShipId = inVsShipId;
        saShipUuid = inSaShipUuid;
    }

    @Override
    public UUID getUuid() { return selfUuid; }
    @Override
    public boolean involveShip(UUID inShipUuid) { return saShipUuid.equals(inShipUuid); }
    @Override
    public boolean involveVsShip(long inVsShipId) { return vsShipId == inVsShipId; }

    @Override
    public void tick(Level level, SandBoxConstraintSolver solver) {
        vsShipCache = VSGameUtilsKt.getShipObjectWorld(level).getLoadedShips().getById(vsShipId);
        if (vsShipCache == null) {
            EzDebug.warn("remove the constraint by null vsShipCache");
            solver.markConstraintRemoved(selfUuid);
        }
    }

    @Nullable protected Ship getVsShip() { return vsShipCache; }
    protected <TS extends ISandBoxShip> TS getSaShip(ISandBoxWorld<TS> world) {
        return world.getShip(saShipUuid);
    }
}
