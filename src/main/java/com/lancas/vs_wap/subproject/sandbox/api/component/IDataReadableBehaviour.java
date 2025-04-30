package com.lancas.vs_wap.subproject.sandbox.api.component;

public interface IDataReadableBehaviour<D extends IComponentDataReader<D>> {
    public IComponentDataReader<D> getDataReader();
}
