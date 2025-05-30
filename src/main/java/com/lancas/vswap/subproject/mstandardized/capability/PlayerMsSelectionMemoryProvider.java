package com.lancas.vswap.subproject.mstandardized.capability;
/*
import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerMsSelectionMemoryProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<PlayerMsSelectionMemory> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    private final PlayerMsSelectionMemory memory = new PlayerMsSelectionMemory();

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if (capability == CAPABILITY)
            return LazyOptional.of(() -> memory).cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return new NbtBuilder().putMap("selection", memory.selection, (k, v) -> {
            return new NbtBuilder()
                .putString("key", k)
                .putString("val", v)
                .get();
        }).get();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        NbtBuilder.modify(nbt).readMapOverwrite("selection", t -> {
            BiTuple<String, String> entry = new BiTuple<>();
            NbtBuilder.modify(t)
                .readStringDo("key", entry::setFirst)
                .readStringDo("val", entry::setSecond);
            return entry;
        }, memory.selection);
    }


    @Mod.EventBusSubscriber
    public static class CapabilityRegistry {
        public static final Capability<PlayerMsSelectionMemory> MEMORY = CapabilityManager.get(new CapabilityToken<>(){});

        @SubscribeEvent
        public static void register(RegisterCapabilitiesEvent event) {
            event.register(PlayerMsSelectionMemory.class);
        }
    }
}
*/