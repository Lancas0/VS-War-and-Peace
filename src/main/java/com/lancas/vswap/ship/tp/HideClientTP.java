package com.lancas.vswap.ship.tp;

import com.lancas.vswap.ship.helper.builder.ShipTransformBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.ClientShipTransformProvider;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;

public class HideClientTP implements ClientShipTransformProvider {
    @Override
    public @Nullable ShipTransform provideNextRenderTransform(@NotNull ShipTransform shipTransform, @NotNull ShipTransform shipTransform1, double v) {
        return ShipTransformBuilder.copy(shipTransform)
            .setYInWorld(-10) //todo
            .get();
    }

    @Override
    public @Nullable ShipTransform provideNextTransform(@NotNull ShipTransform shipTransform, @NotNull ShipTransform shipTransform1, @NotNull ShipTransform shipTransform2) {
        return null;
    }
}
