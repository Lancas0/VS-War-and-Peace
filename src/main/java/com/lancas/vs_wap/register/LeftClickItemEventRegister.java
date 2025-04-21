package com.lancas.vs_wap.register;

import com.lancas.vs_wap.ModMain;
import com.lancas.vs_wap.content.WapItems;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LeftClickItemEventRegister {
    /*@SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();

        if (player.getMainHandItem().getItem() == AllItems.VSWeaponItem.get()) {

        }
    }*/

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();

        if (player.getMainHandItem().getItem() == WapItems.EINHERJAR_WAND.get()) {
            event.setCanceled(true);
            //EzDebug.Log("click block with: " + player.getMainHandItem().getItem().getName(player.getMainHandItem()));
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(AttackEntityEvent event) {
        Player player = event.getEntity();

        if (player.getMainHandItem().getItem() == WapItems.EINHERJAR_WAND.get()) {
            event.setCanceled(true);
            //EzDebug.Log("attack with: " + player.getMainHandItem().getItem().getName(player.getMainHandItem()));
        }
    }
}