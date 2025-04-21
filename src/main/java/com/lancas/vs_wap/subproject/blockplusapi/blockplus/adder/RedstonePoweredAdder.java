package com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class RedstonePoweredAdder extends AbstractPropertyAdder<Boolean> {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    @FunctionalInterface
    public interface OnPowerChangeAction {
        public void apply(Level level, BlockPos pos, BlockState state, boolean hasSignal);
    }

    private final OnPowerChangeAction onPowerChangeAct;
    private final boolean linkable;
    public RedstonePoweredAdder(OnPowerChangeAction inOnPowerChangeAct, boolean inLinkable) {
        onPowerChangeAct = inOnPowerChangeAct;
        linkable = inLinkable;
    }

    @Override
    public Property<Boolean> getProperty() { return POWERED; }
    @Override
    public Boolean getDefaultValue() { return false; }



    @Override
    public void onNeighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        boolean hasSignal = level.hasNeighborSignal(pos);
        if (hasSignal != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, hasSignal), Block.UPDATE_ALL);

            if (onPowerChangeAct != null)
                onPowerChangeAct.apply(level, pos, state, hasSignal);
        }
    }
    public boolean provideRedstoneSrcVerification(BlockState state) { return linkable; }


}
