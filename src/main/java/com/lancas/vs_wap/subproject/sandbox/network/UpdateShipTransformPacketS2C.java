package com.lancas.vs_wap.subproject.sandbox.network;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vs_wap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxClientShip;
import com.lancas.vs_wap.util.StrUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

import java.util.UUID;
import java.util.function.Supplier;

public class UpdateShipTransformPacketS2C {
    private final UUID shipUuid;
    private final TransformPrimitive newTransform;
    //private final AABBd newLocalAABB = new AABBd();  //todo set localAABB in a other Packet

    public UpdateShipTransformPacketS2C(UUID inShipUuid, TransformPrimitive inTransformData/*, AABBdc inLocalAABB*/) {
        shipUuid = inShipUuid;
        newTransform = inTransformData.getImmutable();
        //newLocalAABB.set(inLocalAABB);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(shipUuid);
        buffer.writeNbt(newTransform.saved());
        /*buffer.writeDouble(newLocalAABB.minX);
        buffer.writeDouble(newLocalAABB.minY);
        buffer.writeDouble(newLocalAABB.minZ);
        buffer.writeDouble(newLocalAABB.maxX);
        buffer.writeDouble(newLocalAABB.maxY);
        buffer.writeDouble(newLocalAABB.maxZ);*/
    }
    public static UpdateShipTransformPacketS2C decode(FriendlyByteBuf buffer) {
        TransformPrimitive transform = new TransformPrimitive();
        UUID uuid = buffer.readUUID();
        transform.load(buffer.readNbt());

       /* AABBd aabbFromBuf = new AABBd();
        aabbFromBuf.setMin(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        aabbFromBuf.setMax(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());*/

        return new UpdateShipTransformPacketS2C(uuid, transform/*, aabbFromBuf*/);
    }

    // 客户端处理
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            SandBoxClientShip renderer = SandBoxClientWorld.INSTANCE.getClientShip(shipUuid);

            if (renderer == null) {
                //maybe the player is not in the level
                EzDebug.warn("fail to get renderer with id " + shipUuid + ", cur client level:" + SandBoxClientWorld.INSTANCE.getCurLevelName());
                return;
            }

            //EzDebug.log("update transform, newPos:" + StrUtil.F2(newTransform.position));
            renderer.receiveNetworkTransform(newTransform/*, newLocalAABB*/);
        });
        ctx.get().setPacketHandled(true);
    }
}
