package com.lancas.vswap.subproject.blockplusapi.itemplus.adder;

import com.lancas.vswap.subproject.blockplusapi.itemplus.ItemAdder;
import com.lancas.vswap.subproject.blockplusapi.util.Action;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.jetbrains.annotations.NotNull;

public class BlockItemOnPlaceAdder implements ItemAdder {
    protected Action.InteractionAction<BlockPlaceContext> action;
    public BlockItemOnPlaceAdder(@NotNull Action.InteractionAction<BlockPlaceContext> inAction) {
        action = inAction;
    }

    @Override
    public Action.InteractionAction<BlockPlaceContext> onPlace() { return action; }
}
