package com.lancas.vswap.content.block.blocks.industry.dock;

import com.lancas.vswap.WapCommonConfig;
import com.lancas.vswap.foundation.handler.multiblock.IMultiContainerType;

public class DockMultiContainerType implements IMultiContainerType {
    public static final DockMultiContainerType INSTANCE = new DockMultiContainerType();
    private DockMultiContainerType() {}

    @Override
    public int getMaxHeight() { return 1; }

    @Override
    public int getMaxLength() {
        int maxLen = WapCommonConfig.dockMaxLength;
        return maxLen;
    }

    @Override
    public int getMaxWidth() {
        int maxWidth = WapCommonConfig.dockMaxWidth;
        return maxWidth;
    }
}
