package com.lancas.vswap.content.block.blocks.artillery.breech;

import com.lancas.vswap.ship.attachment.HoldableAttachment;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IBreechBe {
    public boolean canArmLoadDockerNow(ItemStack stack);

    public boolean loadShipMunition(@NotNull HoldableAttachment holdable);
    public boolean loadDockerMunition(ItemStack stack);
    public void unloadMunition();

    public void fire();
}
