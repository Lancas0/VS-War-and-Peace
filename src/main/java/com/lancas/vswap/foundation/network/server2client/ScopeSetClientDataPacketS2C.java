package com.lancas.vswap.foundation.network.server2client;

/*
//server to client
public class ScopeSetClientDataPacketS2C {
    private final boolean scoping;
    private final float scopeFovMultiplier;
    private final Vector3f cameraNewPos;
    private final Vector3f forward;
    //private final float cameraNewXRot;
    //private final float cameraNewYRot;

    public ScopeSetClientDataPacketS2C(boolean inScoping, float inScopeFovMultiplier, Vector3f inCameraNewPos, Vector3f inForward) {
        scoping = inScoping;
        scopeFovMultiplier = inScopeFovMultiplier;
        cameraNewPos = inCameraNewPos;
        forward = inForward;
    }

    public static void encode(ScopeSetClientDataPacketS2C msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.scoping);
        buf.writeFloat(msg.scopeFovMultiplier);
        buf.writeVector3f(msg.cameraNewPos);
        //buf.writeQuaternion(msg.rotation);
        buf.writeVector3f(msg.forward);
    }
    public static ScopeSetClientDataPacketS2C decode(FriendlyByteBuf buf) {
        return new ScopeSetClientDataPacketS2C(
            buf.readBoolean(),
            buf.readFloat(),
            buf.readVector3f(),
            //buf.readQuaternion(),
            buf.readVector3f()
        );
    }
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            //EzDebug.Log("scope set camera handling");

            //todo temp
            ScopeClientManager.setScopeData(scoping, scopeFovMultiplier, cameraNewPos.get(new Vector3d()), forward.get(new Vector3d()));

            //Minecraft.getInstance().player.setPos(JomlUtil.mc(cameraNewPos));

            //TestRenderer2.poses.put("ScopePosDebug", cameraNewPos.get(new Vector3d()));
            //TestRenderer2.vecs.put("ScopePosDebug", new Pair<>(cameraNewPos.get(new Vector3d()), forward.get(new Vector3d())));
        });
        ctx.get().setPacketHandled(true);
    }
}
*/