package com.lancas.vswap.foundation.handler.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

public interface IMultiContainerBE {
    public boolean isPartOf(IMultiContainerType type);

    public void setContinuousHeight(int h);  //continuous height from bottom, start from 1
    public int getContinuousHeight();

    //public void setContinuousXLen(int l);  //continuous xLen from xLower, start from 1
    //public int getContinuousXLen();

    public void setContinuousZLen(int l);
    public int getContinuousZLen();

    public void onMultiContainerReset();
    public void setController(BlockPos controller);
    public void setSize(int length, int width, Direction.Axis lengthAxis, int height);

    public int getLengthOfAxis(Direction.Axis axis);

    public @NotNull BlockPos getController();
    public @NotNull IMultiContainerBE getControllerBE();

    public boolean isController();

    public void setDirty();

    public void onIncludePart(BlockPos bp, IMultiContainerBE part);
}
