package com.lancas.vswap.register;

import com.lancas.vswap.VsWap;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.mixins.valkyrien.ShipObjectServerWorldAccessor;
import com.lancas.vswap.ship.feature.hold.ICanHoldShip;
import com.lancas.vswap.ship.feature.hold.ShipHoldSlot;
import com.lancas.vswap.subproject.sandbox.constraint.base.IConstraint;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = VsWap.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerHoldShipForgeEvent {

    @SubscribeEvent
    public static void tryHoldShip(PlayerInteractEvent.RightClickBlock event) {
        //EzDebug.log("phase:" + event.getPhase() + " has result:" + event.hasResult() + " " + event.);
        //only in server because hold/unhold must be in server and then can be synced to client
        //EzDebug.log("event:" + event);
        if (!(event.getLevel() instanceof ServerLevel sLevel)) return;

        Player player = event.getEntity();
        BlockPos interactBp = event.getPos();

        if (!(player instanceof ICanHoldShip icanHoldShip)) {
            EzDebug.fatal("player can not hold ship because of unkown reason!");
            return;
        }

        //only invoke when hand is empty
        //check air for safety
        if (!player.getMainHandItem().isEmpty() || !player.isShiftKeyDown() || sLevel.getBlockState(interactBp).isAir()) return;

        var vsConstraints = ((ShipObjectServerWorldAccessor)VSGameUtilsKt.getShipObjectWorld(sLevel)).getConstraints();
        ServerShip ship = ShipUtil.getServerShipAt(sLevel, interactBp);
        if (ship == null) return;
        if (ship.getTransformProvider() != null) {  //FIXME multiple invoke
            //FIXME 治标不治本
            if (!Objects.equals(icanHoldShip.getHoldingShipId(ShipHoldSlot.MainHand), ship.getId()))
                player.sendSystemMessage(Component.translatable("msg.vswap.hold_ship.failed.by_tp_nonnull"));
            return;
        }
        boolean hasAnyConstraint = false;
        for (VSConstraint c : vsConstraints.values()) {
            if (c.getShipId1() == ship.getId() || c.getShipId0() == ship.getId()) {
                hasAnyConstraint = true;
                break;
            }
        }
        if (hasAnyConstraint) {
            player.sendSystemMessage(Component.translatable("msg.vswap.hold_ship.failed.by_has_constraint"));
            return;
        }

        //EzDebug.log("which is null?" + icanHoldShip + ", " + ShipHoldSlot.MainHand + ", ship:" + ship);
        boolean success = icanHoldShip.tryHoldInServer(ShipHoldSlot.MainHand, ship.getId(), true);
        if (!success) {
            player.sendSystemMessage(Component.translatable("msg.vswap.hold_ship.failed.by_no_holdable_attachment"));
        }
    }

    @SubscribeEvent
    public static void unHoldWhenHandIsFull(TickEvent.PlayerTickEvent event) {
        //unhold ship must be in server
        if (!(event.player instanceof ServerPlayer sPlayer)) return;
        if (!(event.player instanceof ICanHoldShip icanHoldShip)) {
            EzDebug.fatal("player can't hold ship because of unknown reason");
            return;
        }

        //make sure player is holding ship and holding an item, then unhold ship.
        if (sPlayer.getMainHandItem().isEmpty()) return;
        if (!icanHoldShip.isHoldingShip(ShipHoldSlot.MainHand)) return;

        icanHoldShip.unholdShipInServer(ShipHoldSlot.MainHand, true);
        event.player.sendSystemMessage(Component.translatable("msg.vswap.hold_ship.drop.by_has_item_in_hand"));
    }
}
