package com.lancas.vs_wap.subproject.sandbox;

import com.lancas.vs_wap.subproject.sandbox.compact.mc.GroundShipWrapped;
import com.lancas.vs_wap.subproject.sandbox.compact.vs.WrappedVsShip;
import com.lancas.vs_wap.subproject.sandbox.constraint.SandBoxConstraintSolver;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import net.minecraft.world.level.Level;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.UUID;
import java.util.stream.Stream;

public interface ISandBoxWorld<TShip extends ISandBoxShip> {
    public Level getMcLevel();

    public TShip getShip(UUID uuid);
    public TShip getShipIncludeVS(UUID uuid);
    public TShip getShipIncludeVSAndGround(UUID uuid);
    public WrappedVsShip wrapOrGetVs(Ship vsShip);
    public GroundShipWrapped wrapOrGetGround();

    //use stream because it don't provide remove operation.
    //user must use markShipDeleted, which provide safety.
    public Stream<TShip> allShips();
    public Stream<TShip> allShipsIncludeVs();
    //public boolean shouldRun();

    public SandBoxConstraintSolver getConstraintSolver();//= new SandBoxConstraintSolver();
}
