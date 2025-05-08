package com.lancas.vs_wap.subproject.sandbox.ship;

import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentBehaviour;
import net.minecraft.server.level.ServerLevel;
import org.joml.primitives.AABBd;

public interface IServerSandBoxShip extends ISandBoxShip {
    public void serverTick(ServerLevel level);
    public void physTick();
}
