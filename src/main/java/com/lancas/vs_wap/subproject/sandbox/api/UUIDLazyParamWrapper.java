package com.lancas.vs_wap.subproject.sandbox.api;

import com.lancas.vs_wap.event.api.ILazyEventParam;

import java.util.UUID;

public class UUIDLazyParamWrapper implements ILazyEventParam<UUIDLazyParamWrapper> {
    public final UUID uuid;
    public UUIDLazyParamWrapper(UUID inId) { uuid = inId; }
    public static UUIDLazyParamWrapper of(UUID inId) { return new UUIDLazyParamWrapper(inId); }

    public UUID getUuid() { return uuid; }

    @Override
    public UUIDLazyParamWrapper getImmutable() {
        return this;
    }
}
