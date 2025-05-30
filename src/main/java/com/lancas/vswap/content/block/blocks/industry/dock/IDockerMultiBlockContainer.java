package com.lancas.vswap.content.block.blocks.industry.dock;

import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public interface IDockerMultiBlockContainer extends IMultiBlockEntityContainer {
    @Override
    BlockPos getController();

    @Override
    <T extends BlockEntity & IMultiBlockEntityContainer> T getControllerBE();

    @Override
    boolean isController();

    @Override
    void setController(BlockPos controller);

    @Override
    void removeController(boolean keepContents);

    @Override
    BlockPos getLastKnownPos();

    @Override
    void preventConnectivityUpdate();
    @Override
    void notifyMultiUpdated();

    @Override
    default void setExtraData(@Nullable Object data) { }

    @Override @Nullable
    default Object getExtraData() {
        return null;
    }
    @Override
    default Object modifyExtraData(Object data) {
        return data;
    }

    @Override
    public default Direction.Axis getMainConnectionAxis() { return Direction.Axis.Y; }
    @Override
    public default Direction.Axis getMainAxisOf(BlockEntity be) { return Direction.Axis.Y; }

    @Override
    default int getMaxLength(Direction.Axis longAxis, int width) {
        if (longAxis.isVertical())
            return 1;
        return getMaxWidth();  //todo is it right?
    }

    @Override
    default int getMaxWidth() { return 8; }

    @Override
    default int getHeight() { return 1; }

    @Override
    default void setHeight(int height) {  }  //do nothing because dock must have height 1

    @Override
    int getWidth();

    @Override
    void setWidth(int width);
}
