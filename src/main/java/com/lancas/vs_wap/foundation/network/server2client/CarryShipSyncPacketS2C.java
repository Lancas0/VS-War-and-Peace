package com.lancas.vs_wap.foundation.network.server2client;
/*
import com.lancas.einherjar.debug.EzDebug;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CarryShipSyncPacketS2C {
    private final long newShipId;
    private final BlockPos holdBpInShip;
    private final Direction forwardInShip;

    public CarryShipSyncPacketS2C(long inNewShipId, BlockPos inHoldBpInShip, Direction inForwardInShip) {
        this.newShipId = inNewShipId;
        holdBpInShip = inHoldBpInShip;
        forwardInShip = inForwardInShip;
    }

    public static void encode(CarryShipSyncPacketS2C msg, FriendlyByteBuf buffer) {
        buffer.writeLong(msg.newShipId);
        buffer.writeBlockPos(msg.holdBpInShip);
        buffer.writeEnum(msg.forwardInShip);
    }
    public static CarryShipSyncPacketS2C decode(FriendlyByteBuf buffer) {
        return new CarryShipSyncPacketS2C(
            buffer.readLong(),
            buffer.readBlockPos(),
            buffer.readEnum(Direction.class)
        );
    }

    // 客户端处理
    public static void handle(CarryShipSyncPacketS2C msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            if (!(player instanceof ICanCarryShip icanCarryShip)) {
                EzDebug.fatal("player is not icanHoldShip");
                return;
            }
            icanCarryShip.syncCarryShipInClient(msg.newShipId, msg.holdBpInShip, msg.forwardInShip);
        });
        ctx.get().setPacketHandled(true);
    }
}
*/