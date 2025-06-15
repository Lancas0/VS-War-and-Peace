package com.lancas.vswap.subproject.sandbox.ship;

import net.minecraft.server.level.ServerLevel;

public interface IServerSandBoxShip extends ISandBoxShip {
    public void serverTick(ServerLevel level);
}
