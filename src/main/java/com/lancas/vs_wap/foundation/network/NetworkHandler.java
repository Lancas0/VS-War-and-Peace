package com.lancas.vs_wap.foundation.network;

import com.lancas.vs_wap.ModMain;
import com.lancas.vs_wap.foundation.network.client2server.*;
import com.lancas.vs_wap.foundation.network.debug.ForceOnPosDebugS2C;
import com.lancas.vs_wap.foundation.network.server2client.*;
import com.lancas.vs_wap.ship.ballistics.network.BallisticIdSyncPacketS2C;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static int packetId = 0;

    private static String protocolVer = "0.1";

    public static SimpleChannel channel;

    public static <T> void sendToClientPlayer(ServerPlayer player, T packet) {
        channel.send(
            PacketDistributor.PLAYER.with(() -> player),
            packet
        );
    }
    public static <T> void sendToAllPlayers(T packet) {
        channel.send(
            PacketDistributor.ALL.noArg(),
            packet
        );
    }
    public static <T> void sendToServer(T packet) {
        channel.sendToServer(packet);
    }

    public static void register() {
        channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(ModMain.MODID, "main"))
                .networkProtocolVersion(() -> protocolVer)
                .clientAcceptedVersions(protocolVer::equals)
                .serverAcceptedVersions(protocolVer::equals)
                .simpleChannel();

        channel.messageBuilder(KeyPressPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(KeyPressPacket::encode)
            .decoder(KeyPressPacket::decode)
            .consumerMainThread(KeyPressPacket::handle)
            .add();


        /*channel.messageBuilder(ScopeOnShipPacketC2S.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(ScopeOnShipPacketC2S::encode)
            .decoder(ScopeOnShipPacketC2S::decode)
            .consumerMainThread(ScopeOnShipPacketC2S::handle)
            .add();*/

        channel.messageBuilder(ScopeOnContraptionPacketC2S.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(ScopeOnContraptionPacketC2S::encode)
            .decoder(ScopeOnContraptionPacketC2S::decode)
            .consumerMainThread(ScopeOnContraptionPacketC2S::handle)
            .add();

        channel.messageBuilder(CantShipPacketC2S.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(CantShipPacketC2S::encode)
            .decoder(CantShipPacketC2S::decode)
            .consumerMainThread(CantShipPacketC2S::handle)
            .add();

        channel.messageBuilder(ThrowShipPacketC2S.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(ThrowShipPacketC2S::encode)
            .decoder(ThrowShipPacketC2S::decode)
            .consumerMainThread(ThrowShipPacketC2S::handle)
            .add();
        channel.messageBuilder(SwapShipsInSlotC2S.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(SwapShipsInSlotC2S::encode)
            .decoder(SwapShipsInSlotC2S::decode)
            .consumerMainThread(SwapShipsInSlotC2S::handle)
            .add();

        /*channel.messageBuilder(PlayerFollowShipPacket.class, packetId++, NetworkDirection.SER)
            .encoder(PlayerFollowShipPacket::encode)
            .decoder(PlayerFollowShipPacket::decode)
            .consumerMainThread(PlayerFollowShipPacket::handle)
            .add();*/

        channel.registerMessage(
            packetId++,
            PlayerFollowShipPacket.class,
            PlayerFollowShipPacket::encode,
            PlayerFollowShipPacket::decode,
            PlayerFollowShipPacket::handle
        );
        /*channel.registerMessage(
            packetId++,
            ScopeSetClientDataPacketS2C.class,
            ScopeSetClientDataPacketS2C::encode,
            ScopeSetClientDataPacketS2C::decode,
            ScopeSetClientDataPacketS2C::handle
        );*/
        channel.registerMessage(
            packetId++,
            ConeParticlePacketS2C.class,
            ConeParticlePacketS2C::encode,
            ConeParticlePacketS2C::decode,
            ConeParticlePacketS2C::handle
        );
        channel.registerMessage(
            packetId++,
            FlarePackageS2C.class,
            FlarePackageS2C::encode,
            FlarePackageS2C::decode,
            FlarePackageS2C::handle
        );
        channel.registerMessage(
            packetId++,
            ShipHolderRenderPacketS2C.class,
            ShipHolderRenderPacketS2C::encode,
            ShipHolderRenderPacketS2C::decode,
            ShipHolderRenderPacketS2C::handle
        );
        channel.registerMessage(
            packetId++,
            HoldShipSyncPacketS2C.class,
            HoldShipSyncPacketS2C::encode,
            HoldShipSyncPacketS2C::decode,
            HoldShipSyncPacketS2C::handle
        );
        channel.registerMessage(
            packetId++,
            BallisticIdSyncPacketS2C.class,
            BallisticIdSyncPacketS2C::encode,
            BallisticIdSyncPacketS2C::decode,
            BallisticIdSyncPacketS2C::handle
        );
        channel.registerMessage(
            packetId++,
            ForceOnPosDebugS2C.class,
            ForceOnPosDebugS2C::encode,
            ForceOnPosDebugS2C::decode,
            ForceOnPosDebugS2C::handle
        );

        channel.registerMessage(
            packetId++,
            HideOrShowShipInClientS2C.class,
            HideOrShowShipInClientS2C::encode,
            HideOrShowShipInClientS2C::decode,
            HideOrShowShipInClientS2C::handle
        );


        /*channel.registerMessage(
            packetId++,
            CarryShipSyncPacketS2C.class,
            CarryShipSyncPacketS2C::encode,
            CarryShipSyncPacketS2C::decode,
            CarryShipSyncPacketS2C::handle
        );*/


    }


}
