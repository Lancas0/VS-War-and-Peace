package com.lancas.vs_wap.content.item.items;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.impl.game.ships.ShipDataCommon;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.util.RelocationUtilKt;
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl;


public class ScalableShipCreatorItem extends Item {
    private static final String Scale_Tag = "scale";

    public ScalableShipCreatorItem(Properties props) {
        super(props);
    }


    public static double getScale(ItemStack itemStack) {
        double scale = itemStack.getOrCreateTag().getDouble(Scale_Tag);
        if (scale <= 0) {
            setScale(itemStack, 0.1);
            return 0.1;
        }
        return scale;
    }
    public static void setScale(ItemStack itemStack, double scale) {
        scale = Math.max(scale, 0.1);
        itemStack.getOrCreateTag().putDouble(Scale_Tag, scale);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
        //only run in server side
        if (ctx.getLevel().isClientSide)
            return InteractionResult.PASS;

        ServerLevel level = (ServerLevel)ctx.getLevel();
        ServerPlayer player = (ServerPlayer)ctx.getPlayer();
        BlockPos clickedPos = ctx.getClickedPos();

        BlockState clickedOn = level.getBlockState(clickedPos);
        if (clickedOn.isAir())
            return InteractionResult.PASS;


        double scale = getScale(stack);
        //Vector3i shipPos = new Vector3i(clickedPos.getX(), clickedPos.getY(), clickedPos.getZ());
        ServerShip parentShip = VSGameUtilsKt.getShipManagingPos(level, clickedPos);
        // Make a ship
        String dimensionId = VSGameUtilsKt.getDimensionId(level);

        ServerShip serverShip =
            VSGameUtilsKt.getShipObjectWorld(level).createNewShipAtBlock(JomlUtil.i(clickedPos), false, scale, dimensionId);

        BlockPos centerPos = JomlUtil.bp(serverShip.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level), new Vector3i()));

        // Move the block from the world to a ship
        RelocationUtilKt.relocateBlock(level, clickedPos, centerPos, true, serverShip, Rotation.NONE);

        EzDebug.log("Shipified!");

        if (parentShip != null) {
            // Compute the ship transform
            Vector3d newShipPosInWorld = parentShip.getShipToWorld().transformPosition(JomlUtil.d(clickedPos).add(0.5, 0.5, 0.5));
            Vector3d newShipPosInShipyard = JomlUtil.d(clickedPos).add(0.5, 0.5, 0.5);

            Quaterniondc newShipRotation = parentShip.getTransform().getShipToWorldRotation();
            var newShipScaling = parentShip.getTransform().getShipToWorldScaling().mul(scale, new Vector3d());
            /*if (newShipScaling.x() < minScaling) {
                // Do not allow scaling to go below minScaling
                newShipScaling = Vector3d(minScaling, minScaling, minScaling)
            }*/
            ShipTransformImpl shipTransform =
                new ShipTransformImpl(newShipPosInWorld, newShipPosInShipyard, newShipRotation, new Vector3d(scale, scale, scale));
            ((ShipDataCommon)serverShip).setTransform(shipTransform);
        }

        return super.useOn(ctx);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        //return super.use(p_41432_, p_41433_, p_41434_);
        if (level.isClientSide)
            return super.use(level, player, hand);

        ItemStack stackInHand = player.getItemInHand(hand);

        double oldScale = getScale(stackInHand);
        double newScale;
        if (player.isShiftKeyDown()) {
            newScale = Math.max(0.1, oldScale - 0.1);
        } else {
            newScale = oldScale + 0.1;
        }

        setScale(stackInHand, newScale);
        EzDebug.log("current scale: " + newScale);

        return InteractionResultHolder.success(stackInHand);
    }

    /*private InteractionResult ReadShipAndGetComponentItem(UseOnContext context) {
        BlockPos interactPos = context.getClickedPos();
        ServerPlayer player = Objects.requireNonNull((ServerPlayer)context.getPlayer());
        ServerLevel level = Objects.requireNonNull((ServerLevel)context.getLevel());

        if (level.getBlockState(interactPos).isAir()) {
            //TODO this is DEBUG
            EzDebug.Log("interaction pass by use on air");
            return InteractionResult.PASS;
        }

        ServerShip clickedShip = VSGameUtilsKt.getShipManagingPos(level, interactPos);
        if (clickedShip == null) {
            //TODO this is DEBUG
            EzDebug.Log("interaction pass by not use on ship");
            return InteractionResult.PASS;
        }

        givePlayerComponentItem(clickedShip, context);
        if (deleteShip()) {
            VSGameUtilsKt.getShipObjectWorld(level).deleteShip(clickedShip);
        }
        //ComponentType type = getComponentType(clickedShip);
        /.*ItemUtil.giveItem(player, type.getDefaultItem(), giveStack -> {
            initComponentStack();
        });*./
        //player.sendSystemMessage(Component.literal("already give item: " + (shipRecordItemStack != null)));

        //test
        /.*((ShipDataCommon)clickedShip).setTransform(
                clickedShip.getTransform().copy(
                        clickedShip.getTransform().getPositionInWorld(),
                        clickedShip.getTransform().getPositionInShip(),
                        clickedShip.getTransform().getShipToWorldRotation(),
                        new Vector3d(0.5, 0.5, 0.5)
                )
        );*./

        return InteractionResult.CONSUME;
        /.*
        VSGameUtilsKt.get


        DenseBlockPosSet test = new DenseBlockPosSet();
        test.add(ctx.getClickedPos());

        val level = ctx.level as? ServerLevel ?: return super.useOn(ctx)
        val blockPos = ctx.clickedPos
        val blockState: BlockState = level.getBlockState(blockPos)
        val item = ctx.itemInHand
        val tag = item.orCreateTag;
        val player = ctx.player ?: return InteractionResult.FAIL;
        val dimensionId = level.dimensionId

        player.sendSystemMessage(Component.literal("usedOn is triggered"));


        if (item.item !is MyItem) {
            return InteractionResult.FAIL
        }
        if (blockState.isAir) {
            return super.useOn(ctx)
        }

        //已经获取了第一个点
        if (tag.contains("firstPosX")) {
            val firstPosX = tag.getInt("firstPosX")
            val firstPosY = tag.getInt("firstPosY")
            val firstPosZ = tag.getInt("firstPosZ")

            val currentScale = tag.getDouble("scale");

            if (level.shipObjectWorld.isBlockInShipyard(blockPos.x, blockPos.y, blockPos.z, dimensionId) != level.shipObjectWorld.isBlockInShipyard(firstPosX, firstPosY, firstPosZ, dimensionId)) {
                player.sendSystemMessage(Component.translatable("Cannot assemble between ship and world!"))
            } else if (level.getShipObjectManagingPos(blockPos) != level.getShipObjectManagingPos(Vec3i(firstPosX, firstPosY, firstPosZ))) {
                player.sendSystemMessage(Component.translatable("Cannot assemble something between two ships!"))
            } else {
                val blockAABB = AABBi(blockPos.toJOML(), Vec3i(firstPosX, firstPosY, firstPosZ).toJOML())
                blockAABB.correctBounds()
                val blocks = ArrayList<BlockPos>()

                for (x in blockAABB.minX..blockAABB.maxX) {
                    for (y in blockAABB.minY..blockAABB.maxY) {
                        for (z in blockAABB.minZ..blockAABB.maxZ) {
                            if (level.getBlockState(BlockPos(x, y, z)).isAir) {
                                continue
                            }
                            blocks.add(BlockPos(x, y, z))
                        }
                    }
                }
                player.sendSystemMessage(
                        Component.translatable("Assembling (${blockPos.x}, ${blockPos.y}, ${blockPos.z}) to ($firstPosX, $firstPosY, $firstPosZ)!"))
                ShipAssembler.assembleToShip(level, blocks, true, currentScale)
            }

            item.tag = tag.apply {
                remove("firstPosX")
                remove("firstPosY")
                remove("firstPosZ")
            }
        } else {
            item.tag = tag.apply {
                putInt("firstPosX", blockPos.x)
                putInt("firstPosY", blockPos.y)
                putInt("firstPosZ", blockPos.z)
            }
            player.sendSystemMessage(
                    Component.translatable("First block selected: (${blockPos.x}, ${blockPos.y}, ${blockPos.z})"))
        }

        //选择了点，不触发onUse里的方法(使得onUse只在点击空气时才触发)
        return InteractionResult.CONSUME;*./
    }*/
}
