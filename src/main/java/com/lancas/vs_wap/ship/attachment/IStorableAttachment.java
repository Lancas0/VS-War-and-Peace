package com.lancas.vs_wap.ship.attachment;

import com.lancas.vs_wap.foundation.api.Dest;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

public interface IStorableAttachment {
    public boolean serialize(ServerShip inShip, Dest<String> json);
}
