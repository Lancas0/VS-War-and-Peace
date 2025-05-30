package com.lancas.vswap.subproject.sandbox.network.sync.worldsync;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vswap.subproject.sandbox.network.NetSerializeUtil;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxClientShip;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.*;
import java.util.function.Supplier;

public class DoSyncClientWorldPacketS2C {
    private final String levelName;
    private final List<CompoundTag> savedClientShips;
    private final UUID wrappedGroundShipUuid;

    public DoSyncClientWorldPacketS2C(String inLevelName, List<SandBoxServerShip> allSyncingShips, UUID inWrappedGroundShipUuid) {
        levelName = inLevelName;
        savedClientShips = new ArrayList<>();
        allSyncingShips.forEach(s -> {
            CompoundTag saved = NetSerializeUtil.serializeShipForSendToClient(s);
            savedClientShips.add(saved);
        });
        wrappedGroundShipUuid = inWrappedGroundShipUuid;
    }
    private DoSyncClientWorldPacketS2C(List<CompoundTag> allNetworkNbt, UUID inWrappedGroundShipUuid, String inLevelName) {
        levelName = inLevelName;
        savedClientShips = allNetworkNbt;
        wrappedGroundShipUuid = inWrappedGroundShipUuid;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeCollection(savedClientShips, FriendlyByteBuf::writeNbt);
        buffer.writeUUID(wrappedGroundShipUuid);
        buffer.writeUtf(levelName);
    }
    public static DoSyncClientWorldPacketS2C decode(FriendlyByteBuf buffer) {
        return new DoSyncClientWorldPacketS2C(
            buffer.readCollection(size -> new ArrayList<>(), FriendlyByteBuf::readNbt),
            buffer.readUUID(),
            buffer.readUtf()
        );
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            SandBoxClientWorld clientWorld = SandBoxClientWorld.INSTANCE;

            if (Objects.equals(clientWorld.getCurLevelName(), levelName)) {
                EzDebug.warn("the current level name is equal to " + levelName);
            }

            //clientWorld.setCurrentLevelKey(levelKey);
            //clientWorld.reloadLevel(levelName, savedRenderers);
            List<SandBoxClientShip> clientShips = new ArrayList<>();
            for (CompoundTag nbt : savedClientShips) {
                clientShips.add(NetSerializeUtil.deserializeAsClientShip(nbt));
            }

            clientWorld.reloadLevel(levelName, clientShips, wrappedGroundShipUuid);
        });
        ctx.get().setPacketHandled(true);
    }
}
