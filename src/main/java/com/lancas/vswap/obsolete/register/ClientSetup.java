package com.lancas.vswap.obsolete.register;

import com.lancas.vswap.ModMain;
import com.lancas.vswap.content.WapParticles;
import com.lancas.vswap.content.particle.FragmentParticle;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModMain.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientSetup {
    // 客户端粒子工厂注册（在ClientSetup中）
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(
            WapParticles.CONE_PARTICLE.get(),
            FragmentParticle.Provider::new
        );
    }

}
