package com.lancas.vswap.subproject.sandbox.component.behviour.abs;

import com.lancas.vswap.subproject.sandbox.api.component.IClientBehaviour;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentBehaviour;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxClientShip;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.nbt.CompoundTag;

public abstract class ClientOnlyBehaviour<D extends IComponentData<D>> implements IComponentBehaviour<D>, IClientBehaviour<D> {
    public SandBoxClientShip ship;
    protected final D data;

    protected ClientOnlyBehaviour() {
        data = makeInitialData();
    }
    protected abstract D makeInitialData();


    //@Override
    //public IExposedComponentData<D> getExposedData() { return data; }

    @Override
    public CompoundTag getSavedData() { return data.saved(); }
    @Override
    public void loadSavedData(ISandBoxShip inShip, CompoundTag saved) {
        ship = (SandBoxClientShip)inShip;
        data.load(saved);
        data.overwriteDataByShip(ship);
    }

    @Override
    public void loadData(ISandBoxShip inShip, D dataSrc) {
        ship = (SandBoxClientShip)inShip;
        data.copyData(dataSrc);
        data.overwriteDataByShip(ship);
    }
}
