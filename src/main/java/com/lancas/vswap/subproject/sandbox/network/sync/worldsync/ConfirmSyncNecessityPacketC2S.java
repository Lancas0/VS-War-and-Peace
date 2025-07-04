package com.lancas.vswap.subproject.sandbox.network.sync.worldsync;

import com.lancas.vswap.foundation.network.NetworkHandler;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.*;
import java.util.function.Supplier;

public class ConfirmSyncNecessityPacketC2S {
    //private final String levelName;
    private final boolean necessity;

    public ConfirmSyncNecessityPacketC2S(boolean inNecessity) {
        //levelName = inLevelName;
        necessity = inNecessity;
    }

    public void encode(FriendlyByteBuf buffer) {
        //buffer.writeUtf(levelName)
        buffer.writeBoolean(necessity);
    }
    public static ConfirmSyncNecessityPacketC2S decode(FriendlyByteBuf buffer) {
        return new ConfirmSyncNecessityPacketC2S(
            buffer.readBoolean()
        );
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (!necessity) return;

            ServerPlayer player = ctx.get().getSender();
            ServerLevel level = (ServerLevel) player.level();
            //todo will the level field don't equal to the level invoke in event?

            SandBoxServerWorld serverWorld = SandBoxServerWorld.getOrCreate(level);

            //now is server

            List<SandBoxServerShip> serverShips = new ArrayList<>();
            serverWorld.allServerShips().forEach(serverShips::add);
            NetworkHandler.sendToClientPlayer(player,
                new DoSyncClientWorldPacketS2C(
                    VSGameUtilsKt.getDimensionId(level),
                    serverShips,
                    serverWorld.wrapOrGetGround().getUuid()
                )
            );
            serverWorld.notifyClientLoading(true);
        });
        ctx.get().setPacketHandled(true);
    }
}
