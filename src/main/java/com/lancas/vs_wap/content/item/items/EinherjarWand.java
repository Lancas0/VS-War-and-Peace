package com.lancas.vs_wap.content.item.items;

import com.lancas.vs_wap.foundation.network.NetworkHandler;
import com.lancas.vs_wap.foundation.network.server2client.ShipHolderRenderPacketS2C;
import com.lancas.vs_wap.ship.attachment.HoldableAttachment;
import com.lancas.vs_wap.content.item.items.base.ShipInteractableItem;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

public class EinherjarWand extends ShipInteractableItem {
    public EinherjarWand(Item.Properties p_41383_) { super(p_41383_); }

    @Override
    public InteractionResult onItemUseOnShip(ItemStack stack, @NotNull Ship ship, @NotNull Level level, @NotNull Player player, UseOnContext ctx) {
        if (level.isClientSide) return InteractionResult.PASS;  //have to add attachemnt so have to do it in server

        BlockPos interactBp = ctx.getClickedPos();
        BlockState state = level.getBlockState(interactBp);
        ServerShip sShip = (ServerShip)ship;

        Vector3d forwardInWorld = JomlUtil.d(player.getForward());
        if (player.isShiftKeyDown()) {
            forwardInWorld.negate();
        }
        Direction forwardInShip = JomlUtil.nearestDir(ship.getWorldToShip().transformDirection(forwardInWorld));

        HoldableAttachment holdable = sShip.getAttachment(HoldableAttachment.class);
        if (holdable != null && holdable.forwardInShip.equals(forwardInShip) && holdable.holdPivotBpInShip.equalsBp(interactBp)) {
            sShip.saveAttachment(HoldableAttachment.class, null);
        } else {
            HoldableAttachment.apply(sShip, interactBp, forwardInShip);
        }

        return InteractionResult.PASS;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int ix, boolean selecting) {
        super.inventoryTick(stack, level, entity, ix, selecting);

        if (!selecting) return;

        //get attachment in server, then send packet to client for rendering
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;

        HitResult pickResult = player.pick(5.0, 1f, false);
        if (pickResult.getType() != HitResult.Type.BLOCK) return;
        BlockHitResult blockPickResult = (BlockHitResult)pickResult;

        BlockPos lookAtBp = blockPickResult.getBlockPos();
        ServerShip ship = ShipUtil.getServerShipAt((ServerLevel)level, lookAtBp);
        if (ship == null) return;

        HoldableAttachment holdable = ship.getAttachment(HoldableAttachment.class);
        if (holdable == null) return;

        NetworkHandler.sendToClientPlayer(
            (ServerPlayer)player,
            new ShipHolderRenderPacketS2C(holdable.holdPivotBpInShip.toBp(), holdable.forwardInShip)
        );
    }
}
