package com.lancas.vswap.subproject.sandbox.ship;

/*
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxBlockClusterData;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxTransformData;

import java.util.UUID;
import java.util.function.Supplier;

public class ShipFactory {
    private Supplier<UUID> uuidSupplier;
    private Supplier<SandBoxTransformData> transformDataSupplier;
    private Supplier<SandBoxBlockClusterData> blockClusterDataSupplier;

    public ShipFactory uuid(Supplier<UUID> inUuidSupplier) {
        uuidSupplier = inUuidSupplier;
        return this;
    }
    public ShipFactory transformData(Supplier<SandBoxTransformData> dataSupplier) {
        transformDataSupplier = dataSupplier;
        return this;
    }
    public ShipFactory blockClusterData(Supplier<SandBoxBlockClusterData> dataSupplier) {
        blockClusterDataSupplier = dataSupplier;
        return this;
    }

    public SandBoxClientShip createAsClient() {
        return new SandBoxClientShip(uuidSupplier.get(), transformDataSupplier.get(), blockClusterDataSupplier.get());
    }
    public SandBoxServerShip createAsServer() {
        return new SandBoxServerShip(uuidSupplier.get(), transformDataSupplier.get(), blockClusterDataSupplier.get());
    }

}
*/