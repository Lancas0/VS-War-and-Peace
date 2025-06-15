package com.lancas.vswap.subproject.pondervs.instructions;

import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.ponder.PonderWorld;
import com.simibubi.create.foundation.ponder.Selection;
import com.simibubi.create.foundation.ponder.instruction.WorldModifyInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class UnLimitedWorldModifyInstruction extends WorldModifyInstruction {
    private boolean modificated = false;
    private final BiFunction<BlockPos, BlockState, BlockState> stateTransformer;
    private final Predicate<BlockState> particlePredicator;
    public UnLimitedWorldModifyInstruction(Selection selection, BiFunction<BlockPos, BlockState, BlockState> inStateTransformer, Predicate<BlockState> inParticlePredicator) {
        super(selection);
        stateTransformer = inStateTransformer;
        particlePredicator = inParticlePredicator;
    }

    @Override
    protected void runModification(Selection selection, PonderScene scene) {
        PonderWorld world = scene.getWorld();
        selection.forEach((pos) -> {
            BlockState prevState = world.getBlockState(pos);
            BlockState postState = stateTransformer.apply(pos, prevState);

            if (prevState == postState)
                return;

            modificated = true;
            world.setBlockAndUpdate(pos, this.stateTransformer.apply(pos, prevState));

            if (particlePredicator.test(prevState)) {
                world.addBlockDestroyEffects(pos, prevState);
            }
        });
    }

    @Override
    protected boolean needsRedraw() { return modificated; }
}
