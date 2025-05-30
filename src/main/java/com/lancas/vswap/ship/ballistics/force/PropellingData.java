package com.lancas.vswap.ship.ballistics.force;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vswap.ship.type.ProjectileWrapper;
import org.joml.Vector3d;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropellingData {
    @JsonIgnore
    private static final double INITIAL_GAS_VOL = 1;  //todo config
    @JsonIgnore
    private static final double PRESS_AREA = 1;  //don't scale: scale increase power and area, same as neither is scaled

    public final double totalEnergy;
    public final double initialForcePower;
    private final Vector3d launchDir;

    private PropellingData() { totalEnergy = initialForcePower = 0; launchDir = null; }
    public PropellingData(ProjectileWrapper projectile, double inTotalEnergy) {
        totalEnergy = inTotalEnergy;
        initialForcePower = totalEnergy / INITIAL_GAS_VOL * PRESS_AREA;
        launchDir = projectile.getLaunchDir().get(new Vector3d());
    }

    public void applyPropellingForce(PhysShipImpl physShip, double traveledInBarrel) {
        double curGasVol = INITIAL_GAS_VOL + PRESS_AREA * traveledInBarrel;
        double forceRatio = INITIAL_GAS_VOL / curGasVol;

        Vector3d propellingForce = launchDir.normalize(initialForcePower * forceRatio, new Vector3d());
        physShip.applyInvariantForce(propellingForce);
    }

}
