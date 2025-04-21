package com.lancas.vs_wap.obsolete.register;

import com.lancas.vs_wap.ModMain;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EinArmorRegister {

    private static final HashMap<ItemStack, Boolean> stackInitedDic = new HashMap<>();

    /*
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return; // 仅在每刻结束时执行

        if (!(event.player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer)event.player;
        ServerLevel level = player.serverLevel();

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.getItem() instanceof EinBoot) {
            Boolean inited = stackInitedDic.get(boots);

            EzDebug.Log("inited is " + inited);
            if (inited == null || !inited) {

                ServerShip ship = EinBoot.getShip(level, boots);

                if (ship == null) return;

                PlayerHoldingAttachment holdingAtt = ship.getAttachment(PlayerHoldingAttachment.class);
                if (holdingAtt == null) return;

                ship.setStatic(false);
                ship.setTransformProvider(new MountPlayerFootTP(holdingAtt));

                stackInitedDic.put(boots, true);
            }
        }
    }*/
}