package com.lancas.vswap.content.item.items;

import com.lancas.vswap.content.block.blocks.artillery.breech.IBreech;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BreechUnloader extends Item {
    public BreechUnloader(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        if (!(ctx.getLevel() instanceof ServerLevel level))
            return InteractionResult.PASS;

        BlockPos bp = ctx.getClickedPos();
        BlockState state = level.getBlockState(bp);

        if (state.getBlock() instanceof IBreech iBreech) {
            iBreech.unloadShell(level, bp, state);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
