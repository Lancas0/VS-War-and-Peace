package com.lancas.vswap.obsolete.ship;

import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.PhysShip;

//todo can accept args to avoid repeat calculation
public interface ISubForceInducer<T> {
    public void applyForces(@NotNull PhysShip physShip);
}