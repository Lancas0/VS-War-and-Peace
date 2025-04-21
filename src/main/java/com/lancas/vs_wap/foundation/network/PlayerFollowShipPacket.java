package com.lancas.vs_wap.foundation.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3dc;

import java.util.function.Supplier;

//from server to client
public class PlayerFollowShipPacket {
    private final double x, y, z;


    public PlayerFollowShipPacket(Vector3dc pos) {
        x = pos.x(); y = pos.y(); z = pos.z();
    }
    public PlayerFollowShipPacket(Vec3 pos) {
        x = pos.x(); y = pos.y(); z = pos.z();
    }
    public PlayerFollowShipPacket(double inX, double inY, double inZ) {
        x = inX; y = inY; z = inZ;
    }

    // 数据包序列化与反序列化
    public static void encode(PlayerFollowShipPacket msg, FriendlyByteBuf buffer) {
        buffer.writeDouble(msg.x);
        buffer.writeDouble(msg.y);
        buffer.writeDouble(msg.z);
    }
    public static PlayerFollowShipPacket decode(FriendlyByteBuf buffer) {
        return new PlayerFollowShipPacket(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }
    // 处理数据包逻辑
    public static void handle(PlayerFollowShipPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.setPos(msg.x, msg.y, msg.z);
                //mc.player.travel(new Vec3(msg.x, msg.y, msg.z));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
