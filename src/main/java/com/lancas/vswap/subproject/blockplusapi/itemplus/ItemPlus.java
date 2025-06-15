package com.lancas.vswap.subproject.blockplusapi.itemplus;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.reflect.TypeToken;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.blockitem.IBlockItemAdderSupplier;
import com.lancas.vswap.subproject.blockplusapi.util.Action;
import com.lancas.vswap.subproject.blockplusapi.util.QuadFunction;
import com.simibubi.create.AllPackets;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmPlacementPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ItemPlus<T extends Item>  {

    private static final Hashtable<BlockPlus, ItemPlus<BlockItem>> generatedBlockItemPlus = new Hashtable<>();
    //private static final Hashtable<ItemPlus<?>, Item> allItems = new Hashtable<>();
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
    /*protected static InteractionResult interactionActionRoutine(List<ItemAdder> adders, TriFunction<ItemAdder, InteractionResult, Dest<Boolean>, InteractionResult> pre, Supplier<InteractionResult> original, TriFunction<ItemAdder, InteractionResult, Dest<Boolean>, InteractionResult> post) {
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
    }*/

    private static void setActualItem(ItemPlus<BlockItem> itemPlus, Block block, Item.Properties p) {
        itemPlus.item = new BlockItem(block, p) {
            //only blockItem
            /*@Override
            protected boolean updateCustomBlockEntityTag(@NotNull BlockPos pos, @NotNull Level level, Player player, @NotNull ItemStack stack, @NotNull BlockState state) {
                super.updateCustomBlockEntityTag(pos, level, player, stack, state);

                if (!level.isClientSide && player instanceof ServerPlayer sp)
                    AllPackets.getChannel()
                        .send(PacketDistributor.PLAYER.with(() -> sp), new ArmPlacementPacket.ClientBoundRequest(pos));
                return super.updateCustomBlockEntityTag(pos, world, player, p_195943_4_, p_195943_5_);
            }*/

            @Override
            public InteractionResult place(@NotNull BlockPlaceContext ctx) {
                return Action.multiSandwichInvoke(
                    () -> itemPlus.getAdders().stream().map(ItemAdder::onPlace),
                    ctx,
                    InteractionResult.PASS,
                    super::place
                );
                /*return interactionActionRoutine(
                    itemPlus.getAdders(),
                    (adder, soFar, cancel) -> adder.onPlace().pre(ctx, soFar, cancel),
                    () -> super.place(ctx),
                    (adder, soFar, cancel) -> adder.onPlace().post(ctx, soFar, cancel)
                );*/
            }

            //common
            @Override
            public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> texts, @NotNull TooltipFlag flag) {
                super.appendHoverText(stack, level, texts, flag);
                itemPlus.getAdders().forEach(a -> a.appendHoverText(stack, level, texts, flag));
            }

            @Override
            public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int ix, boolean selecting) {
                super.inventoryTick(stack, level, entity, ix, selecting);
                itemPlus.getAdders().forEach(a -> a.inventoryTick(stack, level, entity, ix, selecting));
            }

            @Override
            public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
                return Action.multiSandwichInvoke(
                    () -> itemPlus.getAdders().stream().map(ItemAdder::onItemUseFirst),
                    ctx,
                    InteractionResult.PASS,
                    (c) -> super.onItemUseFirst(c.getItemInHand(), c)
                );
                /*return interactionActionRoutine(
                    itemPlus.getAdders(),
                    (adder, soFar, cancel) -> adder.onItemUseFirst().pre(ctx, soFar, cancel),
                    () -> super.onItemUseFirst(ctx.getItemInHand(), ctx),
                    (adder, soFar, cancel) -> adder.onItemUseFirst().post(ctx, soFar, cancel)
                );*/
            }
            @Override
            public @NotNull InteractionResult useOn(@NotNull UseOnContext ctx) {
                return Action.multiSandwichInvoke(
                    () -> itemPlus.getAdders().stream().map(ItemAdder::useOn),
                    ctx,
                    InteractionResult.PASS,
                    super::useOn
                );
                /*return interactionActionRoutine(
                    itemPlus.getAdders(),
                    (adder, soFar, cancel) -> adder.useOn().pre(ctx, soFar, cancel),
                    () -> super.useOn(ctx),
                    (adder, soFar, cancel) -> adder.useOn().post(ctx, soFar, cancel)
                );*/
            }

            @Override
            public boolean canAttackBlock(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player) {
                //anyone return false will lead to don't attack block.

                AtomicBoolean canAttack = new AtomicBoolean(super.canAttackBlock(state, level, pos, player));
                //don't care base value and always foreach all adders - sometimes critical logic in canAttackBlock() of adders
                itemPlus.getAdders().forEach(a -> {
                    canAttack.set(canAttack.get() && a.canAttackBlock(state, level, pos, player));
                });
                return canAttack.get();
            }

            @Override
            public boolean isFoil(@NotNull ItemStack stack) {
                for (var adder : itemPlus.getAdders()) {
                    if (adder.foilAdder(stack))
                        return true;
                }
                return false;
            }
        };
    }
    private static void setActualItem(ItemPlus<Item> itemPlus, Item.Properties p) {
        itemPlus.item = new Item(p) {
            //common
            @Override
            public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> texts, @NotNull TooltipFlag flag) {
                super.appendHoverText(stack, level, texts, flag);
                itemPlus.getAdders().forEach(a -> a.appendHoverText(stack, level, texts, flag));
            }

            @Override
            public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int ix, boolean selecting) {
                super.inventoryTick(stack, level, entity, ix, selecting);
                itemPlus.getAdders().forEach(a -> a.inventoryTick(stack, level, entity, ix, selecting));
            }

            @Override
            public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
                return Action.multiSandwichInvoke(
                    () -> itemPlus.getAdders().stream().map(ItemAdder::onItemUseFirst),
                    ctx,
                    InteractionResult.PASS,
                    (c) -> super.onItemUseFirst(c.getItemInHand(), c)
                );
                /*return interactionActionRoutine(
                    itemPlus.getAdders(),
                    (adder, soFar, cancel) -> adder.onItemUseFirst().pre(ctx, soFar, cancel),
                    () -> super.onItemUseFirst(ctx.getItemInHand(), ctx),
                    (adder, soFar, cancel) -> adder.onItemUseFirst().post(ctx, soFar, cancel)
                );*/
            }
            @Override
            public @NotNull InteractionResult useOn(@NotNull UseOnContext ctx) {
                return Action.multiSandwichInvoke(
                    () -> itemPlus.getAdders().stream().map(ItemAdder::useOn),
                    ctx,
                    InteractionResult.PASS,
                    super::useOn
                );
                /*return interactionActionRoutine(
                    itemPlus.getAdders(),
                    (adder, soFar, cancel) -> adder.useOn().pre(ctx, soFar, cancel),
                    () -> super.useOn(ctx),
                    (adder, soFar, cancel) -> adder.useOn().post(ctx, soFar, cancel)
                );*/
            }

            @Override
            public boolean canAttackBlock(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player) {
                //anyone return false will lead to don't attack block.

                AtomicBoolean canAttack = new AtomicBoolean(super.canAttackBlock(state, level, pos, player));
                //don't care base value and always foreach all adders - sometimes critical logic in canAttackBlock() of adders
                itemPlus.getAdders().forEach(a -> {
                    canAttack.set(canAttack.get() && a.canAttackBlock(state, level, pos, player));
                });
                return canAttack.get();
            }

            @Override
            public boolean isFoil(@NotNull ItemStack stack) {
                for (var adder : itemPlus.getAdders()) {
                    if (adder.foilAdder(stack))
                        return true;
                }
                return false;
            }
        };
    }

    protected ItemPlus(Class<T> wrappedType, Item.Properties p, @Nullable Block block) {
        /*if (wrappedType.isAssignableFrom(BlockItem.class)) {  //is block item
            if (block == null) {
                throw new IllegalArgumentException("can't accept a null block for BlockItemPlus");
            }
            setActualItem((ItemPlus<BlockItem>)this, block, p);
        }*/

        if (wrappedType.equals(Item.class)) {
            setActualItem((ItemPlus<Item>)this, p);

        } else if (wrappedType.equals(BlockItem.class)) {
            if (block == null) {
                throw new IllegalArgumentException("can't accept a null block for BlockItemPlus");
            }
            setActualItem((ItemPlus<BlockItem>)this, block, p);

        } else {
            throw new RuntimeException("ItemPlus can only wrap Item or BlockItem");
        }
    }

    public abstract List<ItemAdder> getAdders();
    private T item;

    public T getItem() { return item; }
    /*public T getOrCreateItem() {
        if (item != null)
            return item;

        try {
            Class<T> itemType = (Class<T>) new TypeReference<T>(){}.getType();
            if (itemType.equals(Item.class)) {
                var constructor = itemType.getConstructor(Item.Properties.class);
                constructor.setAccessible(true);
                constructor.newInstance()
            } else if (itemType.equals(BlockItem.class)) {

            } else {
                EzDebug.error("ItemPlus can only have generic type Item or BlockItem");
                return null;
            }
        } catch (Exception e) {
            EzDebug.error("ItemPlus exception:" + e);
            e.printStackTrace();
        }



    }*/

    public static final Hashtable<Class<? extends ItemPlus<?>>, List<ItemAdder>> addersCache = new Hashtable<>();
    public static List<ItemAdder> addersIfAbsent(Class<? extends ItemPlus<?>> type, Supplier<List<ItemAdder>> addersSupplier) {
        if (!addersCache.containsKey(type))
            addersCache.put(type, addersSupplier.get());

        return addersCache.get(type);
    }

    public static ItemPlus<? extends BlockItem> getOrCreateFromBlock(BlockPlus block, @Nullable Item.Properties properties) {
        if (block == null)
            throw new IllegalArgumentException("block plus is null");

        ItemPlus<? extends BlockItem> generated = generatedBlockItemPlus.get(block);
        if (generated != null)
            return generated;


        List<ItemAdder> itemAdders = new ArrayList<>();

        for (IBlockAdder blockAdder : block.getAdders()) {
            if (blockAdder instanceof IBlockItemAdderSupplier itemAdderSupplier) {
                itemAdderSupplier.supplyItemAdders(itemAdders);
            }
        }
        if (properties == null)
            properties = new Item.Properties();


        ItemPlus<BlockItem> newGenerate = new ItemPlus<BlockItem>(BlockItem.class, properties, block) {
            @Override
            public List<ItemAdder> getAdders() { return itemAdders; }
        };
        //setActualItem(newGenerate, block, properties);

        generatedBlockItemPlus.put(block, newGenerate);
        return newGenerate;
    }
    public static BlockItem getOrCreateFromBlockAndGet(BlockPlus block, @Nullable Item.Properties properties) {
        return getOrCreateFromBlock(block, properties).getItem();
    }

    public static boolean isBlockPlusItemOf(ItemStack stack, BlockPlus block) {
        ItemPlus<BlockItem> blockItemPlus = generatedBlockItemPlus.get(block);
        if (blockItemPlus == null)
            return false;

        return blockItemPlus.item == stack.getItem();
    }
}
