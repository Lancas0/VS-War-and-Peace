package com.lancas.vs_wap.subproject.sandbox.component.behviour.abs;

import com.lancas.vs_wap.subproject.sandbox.api.component.IClientBehaviour;
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentBehaviour;
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxClientShip;
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
}
