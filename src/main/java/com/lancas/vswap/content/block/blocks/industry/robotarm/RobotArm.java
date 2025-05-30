package com.lancas.vswap.content.block.blocks.industry.robotarm;
/*
import com.lancas.vswap.content.WapBlockEntites;
import com.lancas.vswap.content.WapBlocks;
import com.lancas.vswap.content.block.blockentity.RobotArmBe;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.sandbox.industry.ConstructingShipBehaviour;
import com.lancas.vswap.subproject.blockplusapi.blockplus.BlockPlus;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.DirectionAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.IBlockAdder;
import com.lancas.vswap.subproject.blockplusapi.blockplus.adder.blockitem.SingleBlockItemAdderWrapper;
import com.lancas.vswap.subproject.blockplusapi.itemplus.ItemPlus;
import com.lancas.vswap.subproject.blockplusapi.itemplus.adder.*;
import com.lancas.vswap.subproject.blockplusapi.util.Action;
import com.lancas.vswap.subproject.lostandfound.content.LostAndFoundBehaviour;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.util.WapColors;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.NbtBuilder;
import com.lancas.vswap.util.WorldUtil;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Mod.EventBusSubscriber
public class RobotArm extends BlockPlus implements IBE<RobotArmBe> {
    //public static final int ALONG_RANGE = 5;
    //public static final int
    public static final double RANGE = 4.5;

    public static class Util {
        public static void clearArmPointNbt(ItemStack stack) {
            BlockItem.setBlockEntityData(stack, WapBlockEntites.ROBOT_ARM_BE.get(), new CompoundTag());
        }
        public static void saveArmBeDataTo(ItemStack stack, ServerLevel level, ArmInteractionPoint armPoint) {
            CompoundTag stackNbt = stack.getOrCreateTag();
            clearArmPointNbt(stack); //clean the itemNbt first: if fail to get, the nbt should be empty

            if (!(level.getBlockEntity(armPoint.getPos()) instanceof SmartBlockEntity be))
                return;

            LostAndFoundBehaviour laf = be.getBehaviour(LostAndFoundBehaviour.TYPE);
            if (laf == null || laf.getUuid() == null)
                return;

            BlockItem.setBlockEntityData(
                stack,
                WapBlockEntites.ROBOT_ARM_BE.get(),
                new NbtBuilder()
                    .putUUID("arm_target_uuid", laf.getUuid())
                    .putEnum("arm_point_mode", armPoint.getMode())
                    .get()
            );
        }
        @Nullable
        public static ArmInteractionPoint getArmPointFromNbt(Level level, @NotNull CompoundTag nbt) {
            //CompoundTag itemBeNbt = BlockItem.getBlockEntityData(stack);
            //if (itemBeNbt == null || itemBeNbt.isEmpty())
            //    return null;
            if (!nbt.contains("arm_target_uuid") || !nbt.contains("arm_point_mode"))
                return null;

            UUID uuid = nbt.getUUID("arm_target_uuid");

            BlockPos claimedBp = LostAndFoundBehaviour.latestClaimBp.get(uuid);
            if (claimedBp == null)
                return null;
            BlockState claimedState = level.getBlockState(claimedBp);

            ArmInteractionPoint armPoint = ArmInteractionPoint.create(level, claimedBp, claimedState);
            if (armPoint == null)
                return null;

            ArmInteractionPoint.Mode mode = NbtBuilder.modify(nbt).getEnum("arm_point_mode", ArmInteractionPoint.Mode.class);
            if (armPoint.getMode() != mode)
                armPoint.cycleMode();  //now mode only have two-mode and I don't want dead loop some situation, so I simply cycle once.

            return armPoint;
        }
    /.*@Nullable
    public static ArmInteractionPoint deserializeArmPointFromItemBeNbt(ItemStack stack, Level level, BlockPos anchor) {
        CompoundTag itemBeNbt = BlockItem.getBlockEntityData(stack);
        if (itemBeNbt == null || itemBeNbt.isEmpty())
            return null;

        CompoundTag savedArmPoint = itemBeNbt.getCompound("arm_point");
        return ArmInteractionPoint.deserialize(savedArmPoint, level, anchor);

    }
    public static void serializeArmPointToItemBeNbt(ItemStack stack, ArmInteractionPoint armPoint, Level level, BlockPos anchor) {
        CompoundTag savedArmPoint = armPoint.serialize(anchor);
        CompoundTag itemBeNbt = new NbtBuilder().putCompound("arm_point", savedArmPoint).get();  //set anchor zero (absolute coordinate)

        BlockItem.setBlockEntityData(stack, WapBlockEntites.ROBOT_ARM_BE.get(), itemBeNbt);  //set target aimInteractPoint
    }*./
    /.*@Nullable
    public static ArmInteractionPoint getArmPointFromNbt(ItemStack stack, Level level) {
        CompoundTag itemBeNbt = BlockItem.getBlockEntityData(stack);
        if (itemBeNbt == null || itemBeNbt.isEmpty())
            return null;

        UUID uuid = itemBeNbt.getUUID("arm_target_uuid");

        BlockPos claimedBp = LostAndFoundBehaviour.latestClaimBp.get(uuid);
        if (claimedBp == null)
            return null;
        BlockState claimedState = level.getBlockState(claimedBp);

        ArmInteractionPoint armPoint = ArmInteractionPoint.create(level, claimedBp, claimedState);
        if (armPoint == null)
            return null;

        ArmInteractionPoint.Mode mode = NbtBuilder.modify(itemBeNbt).getEnum("arm_point_mode", ArmInteractionPoint.Mode.class);
        if (armPoint.getMode() != mode)
            armPoint.cycleMode();  //now mode only have two-mode and I don't want dead loop some situation, so I simply cycle once.

        return armPoint;
    }*./
    }

    private static class Adders {
        private static final DirectionAdder BlockDirectionAdder = DirectionAdder.Horizon(false, true, Shapes.block());
        private static final IBlockAdder RangeArmPointShow = new SingleBlockItemAdderWrapper(new ItemPredictPlacementAdder(List.of(ItemPredictPlacementAdder.PlacementStage.Predict)) {
            @Override
            public InteractionResult predictPlacement(ItemStack stack, Level level, Player player, BlockPlaceContext ctx, PlacementStage stage) {
                if (!level.isClientSide)
                    return InteractionResult.PASS;

                BlockPos toPlacePos = ctx.getClickedPos();
                showRange(level, toPlacePos);

                CompoundTag itemBeNbt = BlockItem.getBlockEntityData(stack);
                if (itemBeNbt == null)
                    return InteractionResult.PASS;

                ArmInteractionPoint armPoint = Util.getArmPointFromNbt(level, itemBeNbt);
                if (armPoint == null)
                    return InteractionResult.PASS;

                showArmPoint(armPoint, isInRange(level, getRange(level, toPlacePos), armPoint.getPos()));
                return InteractionResult.PASS;
            }
        });
        private static final IBlockAdder ArmPointCyclerAndSaver = new SingleBlockItemAdderWrapper(
            new ItemUseFirstAdder(new Action.InteractionAction<UseOnContext>() {
                @Override
                public InteractionResult pre(UseOnContext ctx, InteractionResult soFar, Dest<Boolean> cancel) {
                    Level level = ctx.getLevel();
                    BlockPos useOn = ctx.getClickedPos();
                    ItemStack stack = ctx.getItemInHand();

                    BlockState useOnState = level.getBlockState(useOn);

                    ArmInteractionPoint prevArmPoint = null;
                    CompoundTag itemBeNbt = BlockItem.getBlockEntityData(stack);
                    if (itemBeNbt != null)
                        prevArmPoint = Util.getArmPointFromNbt(level, itemBeNbt);//deserializeArmPointFromItemBeNbt(stack, level, BlockPos.ZERO);

                    if (prevArmPoint != null && prevArmPoint.getPos().equals(useOn)) {  //clicked same point, should cycle
                        if (!level.isClientSide) {
                            prevArmPoint.cycleMode();
                            EzDebug.log("cycle mode to " + prevArmPoint.getMode());
                            //serializeArmPointToItemBeNbt(stack, prevArmPoint, level, BlockPos.ZERO);
                            Util.saveArmBeDataTo(stack, (ServerLevel)level, prevArmPoint);
                        }
                        return InteractionResult.CONSUME;
                    }

                    //should set another armPoint
                    ArmInteractionPoint armPoint = ArmInteractionPoint.create(level, useOn, useOnState);
                    if (armPoint == null)
                        return InteractionResult.PASS;
                    /.*ArmInteractionPointType armPointType = ArmInteractionPointType.getPrimaryType(level, useOn, useOnState);
                    if (armPointType == null || !armPointType.canCreatePoint(level, useOn, useOnState)) {  //armInterType can create point safe check
                        return InteractionResult.PASS;  //not interactable
                    }

                    ArmInteractionPoint armPoint = armPointType.createPoint(level, useOn, useOnState);
                    if (armPoint == null) {  //safe check
                        return InteractionResult.PASS;
                    }*./
                    if (!level.isClientSide) {
                        armPoint.cycleMode();  //it will get deposite mode first, cycle mode once to get take mode(or still dep mode if only dep)
                        //EzDebug.log("save arm point to item, mode:" + armPoint.getMode());
                        //serializeArmPointToItemBeNbt(stack, armPoint, level, BlockPos.ZERO);
                        Util.saveArmBeDataTo(stack, (ServerLevel)level, armPoint);
                        EzDebug.highlight("save item nbt:" + BlockItem.getBlockEntityData(stack));
                    }
                    return InteractionResult.CONSUME;  //consume to avoid place block
                }
            })
        );
        private static final IBlockAdder BeSyncAndItemNbtCleanAfterPlace = new SingleBlockItemAdderWrapper(
            new BlockItemOnPlaceAdder(new Action.InteractionAction<BlockPlaceContext>() {
                @Override
                public InteractionResult post(BlockPlaceContext ctx, InteractionResult soFar, Dest<Boolean> cancel) {
                    if (soFar == InteractionResult.FAIL)
                        return InteractionResult.FAIL;  //fail to put block

                    Level level = ctx.getLevel();
                    ItemStack stack = ctx.getItemInHand();
                    BlockPos useOn = ctx.getClickedPos();
                    EzDebug.log("post place invoked");

                    //clean item nbt after place block (post useOn)
                    if (!level.isClientSide)
                        Util.clearArmPointNbt(stack);

                    //sync server be to client
                    BlockState state = level.getBlockState(useOn);
                    if (!(state.getBlock() instanceof RobotArm robotArm)) {
                        EzDebug.warn("can't get robotArm block at " + useOn.toShortString() + " in post place");
                        return InteractionResult.PASS;
                    }
                    if (!(level.getBlockEntity(useOn) instanceof RobotArmBe be)) {
                        EzDebug.warn("get robotArm block but failed to get RobotArmBe at " + useOn.toShortString() + " in post place");
                        return InteractionResult.PASS;
                    }
                    be.notifyUpdate();
                    return InteractionResult.PASS;
                }
            })
        );
        private static final IBlockAdder ClearItemNbtWhenNotSelecting = new SingleBlockItemAdderWrapper((ItemInventoryTickAdder) (stack, level, entity, ix, selecting) -> {
            //remove tag if not selecting
                /.*if (!selecting && ItemPlus.isBlockPlusItemOf(stack, WapBlocks.Industrial.ROBOT_ARM.get())) {

                }*./
            if (!ItemPlus.isBlockPlusItemOf(stack, WapBlocks.Industrial.ROBOT_ARM.get())) {
                EzDebug.warn("the stack is not ROBOT_ARM");
                return;
            }

            if (!selecting) {
                if (!level.isClientSide)
                    Util.clearArmPointNbt(stack);
            } else {
                //selecting show armPoint is now in BlockItemPredictPlacementAdder
            }
        });
        private static final IBlockAdder DisableAttackOnArmInteractableBlock = new SingleBlockItemAdderWrapper((ItemAttackAdder)(state, level, pos, player) ->
            !ArmInteractionPoint.isInteractable(level, pos, state)
        );
    }




    public RobotArm(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public List<IBlockAdder> getAdders() {
        //Clear nbt when not in hand, and show outline of interact point
        return BlockPlus.addersIfAbsent(RobotArm.class, () -> List.of(
            Adders.BlockDirectionAdder,
            Adders.RangeArmPointShow,
            Adders.ArmPointCyclerAndSaver,
            Adders.BeSyncAndItemNbtCleanAfterPlace,
            Adders.ClearItemNbtWhenNotSelecting,
            Adders.DisableAttackOnArmInteractableBlock

            /.*new BlockItemHoverTextAdder() {
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> texts, TooltipFlag flag) {
                    texts.add(Component.literal("test test test"));
                }
            },*./
            /.*new SingleBlockItemAdderWrapper(new ItemUseOnAdder((ItemAdder.InteractionAction.Post<UseOnContext>) (stack, level, player, useOn, ctx) -> {

            }))*./
        ));
    }
    @SubscribeEvent
    public static void leftClickingBlocksDeselectsThem(PlayerInteractEvent.LeftClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel level))
            return;

        ItemStack stack = event.getItemStack();
        if (!ItemPlus.isBlockPlusItemOf(stack, WapBlocks.Industrial.ROBOT_ARM.get()))
            return;

        //ArmInteractionPoint armPoint = deserializeArmPointFromItemBeNbt(stack, level, BlockPos.ZERO);
        CompoundTag itemBeNbt = BlockItem.getBlockEntityData(stack);
        if (itemBeNbt == null)
            return;

        ArmInteractionPoint armPoint = Util.getArmPointFromNbt(level, itemBeNbt);
        if (armPoint == null)
            return;

        if (armPoint.getPos().equals(event.getPos())) {
              //clear if click same pos
            Util.clearArmPointNbt(stack);
        }
    }
    /.*@SubscribeEvent
    public static void rightClickingBlocksSelectsThem(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel level))
            return;

        ItemStack stack = event.getItemStack();
        if (!ItemPlus.isBlockPlusItemOf(stack, WapBlocks.Industrial.ROBOT_ARM.get()))
            return;

        BlockState clickOnState = level.getBlockState(event.getPos());
        if (ArmInteractionPoint.isInteractable(level, event.getPos(), clickOnState)) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }*./

    public static void showArmPoint(@NotNull ArmInteractionPoint armPoint, boolean inRange) {
        CreateClient.OUTLINER.showCluster("robot_arm_point", List.of(armPoint.getPos()))  //
            .colored(inRange ? armPoint.getMode().getColor() : WapColors.DARK_RED)
            .lineWidth(0.0625f);

        //EzDebug.log("show arm point at " + armPoint);
    }
    public static boolean isInRange(Level level, AABBdc range, BlockPos checkPos) {
        Vector3d armPointWorldCenter = WorldUtil.getWorldCenter(level, checkPos);
        return range.containsPoint(armPointWorldCenter);
    }
    public static AABBd getRange(Level level, BlockPos centerBp) {
        Vector3d worldCenter = WorldUtil.getWorldCenter(level, centerBp);
        return JomlUtil.dCenterExtended(worldCenter, RANGE);
    }
    public static void showRange(Level level, BlockPos bp) {
        CreateClient.OUTLINER.showAABB("robot_arm_range", JomlUtil.aabb(getRange(level, bp))/.*JomlUtil.centerExtended(bp, RANGE, RANGE, RANGE)*./)
            .lineWidth(0.125f);

        CreateClient.OUTLINER.showAABB("robot_arm_center", JomlUtil.centerExtended(bp, 0.03125))
            .lineWidth(0.0625f);
    }

    public static Stream<ConstructingShipBehaviour> getInRangeConstructingBehaviour(ServerLevel level, /.*BlockPos bp*./AABBdc range) {
        //Vector3d worldCenter = WorldUtil.getWorldCenter(level, bp);
        //AABBd worldAABB = JomlUtil.dCenterExtended(worldCenter, RANGE);

        return SandBoxServerWorld.getOrCreate(level).allServerShips()
            .filter(s -> {
                IRigidbodyDataReader rigidReader =  s.getRigidbody().getDataReader();

                AABBdc localAABB = s.getBlockCluster().getDataReader().getLocalAABB();
                if (!localAABB.isValid()) {
                    //if localAABB is not valid, means it has no block. check shipPos instead.
                    return range.containsPoint(rigidReader.getPosition());
                }

                Vector3dc localShipAABBCenter = localAABB.center(new Vector3d());
                Vector3dc worldShipAABBCenter = rigidReader.localToWorldPos(localShipAABBCenter, new Vector3d());

                return range.containsPoint(worldShipAABBCenter);
            })
            .map(s -> s.getBehaviour(ConstructingShipBehaviour.class))
            .filter(Objects::nonNull);
    }


    @Override
    public Class<RobotArmBe> getBlockEntityClass() { return RobotArmBe.class; }
    @Override
    public BlockEntityType<? extends RobotArmBe> getBlockEntityType() { return WapBlockEntites.ROBOT_ARM_BE.get(); }
    /.*@Override
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<S> p_153214_) {
        return (l, bp, state, be) -> { ((RobotArmBe)be).tick(); };
    }*./
    /.*@Override
    public boolean hasShaftTowards(LevelReader levelReader, BlockPos blockPos, BlockState blockState, Direction direction) {
        return false;
    }
    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) { return Direction.Axis.Y; }


    @Override
    public Class<ArmBlockEntity> getBlockEntityClass() { return ArmBlockEntity.class; }
    @Override
    public BlockEntityType<? extends ArmBlockEntity> getBlockEntityType() { return WapBlockEntites.INDUSTRIAL_ROBOT_ARM_BE.get(); }*./
}
*/