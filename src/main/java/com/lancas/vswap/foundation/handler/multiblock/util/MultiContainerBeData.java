package com.lancas.vswap.foundation.handler.multiblock.util;

import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiContainerBeData implements INBTSerializable<CompoundTag> {
    public @Nullable BlockPos controller = null;

    public int continuousHeight = 1;
    public int continuousZLen = 1;

    public int length = 1;
    public int width = 1;
    public int height = 1;
    public @NotNull Direction.Axis lengthAxis = Direction.Axis.X;

    /*public void reset() {
        controller = null;
        continuousHeight = continuousZLen = 1;
        length = width = height = 1;
        lengthAxis = Direction.Axis.X;
    }*/

    /*public void setController(BlockPos controller) { this.controller = controller; }

    public void setContinuousHeight(int h) { continuousHeight = h; }
    public int getContinuousHeight() { return continuousHeight; }

    public void setContinuousZLen(int l) { continuousZLen = l; }
    public int getContinuousZLen() { return continuousZLen; }
    */

    public void setSize(int length, int width, Direction.Axis lengthAxis, int height) {
        this.length = length;  //todo handle lengthAxis
        this.width = width;
        this.height = height;

        this.lengthAxis = lengthAxis;
    }
    public int getLengthOfAxis(Direction.Axis axis) {
        return switch (axis) {
            case X, Z -> lengthAxis == axis ? length : width;
            case Y -> height;
        };
    }




    @Override
    public CompoundTag serializeNBT() {
        return new NbtBuilder()
            .putIfNonNull("controller", controller, NbtBuilder::putBlockPos)
            .putInt("cz", continuousZLen)
            .putInt("ch", continuousHeight)
            .putInt("length", length)
            .putInt("width", width)
            .putInt("height", height)
            .putEnum("length_axis", lengthAxis)
            .get();
    }
    @Override
    public void deserializeNBT(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readDoIfExist("controller", v -> controller = v, NbtBuilder::getBlockPos)
            .readIntDo("cz", v -> continuousZLen = v)
            .readIntDo("ch", v -> continuousHeight = v)
            .readIntDo("length", v -> length = v)
            .readIntDo("width", v -> width = v)
            .readIntDo("height", v -> height = v)
            .readEnumDo("length_axis", Direction.Axis.class, v -> lengthAxis = v);
    }
}
