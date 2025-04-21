package com.lancas.vs_wap.foundation.network.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class CantShipPacketC2S {
    public boolean cantToLeft;

    public CantShipPacketC2S(boolean inCantToLeft) { cantToLeft = inCantToLeft; }

    // 数据包序列化与反序列化
    public static void encode(CantShipPacketC2S msg, FriendlyByteBuf buffer) {
        buffer.writeBoolean(msg.cantToLeft);
    }
    public static CantShipPacketC2S decode(FriendlyByteBuf buffer) {
        return new CantShipPacketC2S(buffer.readBoolean());
    }

    // 处理数据包逻辑
    public static void handle(CantShipPacketC2S msg, Supplier<NetworkEvent.Context> ctx) {
        //todo realize cant handle
        //maybe add a another rotation in Holdable
        /*ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null)
                return;

            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof EinherjarWand wand) {
                ServerShip ship = (ServerShip)EinherjarWand.getShip(player.level(), stack);
                if (ship == null) return;

                ((MountPlayerHandTP)ship.getTransformProvider()).cant(msg.cantToLeft);
            }
        });*/
        ctx.get().setPacketHandled(true);
    }
}
