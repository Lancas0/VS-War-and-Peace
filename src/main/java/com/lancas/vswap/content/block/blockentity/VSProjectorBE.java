package com.lancas.vswap.content.block.blockentity;

import com.lancas.vswap.content.WapBlocks;
import com.lancas.vswap.content.item.items.GreenPrint;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.ship.attachment.NoPlayerCollisionAttachment;
import com.lancas.vswap.ship.attachment.ProjectingShipAtt;
import com.lancas.vswap.ship.data.IShipSchemeData;
import com.lancas.vswap.ship.helper.builder.ShipBuilder;
import com.lancas.vswap.ship.tp.ProjectShipTp;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.NbtBuilder;
import com.lancas.vswap.util.ShipUtil;
import com.simibubi.create.content.kinetics.base.DirectionalShaftHalvesBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;

public class VSProjectorBE extends DirectionalShaftHalvesBlockEntity {
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
            return slot == 0 && GreenPrint.isGreenPrint(stack);
        }
    };
    public ItemStack getGreenPrint(ServerLevel level) {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (stack.isEmpty()) return ItemStack.EMPTY;

        ServerShip ship = ShipUtil.getServerShipByID(level, linkShipId);
        if (ship == null) {
            EzDebug.warn("fail to get projection ship");
            return ItemStack.EMPTY;
        }

        return GreenPrint.readShipTo(stack, level, ship);
    }
    /*public void greenPrintUpdate(BlockPos posInShip, BlockState newState, CompoundTag beNbt) {
        ItemStack greenPrintStack = itemHandler.getStackInSlot(0);
        if (greenPrintStack.isEmpty()) return;

        GreenPrint.scheduleBlockChange(greenPrintStack, posInShip.subtract(shipCenter), newState, beNbt);
    }*/

    private long linkShipId = -1;
    private ScrollValueBehaviour projectHeightInput;
    private BlockPos shipCenter = new BlockPos(0, 0, 0);  //init for safe
    public double scale = 1;

    public double getShipYOffset() {
        return projectHeightInput.getValue() * ProjectorHeightScrollValueBehaviour.VAL_TO_HEIGHT;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.add(projectHeightInput = new ProjectorHeightScrollValueBehaviour(this)
            .between(ProjectorHeightScrollValueBehaviour.HEIGHT_TO_VAL, 60 * ProjectorHeightScrollValueBehaviour.HEIGHT_TO_VAL));

        projectHeightInput.onlyActiveWhen(this::shouldShowProjectShip);
        projectHeightInput.setValue(2 * ProjectorHeightScrollValueBehaviour.HEIGHT_TO_VAL);
    }

    /*private final LazyShip lazyLinkShip = new LazyShip((l, owner) ->
        ShipUtil.getShipByID(l, ((VSProjectorBE) owner).linkShipId)
    );*/
    /*@Override
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
    }*/


    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        //if(clientPacket) return;

        NbtBuilder.modify(tag)
            .putCompound("content", itemHandler.serializeNBT())
            .putLong("link_ship_id", linkShipId)
            //.putDouble("ship_y_offset", shipYOffset)
            .putBlockPos("ship_center", shipCenter);
    }
    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        //if(clientPacket) return;

        NbtBuilder.modify(tag)
            .readCompoundDo("content", itemHandler::deserializeNBT)
            .readLongDo("link_ship_id", v -> linkShipId = v)
            //.readDoubleDo("ship_y_offset", v -> shipYOffset = v)
            .readBlockPosDo("ship_center", v -> shipCenter = v);
    }

    @Override
    public void tick() {
        super.tick();

        if (level.getBlockEntity(worldPosition.above()) instanceof ProjectorLenBe lenBe) {
            scale = lenBe.scale.getValue();
        } else {
            scale = 1.0;
        }

        if (!(level instanceof ServerLevel sLevel)) return;


        if (shouldShowProjectShip()) {
            ServerShip linkShip = ShipUtil.getServerShipByID(sLevel, linkShipId);
            if (linkShip == null)
                return;

            if (!(linkShip.getTransformProvider() instanceof ProjectShipTp tp))
                return;

            tp.rotateTickByRpm(speed);
        }
    }


    /*
    //todo add link_ship_id, ship_y_offset? in updateTag?
    //我在想这个东西没有UI，需要更新吗？不太了解getUpdateTag和handleUpdateTag
    @Override
    public CompoundTag getUpdateTag() {
        super.getUpdateTag()
        write(CompoundTag)
        return serializeNBT();
    }

    //todo update block state or model when update tag
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        load(tag);
    }*/

    @Override
    public void onLoad() {
        super.onLoad();

        afterUpdateGreenPrint();
    }

    public void afterUpdateGreenPrint() {
        if (!(level instanceof ServerLevel sLevel)) return;

        ServerShip linkShip = ShipUtil.getServerShipByID(sLevel, linkShipId);//lazyLinkShip.get(sLevel, this);

        ItemStack greenPrintStack = itemHandler.getStackInSlot(0);
        if (greenPrintStack.isEmpty()) {
            if (linkShip != null && linkShip.getTransformProvider() instanceof ProjectShipTp tp) {
                tp.setRotateRad(0);  //set zero rotation
            }

            //绿图被取出，投影船位置由TP通过shouldShowProjectShip更新
            return;
        }

        //ProjectingShipAtt att;
        if (linkShip == null) {
            //do not use pool since there will be an attachment added
            linkShip = VSGameUtilsKt.getShipObjectWorld(sLevel).createNewShipAtBlock(JomlUtil.i(worldPosition), false, 1.0, VSGameUtilsKt.getDimensionId(sLevel));//ShipPool.getOrCreatePool(sLevel).getOrCreateEmptyShip();
            linkShip.setStatic(true);
            linkShipId = linkShip.getId();
            ProjectingShipAtt.getOrCreate(linkShip, worldPosition);//.initialize(sLevel);
            NoPlayerCollisionAttachment.applyIgnoreAny(linkShip);

            shipCenter = JomlUtil.bpContaining(linkShip.getTransform().getPositionInShip());
        } /*else {
            att = ProjectingShipAtt.getOrCreate(linkShip, worldPosition);
        }*/

        linkShip.setTransformProvider(new ProjectShipTp(sLevel, linkShipId, worldPosition));


        EzDebug.warn("reconstruct the ship by afterUpdateGreenPrint");
        IShipSchemeData schemeData = GreenPrint.getOrCreateSchemeData(greenPrintStack);
        ShipBuilder shipBuilder = ShipBuilder.modify(sLevel, linkShip).overwriteByScheme(schemeData);
        if (schemeData.isEmpty()) {
            EzDebug.highlight("set project cetner because schemeData is empty");
            shipBuilder.addBlockAtOffset(BlockPos.ZERO, WapBlocks.Industrial.Projector.PROJECTION_CENTER.getDefaultState());
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





    public static class ProjectorHeightScrollValueBehaviour extends ScrollValueBehaviour {
        public static double VAL_TO_HEIGHT = 0.5;
        public static int HEIGHT_TO_VAL = 2;

        public ProjectorHeightScrollValueBehaviour(SmartBlockEntity be) {
            super(Component.literal("projection height"), be, new ProjectorValueBox());
            //super(CreateLang.translateDirect("kinetics.valve_handle.rotated_angle"), be, new ProjectorValueBox());
            //withFormatter(v -> String.valueOf(Math.abs(v)) + CreateLang.translateDirect("generic.unit.degrees").getString());
        }

        @Override
        public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
            List<Component> rows = List.of(Component.literal("height")/*.withStyle(ChatFormatting.BOLD)*/);
            return new ValueSettingsBoard(label, 60 * HEIGHT_TO_VAL, 1, rows, new ValueSettingsFormatter(this::formatValue));
        }

        @Override
        public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
            int value = Math.max(HEIGHT_TO_VAL, valueSetting.value());
            if (!valueSetting.equals(getValueSettings()))
                playFeedbackSound(this);
            //setValue(valueSetting.row() == 0 ? -value : value);
            setValue(value);
        }

        @Override
        public ValueSettings getValueSettings() {
            return new ValueSettings(0, Math.abs(value));
        }

        public MutableComponent formatValue(ValueSettings settings) {
            /*return CreateLang.number(Math.max(1, Math.abs(settings.value())))
                .add(CreateLang.translateDirect("generic.unit.degrees"))
                .component();*/
            return Component.literal(String.format("%.1f", settings.value() * VAL_TO_HEIGHT));
        }

        /*@Override
        public void onShortInteract(Player player, InteractionHand hand, Direction side) {

            if (getWorld().isClientSide)
                return;

            BlockState blockState = blockEntity.getBlockState();
            if (blockState.getBlock() instanceof ValveHandleBlock vhb)
                vhb.clicked(getWorld(), getPos(), blockState, player, hand);
        }*/
    }

    public static class ProjectorValueBox extends ValueBoxTransform.Sided {

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            //return direction == state.getValue(ValveHandleBlock.FACING);
            return direction != Direction.DOWN && direction != Direction.UP;
        }

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 16.1);
        }

        /*@Override
        public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
            Vec3 offset = getLocalOffset(level, pos, state);
            if (offset == null)
                return false;
            return localHit.distanceTo(offset) < scale / 1.5f;
        }*/

    }


}