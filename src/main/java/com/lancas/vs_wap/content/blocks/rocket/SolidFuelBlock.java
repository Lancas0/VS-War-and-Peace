package com.lancas.vs_wap.content.blocks.rocket;

import com.lancas.vs_wap.content.blocks.abstrac.DirectionalBlockImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class SolidFuelBlock extends DirectionalBlockImpl implements ISolidFuelBlock {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public SolidFuelBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(LIT, false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }

    @Override //about 5s
    public int getMaxBurnTicks() { return 100; }
    @Override
    public void setAsLited(ServerLevel level, BlockPos pos) {
        //todo or remove
        //level.setBlock(pos, EinherjarBlocks.SOLID_FUEL.get().defaultBlockState()
        //    .setValue(SolidFuelBlock.LIT, true), 3);
    }
}