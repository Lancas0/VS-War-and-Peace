package com.lancas.vswap.content.block.blocks.industry.dock;

import com.lancas.vswap.content.item.items.GreenPrint;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.ship.data.RRWChunkyShipSchemeData;
import com.lancas.vswap.util.JomlUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class DockGreenPrintHolderBe extends DockBe {
    public DockGreenPrintHolderBe(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public final ItemStackHandler gpInventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            notifyUpdate();
            /*if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }*/
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == 0 && GreenPrint.isGreenPrint(stack);
        }
    };

    public @Nullable RRWChunkyShipSchemeData getNotEmptySchemeData() {
        ItemStack gpStack = gpInventory.getStackInSlot(0);
        if (gpStack.isEmpty())
            return null;

        if (!GreenPrint.isGreenPrint(gpStack)) {
            EzDebug.warn("stack stored in gpHolderInventory is not GreenPrint");
            return null;
        }

        RRWChunkyShipSchemeData schemeData = GreenPrint.getSchemeData(gpStack);
        if (schemeData == null || schemeData.isEmpty())
            return null;
        return schemeData;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level == null || level.isClientSide)
            return;

        ItemStack dropGp = gpInventory.getStackInSlot(0);
        if (!dropGp.isEmpty()) {
            Vector3d dropPos = JomlUtil.dCenter(worldPosition);
            Containers.dropItemStack(level, dropPos.x, dropPos.y, dropPos.z, dropGp);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("green_print_inventory", gpInventory.serializeNBT());
    }
    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        gpInventory.deserializeNBT(tag.getCompound("green_print_inventory"));
    }
}
