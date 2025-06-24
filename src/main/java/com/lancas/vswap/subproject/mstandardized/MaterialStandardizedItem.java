package com.lancas.vswap.subproject.mstandardized;

import com.lancas.vswap.content.WapItems;
import com.lancas.vswap.content.item.IFilterItem;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.mixins.accessor.UseOnCtxAccessor;
import com.lancas.vswap.subproject.mstandardized.renderer.MaterialStandardizedRenderer;
import com.lancas.vswap.util.ShipUtil;
import com.lancas.vswap.util.StrUtil;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.filter.ItemAttribute;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;


public class MaterialStandardizedItem extends Item implements IFilterItem {
    public static int getCntByScale(double scale) {
        //return (int)Math.ceil(scale * 16);  //every 16 material for a block scaled by 1
        //FIXME tempory always return1
        return 1;
    }

    public MaterialStandardizedItem(Properties p_41383_) {
        super(p_41383_);
    }

    /*public static void setSelectingBlock(ItemStack stack, Block block) {
        Category category = getCategory(stack);
        if (!category.contains(block)) {
            EzDebug.warn("trying set a invalid block:" + block.getName());
            return;
        }
        stack.getOrCreateTag().putString("selecting_block", Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).toString());
    }*/

    @NotNull
    public static Category getCategory(ItemStack stack) {
        CompoundTag itemNbt = stack.getOrCreateTag();

        if (!itemNbt.contains("category_name"))
            return Category.EMPTY;

        String categoryName = stack.getOrCreateTag().getString("category_name");
        return CategoryRegistry.getCategory(categoryName);
    }
    public static String getCategoryName(ItemStack stack) {
        return stack.getOrCreateTag().getString("category_name");
    }

    /*@Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null)
            return InteractionResult.PASS;

        if (player.isShiftKeyDown()) {
            Category category = getCategory(stack);
            EzDebug.logs(category.ids, null);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }*/
    public static ItemStack defaultStack(int amount) {
        return WapItems.MATERIAL_STANDARDIZED.asStack(amount);
    }
    public static ItemStack setCategory(ItemStack stack, Category category) {
        if (!(stack.getItem() instanceof MaterialStandardizedItem)) {
            EzDebug.warn("try set category to non msItem");
            return stack;
        }

        CompoundTag stackNbt = stack.getOrCreateTag();
        stackNbt.putString("category_name", category.categoryName);  //todo don't reset selecing block if is same as prev cate name
        //stackNbt.remove("selecting_block");
        return stack;
    }
    public static ItemStack fromBlockItem(BlockItem blockItem, int amount) {
        return setCategory(defaultStack(amount), CategoryRegistry.getCategory(blockItem.getBlock()));
    }
    public static ItemStack fromBlock(Block block, int amount) {
        return setCategory(defaultStack(amount), CategoryRegistry.getCategory(block));
    }
    public static ItemStack fromWorldBlock(Level level, BlockPos at) {
        BlockState state = level.getBlockState(at);
        if (state.isAir())
            return ItemStack.EMPTY;

        @Nullable Ship shipOn = ShipUtil.getShipAt(level, at);
        double scale = shipOn == null ? 1 : shipOn.getTransform().getShipToWorldScaling().x();  //todo 3d scale?
        int amount = getCntByScale(scale);
        return setCategory(defaultStack(amount), CategoryRegistry.getCategory(state.getBlock()));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        if (!(ctx.getLevel() instanceof ServerLevel level)) return InteractionResult.PASS;
        if (!(ctx.getPlayer() instanceof ServerPlayer player)) return InteractionResult.PASS;

        ItemStack handStack = ctx.getItemInHand();

        if (player.isShiftKeyDown() && player.isCreative()) {
            BlockState clickedState = level.getBlockState(ctx.getClickedPos());
            if (clickedState.isAir()) return InteractionResult.PASS;

            Category category = CategoryRegistry.getCategory(clickedState.getBlock());;
            setCategory(handStack, category);
            return InteractionResult.SUCCESS;
        } else {
            AtomicReference<InteractionResult> result = new AtomicReference<>(InteractionResult.PASS);

            Optional.ofNullable(ClientBlockSelection.getSelectBlockOrMainBlockOf(handStack))
                /*.map(b -> {
                    if (b.asItem() instanceof BlockItem bi)
                        return bi;
                    EzDebug.warn("the item of block:" + b + " is not blockItem!");
                    return null;
                })*/
                .ifPresentOrElse(
                    toPlace -> {
                        InteractionResult res = simulatePlace(level, player, ctx, toPlace);
                        if (res == InteractionResult.CONSUME || res == InteractionResult.CONSUME_PARTIAL || res == InteractionResult.SUCCESS) {
                            handStack.shrink(1);
                        }
                        result.set(res);
                    },
                    () -> EzDebug.log("get null selecting block item")
                );

            return result.get();
        }
    }
    private InteractionResult simulatePlace(ServerLevel level, Player player, UseOnContext ctx, Block block) {
        Function<UseOnContext, InteractionResult> custom = ICustomPlaceMaterialBlock.getCustomPlaceAction(block);
        if (custom != null) {
            return custom.apply(ctx);
        }

        if (!(block.asItem() instanceof BlockItem blockItem)) {
            EzDebug.warn("Block " + StrUtil.getBlockName(block.defaultBlockState()) + "'s item is not BlockItem!");
            return InteractionResult.FAIL;
        }

        UseOnCtxAccessor ctxAccessor = (UseOnCtxAccessor)ctx;
        UseOnContext simulateUseCtx = new UseOnContext(level, player, ctx.getHand(), blockItem.getDefaultInstance(), ctxAccessor.getHitResult());

        // 创建方块放置上下文
        BlockPlaceContext placeContext = new BlockPlaceContext(simulateUseCtx);

        // 检查是否可放置
        if (!placeContext.canPlace()) {
            EzDebug.light("can't place");
            return InteractionResult.PASS;
        }

        BlockPos placeAgainstBp = placeContext.getClickedPos().relative(placeContext.getClickedFace().getOpposite());
        BlockState placeAgainst = level.getBlockState(placeAgainstBp);
        EzDebug.light("place against " + StrUtil.getBlockName(placeAgainst));
        if (placeAgainst.isAir()) {
            return InteractionResult.PASS;
        }
        BlockState stateToPlace = blockItem.getBlock().getStateForPlacement(placeContext);


        // place logic
        InteractionResult result = blockItem.place(placeContext);
        if (result == InteractionResult.CONSUME || result == InteractionResult.CONSUME_PARTIAL || result == InteractionResult.SUCCESS) {
            // after place event
            String dimID = VSGameUtilsKt.getDimensionId(level);
            ResourceKey<Level> levelKey = VSGameUtilsKt.getResourceKey(dimID);
            BlockEvent.EntityPlaceEvent placeEvent = new BlockEvent.EntityPlaceEvent(
                BlockSnapshot.create(levelKey, level, ctx.getClickedPos()),
                placeAgainst,
                player
            );
            MinecraftForge.EVENT_BUS.post(placeEvent);
        }
        // todo 处理结果
        /*if (result.consumesAction()) {
            // 播放放置音效
            world.playSound(
                player,
                pos,
                stateToPlace.getSoundType().getPlaceSound(),
                SoundSource.BLOCKS,
                1.0F,
                1.0F
            );

            // 消耗物品（原版逻辑）
            if (player != null && !player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            return true;
        }
        return false;*/



        return result;
    }

    public static @NotNull Item getIconItem(ItemStack stack) {
        CompoundTag stackNbt = stack.getOrCreateTag();

        /*if (stackNbt.contains("selecting_block", Tag.TAG_STRING)) {
            String selectingBlockId = stack.getOrCreateTag().getString("selecting_block");

            return Optional.ofNullable(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(selectingBlockId)))
                .map(Block::asItem)
                .orElse(Category.EMPTY.getIconItem());
        }*/
        Category category = getCategory(stack);

        Block selectBlock = ClientBlockSelection.getSelectedBlockIfExist(category.categoryName);
        if (selectBlock != null)
            return selectBlock.asItem();

        return category.getIconItem();  //default empty icon when category is empty
    }
    /*public static @Nullable BlockItem getSelectingBlockItem(ItemStack stack) {
        CompoundTag stackNbt = stack.getOrCreateTag();

        if (!stackNbt.contains("selecting_block", Tag.TAG_STRING)) {
            Category category = getCategory(stack);
            Block mainBlock = category.getMainBlock();
            Optional.ofNullable(mainBlock)
                .map(ForgeRegistries.BLOCKS::getKey)
                .map(ResourceLocation::toString)
                .ifPresent(blockId -> {
                    stackNbt.putString("selecting_block", blockId);
                });

            return Optional.ofNullable(mainBlock).map(b -> (BlockItem)b.asItem()).orElse(null);
        }

        return Optional.of(stackNbt.getString("selecting_block"))
            .map(blockId -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockId)))
            .map(b -> (BlockItem)b.asItem())
            .orElse(null);
    }*/


    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return MaterialStandardizedRenderer.INSTANCE;
            }
        });
    }

    @Override
    public FilterItemStack getFilterItemStack(ItemStack stack) {
        return new FilterItemStack(stack) {
            private final String categoryName = getCategoryName(stack);
            public boolean test(Level world, FluidStack stack, boolean matchNBT) {
                return false;
            }

            public boolean test(Level world, ItemStack stack, boolean matchNBT) {
                return Objects.equals(getCategoryName(stack), categoryName);
            }
        };
    }
}
