package com.lancas.vs_wap.subproject.sandbox.network.worldsync;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.network.NetworkHandler;
import com.lancas.vs_wap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vs_wap.subproject.sandbox.SandBoxServerWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
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
            NetworkHandler.sendToClientPlayer(player,
                new DoSyncClientWorldPacketS2C(
                    VSGameUtilsKt.getDimensionId(level),
                    serverWorld.getSavedRenderers()
                )
            );
        });
        ctx.get().setPacketHandled(true);
    }
}
