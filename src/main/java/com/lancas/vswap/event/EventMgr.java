package com.lancas.vswap.event;

import com.lancas.vswap.VsWap;
import com.lancas.vswap.content.block.blocks.cartridge.primer.PrimerBlock;
import com.lancas.vswap.content.block.blocks.cartridge.ShellFrame;
import com.lancas.vswap.content.saved.vs_constraint.ConstraintsMgr;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.event.impl.SingleEventImpl;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.valkyrienskies.core.api.ships.Ship;

@Mod.EventBusSubscriber(modid = VsWap.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventMgr {
    public static class Server {
        public static final ConstraintAddEvent constraintAddEvent = new ConstraintAddEvent();
        public static final ConstraintRemoveEvent constraintRemoveEvent = new ConstraintRemoveEvent();
        public static final HoldShipEvent holdShipEvent = new HoldShipEvent();
        public static final SingleEventImpl<TickEvent.ServerTickEvent> serverEndTickEvent = new SingleEventImpl<>();
        public static final SingleEventImpl<TickEvent.ServerTickEvent> serverStartTickEvent = new SingleEventImpl<>();

        public static final SingleEventImpl<Long> onVsShipUnloaded = new SingleEventImpl<>();
    }


    public static void registerDefault() {
        Server.constraintAddEvent.addListener(ShellFrame.onConstraintAdd());
        Server.constraintRemoveEvent.addListener(ShellFrame.onConstraintRemove());

        Server.constraintAddEvent.addListener(PrimerBlock.onConstraintAdd);
        Server.constraintRemoveEvent.addListener(PrimerBlock.onConstraintRemove);

        //remove all constraint with the toHoldShip that record in ConstraintMgr
        Server.holdShipEvent.addListener((ServerLevel level, ServerPlayer player, Long shiId) -> {
            Ship toHoldShip = ShipUtil.getServerShipByID(level, shiId);
            if (toHoldShip == null) {
                EzDebug.error("the ship to hold is null!");
                return;
            }
            ConstraintsMgr.removeAllConstraintWith(level, shiId);
        });
    }

    @SubscribeEvent
    public static void ServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            Server.serverEndTickEvent.invokeAll(event);
        if (event.phase == TickEvent.Phase.START)
            Server.serverStartTickEvent.invokeAll(event);
    }
}
