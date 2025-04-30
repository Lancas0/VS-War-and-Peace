package com.lancas.vs_wap.subproject.sandbox.api.component;

import net.minecraft.client.multiplayer.ClientLevel;

public interface IClientBehaviour<D extends IComponentData<D>> extends IComponentBehaviour<D> {
    public void clientTick(ClientLevel level);
}
