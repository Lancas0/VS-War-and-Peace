package com.lancas.vswap.content;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.lancas.vswap.VsWap.MODID;

public class WapParticles {
    private static final DeferredRegister<ParticleType<?>> PARTICLE_REGISTER =
        DeferredRegister.create(Registries.PARTICLE_TYPE, MODID);

    public static RegistryObject<SimpleParticleType>
        CONE_PARTICLE = PARTICLE_REGISTER.register("cone_particle", () -> new SimpleParticleType(false));

}
