package com.lancas.vswap.content.block.blocks.artillery.breech;

import com.lancas.vswap.ship.attachment.HoldableAttachment;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ServerShip;

public interface IBreechBe {
    public boolean canArmLoadDockerNow(ItemStack stack);

    public boolean loadShipMunition(/*@NotNull HoldableAttachment holdable*/@NotNull ServerShip toLoadShip);
    public boolean loadDockerMunition(ItemStack stack);
    public void unloadMunition();

    public void fire();
}
