package com.lancas.vs_wap.ship.ballistics.network;

import com.lancas.vs_wap.ship.ballistics.BallisticsClientManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BallisticIdSyncPacketS2C {
    public final long shipId;
    public final boolean newOrTerminate;

    private BallisticIdSyncPacketS2C(long inShipId, boolean inNewOrTerminate) {
        shipId = inShipId;
        newOrTerminate = inNewOrTerminate;
    }
    public static BallisticIdSyncPacketS2C newId(long inShipId) { return new BallisticIdSyncPacketS2C(inShipId, true); }
    public static BallisticIdSyncPacketS2C terminateId(long inShipId) { return new BallisticIdSyncPacketS2C(inShipId, false); }

    // 序列化与反序列化
    public static void encode(BallisticIdSyncPacketS2C msg, FriendlyByteBuf buffer) {
        buffer.writeLong(msg.shipId);
        buffer.writeBoolean(msg.newOrTerminate);
    }
    public static BallisticIdSyncPacketS2C decode(FriendlyByteBuf buffer) {
        return new BallisticIdSyncPacketS2C(buffer.readLong(), buffer.readBoolean());
    }

    // 客户端处理
    public static void handle(BallisticIdSyncPacketS2C msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BallisticsClientManager.handleSyncPacketInClient(msg);
        });
        ctx.get().setPacketHandled(true);
    }
}
