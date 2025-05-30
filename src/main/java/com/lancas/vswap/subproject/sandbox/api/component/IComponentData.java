package com.lancas.vswap.subproject.sandbox.api.component;

import com.lancas.vswap.subproject.sandbox.api.ISavedObject;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;

//data不参与事件处理，且保证在船体初始化之前可以(目前不完全)安全地操控值
//behaviour保证能够正确处理事件
public interface IComponentData<T extends IComponentData<T>> extends ISavedObject<IComponentData<T>> {
    //todo remove
    public T copyData(T src);
    public default IComponentData<T> overwriteDataByShip(ISandBoxShip ship) { return this; }
}
