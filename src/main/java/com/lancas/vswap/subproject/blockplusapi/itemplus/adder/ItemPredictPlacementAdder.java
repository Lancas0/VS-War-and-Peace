package com.lancas.vswap.subproject.blockplusapi.itemplus.adder;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.subproject.blockplusapi.util.Action;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class ItemPredictPlacementAdder implements ItemInventoryTickAdder {
    public static enum PlacementStage {
        Predict,
        PreUseFirst,
        PostUseFirst,
        PreUseOn,
        PostUseOn,
        PrePlace,
        PostPlace
    };
    private final Set<PlacementStage> activeStages = new HashSet<>();
    public ItemPredictPlacementAdder(Collection<PlacementStage> inActiveStages) {
        activeStages.addAll(inActiveStages);
    }

    private final Action.InteractionAction<UseOnContext> useFirst = new Action.InteractionAction<UseOnContext>() {
        @Override
        public InteractionResult pre(UseOnContext ctx, InteractionResult soFar, Dest<Boolean> cancel) {
            if (!activeStages.contains(PlacementStage.PreUseFirst))
                return InteractionResult.PASS;

            BlockPlaceContext placeCtx = new BlockPlaceContext(ctx);
            return predictPlacement(ctx.getItemInHand(), ctx.getLevel(), ctx.getPlayer(), placeCtx, PlacementStage.PreUseFirst);
        }
        @Override
        public InteractionResult post(UseOnContext ctx, InteractionResult soFar, Dest<Boolean> cancel) {
            if (!activeStages.contains(PlacementStage.PostUseFirst))
                return InteractionResult.PASS;

            BlockPlaceContext placeCtx = new BlockPlaceContext(ctx);
            return predictPlacement(ctx.getItemInHand(), ctx.getLevel(), ctx.getPlayer(), placeCtx, PlacementStage.PostUseFirst);
        }
    };
    private final Action.InteractionAction<UseOnContext> useOn = new Action.InteractionAction<UseOnContext>() {
        @Override
        public InteractionResult pre(UseOnContext ctx, InteractionResult soFar, Dest<Boolean> cancel) {
            if (!activeStages.contains(PlacementStage.PreUseOn))
                return InteractionResult.PASS;

            BlockPlaceContext placeCtx = new BlockPlaceContext(ctx);
            return predictPlacement(ctx.getItemInHand(), ctx.getLevel(), ctx.getPlayer(), placeCtx, PlacementStage.PreUseOn);
        }

        @Override
        public InteractionResult post(UseOnContext ctx, InteractionResult soFar, Dest<Boolean> cancel) {
            if (!activeStages.contains(PlacementStage.PostUseOn))
                return InteractionResult.PASS;

            BlockPlaceContext placeCtx = new BlockPlaceContext(ctx);
            return predictPlacement(ctx.getItemInHand(), ctx.getLevel(), ctx.getPlayer(), placeCtx, PlacementStage.PostUseOn);
        }
    };
    private final Action.InteractionAction<BlockPlaceContext> onPlace = new Action.InteractionAction<BlockPlaceContext>() {
        @Override
        public InteractionResult pre(BlockPlaceContext ctx, InteractionResult soFar, Dest<Boolean> cancel) {
            if (!activeStages.contains(PlacementStage.PrePlace))
                return InteractionResult.PASS;

            return predictPlacement(ctx.getItemInHand(), ctx.getLevel(), ctx.getPlayer(), ctx, PlacementStage.PrePlace);
        }

        @Override
        public InteractionResult post(BlockPlaceContext ctx, InteractionResult soFar, Dest<Boolean> cancel) {
            if (!activeStages.contains(PlacementStage.PostPlace))
                return InteractionResult.PASS;

            return predictPlacement(ctx.getItemInHand(), ctx.getLevel(), ctx.getPlayer(), ctx, PlacementStage.PostPlace);
        }
    };

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int ix, boolean selecting) {
        if (!activeStages.contains(PlacementStage.Predict)) return;
        if (!level.isClientSide) return;
        if (!selecting || !(entity instanceof LocalPlayer player)) return;

        HitResult playerHit = Minecraft.getInstance().hitResult;
        if (!(playerHit instanceof BlockHitResult blockHit)) return;

        InteractionHand hand;
        if (player.getMainHandItem() == stack) {
            hand = InteractionHand.MAIN_HAND;
        } else if (player.getOffhandItem() == stack) {
            hand = InteractionHand.OFF_HAND;
        } else {
            EzDebug.warn("something strange happen. inventoryTick.selecting is true but no hand item match itemStack");
            return;
        }

        BlockPlaceContext ctx = new BlockPlaceContext(player, hand, stack, blockHit);
        predictPlacement(stack, level, player, ctx, PlacementStage.Predict);
    }


    public abstract InteractionResult predictPlacement(ItemStack stack, Level level, Player player, BlockPlaceContext ctx, PlacementStage stage);
    @Override
    public @NotNull Action.InteractionAction<UseOnContext> onItemUseFirst() { return useFirst; }
    @Override
    public @NotNull Action.InteractionAction<UseOnContext> useOn() { return useOn; }
    @Override
    public @NotNull Action.InteractionAction<BlockPlaceContext> onPlace() { return onPlace; }
}
