package com.lancas.vswap.subproject.sandbox.api.component;

public interface IDataWritableBehaviour<D extends IComponentDataWriter<D>> {
    public IComponentDataWriter<D> getDataWriter();
}
