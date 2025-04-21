package com.lancas.vs_wap.content.items;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.Blocks;
import org.joml.*;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.lang.Math;
import java.text.NumberFormat;
import java.util.Locale;

public class TestItem extends Item {
    public TestItem(Properties p_41383_) { super(p_41383_); }

    public void setMove(ItemStack stack, int y) { stack.getOrCreateTag().putInt("move", y); }
    public int getMove(ItemStack stack) { return stack.getOrCreateTag().getInt("move"); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) return InteractionResultHolder.pass(stack);

        int move = player.isShiftKeyDown() ? -1 : 1;
        setMove(stack, getMove(stack) + move);
        EzDebug.log("current move:" + getMove(stack));

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
        //only run in server side
        if (ctx.getLevel().isClientSide)
            return InteractionResult.FAIL;

        ServerLevel level = (ServerLevel)ctx.getLevel();
        ServerPlayer player = (ServerPlayer)ctx.getPlayer();
        BlockPos interactPos = ctx.getClickedPos();

        if (level.getBlockState(interactPos).isAir()) return InteractionResult.FAIL;

        if (player.isShiftKeyDown()) {
            testBuild(stack, level, ctx.getClickedPos(), ctx.getClickedFace());
            return InteractionResult.SUCCESS;
        }

        //ServerShip clickedShip = VSGameUtilsKt.getShipManagingPos(level, interactPos);
        ServerShip ship = ShipUtil.getServerShipAt(level, interactPos);
        if (ship == null) {
            EzDebug.log("get null ship");
            return InteractionResult.FAIL;
        }

        //InverseGravityForceInducer.getOrCreate(ship);
        //ThrowForceInducer.createOrReset(ship, JomlUtil.d(player.getViewVector(1f)).mul(100));

        /*if (!player.isShiftKeyDown()) {

            //checkPos(ship, ctx.getClickedPos());
            return InteractionResult.SUCCESS;
        }*/
        if (player.isShiftKeyDown()) {
            VSGameUtilsKt.getShipObjectWorld(level).teleportShip(ship, new ShipTeleportDataImpl(
                ship.getTransform().getPositionInWorld(),
                ship.getTransform().getShipToWorldRotation(),
                ship.getVelocity(),
                ship.getOmega(),
                VSGameUtilsKt.getDimensionId(level),
                Math.max(0.05, ship.getTransform().getShipToWorldScaling().x() - 0.1)
            ));
        } else {
            VSGameUtilsKt.getShipObjectWorld(level).teleportShip(ship, new ShipTeleportDataImpl(
                ship.getTransform().getPositionInWorld(),
                ship.getTransform().getShipToWorldRotation(),
                ship.getVelocity(),
                ship.getOmega(),
                VSGameUtilsKt.getDimensionId(level),
                ship.getTransform().getShipToWorldScaling().x() + 0.1
            ));
        }

        return InteractionResult.SUCCESS;



        /*ServerLevel level = (ServerLevel)ctx.getLevel();
        ServerPlayer player = (ServerPlayer)ctx.getPlayer();
        BlockPos interactPos = ctx.getClickedPos();

        if (level.getBlockState(interactPos).isAir()) {
            return InteractionResult.PASS;
        }

        ServerShip clickedShip = VSGameUtilsKt.getShipObjectManagingPos(level, interactPos);
        if (clickedShip == null) {
            clickedShip = VSGameUtilsKt.getShipManagingPos(level, interactPos);
        }
        if (clickedShip == null) return InteractionResult.PASS;

        clickedShip.setSlug(clickedShip.getSlug() + "_player_no_collision_");
        TestForceInductor.getOrCreate(clickedShip, player.getUUID());
        return InteractionResult.CONSUME;*/
    }
    public static NumberFormat format() {
        NumberFormat df;
        df = NumberFormat.getNumberInstance(Locale.ENGLISH);
        df.setGroupingUsed(false);
        return df;
    }

    private void checkPos(ServerShip ship, BlockPos clickPos) {
        EzDebug.log("----------------------------");

        EzDebug.log("click block pos:" + clickPos);
        EzDebug.log("click block worldCenter:" + ship.getShipToWorld().transformPosition(JomlUtil.dCenter(clickPos)).toString(format()));
        EzDebug.log("click ship pos:" + ship.getTransform().getPositionInShip().get(new Vector3d()).toString(format()));
        EzDebug.log("click ship worldPos:" + ship.getTransform().getPositionInWorld().get(new Vector3d()).toString(format()));
        EzDebug.log("click ship mass center:" + ship.getInertiaData().getCenterOfMassInShip().get(new Vector3d()).toString(format()));
    }
    private void testBuild(ItemStack stack, ServerLevel level, BlockPos clickPos, Direction face) {
        EzDebug.log("----------------------------");

        BlockPos startPos = clickPos.relative(face);
        ShipBuilder builder = new ShipBuilder(startPos, level, 0.5, false).setStatic(true);

        builder.addBlock(new BlockPos(0, 0, 0), Blocks.OAK_LOG.defaultBlockState());
        /*Vector3d initShipCenterShipSpace = builder.get().getTransform().getPositionInShip().get(new Vector3d());
        Vector3d initWorldPos = builder.get().getTransform().getPositionInWorld().get(new Vector3d());

        EzDebug.Log("[inital] shipPos:" + builder.get().getTransform().getPositionInShip().get(new Vector3d()).toString(format()));
        EzDebug.Log("[inital] worldPos:" + builder.get().getTransform().getPositionInWorld().get(new Vector3d()).toString(format()));
        EzDebug.Log("[inital] massCenter:" + builder.get().getInertiaData().getCenterOfMassInShip().get(new Vector3d()).toString(format()));
*/
        builder.addBlock(new BlockPos(0, 1, 0), Blocks.CARVED_PUMPKIN.defaultBlockState());
        builder.addBlock(new BlockPos(0, 2, 0), Blocks.CARVED_PUMPKIN.defaultBlockState());

        Vector3d realWorld = builder.calUpdatedWorldPos();
        Vector3d realShipPos = builder.calUpdatedShipPos();

        EzDebug.log("[built] massCenter:" + builder.get().getInertiaData().getCenterOfMassInShip().get(new Vector3d()).toString(format()));
        EzDebug.log("[Guess] worldOffset: " + realWorld.sub(builder.get().getTransform().getPositionInWorld(), new Vector3d()));



        //builder.setWorldPos(new Vector3d(0, getMove(stack), 0).add(realWorld));
        //builder.moveShipPosToWorldPos(initShipCenterShipSpace, JomlUtil.dCenter(startPos).add(0, getMove(stack), 0));
        /*builder.moveLocalPosToWorldPos(new Vector3d(), new Vector3d(0, getMove(stack), 0).add(initWorldPos));

        EzDebug.Log("[moved] shipPos:" + builder.get().getTransform().getPositionInShip().get(new Vector3d()).toString(format()));
        EzDebug.Log("[moved] worldPos:" + builder.get().getTransform().getPositionInWorld().get(new Vector3d()).toString(format()));
        EzDebug.Log("[moved] massCenter:" + builder.get().getInertiaData().getCenterOfMassInShip().get(new Vector3d()).toString(format()));

         */
        /*
        EzDebug.Log("[added] shipPos:" + builder.get().getTransform().getPositionInShip().get(new Vector3d()).toString(format()));
        EzDebug.Log("[added] worldPos:" + builder.get().getTransform().getPositionInWorld().get(new Vector3d()).toString(format()));
        EzDebug.Log("[added] massCenter:" + builder.get().getInertiaData().getCenterOfMassInShip().get(new Vector3d()).toString(format()));

        EzDebug.Log("[Guess] updated shipPos:" + builder.get().getInertiaData().getCenterOfMassInShip().get(new Vector3d()).toString(format()));
        Vector3d worldOffset = builder.get()
            .getShipToWorld().transformPosition(
                builder.get()
                .getInertiaData().getCenterOfMassInShip().get(new Vector3d())
            );
        EzDebug.Log("[Guess] worldPos pos:" + worldOffset.toString(format()));*/
        //EzDebug.Log("[Guess] updated worldPos:" + (builder.get().getTransform().getPositionInWorld().get(new Vector3d()).add(worldOffset)).toString(format()));
        /*EzDebug.Log("add block at local pos (0, 1, 0)");
        EzDebug.Log("cur shipPos:" + builder.get().getTransform().getPositionInShip().get(new Vector3d()).toString(format()));

        //EzDebug.Log("updated? shipPos:" + builder.get().getTransform().getPositionInShip().get(new Vector3d()).toString(format()));

        EzDebug.Log("loca2World:" + builder.getLocalToWorld().get(new Matrix4d()).toString(format()));
        EzDebug.Log("local2World (0,0,0):" + builder.localToWorldPos(new Vector3d()).toString(format()));
        EzDebug.Log("local2World (0,1,0):" + builder.localToWorldPos(new Vector3d(0, 1, 0)).toString(format()));

        builder.move(new Vector3d(0, 1, 0));
        EzDebug.Log("[moved] worldPos:" + builder.get().getTransform().getPositionInWorld().get(new Vector3d()).toString(format()));
        EzDebug.Log("[moved] local2World (0,1,0):" + builder.localToWorldPos(new Vector3d(0, 1, 0)).toString(format()));


        builder.move(new Vector3d(0, -1, 0));*/
        /*EzDebug.Log("[init] local2World (0,0,1):" + builder.localToWorldPos(new Vector3i(0, 0, 1)).toString(format()));
        builder.rotateForwardTo(new Vector3d(0, 1, 0));
        EzDebug.Log("[rotated] local2World (0,0,1):" + builder.localToWorldPos(new Vector3i(0, 0, 1)).toString(format()));*/
        /*builder.rotate(new Quaterniond().rotateLocalY(Math.toRadians(90)));
        EzDebug.Log("[rotated] local2World (1,0,0):" + builder.localToWorldPos(new Vector3d(0, 0, 1)).toString(format()));
        builder.rotate(new Quaterniond().rotateLocalY(Math.toRadians(90)));
        EzDebug.Log("[rotated] local2World (1,0,0):" + builder.localToWorldPos(new Vector3d(0, 0, 1)).toString(format()));
        builder.rotate(new Quaterniond().rotateLocalY(Math.toRadians(90)));
        EzDebug.Log("[rotated] local2World (1,0,0):" + builder.localToWorldPos(new Vector3d(0, 0, 1)).toString(format()));*/
    }
}
