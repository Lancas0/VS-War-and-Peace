package com.lancas.vswap.content.item.items;


import com.lancas.vswap.content.WapBlocks;
import com.lancas.vswap.content.WapItems;
import com.lancas.vswap.content.block.blockentity.UnderConstructionBe;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.ship.data.RRWChunkyShipSchemeData;
import com.lancas.vswap.subproject.blockplusapi.itemplus.ItemAdder;
import com.lancas.vswap.subproject.blockplusapi.itemplus.ItemPlus;
import com.lancas.vswap.subproject.blockplusapi.itemplus.adder.ItemPlaceLikeActionAdder;
import com.lancas.vswap.subproject.blockplusapi.itemplus.adder.ItemPredictPlacementAdder;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.List;

public class GreenPrint extends ItemPlus<Item> {
    public GreenPrint(Item.Properties p) {
        super(Item.class, p, null);
    }

    @Override
    public List<ItemAdder> getAdders() {
        return List.of(
            /*new ItemPlaceLikeActionAdder() {
                @Override
                public InteractionResult placeLikeAction(ItemStack stack, Level level, Player player, BlockPos placeAt, UseOnContext ctx, InteractionResult soFar) {
                    if (!player.isShiftKeyDown()) return InteractionResult.PASS;
                    if (!(level instanceof ServerLevel sLevel)) return InteractionResult.PASS;

                    RRWChunkyShipSchemeData schemeData = getOrCreateSchemeData(stack);
                    if (schemeData.isEmpty()) return InteractionResult.PASS;

                    sLevel.setBlockAndUpdate(placeAt, WapBlocks.Industrial.UNDER_CONSTRUCTION.getDefaultState());
                    BlockEntity getBe = sLevel.getBlockEntity(placeAt);

                    if (!(getBe instanceof UnderConstructionBe be)) {
                        EzDebug.warn("get non UnderConstructionBe:" + getBe);
                        return InteractionResult.PASS;
                    }

                    be.initializeConstructingShip(sLevel, schemeData);
                    return InteractionResult.CONSUME;
                }
            }*/
            /*new ItemPlaceLikeActionAdder.PRE() {
                @Override
                public InteractionResult placeLikeAction(ItemStack stack, Level level, Player player, BlockPos placeAt, UseOnContext ctx) {
                    if (!player.isShiftKeyDown()) return InteractionResult.PASS;
                    if (!(level instanceof ServerLevel sLevel)) return InteractionResult.PASS;

                    RRWChunkyShipSchemeData schemeData = getOrCreateSchemeData(stack);
                    if (schemeData.isEmpty()) {
                        EzDebug.warn("try to make a underConstruction with empty schemeData");
                        return InteractionResult.PASS;
                    }

                    //has problem when place on ship
                    sLevel.setBlockAndUpdate(placeAt, WapBlocks.Industrial.UNDER_CONSTRUCTION.getDefaultState());
                    BlockEntity getBe = sLevel.getBlockEntity(placeAt);

                    if (!(getBe instanceof UnderConstructionBe be)) {
                        EzDebug.warn("get non UnderConstructionBe:" + getBe);
                        return InteractionResult.PASS;
                    }

                    be.initializeConstructingShip(sLevel, schemeData);
                    return InteractionResult.CONSUME;
                }
            }*/
        );
    }
    /*public GreenPrint(Properties p_41383_) {
        super(p_41383_);
    }*/
    public static boolean isGreenPrint(ItemStack stack) {
        return stack.getItem() == WapItems.GREEN_PRINT.get();
    }

    /*@Nullable
    public static RRWChunkyShipSchemeData getSchemeData(ItemStack stack) {
        CompoundTag stackNbt = stack.getOrCreateTag();
        if (!stackNbt.contains("green_print_data")) return null;
        return new RRWChunkyShipSchemeData().load(stackNbt.getCompound("green_print_data"));
    }
    @NotNull
    public static RRWChunkyShipSchemeData getOrCreateSchemeData(ItemStack stack) {
        CompoundTag stackNbt = stack.getOrCreateTag();
        if (!stackNbt.contains("green_print_data")) return null;
        return new RRWChunkyShipSchemeData().load(stackNbt.getCompound("green_print_data"));
    }*/
    /*public static void scheduleBlockChange(ItemStack stack, BlockPos localBp, BlockState state, @Nullable CompoundTag beNbt) {
        NbtBuilder nbt = NbtBuilder.modify(stack.getOrCreateTag());
        nbt.putCompound(localBp.toShortString(), NbtBuilder.tagOfBlock(localBp, state, beNbt));
    }
    public static RRWChunkyShipSchemeData getOrCreateFlushedSchemeData(ItemStack stack) {
        NbtBuilder nbt = NbtBuilder.modify(stack.getOrCreateTag());

        RRWChunkyShipSchemeData data;
        if (!nbt.get().contains("scheme_data")) {
            data = new RRWChunkyShipSchemeData();
        } else
            data = new RRWChunkyShipSchemeData().load(nbt.getCompound("scheme_data"));

        var nbtKeyIt = nbt.get().getAllKeys().iterator();
        while (nbtKeyIt.hasNext()) {
            String key = nbtKeyIt.next();
            if (key.equals("scheme_data")) continue;

            CompoundTag blockTag = nbt.get().getCompound(key);
            NbtBuilder.blockOfTagDo(blockTag, data::setBlockAtLocalBp);  //will properly handle air blockState
            nbtKeyIt.remove();
        }

        stack.getOrCreateTag().put("scheme_data", data.saved());
        return data;
    }*/

    public static RRWChunkyShipSchemeData getOrCreateSchemeData(ItemStack stack) {
        NbtBuilder nbt = NbtBuilder.modify(stack.getOrCreateTag());

        RRWChunkyShipSchemeData data;
        if (!nbt.contains("scheme_data")) {
            data = new RRWChunkyShipSchemeData();
            nbt.putCompound("scheme_data", data.saved());
        } else
            data = new RRWChunkyShipSchemeData().load(nbt.getCompound("scheme_data"));
        /*var nbtKeyIt = nbt.get().getAllKeys().iterator();
        while (nbtKeyIt.hasNext()) {
            String key = nbtKeyIt.next();
            if (key.equals("scheme_data")) continue;

            CompoundTag blockTag = nbt.get().getCompound(key);
            NbtBuilder.blockOfTagDo(blockTag, data::setBlockAtLocalBp);  //will properly handle air blockState
            nbtKeyIt.remove();
        }

        stack.getOrCreateTag().put("scheme_data", data.saved());*/
        return data;
    }
    public @Nullable static RRWChunkyShipSchemeData getSchemeData(ItemStack stack) {
        NbtBuilder nbt = NbtBuilder.modify(stack.getOrCreateTag());

        if (!nbt.contains("scheme_data"))
            return null;
        else
            return new RRWChunkyShipSchemeData().load(nbt.getCompound("scheme_data"));
    }

    public static ItemStack readShipTo(ItemStack stack, ServerLevel level, ServerShip ship) {
        RRWChunkyShipSchemeData data = new RRWChunkyShipSchemeData().readShip(level, ship);
        stack.getOrCreateTag().put("scheme_data", data.saved());
        return stack;
    }
}
