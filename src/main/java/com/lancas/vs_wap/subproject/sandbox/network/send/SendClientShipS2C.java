package com.lancas.vs_wap.subproject.sandbox.network.send;

import com.lancas.vs_wap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vs_wap.subproject.sandbox.network.NetSerializeUtil;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxClientShip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class SendClientShipS2C {
    private final String levelName;
    private final CompoundTag shipNbt;
    public SendClientShipS2C(String inLevelName, SandBoxClientShip clientShipMadeInServer) {
        levelName = inLevelName;
        shipNbt = NetSerializeUtil.serializeShipForSendToClient(clientShipMadeInServer);
    }
    private SendClientShipS2C(String inLevelName, CompoundTag nbt) {
        levelName = inLevelName;
        shipNbt = nbt;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(levelName).writeNbt(shipNbt);
    }
    public static SendClientShipS2C decode(FriendlyByteBuf buffer) {
        return new SendClientShipS2C(buffer.readUtf(), buffer.readNbt());
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
