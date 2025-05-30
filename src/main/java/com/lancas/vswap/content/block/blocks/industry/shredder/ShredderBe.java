package com.lancas.vswap.content.block.blocks.industry.shredder;

import com.lancas.vswap.content.behaviour.block.ChuteLikeOutputBehaviour;
import com.lancas.vswap.content.capacity.IOInventoryUtil;
import com.lancas.vswap.content.capacity.ListInventory;
import com.lancas.vswap.content.recipe.PersistenceKineticProcessContext;
import com.lancas.vswap.content.recipe.SingleStackProcessHandler;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.LazyTicks;
import com.lancas.vswap.subproject.mstandardized.MaterialStandardizedItem;
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShredderBe extends BlockBreakingKineticBlockEntity /*KineticBlockEntity*/ {

    protected final ItemStackHandler inInventory = new ItemStackHandler(1);  //don't create in because inner class have to extract stack//InOutInventory.In.create(1);  //special handle docker
    protected final ListInventory outInventory = new ListInventory();//new InventoryWithCache(new ItemStackHandler(4));
    //protected final IItemHandler outInventoryWrapper = InOutInventory.Out.wrap(realOutInventory);

    /*protected final LazyOptional<IOItemHandlerWrapper> ioHandler = LazyOptional.of(() ->
        new IOItemHandlerWrapper(inInventory, outInventory)
    );*/
    protected final LazyOptional<IItemHandler> inOnlyHandler = LazyOptional.of(() -> IOInventoryUtil.In.wrap(inInventory));
    protected final LazyOptional<IItemHandler> outOnlyHandler = LazyOptional.of(() -> IOInventoryUtil.Out.wrap(outInventory));

    //todo time increase as stack count increase
    protected final PersistenceKineticProcessContext processCtx = PersistenceKineticProcessContext.speedFromBe(this).buildByTicksUnderMaxSpeed(10);
    protected final SingleStackProcessHandler<PersistenceKineticProcessContext> processHandler = new SingleStackProcessHandler<>() {
        @Override
        public ItemStack collectStackToProcess() {
            ItemStack collected = inInventory.extractItem(0, 64, false);
            //EzDebug.log("collect item:" + collected);
            if (!collected.isEmpty())
                notifyUpdate();
            return collected;
        }  //get item by extraction

        @Override
        public void onComplete() {
            if (processingStack.isEmpty()) {
                EzDebug.warn("on complete process and find that processedStack is empty");
                return;
            }

            Item processedItem = processingStack.getItem();
            if (processedItem instanceof IShredderableItem shredderable) {
                outInventory.insertAllStacked(shredderable.getProducts(processingStack));
            } else if (processedItem instanceof BlockItem blockItem) {
                ItemStack msStack = MaterialStandardizedItem.fromBlockItem(blockItem, processingStack.getCount() * MaterialStandardizedItem.getCntByScale(1));  //todo count is mul by scale 1 count
                outInventory.insertItemStacked(msStack);

                EzDebug.highlight("onComplete insert to outInv");
            } else {
                //will destroy the item
            }
            notifyUpdate();
        }

        private final LazyTicks lazyTicks = new LazyTicks(4);
        @Override
        public boolean shouldLazyWork(PersistenceKineticProcessContext ctx) { return lazyTicks.shouldWork(); }

        @Override
        public void tick(PersistenceKineticProcessContext ctx) {
            //EzDebug.log("current progress:" + progress + ", tickProg:" + ctx.getTickProgression() + ", processing:" + processingStack);
            super.tick(ctx);
        }
    };


    public ShredderBe(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        //AllBlockEntityTypes.ITEM_VAULT.ITEM_VAULT
        EzDebug.log("on create inv:" + inInventory);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this)
            .onlyInsertWhen((dir) -> outInventory != null && outInventory.isEmpty() && !processHandler.canProcess())
        );
        behaviours.add(new ChuteLikeOutputBehaviour(this, () -> outInventory,  //must deferred or will accept a null inventory
            () -> getBlockState().getValue(Shredder.FACING).getOpposite(),
            be -> false  //allow all
            //be -> be instanceof BeltBlockEntity  //don't insert to belt  //todo allow belt?
        ));
        super.addBehaviours(behaviours);
    }


    @Override
    public void tick() {
        super.tick();
        processHandler.tick(processCtx);
    }
    @Override
    public void lazyTick() {
        super.lazyTick();
        outInventory.clearEmptySlots();
    }

    @Override
    protected BlockPos getBreakingPos() {
        /*BlockPos target = this.getBlockPos().relative(this.getBlockState().getValue(Pulverizer.FACING), 2);
        EzDebug.log("target:" + target.toShortString());
        //return this.getBlockPos().relative(this.getBlockState().getValue(Pulverizer.FACING));
        CreateClient.OUTLINER.showCluster("pul breakingPos", List.of(target))
            .lineWidth(0.0675f);

        if (level != null)
            EzDebug.log("to break state:" + StrUtil.getBlockName(level.getBlockState(target)));

        return target;*/
        return this.getBlockPos().relative(this.getBlockState().getValue(Shredder.FACING));
    }

    //todo can't break when outInvHasItem?

    @Override
    public void onBlockBroken(BlockState stateToBreak) {
        if (level == null)
            return;

        //super.onBlockBroken(stateToBreak);
        BlockPos breakingPos = this.breakingPos;
        if (breakingPos == null) {
            EzDebug.warn("onBlockBroken get null breakingPos");
            return;
        }
        outInventory.insertItemStacked(MaterialStandardizedItem.fromWorldBlock(level, breakingPos));

        //level.setBlockAndUpdate(breakingPos, Blocks.AIR.defaultBlockState());
        BlockHelper.destroyBlock(level, breakingPos, 1, drop -> {});
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            Direction stateDir = getBlockState().getValue(Shredder.FACING);
            if (side == stateDir.getOpposite()) {
                EzDebug.log("get outOnly Handler from ");
                return outOnlyHandler.cast();
            }

            return inOnlyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override // com.simibubi.create.foundation.blockEntity.SmartBlockEntity
    public void destroy() {
        super.destroy();
        if (level == null) {
            EzDebug.error("fail to drop items because level is null");
            return;
        }

        ItemHelper.dropContents(level, worldPosition, inInventory);
        ItemHelper.dropContents(level, worldPosition, outInventory);
        /*for(int slot = 0; slot < outInventoryWrapper.getSlots(); ++slot) {
            ItemStack stack = outInventoryWrapper.getStackInSlot(slot);
            EzDebug.warn("slot:" + slot + ", drop item:" + stack);
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
        }*/

        //no need to get worldPos because vs have mixin it
        ItemStack processingStack = processHandler.getProcessingStack();
        if (!processingStack.isEmpty())
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(),worldPosition.getZ(), processingStack);
    }


    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        inInventory.deserializeNBT(tag.getCompound("in_inventory"));
        outInventory.deserializeNBT(tag.getCompound("out_inventory"));
    }

    @Override
    public void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("in_inventory", inInventory.serializeNBT());
        tag.put("out_inventory", outInventory.serializeNBT());
    }
    /*@Override
        protected BlockPos getBreakingPos() {
            BlockPos bp = getBlockPos().relative(getBlockState().getValue(Pulverizer.FACING));
            CreateClient.OUTLINER.showCluster("puliverizer test", List.of(bp));
            return bp;
        }


        @Override
        public void tick() {
            super.tick();
            if (speed != 0)
                EzDebug.light("be speed:" + speed);

        }*/
}
