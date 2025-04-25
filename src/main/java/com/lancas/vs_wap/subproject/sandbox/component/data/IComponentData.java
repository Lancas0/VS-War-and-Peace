package com.lancas.vs_wap.subproject.sandbox.component.data;

import com.lancas.vs_wap.subproject.sandbox.INbtSavedObject;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedComponentData;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;

public interface IComponentData<T extends IComponentData<T> & IExposedComponentData<T>> extends INbtSavedObject<IComponentData<T>> {
    public T copyData(T src);
    public default IComponentData<T> overwriteDataByShip(SandBoxServerShip ship) { return this; }
}
