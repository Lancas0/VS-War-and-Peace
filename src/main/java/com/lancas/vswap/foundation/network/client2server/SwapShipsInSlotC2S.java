package com.lancas.vswap.foundation.network.client2server;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.api.Dest;
import com.lancas.vswap.ship.feature.hold.ICanHoldShip;
import com.lancas.vswap.ship.feature.hold.ShipHoldSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SwapShipsInSlotC2S {
    private final String fromSlotName;
    private final String toSlotName;

    public SwapShipsInSlotC2S(String inFromSlot, String inToSlot) {
        fromSlotName = inFromSlot;
        toSlotName = inToSlot;
    }
    public SwapShipsInSlotC2S(ShipHoldSlot inFromSlot, ShipHoldSlot inToSlot) {
        fromSlotName = inFromSlot.slotName();
        toSlotName = inToSlot.slotName();
    }

    public static void encode(SwapShipsInSlotC2S msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.fromSlotName);
        buf.writeUtf(msg.toSlotName);
    }
    public static SwapShipsInSlotC2S decode(FriendlyByteBuf buf) {
        return new SwapShipsInSlotC2S(buf.readUtf(), buf.readUtf());
    }
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (!(ctx.get().getSender() instanceof ICanHoldShip icanHoldShip)) {
                EzDebug.fatal("player can't hold ship because unknown reason");
                return;
            }

            //todo makesure there will not cause hold id error

            ShipHoldSlot fromSlot = ShipHoldSlot.valueOf(fromSlotName);
            ShipHoldSlot toSlot = ShipHoldSlot.valueOf(toSlotName);

            Long fromSlotShipId = icanHoldShip.getHoldingShipId(fromSlot);
            Long toSlotShipId = icanHoldShip.getHoldingShipId(toSlot);

            if (fromSlotShipId != null && toSlotShipId != null) {
                icanHoldShip.unholdShipInServer(fromSlot, false);
                icanHoldShip.unholdShipInServer(toSlot, false);
                icanHoldShip.tryHoldInServer(fromSlot, toSlotShipId, true);
                icanHoldShip.tryHoldInServer(toSlot, fromSlotShipId, true);
                return;
            }

            //FIXME will packet arrive time effects?
            if (fromSlotShipId != null) {
                icanHoldShip.unholdShipInServer(fromSlot, true);
                icanHoldShip.tryHoldInServer(toSlot, fromSlotShipId, true);
                return;
            }
            if (toSlotShipId != null) {
                icanHoldShip.unholdShipInServer(toSlot, true);
                icanHoldShip.tryHoldInServer(fromSlot, toSlotShipId, true);
                return;
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
