package com.lancas.vswap.ship.attachment;

import com.lancas.vswap.foundation.api.Dest;
import org.valkyrienskies.core.api.ships.ServerShip;

public interface IStorableAttachment {
    public boolean serialize(ServerShip inShip, Dest<String> json);
}
