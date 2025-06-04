package com.lancas.vswap.content;

import com.lancas.vswap.VsWap;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WapSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, VsWap.MODID);

    public static final RegistryObject<SoundEvent> ARTILLERY_FIRE0 = register("artillery_fire0");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(VsWap.asRes(name)));
    }

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }
}
