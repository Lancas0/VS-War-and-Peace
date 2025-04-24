package com.lancas.vs_wap.subproject.sandbox;

import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;

import java.util.UUID;

public interface ISandBoxWorld {
    public ISandBoxShip getShip(UUID uuid);
}
