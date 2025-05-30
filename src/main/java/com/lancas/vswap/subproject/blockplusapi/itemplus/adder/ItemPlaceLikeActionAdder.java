package com.lancas.vswap.subproject.blockplusapi.itemplus.adder;

import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.subproject.blockplusapi.itemplus.ItemAdder;
import com.lancas.vswap.subproject.blockplusapi.util.Action;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public abstract class ItemPlaceLikeActionAdder implements ItemAdder {
    protected Action.InteractionAction<UseOnContext> preAction = new Action.InteractionAction<UseOnContext>() {
        @Override
        public InteractionResult pre(UseOnContext ctx, InteractionResult soFar, Dest<Boolean> cancel) {
            ItemStack stack = ctx.getItemInHand();
            Level level = ctx.getLevel();
            Player player = ctx.getPlayer();
            BlockPos useOn = ctx.getClickedPos();

            BlockPos toPlaceAt = useOn.relative(ctx.getClickedFace());
            if (toPlaceAt.getY() >= level.getMinBuildHeight() && toPlaceAt.getY() <= level.getMaxBuildHeight() && level.getBlockState(toPlaceAt).isAir()) {
                return placeLikeAction(stack, level, player, toPlaceAt, ctx);
            }
            return InteractionResult.PASS;
        }
    };
    protected Action.InteractionAction<UseOnContext> postAction = new Action.InteractionAction<UseOnContext>() {
        @Override
        public InteractionResult pre(UseOnContext ctx, InteractionResult soFar, Dest<Boolean> cancel) {
            ItemStack stack = ctx.getItemInHand();
            Level level = ctx.getLevel();
            Player player = ctx.getPlayer();
            BlockPos useOn = ctx.getClickedPos();

            BlockPos toPlaceAt = useOn.relative(ctx.getClickedFace());
            if (toPlaceAt.getY() >= level.getMinBuildHeight() && toPlaceAt.getY() <= level.getMaxBuildHeight() && level.getBlockState(toPlaceAt).isAir()) {
                return placeLikeAction(stack, level, player, toPlaceAt, ctx);
            }
            return InteractionResult.PASS;
        }
    };

    /*@Override
    public InteractionAction onItemUseFirst() { return preAction; }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, Level level, Player player, BlockPos useOn, UseOnContext ctx, InteractionResult soFar) {
        //return null;
        BlockPos toPlaceAt = useOn.relative(ctx.getClickedFace());
        if (toPlaceAt.getY() >= level.getMinBuildHeight() && toPlaceAt.getY() <= level.getMaxBuildHeight() && level.getBlockState(toPlaceAt).isAir()) {
            return placeLikeAction(stack, level, player, toPlaceAt, ctx, soFar);
        }
        return InteractionResult.PASS;
    }*/
    public static abstract class PRE extends ItemPlaceLikeActionAdder {
        @Override
        public Action.InteractionAction<UseOnContext> useOn() { return preAction; }
    }
    public static abstract class POST extends ItemPlaceLikeActionAdder {
        @Override
        public Action.InteractionAction<UseOnContext> useOn() { return postAction; }
    }

    @Override
    public abstract Action.InteractionAction<UseOnContext> useOn();

    public abstract InteractionResult placeLikeAction(ItemStack stack, Level level, Player player, BlockPos placeAt, UseOnContext ctx);
}
