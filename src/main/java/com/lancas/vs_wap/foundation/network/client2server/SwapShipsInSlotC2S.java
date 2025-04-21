package com.lancas.vs_wap.foundation.network.client2server;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.api.Dest;
import com.lancas.vs_wap.ship.feature.hold.ICanHoldShip;
import com.lancas.vs_wap.ship.feature.hold.ShipHoldSlot;
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

            Dest<Long> fromSlotShipId = new Dest<>();
            Dest<Long> toSlotShipId = new Dest<>();
            icanHoldShip.getHoldingShipId(fromSlot, fromSlotShipId);
            icanHoldShip.getHoldingShipId(toSlot, toSlotShipId);

            if (fromSlotShipId.hasValue() && toSlotShipId.hasValue()) {
                icanHoldShip.unholdShipInServer(fromSlot, false, null);
                icanHoldShip.unholdShipInServer(toSlot, false, null);
                icanHoldShip.tryHoldInServer(fromSlot, toSlotShipId.get(), true);
                icanHoldShip.tryHoldInServer(toSlot, fromSlotShipId.get(), true);
                return;
            }

            //will packet arrive time effects?
            if (fromSlotShipId.hasValue()) {
                icanHoldShip.unholdShipInServer(fromSlot, true, null);
                icanHoldShip.tryHoldInServer(toSlot, fromSlotShipId.get(), true);
                return;
            }
            if (toSlotShipId.hasValue()) {
                icanHoldShip.unholdShipInServer(toSlot, true, null);
                icanHoldShip.tryHoldInServer(fromSlot, toSlotShipId.get(), true);
                return;
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
