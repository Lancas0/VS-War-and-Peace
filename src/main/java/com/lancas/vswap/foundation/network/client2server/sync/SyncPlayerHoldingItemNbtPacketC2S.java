package com.lancas.vswap.foundation.network.client2server.sync;

import com.lancas.vswap.debug.EzDebug;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncPlayerHoldingItemNbtPacketC2S {
    //public UUID playerUuid;
    public InteractionHand hand;
    public CompoundTag newTag;

    public SyncPlayerHoldingItemNbtPacketC2S(/*UUID inPlayerUuid, */InteractionHand inHand, CompoundTag inNewTag) {
        //playerUuid = inPlayerUuid;
        hand = inHand;
        newTag = inNewTag;
    }

    public void encode(FriendlyByteBuf buf) {
        //buf.writeUUID(playerUuid);
        buf.writeEnum(hand);
        buf.writeNbt(newTag);
    }
    public static SyncPlayerHoldingItemNbtPacketC2S decode(FriendlyByteBuf buf) {
        return new SyncPlayerHoldingItemNbtPacketC2S(/*buf.readUUID(), */buf.readEnum(InteractionHand.class), buf.readNbt());
    }

    // 处理数据包逻辑
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            //now is server
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                EzDebug.warn("fail to set nbt of player hand item because sender is null");
                return;
            }

            ItemStack handStack = player.getItemInHand(hand);
            if (handStack.isEmpty()) {
                EzDebug.warn("player has no item on hand:" + hand + ", so can't set nbt");
                return;
            }

            handStack.setTag(newTag);
        });
        ctx.get().setPacketHandled(true);
    }
}
