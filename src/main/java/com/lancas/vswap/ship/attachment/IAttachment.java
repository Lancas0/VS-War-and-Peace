package com.lancas.vswap.ship.attachment;

import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ServerShip;

public interface IAttachment {
    public void addTo(@NotNull ServerShip ship);
}
