package com.lancas.vswap.subproject.blockplusapi.itemplus.adder;

import com.lancas.vswap.subproject.blockplusapi.itemplus.ItemAdder;
import com.lancas.vswap.subproject.blockplusapi.util.Action;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class ItemUseOnAdder implements ItemAdder {
    protected Action.InteractionAction<UseOnContext> action;
    public ItemUseOnAdder(@NotNull Action.InteractionAction<UseOnContext> inAction) {
        action = inAction;
    }

    @Override
    public Action.InteractionAction<UseOnContext> useOn() { return action; }
}
