package com.lancas.vs_wap.foundation.network.server2client;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.feature.hold.ICanHoldShip;
import com.lancas.vs_wap.ship.feature.hold.ShipHoldSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HoldShipSyncPacketS2C {
    private String slotName;
    private final long newShipId;
    private final BlockPos holdBpInShip;
    private final Direction forwardInShip;

    public HoldShipSyncPacketS2C(String inSlotName, long inNewShipId, BlockPos inHoldBpInShip, Direction inForwardInShip) {
        slotName = inSlotName;
        this.newShipId = inNewShipId;
        holdBpInShip = inHoldBpInShip;
        forwardInShip = inForwardInShip;
    }

    public static void encode(HoldShipSyncPacketS2C msg, FriendlyByteBuf buffer) {
        buffer.writeUtf(msg.slotName);
        buffer.writeLong(msg.newShipId);
        buffer.writeBlockPos(msg.holdBpInShip);
        buffer.writeEnum(msg.forwardInShip);
    }
    public static HoldShipSyncPacketS2C decode(FriendlyByteBuf buffer) {
        return new HoldShipSyncPacketS2C(
            buffer.readUtf(),
            buffer.readLong(),
            buffer.readBlockPos(),
            buffer.readEnum(Direction.class)
        );
    }

    // 客户端处理
    public static void handle(HoldShipSyncPacketS2C msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            if (!(player instanceof ICanHoldShip icanHoldShip)) {
                EzDebug.fatal("player is not icanHoldShip");
                return;
            }
            icanHoldShip.syncHoldShipInClient(ShipHoldSlot.valueOf(msg.slotName), msg.newShipId, msg.holdBpInShip, msg.forwardInShip);
        });
        ctx.get().setPacketHandled(true);
    }
}
