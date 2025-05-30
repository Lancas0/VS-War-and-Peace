package com.lancas.vswap.subproject.sandbox.component.behviour.abs;

import com.lancas.vswap.subproject.sandbox.api.component.IComponentBehaviour;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vswap.subproject.sandbox.api.component.IServerBehaviour;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.nbt.CompoundTag;

public abstract class ServerOnlyBehaviour<D extends IComponentData<D>> implements IComponentBehaviour<D>, IServerBehaviour<D> {
    public SandBoxServerShip ship;
    protected final D data;

    protected ServerOnlyBehaviour() {
        data = makeInitialData();
    }
    protected abstract D makeInitialData();


    //@Override
    //public IExposedComponentData<D> getExposedData() { return data; }

    @Override
    public CompoundTag getSavedData() { return data.saved(); }
    @Override
    public void loadSavedData(ISandBoxShip inShip, CompoundTag saved) {
        ship = (SandBoxServerShip)inShip;
        data.load(saved);
        data.overwriteDataByShip(ship);
    }
    @Override
    public void loadData(ISandBoxShip inShip, D dataSrc) {
        ship = (SandBoxServerShip)inShip;
        data.copyData(dataSrc);
        data.overwriteDataByShip(ship);
    }
}
