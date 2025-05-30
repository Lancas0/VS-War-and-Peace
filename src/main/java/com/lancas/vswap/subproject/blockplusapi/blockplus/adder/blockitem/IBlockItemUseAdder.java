package com.lancas.vswap.subproject.blockplusapi.blockplus.adder.blockitem;

/*
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.itemplus.ItemAdder;
import com.lancas.vswap.subproject.blockplusapi.itemplus.adder.ItemUseFirstAdder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public interface IBlockItemUseAdder extends IBlockAdder, IBlockItemAdderSupplier {

    public InteractionResult onItemUseFirst(ItemStack stack, Level level, Player player, BlockPos useOn, UseOnContext ctx, InteractionResult soFar);

    @Override
    public default void supplyItemAdders(List<ItemAdder> adderList) {
        var superAdder = this;

        adderList.add(new ItemUseFirstAdder() {
            @Override
            public InteractionResult onItemUseFirst(ItemStack stack, Level level, Player player, BlockPos useOn, UseOnContext ctx, InteractionResult soFar) {
                return superAdder.onItemUseFirst(stack, level, player, useOn, ctx, soFar);
            }
        });
    }
}
*/