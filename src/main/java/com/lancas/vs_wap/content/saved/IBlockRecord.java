package com.lancas.vs_wap.content.saved;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
public interface IBlockRecord {

    public default void onTick() {}
}
