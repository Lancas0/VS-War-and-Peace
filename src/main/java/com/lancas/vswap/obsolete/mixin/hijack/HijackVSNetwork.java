package com.lancas.vswap.obsolete.mixin.hijack;

/*
@Mixin(NetworkChannel.class)
public abstract class HijackVSNetwork {

    @Shadow
    protected abstract Packet bytesToPacket(ByteBuf data);

    @Inject(method = "onReceiveClient", at = @At("HEAD"), remap = false)
    public void hijackClient(ByteBuf data, CallbackInfo ci) {
        //Packet packet = bytesToPacket(data);
        //EzDebug.Log("hijack client? packet:" + packet.getType().getName() + ", channel" + packet.getType().getChannel().toString());
        //EzDebug.Log("server packet:" + data.toString());
    }

    @Inject(method = "serverReceive", at = @At("HEAD"), remap = false)
    public void hijackServer(Packet packet, IPlayer player, CallbackInfo ci) {
        //Packet packet = bytesToPacket(data);
        //EzDebug.Log("hijack server? packet:" + packet.getType().getName() + ", player:" + player.getUuid());
    }

    @Inject(method = "clientReceive", at = @At("HEAD"), remap = false)
    public void hijackClient(Packet packet, CallbackInfo ci) {
        //Packet packet = bytesToPacket(data);
        //EzDebug.Log("clientReceive:" + packet.getType());


    }
}
*/