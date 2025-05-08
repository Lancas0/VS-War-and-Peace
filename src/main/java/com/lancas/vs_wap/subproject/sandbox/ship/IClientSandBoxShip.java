package com.lancas.vs_wap.subproject.sandbox.ship;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;

public interface IClientSandBoxShip extends ISandBoxShip {
    public void clientTick(ClientLevel level);
    public void physTick();
}
