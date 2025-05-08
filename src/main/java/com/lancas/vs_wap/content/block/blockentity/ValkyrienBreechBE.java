package com.lancas.vs_wap.content.block.blockentity;

import com.lancas.vs_wap.content.ui.ValkyrienBreechMenu;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.api.Dest;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ValkyrienBreechBE extends BlockEntity implements MenuProvider, IHaveGoggleInformation {
    //SchematicannonScreen
    public ValkyrienBreechBE(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }
    //@Override
    //public void addBehaviours(List<BlockEntityBehaviour> list) { }

    // Inventory
    public ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            ItemStack stack = this.getStackInSlot(0);
            Dest<Boolean> shouldChange = new Dest<>();
            int newAmount = addFuel(stack.getCount(), shouldChange);

            if (shouldChange.get()) {
                stack.setCount(newAmount);
                this.setStackInSlot(0, stack);
                setChanged();

                EzDebug.log((level.isClientSide ? "client" : "server") + " on gunPowder changed:" + newAmount);
            }
        }
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == 0 && (stack.is(Items.GUNPOWDER));
        }
    };
    public static final int MAX_FUEL = 200;
    public static final float MAX_FUEL_F = (float)MAX_FUEL;
    public int storingFuel = 0;
    protected int nextLaunchUse = 0;

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("inventory"));
        storingFuel = tag.getInt("fuel");
        nextLaunchUse = tag.getInt("next_launch_use");
    }
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", inventory.serializeNBT());
        tag.putInt("fuel", storingFuel);
        tag.putInt("next_launch_use", nextLaunchUse);
    }
    // 数据同步
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put("inventory", inventory.serializeNBT());
        tag.putInt("fuel", storingFuel);
        tag.putInt("next_launch_use", nextLaunchUse);
        EzDebug.log((level.isClientSide ? "client" : "server") + "get update tag nextUse:" + nextLaunchUse + ", fuel:" + storingFuel);
        return tag;
    }
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        inventory.deserializeNBT(tag.getCompound("inventory"));
        storingFuel = tag.getInt("fuel");
        nextLaunchUse = tag.getInt("next_launch_use");

        EzDebug.log((level.isClientSide ? "client" : "server") + "handle update tag nextUse:" + nextLaunchUse + ", fuel:" + storingFuel);
    }

    public void sendToMenu(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(getBlockPos());
        buffer.writeNbt(getUpdateTag());
    }


    public int addFuel(int amount, Dest<Boolean> shouldChange) {
        int accept = Math.min(amount, MAX_FUEL - storingFuel);
        if (accept <= 0) {
            shouldChange.set(false);
            return 0;
        }

        shouldChange.set(true);
        storingFuel += accept;
        return amount - accept;
    }
    public int getNextLaunchUse() { return nextLaunchUse; }
    public int setNextLaunchUse(int newLaunchUse) {
        int realVal = Math.min(newLaunchUse, storingFuel);
        nextLaunchUse = realVal;
        //EzDebug.log("next lauch:" + newLaunchUse);
        setChanged();
        return realVal;
    }

    //todo translation
    @Override
    public Component getDisplayName() { return Component.literal("Valkyrien Artillery"); }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return ValkyrienBreechMenu.create(id, inventory, this);
    }


    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (nextLaunchUse <= 0) return false;



        //CreateClient.OUTLINER.showCluster("test", List.of(getBlockPos()));
        return false;
    }
}