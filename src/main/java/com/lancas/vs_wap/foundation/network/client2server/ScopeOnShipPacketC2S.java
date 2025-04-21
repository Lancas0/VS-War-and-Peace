package com.lancas.vs_wap.foundation.network.client2server;

/*
public class ScopeOnShipPacketC2S {
    private final boolean scoping;
    private final float fovMultiplier;
    private final BlockPos scopeBP;
    private final Direction scopeDir;
    private final Vector3f cameraOffsetAlongForward;

    public ScopeOnShipPacketC2S(boolean inScoping, float inFovMultiplier, BlockPos inScopeBP, Direction inScopeDir, Vector3f inCameraOffsetAlongForward) {
        scoping = inScoping;
        fovMultiplier = inFovMultiplier;
        scopeBP = inScopeBP;
        scopeDir = inScopeDir;
        cameraOffsetAlongForward = inCameraOffsetAlongForward;
    }
    public static void encode(ScopeOnShipPacketC2S msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.scoping);
        buf.writeFloat(msg.fovMultiplier);
        buf.writeBlockPos(msg.scopeBP);
        buf.writeEnum(msg.scopeDir);
        buf.writeVector3f(msg.cameraOffsetAlongForward);
    }
    public static ScopeOnShipPacketC2S decode(FriendlyByteBuf buf) {
        return new ScopeOnShipPacketC2S(
            buf.readBoolean(),
            buf.readFloat(),
            buf.readBlockPos(),
            buf.readEnum(Direction.class),
            buf.readVector3f()
        );
    }
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ScopeServerHandler.scopeOnShip(player, scoping, fovMultiplier, scopeBP, scopeDir, cameraOffsetAlongForward);
            }
        });
        ctx.get().setPacketHandled(true);
    }


}
*/