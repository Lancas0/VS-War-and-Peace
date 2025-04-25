package com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder;

import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class AbstractPropertyAdder<T extends Comparable<T>> implements IBlockAdder {
    public abstract Property<T> getProperty();
    public abstract T getDefaultValue();

    /*@Override
    public void onInit(BlockPlus thisBlock) {
        thisBlock.acceptPropertyForDefaultState(getProperty(), getDefaultValue());
    }*/

    @Override
    public void onCreateBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(getProperty());
        //defaultState.setValue(getProperty(), getDefaultValue());
    }
    @Override
    public BlockState setValueForDefaultState(BlockState defaultState) {
        return defaultState.setValue(getProperty(), getDefaultValue());
    }
}
