package com.lancas.vswap.content.ui;

import com.lancas.vswap.content.WapUI;
import com.lancas.vswap.content.block.blockentity.ValkyrienBreechBE;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ValkyrienBreechMenu extends MenuBase<ValkyrienBreechBE> {
    public ValkyrienBreechMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf buffer) {
        super(type, id, inv, buffer);
    }
    public ValkyrienBreechMenu(MenuType<?> type, int id, Inventory inv, ValkyrienBreechBE be) {
        super(type, id, inv, be);
    }

    public static ValkyrienBreechMenu create(int id, Inventory inv, ValkyrienBreechBE be) {
        return new ValkyrienBreechMenu(WapUI.VALKYRIEN_BREECH_MENU.get(), id, inv, be);
    }

    @Override
    protected ValkyrienBreechBE createOnClient(FriendlyByteBuf extraData) {
        ClientLevel world = Minecraft.getInstance().level;
        BlockEntity blockEntity = world.getBlockEntity(extraData.readBlockPos());
        if (blockEntity instanceof ValkyrienBreechBE be) {
            CompoundTag tag = extraData.readNbt();
            be.handleUpdateTag(tag);

            int fuel = tag.getInt("fuel");
            int next_use = tag.getInt("next_launch_use");
            //EzDebug.log("create on client, fuel" + fuel + ", nextuse:" + next_use);
            return be;
        }
        return null;
    }

    @Override
    protected void initAndReadInventory(ValkyrienBreechBE valkyrienBreechBE) {

    }

    @Override
    protected void addSlots() {
        int x = 0, y = 0;

        addSlot(new SlotItemHandler(contentHolder.inventory, 0, x + 27, y + 24));
        //addSlot(new SlotItemHandler(contentHolder.inventory, 1, x + 171, y + 65));
        //addSlot(new SlotItemHandler(contentHolder.inventory, 2, x + 134, y + 19));
        //addSlot(new SlotItemHandler(contentHolder.inventory, 3, x + 174, y + 19));
        //addSlot(new SlotItemHandler(contentHolder.inventory, 4, x + 15, y + 19));

        addPlayerSlots(37, 161);
    }

    @Override
    protected void saveData(ValkyrienBreechBE valkyrienBreechBE) {

    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }
}
