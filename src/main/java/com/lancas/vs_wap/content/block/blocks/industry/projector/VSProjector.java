package com.lancas.vs_wap.content.block.blocks.industry.projector;

import com.lancas.vs_wap.content.block.blockentity.VSProjectorBE;
import com.lancas.vs_wap.content.item.items.GreenPrint;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vs_wap.subproject.blockplusapi.blockplus.adder.*;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;

import static com.lancas.vs_wap.content.WapBlockEntites.VS_PROJECTOR_BE;

public class VSProjector extends BlockPlus implements IBE<VSProjectorBE>, ICogWheel {
    public static BooleanProperty HAS_GREEN_PRINT = BooleanProperty.create("has_green_print");

    public VSProjector(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public Iterable<IBlockAdder> getAdders() {
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
                        if (handStack.isEmpty() || !(handStack.getItem() instanceof GreenPrint) || !be.itemHandler.isItemValid(0, handStack)) {
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
                }
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
