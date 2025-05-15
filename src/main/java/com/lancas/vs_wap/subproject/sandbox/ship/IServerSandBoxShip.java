package com.lancas.vs_wap.subproject.sandbox.ship;

import net.minecraft.server.level.ServerLevel;

public interface IServerSandBoxShip extends ISandBoxShip {
    public void serverTick(ServerLevel level);
    public void physTick();
}
