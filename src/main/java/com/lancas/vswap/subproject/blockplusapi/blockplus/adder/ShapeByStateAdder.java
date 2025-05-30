package com.lancas.vswap.subproject.blockplusapi.blockplus.adder;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Function;

public class ShapeByStateAdder implements IBlockAdder {
    private final Function<BlockState, VoxelShape> shapeGetter;

    public ShapeByStateAdder(Function<BlockState, VoxelShape> inGetter) {
        shapeGetter = inGetter;
    }

    public VoxelShape appendShape(BlockState state) {
        if (shapeGetter == null) return Shapes.empty();
        VoxelShape shape = shapeGetter.apply(state);

        if (shape == null) return Shapes.empty();

        return shape;
    }
}
