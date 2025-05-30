package com.lancas.vswap.ship.ballistics.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vswap.ship.type.ProjectileWrapper;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ServerShip;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BallisticData {
    public final BallisticsShipData shipData;
    public final BallisticsComponentData componentData;
    public final BallisticStateData stateData;
    public final BallisticBarrelData barrelData;


    private boolean terminated = false;
    public void setAsTerminated() { terminated = true; }
    public boolean isTerminated() { return terminated; }

    private BallisticData() { shipData = null; componentData = null; stateData = null; barrelData = null; }
    public BallisticData(ServerLevel level, @NotNull ProjectileWrapper inProjectile, long propellantShipId, long artilleryShipId, double inPropellantEnergy) {
        ServerShip projectileShip = inProjectile.getShip(level);

        shipData = new BallisticsShipData(inProjectile, propellantShipId, artilleryShipId);
        componentData = new BallisticsComponentData(level, projectileShip);
        stateData = new BallisticStateData(inPropellantEnergy);

        //todo temp
        barrelData = new BallisticBarrelData(10);
    }
}
