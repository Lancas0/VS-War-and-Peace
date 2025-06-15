package com.lancas.vswap.content.block.api;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.ship.feature.hold.ICanHoldShip;
import com.lancas.vswap.ship.feature.hold.ShipHoldSlot;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.Objects;

@Mod.EventBusSubscriber
public class HoldingShipInteractableBlockEvt {
    public static class BugFix {
        public static boolean isTryInteractSelf(Ship holdingShip, Level level, BlockPos interactableBlockPos) {
            @Nullable Ship interactableBlockInShip = ShipUtil.getShipAt(level, interactableBlockPos);
            if (interactableBlockInShip == null)
                return false;

            return Objects.equals(interactableBlockInShip.getId(), holdingShip.getId());
        }
    }

    @SubscribeEvent
    public static void holdingShipInteract(PlayerInteractEvent.RightClickBlock event) {
        ItemStack handStack = event.getItemStack();
        Player player = event.getEntity();
        if (!(player instanceof ICanHoldShip iCanHold)) {
            EzDebug.fatal("player is not ICanHoldShip!");
            return;
        }

        Long holdingShipId = iCanHold.getHoldingShipId(ShipHoldSlot.MainHand);

        if (!handStack.isEmpty()) {
            if (holdingShipId != null) {
                EzDebug.warn("Player is holding ship with handStack:" + handStack);
            }
            return;
        }

        Level level = event.getLevel();
        Ship holdingShip = ShipUtil.getShipByID(level, holdingShipId);
        if (holdingShip == null)
            return;

        BlockPos bp = event.getPos();
        BlockState state = level.getBlockState(bp);

        if (BugFix.isTryInteractSelf(holdingShip, level, bp)) {
            EzDebug.warn("try interact self and cancel interaction");
            return;
        }

        if (!(state.getBlock() instanceof IHoldingShipInteractableBlock sib))
            return;

        if (sib.interact(holdingShip, level, player, bp, state))
            event.setCanceled(true);
    }
    @SubscribeEvent
    public static void lookingBlockEvent(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        HitResult hitResult = mc.hitResult;
        Player player = mc.player;
        ClientLevel level = mc.level;

        if ((!(player instanceof ICanHoldShip iCanHold)) || level == null)
            return;

        ItemStack handStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        Long holdingShipId = iCanHold.getHoldingShipId(ShipHoldSlot.MainHand);

        if (!handStack.isEmpty()) {
            if (holdingShipId != null) {
                EzDebug.warn("Player is holding ship with handStack:" + handStack);
            }
            return;
        }
        ClientShip holdingShip = ShipUtil.getClientShipByID(level, holdingShipId);
        if (holdingShip == null)
            return;

        if (hitResult instanceof BlockHitResult blockHit && blockHit.getType() != HitResult.Type.MISS) {
            BlockPos hitBp = blockHit.getBlockPos();
            BlockState hitState = level.getBlockState(hitBp);

            if (BugFix.isTryInteractSelf(holdingShip, level, hitBp)) {
                EzDebug.warn("try interact self and cancel interaction");
                return;
            }

            if (hitState.getBlock() instanceof IHoldingShipInteractableBlock sib) {
                if (sib.mayInteract(holdingShip, level, player, hitBp, hitState)) {
                    //event.setCanceled(true);
                    //todo something?
                }
            }
        }
    }
}
