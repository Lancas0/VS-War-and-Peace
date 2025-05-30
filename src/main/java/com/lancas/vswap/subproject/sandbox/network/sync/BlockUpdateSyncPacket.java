package com.lancas.vswap.subproject.sandbox.network.sync;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.SandBoxClientWorld;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxClientShip;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vswap.util.BytesUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.UUID;
import java.util.function.Supplier;

public class BlockUpdateSyncPacket {
    private final boolean toServer;
    private final UUID uuid;
    private final Vector3i localPos;
    private final BlockState newState;

    public BlockUpdateSyncPacket(boolean inToServer, UUID inUuid, Vector3ic inLocalPos, BlockState inNewState) {
        toServer = inToServer;
        uuid = inUuid;
        localPos = new Vector3i(inLocalPos);
        newState = inNewState;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(toServer);
        buffer.writeUUID(uuid);
        BytesUtil.writeVector3i(buffer, localPos);
        BytesUtil.writeBlockState(buffer, newState);
    }
    public static BlockUpdateSyncPacket decode(FriendlyByteBuf buffer) {
        return new BlockUpdateSyncPacket(
            buffer.readBoolean(),
            buffer.readUUID(),
            BytesUtil.readVector3i(buffer, new Vector3i()),
            BytesUtil.readBlockState(buffer)
        );
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            /*switch (ctx.get().getDirection()) {
                case PLAY_TO_CLIENT, LOGIN_TO_CLIENT:
            }*/
            if (toServer) {
                //handle in server: client update and sync to server
                ServerPlayer sender = ctx.get().getSender();
                if (sender == null) {
                    EzDebug.warn("BlockUpdateSyncPacket is considered sending to server but failed to get sender. Is the dist wrong?");
                    return;
                }

                SandBoxServerShip ship = SandBoxServerWorld.getOrCreate((ServerLevel)sender.level()).getServerShip(uuid);
                if (ship == null) {
                    EzDebug.warn("when sync block update, fail to get server ship with uuid:" + uuid);
                    return;
                }
                ship.getBlockCluster().getDataWriter().setBlock(localPos, newState, false);  //don't loop sync
            } else {
                //handle in client: server update and sync to client
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    SandBoxClientShip ship = SandBoxClientWorld.INSTANCE.getClientShip(uuid);
                    if (ship == null) {
                        EzDebug.warn("when sync block update, fail to get client ship with uuid:" + uuid);
                        return;
                    }
                    ship.getBlockCluster().getDataWriter().setBlock(localPos, newState, false);  //don't loop sync
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
