package com.lancas.vswap.content.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;

import java.util.Optional;

public class IgnoreFluidExplosionDamageCalculator extends ExplosionDamageCalculator {
    public IgnoreFluidExplosionDamageCalculator() {
    }

    @Override
    public Optional<Float> getBlockExplosionResistance(Explosion exp, BlockGetter blockGetter, BlockPos bp, BlockState state, FluidState fluid) {
        if (state.isAir())
            return Optional.empty();

        if (!fluid.isEmpty())
            return Optional.of(0f);

        return Optional.of(state.trySetValue(BlockStateProperties.WATERLOGGED, false).getExplosionResistance(blockGetter, bp, exp));
        //return state.isAir() && fluid.isEmpty() ? Optional.empty() : Optional.of(Math.max(state.getExplosionResistance(blockGetter, bp, exp), fluid.getExplosionResistance(blockGetter, bp, exp)));
    }

    @Override
    public boolean shouldBlockExplode(Explosion p_46094_, BlockGetter p_46095_, BlockPos p_46096_, BlockState p_46097_, float p_46098_) {
        return true;
    }
}