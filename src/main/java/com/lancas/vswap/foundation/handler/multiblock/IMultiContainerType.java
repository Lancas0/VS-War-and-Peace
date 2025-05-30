package com.lancas.vswap.foundation.handler.multiblock;

public interface IMultiContainerType {
    public int getMaxHeight();
    public int getMaxLength();  //length must >= width
    public int getMaxWidth();
}
