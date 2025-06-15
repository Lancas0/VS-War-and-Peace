package com.lancas.vswap.foundation.network.client2server;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.ship.feature.hold.ICanHoldShip;
import com.lancas.vswap.ship.feature.hold.ShipHoldSlot;
import com.lancas.vswap.ship.attachment.phys.ThrowForceInducer;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.function.Supplier;

public class ThrowShipPacketC2S {
    private final static int MAX_POWER_TICK_CNT = 40;  //about 2s  todo configurable
    private final static float MAX_POWER = 24;  //todo configurable

    private int tickCnt;
    public ThrowShipPacketC2S(int inTickCnt) {
        tickCnt = inTickCnt;
    }

    public static void encode(ThrowShipPacketC2S msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.tickCnt);
    }
    public static ThrowShipPacketC2S decode(FriendlyByteBuf buf) {
        return new ThrowShipPacketC2S(buf.readInt());
    }
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            EzDebug.log("try throw ship");

            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = (ServerLevel)player.level();

            if (!(player instanceof ICanHoldShip icanHoldShip)) {
                EzDebug.fatal("player can't hold ship beacuse unknown error!");
                return;
            }

            Long prevHoldShipId = icanHoldShip.unholdShipInServer(ShipHoldSlot.MainHand, true);

            if (prevHoldShipId == null) {
                EzDebug.log("player is not holding a ship");
                return;
                //player is not holding a ship
            }
            Ship ship = ShipUtil.getLoadedShipByID(level, prevHoldShipId);
            if (ship == null) return;  //player is not holding a ship

            ThrowForceInducer.createOrReset((ServerShip)ship, getForce(player, this.tickCnt));
            /*NetworkHandler.sendToClientPlayer(
                player,
                new ICanHoldShipSyncPacketS2C(-1)
            );*/
        });
        ctx.get().setPacketHandled(true);
    }

    private Vector3d getForce(ServerPlayer player, int inTickCnt) {
        EzDebug.log("throw force: tick is " + inTickCnt + ", scalar is " + (MAX_POWER * (float)inTickCnt / MAX_POWER_TICK_CNT));

        if (inTickCnt <= 0) {
            EzDebug.log("[AssertFail] tick is <= 0");
            inTickCnt = 1;
        }

        int countedTick = Math.min(inTickCnt, MAX_POWER_TICK_CNT);

        return JomlUtil.d(player.getViewVector(1f)).mul(MAX_POWER * (float)countedTick / MAX_POWER_TICK_CNT);
    }
}
