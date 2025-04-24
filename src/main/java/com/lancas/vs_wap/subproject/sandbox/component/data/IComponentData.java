package com.lancas.vs_wap.subproject.sandbox.component.data;

import com.lancas.vs_wap.subproject.sandbox.INbtSerializable;

public interface IComponentData<T extends IComponentData<T> & IExposedComponentData<T>> extends INbtSerializable<IComponentData<T>> {
    public T copyData(T src);
}
