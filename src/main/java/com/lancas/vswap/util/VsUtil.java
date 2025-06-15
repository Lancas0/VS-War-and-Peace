package com.lancas.vswap.util;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.valkyrienskies.core.impl.game.ships.DummyShipWorldClient;
import org.valkyrienskies.core.impl.game.ships.DummyShipWorldServer;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class VsUtil {
    public static boolean isDummy(Level level) {
        if (level == null)
            return true;

        var vsWorld = VSGameUtilsKt.getShipObjectWorld(level);
        return vsWorld instanceof DummyShipWorldServer || vsWorld instanceof DummyShipWorldClient;
    }
}
