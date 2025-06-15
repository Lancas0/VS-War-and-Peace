package com.lancas.vswap.subproject.blockplusapi.itemplus;

import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.subproject.blockplusapi.util.Action;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.List;

public interface ItemAdder {
    /// block item only
    /*public default boolean updateCustomBlockEntityTag(@NotNull BlockPos pos, @NotNull Level level, Player player, @NotNull ItemStack stack, @NotNull BlockState state) {
        return true;
    }*/

    /// common
    public default void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> texts, TooltipFlag flag) {}

    public default void inventoryTick(ItemStack stack, Level level, Entity entity, int ix, boolean selecting) {}

    /*public default InteractionResult onItemUseFirst(ItemStack stack, Level level, Player player, BlockPos useOn, UseOnContext ctx, InteractionResult soFar) { return InteractionResult.PASS; }
    public default InteractionResult post() {

    }*/
    public @Nullable default Action.InteractionAction<UseOnContext> onItemUseFirst() { return null; }
    public @Nullable default Action.InteractionAction<UseOnContext> useOn() { return null; }
    public @Nullable default Action.InteractionAction<BlockPlaceContext> onPlace() { return null; }

    public default boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) { return true; }

    public default boolean foilAdder(ItemStack stack) { return false; }
}
