package com.lancas.vswap.foundation.network.server2client;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.renderer.WandRenderer;
import com.lancas.vswap.util.BytesUtil;
import com.simibubi.create.CreateClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CreateOutlinePacketS2C {
    private final String key;
    private final AABB aabb;
    private int color = 16777215;

    public CreateOutlinePacketS2C(String inKey, AABB inAABB) {
        key = inKey;
        aabb = inAABB;
    }
    public CreateOutlinePacketS2C(String inKey, AABB inAABB, int inColor) {
        key = inKey;
        aabb = inAABB;
        color = inColor;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(key);
        BytesUtil.writeAABB(buffer, aabb);
        buffer.writeInt(color);
    }
    public static CreateOutlinePacketS2C decode(FriendlyByteBuf buffer) {
        return new CreateOutlinePacketS2C(
            buffer.readUtf(),
            BytesUtil.readAABB(buffer),
            buffer.readInt()
        );
    }

    // 客户端处理
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) {
                EzDebug.error("client mc can't have null level");
                return;
            }

            CreateClient.OUTLINER.showAABB(key, aabb)
                .lineWidth(0.0625f)
                .colored(color);
        });
        ctx.get().setPacketHandled(true);
    }
}
