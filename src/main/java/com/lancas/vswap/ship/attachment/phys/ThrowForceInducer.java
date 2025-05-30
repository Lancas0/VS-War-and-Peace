package com.lancas.vswap.ship.attachment.phys;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThrowForceInducer implements ShipForcesInducer {
    private static final int APPLY_TICK = 5;

    @JsonIgnore
    private double mass;
    @JsonIgnore
    private Vector3d forceIndependOfMass;

    private int remainApplyTick = 0;

    public static void createOrReset(@NotNull ServerShip ship, Vector3d inForceIndependOfMass) {
        double shipMass = ship.getInertiaData().getMass();

        ThrowForceInducer inducer = ship.getAttachment(ThrowForceInducer.class);
        if (inducer != null) {
            inducer.mass = shipMass;
            inducer.forceIndependOfMass = inForceIndependOfMass;
            inducer.remainApplyTick = APPLY_TICK;
        } else {
            ship.saveAttachment(ThrowForceInducer.class, new ThrowForceInducer(shipMass, inForceIndependOfMass));
        }
    }

    public ThrowForceInducer() { }
    public ThrowForceInducer(double inMass, Vector3d inForceIndependOfMass) {
        mass = inMass; forceIndependOfMass = inForceIndependOfMass; remainApplyTick = APPLY_TICK;
    }


    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        if (remainApplyTick <= 0) return;

        physShip.applyInvariantForce(forceIndependOfMass.mul(mass, new Vector3d()));
        remainApplyTick--;
    }
}
