package com.lancas.vswap.foundation.network.server2client;

import com.lancas.vswap.ship.tp.HideClientTP;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.valkyrienskies.core.api.ships.ClientShip;

import java.util.function.Supplier;

public class HideOrShowShipInClientS2C {
    private final long shipId;
    private final boolean hide;

    public HideOrShowShipInClientS2C(long inShipId, boolean inHide) { shipId = inShipId; hide = inHide; }

    public static void encode(HideOrShowShipInClientS2C msg, FriendlyByteBuf buf) {
        buf.writeLong(msg.shipId);
        buf.writeBoolean(msg.hide);
    }
    public static HideOrShowShipInClientS2C decode(FriendlyByteBuf buf) {
        return new HideOrShowShipInClientS2C(buf.readLong(), buf.readBoolean());
    }
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;

            ClientShip cShip = ShipUtil.getClientShipByID(mc.level, shipId);
            if (hide)
                cShip.setTransformProvider(new HideClientTP());
            else
                cShip.setTransformProvider(null);
        });
        ctx.get().setPacketHandled(true);
    }
}
