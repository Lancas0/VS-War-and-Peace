package com.lancas.vswap.subproject.sandbox.network.sync.worldsync;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.network.NetworkHandler;
import com.lancas.vswap.subproject.sandbox.SandBoxClientWorld;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class SyncClientWorldIfNecessaryPacketS2C {
    private final String newLevelName;

    public SyncClientWorldIfNecessaryPacketS2C(String inLevelName) {
        newLevelName = inLevelName;
    }

    public static void encode(SyncClientWorldIfNecessaryPacketS2C msg, FriendlyByteBuf buffer) {
        buffer.writeUtf(msg.newLevelName);
    }
    public static SyncClientWorldIfNecessaryPacketS2C decode(FriendlyByteBuf buffer) {
        return new SyncClientWorldIfNecessaryPacketS2C(
            buffer.readUtf()
        );
    }

    // 客户端处理
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            SandBoxClientWorld clientWorld = SandBoxClientWorld.INSTANCE;
            //name don't equal: neccessary
            boolean necessity = !(Objects.equals(clientWorld.getCurLevelName(), newLevelName));

            EzDebug.log("necessity: " + necessity);

            NetworkHandler.sendToServer(new ConfirmSyncNecessityPacketC2S(necessity));
        });
        ctx.get().setPacketHandled(true);
    }
}
