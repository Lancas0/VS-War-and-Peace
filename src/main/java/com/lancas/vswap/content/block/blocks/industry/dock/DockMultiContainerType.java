package com.lancas.vswap.content.block.blocks.industry.dock;

import com.lancas.vswap.foundation.handler.multiblock.IMultiContainerType;

public class DockMultiContainerType implements IMultiContainerType {
    public static final DockMultiContainerType INSTANCE = new DockMultiContainerType();
    private DockMultiContainerType() {}

    @Override
    public int getMaxHeight() {
        return 1;  //not used now and set later
    }

    @Override
    public int getMaxLength() {
        return 16;
    }

    @Override
    public int getMaxWidth() {
        return 16;
    }
}
