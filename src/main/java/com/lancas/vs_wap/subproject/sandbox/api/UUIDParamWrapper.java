package com.lancas.vs_wap.subproject.sandbox.api;

import com.lancas.vs_wap.event.api.ILazyEventParam;

import java.util.UUID;

public class UUIDParamWrapper implements ILazyEventParam<UUIDParamWrapper> {
    public final UUID uuid;
    public UUIDParamWrapper(UUID inId) { uuid = inId; }
    public static UUIDParamWrapper of(UUID inId) { return new UUIDParamWrapper(inId); }

    public UUID getUuid() { return uuid; }

    @Override
    public UUIDParamWrapper getImmutable() {
        return this;
    }
}
