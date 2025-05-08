package com.lancas.vs_wap.subproject.sandbox.component.behviour.abs;

import com.lancas.vs_wap.subproject.sandbox.api.component.IClientBehaviour;
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentBehaviour;
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vs_wap.subproject.sandbox.api.component.IServerBehaviour;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import net.minecraft.nbt.CompoundTag;

public abstract class BothSideBehaviour<D extends IComponentData<D>> implements IComponentBehaviour<D>, IServerBehaviour<D>, IClientBehaviour<D> {
    public ISandBoxShip ship;
    protected final D data;

    protected BothSideBehaviour() {
        data = makeInitialData();
    }
    protected abstract D makeInitialData();


    //@Override
    //public IExposedComponentData<D> getExposedData() { return data; }

    @Override
    public CompoundTag getSavedData() { return data.saved(); }
    @Override
    public void loadSavedData(ISandBoxShip inShip, CompoundTag saved) {
        ship = inShip;
        //data.copyData(saved);
        data.load(saved);
        data.overwriteDataByShip(ship);
    }
    @Override
    public void loadData(ISandBoxShip inShip, D dataSrc) {
        ship = inShip;
        //data.copyData(saved);
        data.copyData(dataSrc);
        data.overwriteDataByShip(ship);
    }
}
