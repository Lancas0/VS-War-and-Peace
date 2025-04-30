package com.lancas.vs_wap.subproject.sandbox;

import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;

import java.util.UUID;
import java.util.stream.Stream;

public interface ISandBoxWorld {
    public ISandBoxShip getShip(UUID uuid);

    //use stream because it don't provide remove operation.
    //user must use markShipDeleted, which provide safety.
    public Stream<ISandBoxShip> allShips();
    //public boolean shouldRun();

}
