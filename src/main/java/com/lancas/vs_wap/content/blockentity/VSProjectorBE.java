package com.lancas.vs_wap.content.blockentity;

import com.lancas.vs_wap.content.WapBlocks;
import com.lancas.vs_wap.content.items.GreenPrint;
import com.lancas.vs_wap.ship.attachment.NoPlayerCollisionAttachment;
import com.lancas.vs_wap.ship.attachment.ProjectingShipAtt;
import com.lancas.vs_wap.ship.helper.LazyShip;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.ship.tp.ProjectShipTp;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class VSProjectorBE extends BlockEntity {
    public VSProjectorBE(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == 0 && stack.getItem() instanceof GreenPrint;
        }
    };
    private long linkShipId = -1;
    private double shipYOffset = 5;

    public double getShipYOffset() {
        return shipYOffset;
    }

    private final LazyShip lazyLinkShip = new LazyShip((l, owner) ->
        ShipUtil.getShipByID(l, ((VSProjectorBE) owner).linkShipId)
    );

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("content"));
        linkShipId = tag.getLong("link_ship_id");
        shipYOffset = tag.getDouble("ship_y_offset");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("content", itemHandler.serializeNBT());
        tag.putLong("link_ship_id", linkShipId);
        tag.putDouble("ship_y_offset", shipYOffset);
    }


    //todo add link_ship_id, ship_y_offset? in updateTag?
    //我在想这个东西没有UI，需要更新吗？不太了解getUpdateTag和handleUpdateTag
    @Override
    public CompoundTag getUpdateTag() {
        return serializeNBT();
    }

    //todo update block state or model when update tag
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        load(tag);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        afterUpdateGreenPrint();
    }

    public void afterUpdateGreenPrint() {
        ItemStack greenPrintStack = itemHandler.getStackInSlot(0);
        if (greenPrintStack.isEmpty()) return;  //关于绿图被取出，投影船位置由TP通过shouldShowProjectShip更新

        if (!(level instanceof ServerLevel sLevel)) return;

        ServerShip linkShip = lazyLinkShip.get(sLevel, this);
        //ProjectingShipAtt att;
        if (linkShip == null) {
            //do not use pool since there will be an attachment added
            linkShip = VSGameUtilsKt.getShipObjectWorld(sLevel).createNewShipAtBlock(JomlUtil.i(worldPosition), false, 1.0, VSGameUtilsKt.getDimensionId(sLevel));//ShipPool.getOrCreatePool(sLevel).getOrCreateEmptyShip();
            linkShipId = linkShip.getId();
            ProjectingShipAtt.getOrCreate(linkShip, worldPosition);//.initialize(sLevel);
            NoPlayerCollisionAttachment.applyIgnoreAny(linkShip);
        } /*else {
            att = ProjectingShipAtt.getOrCreate(linkShip, worldPosition);
        }*/

        linkShip.setTransformProvider(new ProjectShipTp(sLevel, worldPosition));

        ShipBuilder shipBuilder = ShipBuilder.modify(sLevel, linkShip).overwriteByScheme(GreenPrint.getOrCreateFlushedSchemeData(greenPrintStack));
        if (shipBuilder.isEmpty()) {
            shipBuilder.addBlock(BlockPos.ZERO, WapBlocks.Industrial.PROJECT_CENTER.getDefaultState());
        }
    /*
    // Container 接口实现
    @Override public int getContainerSize() { return 1; }
    @Override public boolean isEmpty() { return itemHandler.getStackInSlot(0).isEmpty(); }
    @Override public ItemStack getItem(int slot) { return itemHandler.getStackInSlot(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { return itemHandler.extractItem(slot, amount, false); }
    @Override public ItemStack removeItemNoUpdate(int slot) { return itemHandler.extractItem(slot, itemHandler.getStackInSlot(slot).getCount(), false); }
    @Override public void setItem(int slot, ItemStack stack) { itemHandler.setStackInSlot(slot, stack); }
    @Override public boolean stillValid(Player player) { return !(player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) > 64.0D); }
    @Override public void clearContent() { itemHandler.setStackInSlot(0, ItemStack.EMPTY); }

     */
    }
    public boolean shouldShowProjectShip() { return !itemHandler.getStackInSlot(0).isEmpty(); }


}