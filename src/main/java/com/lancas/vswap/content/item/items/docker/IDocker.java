package com.lancas.vswap.content.item.items.docker;

/*
import com.lancas.vswap.ship.attachment.HoldableAttachment;
import com.lancas.vswap.ship.data.IShipSchemeData;
import com.lancas.vswap.ship.data.IShipSchemeRandomReader;
import com.lancas.vswap.ship.helper.builder.ShipBuilder;
import com.lancas.vswap.util.JomlUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.UUID;

public interface IDocker {
    @Nullable
    public static ShipBuilder makeShipBuilderFromStack(ServerLevel level, ItemStack stack) {
        if (!(stack.getItem() instanceof IDocker docker)) return null;
        return docker.makeShipBuilder(level, stack);
    }

    public ShipBuilder makeShipBuilder(ServerLevel level, ItemStack stack);
    //it will delete, or hide the ship
    public ItemStack saveShip(ServerLevel level, ServerShip ship, ItemStack stack);
    public boolean hasShipData(ItemStack stack);
    @Nullable
    public IShipSchemeData getShipData(ItemStack stack);

    @Nullable
    public UUID getOrCreateDockerUuidIfHasData(ItemStack stack);
    @Nullable
    public IShipSchemeRandomReader getShipDataReader(ItemStack stack);

    /.*
    @Nullable
    public Vector3ic getLocalPivot(ItemStack stack);
    @Nullable
    public Vector3ic getLocalHoldForward(ItemStack stack);
    *./


}
*/