package com.lancas.vswap.foundation.network.debug;

import com.lancas.vswap.foundation.api.math.ForceOnPos;
import com.lancas.vswap.util.BytesUtil;
import com.lancas.vswap.util.JomlUtil;
import com.simibubi.create.CreateClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ForceOnPosDebugS2C {
    private final ForceOnPos forceOnPos;
    private final String key;
    private final int color;
    public ForceOnPosDebugS2C(ForceOnPos inForceOnPos, String inKey, int inColor) {
        forceOnPos = inForceOnPos;
        key = inKey;
        color = inColor;
    }

    public static void encode(ForceOnPosDebugS2C msg, FriendlyByteBuf buf) {
        BytesUtil.writeForceOnPos(buf, msg.forceOnPos)
            .writeUtf(msg.key)
            .writeInt(msg.color);
    }
    public static ForceOnPosDebugS2C decode(FriendlyByteBuf buf) {
        return new ForceOnPosDebugS2C(BytesUtil.readForceOnPos(buf), buf.readUtf(), buf.readInt());
    }
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Vec3 pos = JomlUtil.v3(forceOnPos.pos());
            Vec3 force = JomlUtil.v3(forceOnPos.force());

            CreateClient.OUTLINER.showLine(key + "_force", pos, pos.add(force))
                .lineWidth(0.25f)
                .colored(color);
            CreateClient.OUTLINER.showAABB(key + "_pos", JomlUtil.centerExtended(forceOnPos.pos(), 0.5))
                .lineWidth(0.125f)
                .colored(color);
        });
        ctx.get().setPacketHandled(true);
    }
}
