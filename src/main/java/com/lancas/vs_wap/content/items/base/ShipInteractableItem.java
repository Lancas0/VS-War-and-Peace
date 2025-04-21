package com.lancas.vs_wap.content.items.base;

import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.Ship;


public abstract class ShipInteractableItem extends Item {

    public ShipInteractableItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        BlockPos useOn = ctx.getClickedPos();

        if (level == null || player == null) return InteractionResult.PASS;

        Ship ship = ShipUtil.getShipAt(level, useOn);

        if (ship == null)
            return onItemNotUseOnShip(stack, level, player, ctx);

        return onItemUseOnShip(stack, ship, level, player, ctx);
    }

    public abstract InteractionResult onItemUseOnShip(
        ItemStack stack,
        @NotNull Ship ship,
        @NotNull Level level,
        @NotNull Player player,
        UseOnContext ctx);

    public InteractionResult onItemNotUseOnShip(ItemStack stack, Level level, Player player, UseOnContext ctx) {
        return InteractionResult.PASS;
    }
}
