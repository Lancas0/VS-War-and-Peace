package com.lancas.vs_wap.subproject.sandbox.network.worldsync;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vs_wap.subproject.sandbox.network.NetSerializeUtil;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxClientShip;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.*;
import java.util.function.Supplier;

public class DoSyncClientWorldPacketS2C {
    private final String levelName;
    private final List<CompoundTag> savedClientShips;

    public DoSyncClientWorldPacketS2C(String inLevelName, List<SandBoxServerShip> allSyncingShips) {
        levelName = inLevelName;
        savedClientShips = new ArrayList<>();
        allSyncingShips.forEach(s -> {
            CompoundTag saved = NetSerializeUtil.serializeShipForSendToClient(s);
            savedClientShips.add(saved);
        });
    }
    private DoSyncClientWorldPacketS2C(List<CompoundTag> allNetworkNbt, String inLevelName) {
        levelName = inLevelName;
        savedClientShips = allNetworkNbt;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeCollection(savedClientShips, FriendlyByteBuf::writeNbt);
        buffer.writeUtf(levelName);
    }
    public static DoSyncClientWorldPacketS2C decode(FriendlyByteBuf buffer) {
        return new DoSyncClientWorldPacketS2C(
            buffer.readCollection(size -> new ArrayList<>(), FriendlyByteBuf::readNbt),
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

            clientWorld.reloadLevel(levelName, clientShips);
        });
        ctx.get().setPacketHandled(true);
    }
}
