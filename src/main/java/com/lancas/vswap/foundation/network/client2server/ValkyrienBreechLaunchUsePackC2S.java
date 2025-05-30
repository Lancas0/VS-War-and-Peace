package com.lancas.vswap.foundation.network.client2server;

import com.lancas.vswap.content.block.blockentity.ValkyrienBreechBE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ValkyrienBreechLaunchUsePackC2S {

    private final BlockPos bePos;
    private final int nextLaunchUse;
    public ValkyrienBreechLaunchUsePackC2S(int inNextLaunchUse, BlockPos inBePos) {
        nextLaunchUse = inNextLaunchUse;
        bePos = inBePos;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(nextLaunchUse);
        buf.writeBlockPos(bePos);
    }
    public static ValkyrienBreechLaunchUsePackC2S decode(FriendlyByteBuf buf) {
        return new ValkyrienBreechLaunchUsePackC2S(buf.readInt(), buf.readBlockPos());
    }
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level level = ctx.get().getSender().level();
            if (level instanceof ServerLevel sLevel && sLevel.getBlockEntity(bePos) instanceof ValkyrienBreechBE be) {
                be.setNextLaunchUse(nextLaunchUse);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
