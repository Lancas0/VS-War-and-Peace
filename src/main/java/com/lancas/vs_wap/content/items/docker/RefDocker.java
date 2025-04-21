package com.lancas.vs_wap.content.items.docker;

import com.lancas.vs_wap.content.WapItems;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.ship.feature.pool.ShipPool;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.valkyrienskies.core.api.ships.ServerShip;

public class RefDocker extends Item implements IDocker {
    public static ItemStack stackOf(ServerLevel level, ServerShip ship) {
        ItemStack stack = WapItems.Docker.REF_DOCKER.asStack();
        RefDocker refDocker = (RefDocker)stack.getItem();
        return refDocker.saveShipToStack(level, ship, stack);
    }
    public RefDocker(Properties p_41383_) {
        super(p_41383_);
    }

    //todo shrink stack?
    @Override
    public ShipBuilder makeShipBuilderFromStack(ServerLevel level, ItemStack stack) {
        if (!stack.getOrCreateTag().contains("ship_id")) return null;

        long shipId = stack.getOrCreateTag().getLong("ship_id");
        ServerShip ship = ShipUtil.getServerShipByID(level, shipId);

        ShipPool.getOrCreatePool(level).showShip(shipId);
        return ShipBuilder.modify(level, ship);
    }

    @Override
    public ItemStack saveShipToStack(ServerLevel level, ServerShip ship, ItemStack stack) {
        stack.getOrCreateTag().putLong("ship_id", ship.getId());
        ShipPool.getOrCreatePool(level).hideShip(ship, ShipPool.HideType.StaticAndInvisible);

        return stack;
    }
}
