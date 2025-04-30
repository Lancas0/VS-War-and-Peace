package com.lancas.vs_wap.subproject.sandbox.component.behviour.abs;

import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentBehaviour;
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vs_wap.subproject.sandbox.api.component.IServerBehaviour;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
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
    public void loadData(ISandBoxShip inShip, D src) {
        ship = (SandBoxServerShip)inShip;
        data.copyData(src);
        data.overwriteDataByShip(ship);
    }
}
