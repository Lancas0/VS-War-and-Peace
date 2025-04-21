package com.lancas.vs_wap.foundation.network.server2client;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.renderer.WandRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ShipHolderRenderPacketS2C {
    private final BlockPos holdPivotBp;
    private final Direction forwardInShip;

    public ShipHolderRenderPacketS2C(BlockPos inHoldPivotBp, Direction inForwardInShip) {
        holdPivotBp = inHoldPivotBp;
        forwardInShip = inForwardInShip;
    }

    public static void encode(ShipHolderRenderPacketS2C msg, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(msg.holdPivotBp);
        buffer.writeEnum(msg.forwardInShip);
    }
    public static ShipHolderRenderPacketS2C decode(FriendlyByteBuf buffer) {
        return new ShipHolderRenderPacketS2C(
            buffer.readBlockPos(),
            buffer.readEnum(Direction.class)
        );
    }

    // 客户端处理
    public static void handle(ShipHolderRenderPacketS2C msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) {
                EzDebug.fatal("client mc can't have null level");
                return;
            }

            WandRenderer.drawOutline(msg.holdPivotBp, msg.forwardInShip, 255, "ship_holder_render");
        });
        ctx.get().setPacketHandled(true);
    }
}
