package com.lancas.vswap.foundation.network.client2server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3f;

import java.util.function.Supplier;

//client to server
public class ScopeOnContraptionPacketC2S {
    private final boolean scoping;
    private final float scopeFovMultiplier;
    private final BlockPos anchoredBP;
    private final Vector3f anchorRelative;
    //private final Direction scopeDir;
    private final Vector3f forwardOnContraption;
    private final Vector3f cameraOffsetOnContraption;

    /*private static Vector3f forwardByDirection(Direction dir) {
        return switch (dir) {
            case NORTH -> new Vector3f(0, 0, -1);
            case SOUTH -> new Vector3f(0, 0, 1);
            case WEST -> new Vector3f(-1, 0, 0);
            case EAST -> new Vector3f(1, 0, 0);

            //should not be called
            default -> new Vector3f(0, 0, 1);
        };
    }*/

    /*public static ScopeOnContraptionToServerPacket onContraption(boolean inScoping, float inScopeFovMultiplier, MovementContext movementCtx, Direction inScopeDir, Vector3f inCameraOffsetAlongForward) {
        Vec3 relative = movementCtx.position.subtract(movementCtx.contraption.anchor.getCenter());
        Vec3 forward = movementCtx.rotation.apply(new Vec3(0, 0, 1));
        ScopeOnContraptionToServerPacket packet = new ScopeOnContraptionToServerPacket(
            inScoping,
            inScopeFovMultiplier,
            movementCtx.contraption.anchor,
            JomlUtil.f(relative),
            inScopeDir,
            JomlUtil.f(forward),
            inCameraOffsetAlongForward,
            true
        );
        return packet;
    }
    public static ScopeOnContraptionToServerPacket onShip(boolean inScoping, float inScopeFovMultiplier, BlockPos scopeBP, Direction inScopeDir, Vector3f inCameraOffsetAlongForward) {
        ScopeOnContraptionToServerPacket packet = new ScopeOnContraptionToServerPacket(
            inScoping,
            inScopeFovMultiplier,
            scopeBP,
            new Vector3f(),
            inScopeDir,
            new Vector3f(),
            inCameraOffsetAlongForward,
            false
        );
        return packet;
    }*/

    public ScopeOnContraptionPacketC2S(
        boolean inScoping,
        float inScopeFovMultiplier,
        BlockPos inAnchoredBP,
        Vector3f inRelaToAnchorPos,
        //Direction inScopeDir,
        Vector3f inForwardOnContraption,
        Vector3f inCameraOffsetOnContraption
    ) {
        scoping = inScoping;
        scopeFovMultiplier = inScopeFovMultiplier;
        anchoredBP = inAnchoredBP;
        anchorRelative = inRelaToAnchorPos;
        //scopeDir = inScopeDir;
        forwardOnContraption = inForwardOnContraption;
        //cameraOffsetAlongForward = inCameraOffsetAlongForward;
        cameraOffsetOnContraption = inCameraOffsetOnContraption;
    }

    public static void encode(ScopeOnContraptionPacketC2S msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.scoping);
        buf.writeFloat(msg.scopeFovMultiplier);
        buf.writeBlockPos(msg.anchoredBP);
        buf.writeVector3f(msg.anchorRelative);
        //buf.writeEnum(msg.scopeDir);
        buf.writeVector3f(msg.forwardOnContraption);
        //buf.writeVector3f(msg.cameraOffsetAlongForward);
        buf.writeVector3f(msg.cameraOffsetOnContraption);
    }
    public static ScopeOnContraptionPacketC2S decode(FriendlyByteBuf buf) {
        return new ScopeOnContraptionPacketC2S(
            buf.readBoolean(),
            buf.readFloat(),
            buf.readBlockPos(),
            buf.readVector3f(),
            //buf.readEnum(Direction.class),
            buf.readVector3f(),
            buf.readVector3f()
        );
    }
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            //EzDebug.Log("[ScopeZoomPacketEvent] packet start handling, player:" + player);
            if (player != null) {
                // 切换玩家视角状态
                /*if (isOnContraption)
                    ScopeServerHandler.scopeOnContraption(player, scoping, scopeFovMultiplier, anchoredBP, anchorRelative, forwardOnContraption, cameraOffsetAlongForward);
                else
                    ScopeServerHandler.scopeOnShip(player, scoping, scopeFovMultiplier, anchoredBP, forwardOnContraption, cameraOffsetAlongForward);
                 */
                //todo contraption scope
                /*ScopeServerHandler.scopeOnContraption(
                    player,
                    scoping,
                    scopeFovMultiplier,
                    anchoredBP,
                    anchorRelative/.*, scopeDir, forwardOnContraption, cameraOffsetAlongForward*./,
                    forwardOnContraption,
                    cameraOffsetOnContraption
                );*/
            }
        });
        ctx.get().setPacketHandled(true);
    }
}