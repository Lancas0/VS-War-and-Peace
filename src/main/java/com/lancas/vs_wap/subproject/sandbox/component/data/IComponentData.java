package com.lancas.vs_wap.subproject.sandbox.component.data;

import com.lancas.vs_wap.subproject.sandbox.INbtSavedObject;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedComponentData;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;

//data不参与事件处理，且保证在船体初始化之前可以(部分安全地)操控值
//behaviour保证能够正确处理事件
public interface IComponentData<T extends IComponentData<T> & IExposedComponentData<T>> extends INbtSavedObject<IComponentData<T>> {
    public T copyData(T src);
    public default IComponentData<T> overwriteDataByShip(SandBoxServerShip ship) { return this; }
}
