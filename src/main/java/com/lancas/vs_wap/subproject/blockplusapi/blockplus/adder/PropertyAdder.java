package com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class PropertyAdder<T extends Comparable<T>> extends AbstractPropertyAdder<T> {
    private final Property<T> property;
    private final T defaultValue;

    public PropertyAdder(Property<T> inProperty, T inDefaultValue) {
        property = inProperty;
        defaultValue = inDefaultValue;
    }

    @Override
    public Property<T> getProperty() {
        return property;
    }
    @Override
    public T getDefaultValue() {
        return defaultValue;
    }
    /*@Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx, BlockState dest) {
        //todo may break nbt?
        return dest.setValue(property, defaultValue);
    }*/
}
