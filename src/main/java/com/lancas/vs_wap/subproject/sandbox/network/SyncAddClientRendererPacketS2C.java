package com.lancas.vs_wap.subproject.sandbox.network;

/*
import com.lancas.vs_wap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vs_wap.subproject.sandbox.ship.ShipClientRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class SyncAddClientRendererPacketS2C {
    private final String levelName;
    private final CompoundTag savedRenderer;

    public SyncAddClientRendererPacketS2C(String inLevelName, CompoundTag inSavedRenderer) {
        levelName = inLevelName;
        savedRenderer = inSavedRenderer;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(levelName);
        buffer.writeNbt(savedRenderer);
    }
    public static SyncAddClientRendererPacketS2C decode(FriendlyByteBuf buffer) {
        return new SyncAddClientRendererPacketS2C(
            buffer.readUtf(),
            buffer.readNbt()
        );
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            SandBoxClientWorld clientWorld = SandBoxClientWorld.INSTANCE;

            if (!Objects.equals(clientWorld.getCurLevelName(), levelName)) {
                return;  //no need for update because it is not in current level
            }

            clientWorld.addClientShip(new Clietn(savedRenderer));
        });
        ctx.get().setPacketHandled(true);
    }
}
*/