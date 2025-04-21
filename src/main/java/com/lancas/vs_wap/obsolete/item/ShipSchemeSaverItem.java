package com.lancas.vs_wap.obsolete.item;

/*
import com.lancas.vs_wap.ship.data.ShipSchemeDataAsTag;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class ShipSchemeSaverItem extends Item {
    public ShipSchemeSaverItem(Properties p_41383_) {
        super(p_41383_);
    }


    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
        //only run in server side
        if (ctx.getLevel().isClientSide)
            return InteractionResult.PASS;

        ServerLevel level = (ServerLevel)ctx.getLevel();
        ServerPlayer player = (ServerPlayer)ctx.getPlayer();
        BlockPos interactPos = ctx.getClickedPos();
        ItemStack stackInHand = ctx.getItemInHand();

        if (level.getBlockState(interactPos).isAir()) {
            //TODO this is DEBUG
            EzDebug.log("interaction pass by use on air");
            return InteractionResult.PASS;
        }

        ServerShip clickedShip = VSGameUtilsKt.getShipManagingPos(level, interactPos);
        if (clickedShip == null) {
            //TODO this is DEBUG
            EzDebug.log("interaction pass by not use on ship");
            return InteractionResult.PASS;
        }

        ShipSchemeDataAsTag curShipSchemeData = new ShipSchemeDataAsTag().readShip(level, clickedShip);
        CompoundTag flawlessShipSchemeDataTag = ShipUtil.readTag(clickedShip);
        ShipSchemeDataAsTag flawlessShipSchemeData = flawlessShipSchemeDataTag == null ? null : new ShipSchemeDataAsTag(flawlessShipSchemeDataTag);
/.*
        ItemUtil.giveItem(player, AllItems.ShipSchemeItem.get(), itemStack -> {
            ShipSchemeItem.setSchemeData(itemStack, curShipSchemeData);

            if (flawlessShipSchemeData == null) {
                ShipSchemeItem.setFlawlessSchemeData(itemStack, curShipSchemeData);
            } else {
                ShipSchemeItem.setFlawlessSchemeData(itemStack, flawlessShipSchemeData);
            }
        });
*./
        //todo add a method to decide whehter to delete ship
        VSGameUtilsKt.getShipObjectWorld(level).deleteShip(clickedShip);

        return InteractionResult.CONSUME;
    }
}
*/