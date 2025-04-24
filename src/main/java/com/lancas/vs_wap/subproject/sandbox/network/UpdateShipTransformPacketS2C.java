package com.lancas.vs_wap.subproject.sandbox.network;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxTransformData;
import com.lancas.vs_wap.subproject.sandbox.ship.ShipClientRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class UpdateShipTransformPacketS2C {
    private final UUID shipUuid;
    private final SandBoxTransformData newTransformData;

    public UpdateShipTransformPacketS2C(UUID inShipUuid, SandBoxTransformData inTransformData) {
        shipUuid = inShipUuid;
        newTransformData = inTransformData.getImmutable();
    }

    public static void encode(UpdateShipTransformPacketS2C msg, FriendlyByteBuf buffer) {
        buffer.writeUUID(msg.shipUuid);
        buffer.writeNbt(msg.newTransformData.saved());
    }
    public static UpdateShipTransformPacketS2C decode(FriendlyByteBuf buffer) {
        SandBoxTransformData data = new SandBoxTransformData();
        UUID uuid = buffer.readUUID();
        data.load(buffer.readNbt());

        return new UpdateShipTransformPacketS2C(uuid, data);
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
            renderer.receiveNetworkTransform(newTransformData);
        });
        ctx.get().setPacketHandled(true);
    }
}
