package com.lancas.vs_wap.content.item.items;

import com.lancas.vs_wap.content.item.items.base.ShipInteractableItem;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;


public class ShipDuplicatorItem extends ShipInteractableItem {
    public static final String ID = "ship_duplicator_item";

    public ShipDuplicatorItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult onItemUseOnShip(ItemStack stack, @NotNull Ship ship, @NotNull Level level, @NotNull Player player, UseOnContext ctx) {
        /*Vector3d worldUseOnPos = ship.getShipToWorld().transformPosition(JomlUtil.dCenter(ctx.getClickedPos()));
        Vector3d worldUseOnNormal = ship.getTransform().getShipToWorldRotation().transform(JomlUtil.dNormal(ctx.getClickedFace()));

        Vector3d newShip*/
        if (!(level instanceof ServerLevel sLevel)) return InteractionResult.PASS;
        if (!(ship instanceof ServerShip sShip)) return InteractionResult.PASS;


        ServerShip newShip = ShipBuilder.copy(player.getOnPos().above(3), sLevel, sShip)
            .setWorldPos(player.position().add(0, 3, 0))
            .get();

        EzDebug.log("newShipName:" + newShip.getSlug());

        return InteractionResult.CONSUME;
    }
}
