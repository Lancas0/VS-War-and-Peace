package com.lancas.vswap.subproject.blockplusapi.blockplus.adder.blockitem;
/*
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.itemplus.ItemAdder;
import com.lancas.vswap.subproject.blockplusapi.itemplus.adder.ItemInventoryTickAdder;
import com.lancas.vswap.subproject.blockplusapi.itemplus.adder.ItemPredictPlacementAdder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public interface BlockItemPredictPlacementAdder extends IBlockAdder, IBlockItemAdderSupplier {
    public abstract void predictPlacement(ItemStack stack, ClientLevel level, Player player, BlockPlaceContext ctx);

    @Override
    public default void supplyItemAdders(List<ItemAdder> adderList) {
        var superAdder = this;

        /.*adderList.add(new ItemInventoryTickAdder() {
            @Override
            public void inventoryTick(ItemStack stack, Level level, Entity entity, int ix, boolean selecting) {
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
                superAdder.predictPlacement(stack, (ClientLevel)level, player, ctx);
            }
        });*./
        adderList.add(new ItemPredictPlacementAdder() {
            @Override
            public void predictPlacement(ItemStack stack, ClientLevel level, Player player, BlockPlaceContext ctx) {
                superAdder.predictPlacement(stack, level, player, ctx);
            }
        });
    }
}
*/