package com.lancas.vswap.subproject.blockplusapi.blockplus.adder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class ToggleLikeBlockAdder extends AbstractPropertyAdder<Boolean> {

    @Override
    public Property<Boolean> getProperty() { return BlockStateProperties.POWERED; }

    @Override
    public Boolean getDefaultValue() { return false; }

    @Override
    public boolean provideRedstoneSrcVerification(BlockState state) { return true; }

    @Override
    public int getRedstoneModifyValue(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return state.getValue(BlockStateProperties.POWERED) ? 15 : 0;
    }


}
