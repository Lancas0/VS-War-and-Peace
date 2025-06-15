package com.lancas.vswap.subproject.sandbox.ship;

import net.minecraft.client.multiplayer.ClientLevel;

public interface IClientSandBoxShip extends ISandBoxShip {
    public void clientTick(ClientLevel level);
}
