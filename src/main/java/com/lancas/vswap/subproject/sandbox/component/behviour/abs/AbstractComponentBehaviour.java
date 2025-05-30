package com.lancas.vswap.subproject.sandbox.component.behviour.abs;

/*
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentBehaviour;
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.nbt.CompoundTag;

public abstract class AbstractComponentBehaviour<D extends IComponentData<D>> implements IComponentBehaviour<D> {
    public ISandBoxShip ship;
    protected final D data;

    protected AbstractComponentBehaviour() {
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
*/