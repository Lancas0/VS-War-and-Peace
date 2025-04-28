package com.lancas.vs_wap.subproject.sandbox.component.data;

import com.lancas.vs_wap.subproject.sandbox.api.ISavedLevelObject;
import com.lancas.vs_wap.subproject.sandbox.api.ISavedObject;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedComponentData;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;

//data不参与事件处理，且保证在船体初始化之前可以(目前不完全)安全地操控值
//behaviour保证能够正确处理事件
public interface IComponentData<T extends IComponentData<T> & IExposedComponentData<T>> extends ISavedObject<IComponentData<T>> {
    public T copyData(T src);
    public default IComponentData<T> overwriteDataByShip(SandBoxServerShip ship) { return this; }
}
