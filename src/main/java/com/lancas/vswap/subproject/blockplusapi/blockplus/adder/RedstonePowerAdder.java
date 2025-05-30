package com.lancas.vswap.subproject.blockplusapi.blockplus.adder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class RedstonePowerAdder extends AbstractPropertyAdder<Integer> {
    public static IntegerProperty POWER = BlockStateProperties.POWER;


    @Override
    public Property<Integer> getProperty() { return BlockStateProperties.POWER; }
    @Override
    public Integer getDefaultValue() { return 0; }


    public int getRedstoneModifyValue(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return state.getValue(POWER);
    }
    public boolean provideRedstoneSrcVerification(BlockState state) { return true; }
}
