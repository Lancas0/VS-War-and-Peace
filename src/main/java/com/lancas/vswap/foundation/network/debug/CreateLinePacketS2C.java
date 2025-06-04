package com.lancas.vswap.foundation.network.debug;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.util.BytesUtil;
import com.lancas.vswap.util.JomlUtil;
import com.simibubi.create.CreateClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.function.Supplier;

public class CreateLinePacketS2C {
    private final String key;
    private final Vector3d p0 = new Vector3d();
    private final Vector3d p1 = new Vector3d();
    private int color = 16777215;

    public CreateLinePacketS2C(String inKey, Vector3dc inP0, Vector3dc inP1) {
        key = inKey;
        p0.set(inP0);
        p1.set(inP1);
    }
    public CreateLinePacketS2C(String inKey, Vector3dc inP0, Vector3dc inP1, int inColor) {
        this(inKey, inP0, inP1);
        color = inColor;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(key);
        BytesUtil.writeVector3d(buffer, p0);
        BytesUtil.writeVector3d(buffer, p1);
        buffer.writeInt(color);
    }
    public static CreateLinePacketS2C decode(FriendlyByteBuf buffer) {
        return new CreateLinePacketS2C(
            buffer.readUtf(),
            BytesUtil.getVector3d(buffer, new Vector3d()),
            BytesUtil.getVector3d(buffer, new Vector3d()),
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

            CreateClient.OUTLINER.showLine(key, JomlUtil.v3(p0), JomlUtil.v3(p1))
                .lineWidth(0.0625f)
                .colored(color);
        });
        ctx.get().setPacketHandled(true);
    }
}
