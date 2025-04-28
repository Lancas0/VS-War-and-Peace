package com.lancas.vs_wap.subproject.sandbox.network;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxTransformData;
import com.lancas.vs_wap.subproject.sandbox.ship.ShipClientRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;

import java.util.UUID;
import java.util.function.Supplier;

public class UpdateShipTransformPacketS2C {
    private final UUID shipUuid;
    private final SandBoxTransformData newTransformData;
    private final AABBd newLocalAABB = new AABBd();  //todo set localAABB in a other Packet

    public UpdateShipTransformPacketS2C(UUID inShipUuid, SandBoxTransformData inTransformData, AABBdc inLocalAABB) {
        shipUuid = inShipUuid;
        newTransformData = inTransformData.getImmutable();
        newLocalAABB.set(inLocalAABB);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(shipUuid);
        buffer.writeNbt(newTransformData.saved());
        buffer.writeDouble(newLocalAABB.minX);
        buffer.writeDouble(newLocalAABB.minY);
        buffer.writeDouble(newLocalAABB.minZ);
        buffer.writeDouble(newLocalAABB.maxX);
        buffer.writeDouble(newLocalAABB.maxY);
        buffer.writeDouble(newLocalAABB.maxZ);
    }
    public static UpdateShipTransformPacketS2C decode(FriendlyByteBuf buffer) {
        SandBoxTransformData data = new SandBoxTransformData();
        UUID uuid = buffer.readUUID();
        data.load(buffer.readNbt());

        AABBd aabbFromBuf = new AABBd();
        aabbFromBuf.setMin(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        aabbFromBuf.setMax(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());

        return new UpdateShipTransformPacketS2C(uuid, data, aabbFromBuf);
    }

    // 客户端处理
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ShipClientRenderer renderer = SandBoxClientWorld.INSTANCE.getRenderer(shipUuid);

            if (renderer == null) {
                //maybe the player is not in the level
                EzDebug.warn("fail to get renderer with id " + shipUuid + ", cur client level:" + SandBoxClientWorld.INSTANCE.getCurLevelName());
                return;
            }

            //EzDebug.log("update transform");
            renderer.receiveNetworkTransform(newTransformData, newLocalAABB);
        });
        ctx.get().setPacketHandled(true);
    }
}
