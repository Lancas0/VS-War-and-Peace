package com.lancas.vs_wap.subproject.sandbox.component.behviour;

import com.lancas.vs_wap.subproject.sandbox.component.data.IComponentData;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedComponentData;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

public abstract class AbstractComponentBehaviour<D extends IComponentData<D> & IExposedComponentData<D>> implements IComponentBehaviour<D> {
    public SandBoxServerShip ship;
    protected final D data;

    protected AbstractComponentBehaviour() {
        data = makeData();
    }
    protected abstract D makeData();


    //@Override
    //public IExposedComponentData<D> getExposedData() { return data; }

    @Override
    public CompoundTag getSavedData() { return data.saved(); }
    @Override
    public void loadData(SandBoxServerShip inShip, D src) {
        ship = inShip;
        data.copyData(src);
        data.overwriteDataByShip(ship);
    }
}
