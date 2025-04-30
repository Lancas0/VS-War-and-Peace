package com.lancas.vs_wap.subproject.sandbox.api.component;

import net.minecraft.server.level.ServerLevel;

public interface IServerBehaviour<D extends IComponentData<D>> extends IComponentBehaviour<D> {
    public void serverTick(ServerLevel level);
}
