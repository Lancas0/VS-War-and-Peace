package com.lancas.vswap.subproject.mstandardized;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.mixins.accessor.UseOnCtxAccessor;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.belt.item.BeltConnectorItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public interface ICustomPlaceMaterialBlock {
    public static BlockPlaceContext makePlaceCtxWithItem(UseOnContext ctx, ItemStack stack) {
        return new BlockPlaceContext(
            ctx.getLevel(),
            ctx.getPlayer(),
            ctx.getHand(),
            stack,
            ((UseOnCtxAccessor)ctx).getHitResult()
        );
    }
    //@FunctionalInterface
    //public interface CustomPlaceAction extends Function<UseOnContext, InteractionResult> {}
    public static ConcurrentHashMap<Block, Function<UseOnContext, InteractionResult>> defaultCustom = new ConcurrentHashMap<>();

    public static @Nullable Function<UseOnContext, InteractionResult> getCustomPlaceAction(Block block) {
        if (defaultCustom.isEmpty()) {
            getDefaultCustom();
        }

        if (defaultCustom.containsKey(block))
            return defaultCustom.get(block);
        if (block instanceof ICustomPlaceMaterialBlock cb)
            return cb.getCustom();
        return null;
    }
    public static void getDefaultCustom() {
        defaultCustom.put(
            AllBlocks.BRASS_BELT_FUNNEL.get(),
            ctx -> {
                if (!(AllBlocks.BRASS_FUNNEL.get().asItem() instanceof BlockItem bi)) {
                    EzDebug.warn("Brass Funnel item is not BlockItem!");
                    return InteractionResult.FAIL;
                }
                return bi.place(makePlaceCtxWithItem(ctx, bi.getDefaultInstance()));
            }
        );
        defaultCustom.put(
            AllBlocks.ANDESITE_BELT_FUNNEL.get(),
            ctx -> {
                if (!(AllBlocks.ANDESITE_FUNNEL.get().asItem() instanceof BlockItem bi)) {
                    EzDebug.warn("Brass Funnel item is not BlockItem!");
                    return InteractionResult.FAIL;
                }
                return bi.place(makePlaceCtxWithItem(ctx, bi.getDefaultInstance()));
            }
        );
        defaultCustom.put(
            AllBlocks.BELT.get(),
            ctx -> {
                return InteractionResult.FAIL;  //FIXME simple return fail for belt now, fix it later
            }
        );
    }

    //The UseOnContext will be prime UseOnCtx: item is MsItem
    public Function<UseOnContext, InteractionResult> getCustom();
}
