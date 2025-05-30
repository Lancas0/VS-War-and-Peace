package com.lancas.vswap.subproject.sandbox.api.component;

import net.minecraft.server.level.ServerLevel;

public interface IServerAsyncLogicBehaviour<D extends IComponentData<D>> extends IServerBehaviour<D> {
    public void serverAsyncLogicTick();
}
