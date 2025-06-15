package com.lancas.vswap.subproject.sandbox.obsolete;

/*
import com.lancas.vswap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vswap.subproject.sandbox.constraint.base.IConstraint;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class BiCompoundConstraint implements IConstraint {
    private final UUID selfUuid;
    private final IConstraint c1;
    private final IConstraint c2;

    private BiCompoundConstraint() { this(null, null, null); }
    public BiCompoundConstraint(UUID inSelfUuid, IConstraint inC1, IConstraint inC2) {
        selfUuid = inSelfUuid;
        c1 = inC1;
        c2 = inC2;
    }

    @Override
    public UUID getUuid() { return selfUuid; }

    @Override
    public boolean involveShip(UUID inShipUuid) { return c1.involveShip(inShipUuid) || c2.involveShip(inShipUuid); }
    @Override
    public boolean involveVsShip(long inVsShipId) { return c1.involveVsShip(inVsShipId) || c2.involveVsShip(inVsShipId); }

    @Override
    public void project(ISandBoxWorld<?> world) {
        c1.project(world);
        c2.project(world);
    }

    @Override
    public void tick(Level level, SandBoxConstraintSolver solver) {
        c1.tick(level, solver);
        c2.tick(level, solver);
    }

    public IConstraint getFirst() { return c1; }
    public IConstraint getSecond() { return c2; }

}
*/