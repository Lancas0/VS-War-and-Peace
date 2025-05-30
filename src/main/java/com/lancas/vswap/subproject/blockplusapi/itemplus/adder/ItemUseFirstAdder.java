package com.lancas.vswap.subproject.blockplusapi.itemplus.adder;

import com.lancas.vswap.subproject.blockplusapi.itemplus.ItemAdder;
import com.lancas.vswap.subproject.blockplusapi.util.Action;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class ItemUseFirstAdder implements ItemAdder {
	protected Action.InteractionAction<UseOnContext> action;
	public ItemUseFirstAdder(@NotNull Action.InteractionAction<UseOnContext> inAction) {
		action = inAction;
	}

	@Override
	public Action.InteractionAction<UseOnContext> onItemUseFirst() { return action; }
	//@Override
	//public abstract InteractionResult onItemUseFirst(ItemStack stack, Level level, Player player, BlockPos useOn, UseOnContext ctx, InteractionResult soFar);


}
