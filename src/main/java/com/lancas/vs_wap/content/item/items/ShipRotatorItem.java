package com.lancas.vs_wap.content.item.items;

import com.lancas.vs_wap.content.item.items.base.ShipInteractableItem;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;


public class ShipRotatorItem extends ShipInteractableItem {
    public static final String ID = "ship_rotator";
    public ShipRotatorItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult onItemUseOnShip(ItemStack stack, @NotNull Ship ship, @NotNull Level level, @NotNull Player player, UseOnContext ctx) {
        if (!(level instanceof ServerLevel sLevel)) return InteractionResult.PASS;
        if (!(ship instanceof ServerShip sShip)) return InteractionResult.PASS;

        double rotRad = player.isShiftKeyDown() ? Math.toRadians(-10) : Math.toRadians(10);
        VSGameUtilsKt.getShipObjectWorld(sLevel).teleportShip(sShip, new ShipTeleportDataImpl(
            ship.getTransform().getPositionInWorld(),
            new Quaterniond().rotateAxis(rotRad, JomlUtil.dNormal(ctx.getClickedFace())).mul(ship.getTransform().getShipToWorldRotation()),
            new Vector3d(0, 0, 0),
            new Vector3d(0, 0, 0),
            VSGameUtilsKt.getDimensionId(level),
            ship.getTransform().getShipToWorldScaling().x()
        ));
        return InteractionResult.CONSUME;
    }
}
