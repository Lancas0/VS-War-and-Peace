package com.lancas.vs_wap.register;

import com.lancas.vs_wap.ModMain;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.feature.hold.ICanHoldShip;
import com.lancas.vs_wap.ship.feature.hold.ShipHoldSlot;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.valkyrienskies.core.api.ships.ServerShip;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerHoldShipForgeEvent {

    @SubscribeEvent
    public static void tryHoldShip(PlayerInteractEvent.RightClickBlock event) {
        //only in server because hold/unhold must be in server and then can be synced to client
        if (!(event.getLevel() instanceof ServerLevel sLevel)) return;

        Player player = event.getEntity();
        BlockPos interactBp = event.getPos();

        if (!(player instanceof ICanHoldShip icanHoldShip)) {
            EzDebug.fatal("player can not hold ship because of unkown reason!");
            return;
        }

        //only invoke when hand is empty
        //check air for safety
        if (!player.getMainHandItem().isEmpty() || sLevel.getBlockState(interactBp).isAir()) return;

        ServerShip ship = ShipUtil.getServerShipAt(sLevel, interactBp);
        if (ship == null) return;

        EzDebug.log("which is null?" + icanHoldShip + ", " + ShipHoldSlot.MainHand + ", ship:" + ship);
        boolean success = icanHoldShip.tryHoldInServer(ShipHoldSlot.MainHand, ship.getId(), true);
        if (!success) {
            //todo translate
            player.sendSystemMessage(Component.literal("你无法拿起这个瓦尔基里载具，也许你需要[很有武德的杖]"));
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

        icanHoldShip.unholdShipInServer(ShipHoldSlot.MainHand, true, null);
        //todo translate
        event.player.sendSystemMessage(Component.literal("你手里有物品，这使你手持的瓦尔基里载具掉了下来"));
    }
}
