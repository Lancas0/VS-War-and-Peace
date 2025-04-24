package com.lancas.vs_wap.subproject.sandbox.network.worldsync;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.SandBoxClientWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class DoSyncClientWorldPacketS2C {
    private final String levelName;
    private final Map<UUID, CompoundTag> savedRenderers;

    public DoSyncClientWorldPacketS2C(String inLevelName, Map<UUID, CompoundTag> inSavedRenderers) {
        levelName = inLevelName;
        savedRenderers = inSavedRenderers;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(levelName)
            .writeMap(savedRenderers, FriendlyByteBuf::writeUUID, FriendlyByteBuf::writeNbt);
    }
    public static DoSyncClientWorldPacketS2C decode(FriendlyByteBuf buffer) {
        return new DoSyncClientWorldPacketS2C(
            buffer.readUtf(),
            buffer.readMap(FriendlyByteBuf::readUUID, FriendlyByteBuf::readNbt)
        );
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            SandBoxClientWorld clientWorld = SandBoxClientWorld.INSTANCE;

            if (Objects.equals(clientWorld.getCurLevelName(), levelName)) {
                EzDebug.warn("the current level name is equal to " + levelName);
            }

            //clientWorld.setCurrentLevelKey(levelKey);
            EzDebug.log("do sync client world");
            clientWorld.reloadLevel(levelName, savedRenderers);
        });
        ctx.get().setPacketHandled(true);
    }
}
