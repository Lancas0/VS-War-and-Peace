package com.lancas.vswap.subproject.blockplusapi.itemplus.exp;

/*
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.subproject.blockplusapi.itemplus.ItemAdder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public abstract class ItemPlus extends Item {
    public static final Hashtable<Class<? extends ItemPlus>, List<ItemAdder>> addersCache = new Hashtable<>();
    public static List<ItemAdder> addersIfAbsent(Class<? extends ItemPlus> type, Supplier<List<ItemAdder>> addersSupplier) {
        if (!addersCache.containsKey(type))
            addersCache.put(type, addersSupplier.get());

        return addersCache.get(type);
    }


    public ItemPlus(Properties p_41383_) {
        super(p_41383_);
    }
    public abstract List<ItemAdder> getAdders();

    protected static void combineInteractionResult(InteractionResult a, InteractionResult b, Dest<InteractionResult> dest) {
        if (a == InteractionResult.FAIL || b == InteractionResult.FAIL) {
            dest.set(InteractionResult.FAIL);
            return;
        }

        if (a.consumesAction() || b.consumesAction()) {
            dest.set(InteractionResult.CONSUME);
            return;
        }

        dest.set(InteractionResult.PASS);
    }
    protected static InteractionResult interactionActionRoutine(List<ItemAdder> adders, TriFunction<ItemAdder, InteractionResult, Dest<Boolean>, InteractionResult> pre, Supplier<InteractionResult> original, TriFunction<ItemAdder, InteractionResult, Dest<Boolean>, InteractionResult> post) {
        Dest<InteractionResult> soFar = new Dest<>(InteractionResult.PASS);
        Dest<Boolean> cancel = new Dest<>(false);

        adders.forEach(a -> {
            if (cancel.get())
                return;

            InteractionResult result = pre.apply(a, soFar.get(), cancel);
            combineInteractionResult(result, soFar.get(), soFar);
        });

        if (cancel.get())
            return soFar.get();

        //actually useless
        InteractionResult oriResult = original.get();
        combineInteractionResult(oriResult, soFar.get(), soFar);

        adders.forEach(a -> {
            if (cancel.get())
                return;

            InteractionResult result = post.apply(a, soFar.get(), cancel);
            combineInteractionResult(result, soFar.get(), soFar);
        });

        return soFar.get();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> texts, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, texts, flag);
        getAdders().forEach(a -> a.appendHoverText(stack, level, texts, flag));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int ix, boolean selecting) {
        super.inventoryTick(stack, level, entity, ix, selecting);
        getAdders().forEach(a -> a.inventoryTick(stack, level, entity, ix, selecting));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
        return interactionActionRoutine(
            getAdders(),
            (adder, soFar, cancel) -> adder.onItemUseFirst().pre(ctx, soFar, cancel),
            () -> super.onItemUseFirst(ctx.getItemInHand(), ctx),
            (adder, soFar, cancel) -> adder.onItemUseFirst().post(ctx, soFar, cancel)
        );
    }
    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext ctx) {
        return interactionActionRoutine(
            getAdders(),
            (adder, soFar, cancel) -> adder.useOn().pre(ctx, soFar, cancel),
            () -> super.useOn(ctx),
            (adder, soFar, cancel) -> adder.useOn().post(ctx, soFar, cancel)
        );
    }

    @Override
    public boolean canAttackBlock(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player) {
        //anyone return false will lead to don't attack block.

        AtomicBoolean canAttack = new AtomicBoolean(super.canAttackBlock(state, level, pos, player));
        //don't care base value and always foreach all adders - sometimes critical logic in canAttackBlock() of adders
        getAdders().forEach(a -> {
            canAttack.set(canAttack.get() && a.canAttackBlock(state, level, pos, player));
        });
        return canAttack.get();
    }
}
*/