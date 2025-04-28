package com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class RedstonePowerReceiveAdder extends AbstractPropertyAdder<Integer> {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    private final boolean linkable;
    public RedstonePowerReceiveAdder(boolean inLinkable) {
        linkable = inLinkable;
    }

    @Override
    public Property<Integer> getProperty() { return POWER; }
    @Override
    public Integer getDefaultValue() { return 0; }


    @Override
    public void onNeighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        int signal = level.getBestNeighborSignal(pos);

        if (signal != state.getValue(POWER)) {
            level.setBlock(pos, state.setValue(POWER, signal), Block.UPDATE_ALL);

            //if (onPowerChangeAct != null)
            //    onPowerChangeAct.apply(level, pos, state, hasSignal);
            onRedstoneSignalChange(level, pos, state, signal);
        }
    }
    public boolean provideRedstoneSrcVerification(BlockState state) { return linkable; }

    public abstract void onRedstoneSignalChange(Level level, BlockPos pos, BlockState state, int signal);

}
