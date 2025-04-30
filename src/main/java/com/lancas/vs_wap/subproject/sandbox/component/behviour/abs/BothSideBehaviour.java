package com.lancas.vs_wap.subproject.sandbox.component.behviour.abs;

import com.lancas.vs_wap.subproject.sandbox.api.component.IClientBehaviour;
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentBehaviour;
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vs_wap.subproject.sandbox.api.component.IServerBehaviour;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

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
    public void loadData(ISandBoxShip inShip, D src) {
        ship = inShip;
        data.copyData(src);
        data.overwriteDataByShip(ship);
    }
}
