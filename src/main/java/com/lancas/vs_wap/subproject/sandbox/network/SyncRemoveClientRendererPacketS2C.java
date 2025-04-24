package com.lancas.vs_wap.subproject.sandbox.network;

import com.lancas.vs_wap.subproject.sandbox.SandBoxClientWorld;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncRemoveClientRendererPacketS2C {
    private final String levelName;
    private final UUID uuid;

    public SyncRemoveClientRendererPacketS2C(String inLevelName, UUID inUuid) {
        levelName = inLevelName;
        uuid = inUuid;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(levelName);
        buffer.writeUUID(uuid);
    }
    public static SyncRemoveClientRendererPacketS2C decode(FriendlyByteBuf buffer) {
        return new SyncRemoveClientRendererPacketS2C(
            buffer.readUtf(),
            buffer.readUUID()
        );
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            SandBoxClientWorld clientWorld = SandBoxClientWorld.INSTANCE;

            //最好还是检查一下，万一以后需要移动船的其他维度，防止误删
            if (!Objects.equals(clientWorld.getCurLevelName(), levelName)) {
                return;  //no need for update because it is not in current level
            }

            clientWorld.removeRenderer(uuid);
        });
        ctx.get().setPacketHandled(true);
    }
}
