package com.lancas.vswap.content.block.blocks.industry.projector;

import com.lancas.vswap.content.WapBlocks;
import com.lancas.vswap.content.block.blocks.blockplus.util.InteractToInsertOrExtractAdder;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.*;
import com.lancas.vswap.subproject.blockplusapi.blockplus.ctx.BlockChangeContext;
import com.lancas.vswap.subproject.blockplusapi.util.Action;
import com.lancas.vswap.util.WorldUtil;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;
import java.util.function.Supplier;

import static com.lancas.vswap.content.WapBlockEntites.VS_PROJECTOR_BE;

public class VSProjector extends BlockPlus implements IBE<VSProjectorBE>, ICogWheel {
    public static BooleanProperty HAS_GREEN_PRINT = BooleanProperty.create("has_green_print");
    public static Supplier<BlockState> HAS_STATE = () -> WapBlocks.Industrial.Projector.VS_PROJECTOR.getDefaultState().setValue(HAS_GREEN_PRINT, true);
    public static Supplier<BlockState> EMPTY_STATE = () -> WapBlocks.Industrial.Projector.VS_PROJECTOR.getDefaultState().setValue(HAS_GREEN_PRINT, false);

    public VSProjector(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public List<IBlockAdder> getAdders() {
        return BlockPlus.addersIfAbsent(VSProjector.class,
            () -> List.of(
                new ShapeByStateAdder(state -> Shapes.block()),
                new PropertyAdder<>(HAS_GREEN_PRINT, false),
                new AnalogSignalAdder() {
                    @Override
                    public int getAnalogModifySignal(BlockState state, Level level, BlockPos pos) {
                        if (!(level.getBlockEntity(pos) instanceof VSProjectorBE be)) {
                            EzDebug.warn("fail to get VSProjectorBE at " + pos.toShortString());
                            return 0;
                        }
                        return be.itemHandler.getStackInSlot(0).isEmpty() ? 0 : 15;
                    }
                },
                new InteractToInsertOrExtractAdder() {
                    @Override
                    public boolean canInteract(Level level, BlockPos bp, BlockEntity be) {
                        return !level.isClientSide && be instanceof VSProjectorBE;
                    }

                    @Override
                    public ItemStack insert(BlockEntity be, ItemStack stack) {
                        VSProjectorBE projectorBE = (VSProjectorBE) be;

                        if (projectorBE.itemHandler.getStackInSlot(0).isEmpty()) {  //can insert only when inventory is empty
                            ItemStack remain = projectorBE.itemHandler.insertItem(0, stack, false);
                            if (!remain.equals(stack, true)) {
                                WorldUtil.updateBlockStateOfBe(projectorBE, HAS_STATE.get());
                                projectorBE.notifyUpdate();
                                projectorBE.afterUpdateGreenPrint();
                            }
                            return remain;
                        }

                        return stack;
                    }

                    @Override
                    public ItemStack extract(BlockEntity be) {
                        VSProjectorBE projectorBE = (VSProjectorBE) be;

                        if (!(be.getLevel() instanceof ServerLevel level)) {
                            EzDebug.warn("try extract in client! (or level is null?)");
                            return ItemStack.EMPTY;
                        }

                        ItemStack extract = projectorBE.getGreenPrint(level);
                        if (!extract.isEmpty()) {
                            WorldUtil.updateBlockStateOfBe(projectorBE, EMPTY_STATE.get());
                            projectorBE.itemHandler.setStackInSlot(0, ItemStack.EMPTY);
                            projectorBE.notifyUpdate();
                            projectorBE.afterUpdateGreenPrint();
                        }
                        return extract;
                    }
                },
                new IBlockRemoveCallbackAdder() {
                    private final Action<BlockChangeContext, Void> action = (Action.Pre<BlockChangeContext, Void>) (ctx, soFar, cancel) -> {
                        IBE.onRemove(ctx.oldState, ctx.level, ctx.pos, ctx.newState);
                        return null;
                    };
                    @Override
                    public Action<BlockChangeContext, Void> onRemove() { return action; }
                }
                /*,
                new InteractableBlockAdder() {
                    @Override
                    public InteractionResult onInteracted(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                        if (level.isClientSide) return InteractionResult.SUCCESS;
                        ServerLevel sLevel = (ServerLevel)level;

                        if (!(sLevel.getBlockEntity(pos) instanceof VSProjectorBE be)) {
                            EzDebug.warn("fail to get VSProjectorBE at " + pos.toShortString());
                            return InteractionResult.FAIL;
                        }

                        ItemStack handStack = player.getItemInHand(hand);
                        ItemStack inProjectorStack = be.getGreenPrint(sLevel);//be.itemHandler.getStackInSlot(0);

                        EzDebug.log("handStack:" + handStack.getItem().getName(handStack) + ", projectorStack:" + inProjectorStack.getItem().getName(inProjectorStack));

                        if (player.isShiftKeyDown()) { // 潜行右键取出物品
                            EzDebug.log("try extract item by player");
                            if (!handStack.isEmpty()) {
                                EzDebug.log("player has item in hand, can't extract");
                                return InteractionResult.PASS;  //玩家手里有东西，取出失败
                            }
                            if (!inProjectorStack.isEmpty()) {
                                //player.getInventory().placeItemBackInInventory(containerStack);
                                player.setItemInHand(hand, inProjectorStack);
                                be.itemHandler.extractItem(0, 1, false);
                                be.notifyUpdate();
                                be.afterUpdateGreenPrint();

                                EzDebug.log("player successfully extract item");
                                level.setBlockAndUpdate(pos, state.setValue(HAS_GREEN_PRINT, false));  //set the blockstate when extract
                                //todo level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5F, 1.0F);
                                return InteractionResult.SUCCESS;
                            }

                            EzDebug.warn("fail to extract green print");
                            return InteractionResult.PASS;
                        }

                        //放入绿图或者替换绿图
                        if (handStack.isEmpty() || !be.itemHandler.isItemValid(0, handStack)) {
                            EzDebug.log("item is not valid");
                            return InteractionResult.PASS;
                        }

                        if (inProjectorStack.isEmpty()) {  // 右键放入
                            //be.itemHandler.insertItem(0, handStack.copy(), false);
                            be.itemHandler.setStackInSlot(0, handStack.copy());
                            if (!player.isCreative()) {
                                player.setItemInHand(hand, ItemStack.EMPTY);
                            }

                            EzDebug.log("successfully insert item");
                            level.setBlockAndUpdate(pos, state.setValue(HAS_GREEN_PRINT, true));  //set the blockstate when insert

                            be.notifyUpdate();
                            be.afterUpdateGreenPrint();

                            //todo level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.5F, 1.0F);
                            return InteractionResult.SUCCESS;
                        } else {  //原物品不为空，替换物品
                            //ItemStack inProjectCopy = inProjectorStack.copy();
                            //ItemStack getGreenPrint = be.getGreenPrint(sLevel);
                            ItemStack inHandCopy = handStack.copy();
                            ItemStack inProjectorCopy = inProjectorStack.copy();

                            //player.setItemInHand(hand, inProjectorCopy);
                            be.itemHandler.setStackInSlot(0, handStack.copy());
                            player.setItemInHand(hand, inProjectorCopy);
                            be.notifyUpdate();
                            be.afterUpdateGreenPrint();

                            EzDebug.log("successully swap item");  //swap item, no need to replace block state

                            //todo level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5F, 1.0F);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }*/
            )
        );
    }


    @Override
    public Class<VSProjectorBE> getBlockEntityClass() { return VSProjectorBE.class; }
    @Override
    public BlockEntityType<? extends VSProjectorBE> getBlockEntityType() { return VS_PROJECTOR_BE.get(); }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.UP.getAxis();
    }
    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

}
