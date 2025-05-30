package com.lancas.vswap.obsolete.register;

import com.lancas.vswap.ModMain;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonSetup {
    /*@SubscribeEvent
    public static void registerDispenserBehaviors(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 获取原版发射器默认物品行为（用于保留其他逻辑）todo ? should remove?
            //DispenseItemBehavior defaultBehavior = DispenserBlock..DISPENSER_REGISTRY.get(Items.AIR);
            // 注册自定义行为到你的物品
            DispenserBlock.registerBehavior(EinheriarItems.DOCKER.get(), new DockerDispenseBehaviour());
        });
    }*/




}
