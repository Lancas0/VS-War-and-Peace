package com.lancas.vswap.subproject.sandbox.api.component;

public interface IDataExposedBehaviour<D extends IComponentData<D>> {
    public D getData();
}
