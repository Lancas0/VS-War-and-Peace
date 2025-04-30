package com.lancas.vs_wap.subproject.sandbox.network.worldsync;

import com.lancas.vs_wap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vs_wap.subproject.sandbox.network.NetSerializeUtil;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class SyncServerShipToClientPacket {
    private final String levelName;
    private final CompoundTag shipNbt;
    public SyncServerShipToClientPacket(String inLevelName, SandBoxServerShip serverShip) {
        levelName = inLevelName;
        shipNbt = NetSerializeUtil.serializeShipForSendToClient(serverShip);
    }
    private SyncServerShipToClientPacket(String inLevelName, CompoundTag nbt) {
        levelName = inLevelName;
        shipNbt = nbt;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(levelName).writeNbt(shipNbt);
    }
    public static SyncServerShipToClientPacket decode(FriendlyByteBuf buffer) {
        return new SyncServerShipToClientPacket(buffer.readUtf(), buffer.readNbt());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            SandBoxClientWorld clientWorld = SandBoxClientWorld.INSTANCE;

            if (!Objects.equals(clientWorld.getCurLevelName(), levelName)) {
                return;  //no need for sync because it is not in current level
            }
            //EzDebug.log("remove render:" + uuid);
            clientWorld.addClientShip(NetSerializeUtil.deserializeAsClientShip(shipNbt));  //todo add ship
        });
        ctx.get().setPacketHandled(true);
    }
}
