package com.lancas.vs_wap.foundation.network;

import com.lancas.vs_wap.content.block.blocks.SignalDetectorBlock;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class KeyPressPacket {
    private final int key;
    private final boolean isKeyDown;

    public KeyPressPacket(int inKey, boolean inIsKeyDown) { key = inKey; isKeyDown = inIsKeyDown;
        //EzDebug.warn("signal:" + inKey + ", isDown:" + isKeyDown);
    }

    // 数据包序列化与反序列化
    public static void encode(KeyPressPacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.key);
        buffer.writeBoolean(msg.isKeyDown);
    }
    public static KeyPressPacket decode(FriendlyByteBuf buffer) {
        return new KeyPressPacket(buffer.readInt(), buffer.readBoolean());
    }

    // 处理数据包逻辑
    public static void handle(KeyPressPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null)
                return;

            //EzDebug.Log("isKeyDown is " + msg.isKeyDown + ", player uuid is " + player.getUUID());
            SignalDetectorBlock.setSingal(player.getUUID(), msg.key, msg.isKeyDown);
            //EzDebug.Log("[set trigger](" + player.getUUID() + ", " + msg.key + ", " + msg.isKeyDown + ")");
        });
        ctx.get().setPacketHandled(true);
    }
}