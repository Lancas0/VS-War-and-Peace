package com.lancas.vs_wap.subproject.sandbox.component.behviour.abs;

/*
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentBehaviour;
import com.lancas.vs_wap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vs_wap.subproject.sandbox.api.component.IExposedComponentData;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.nbt.CompoundTag;

import java.util.concurrent.atomic.AtomicReference;

public abstract class ConcurrentBehaviour<D extends IComponentData<D> & IExposedComponentData<D>> implements IComponentBehaviour<D> {
    public SandBoxServerShip ship;
    protected final AtomicReference<D> data;

    protected ConcurrentBehaviour() {
        data = new AtomicReference<>(makeInitialData());
    }
    protected abstract D makeInitialData();


    //@Override
    //public IExposedComponentData<D> getExposedData() { return data; }

    @Override
    public CompoundTag getSavedData() { return data.get().saved(); }
    @Override
    public void loadData(SandBoxServerShip inShip, D src) {
        ship = inShip;
        data.updateAndGet(d -> {
            d.copyData(src);
            d.overwriteDataByShip(ship);
            return d;
        });
    }
}
*/