package com.lancas.vs_wap.ship.attachment.force;

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
public class AntiGravityForceInducer implements ShipForcesInducer {
    @JsonIgnore
    public static final Vector3d ANTI_GRAVITY = new Vector3d(0, 10, 0);

    private double mass;
    private boolean active;

    public static void active(@NotNull ServerShip ship, boolean inActive){
        AntiGravityForceInducer inducer = ship.getAttachment(AntiGravityForceInducer.class);
        double shipMass = ship.getInertiaData().getMass();
        if (inducer != null) {
            inducer.mass = shipMass;
            inducer.active = inActive;
        } else {
            ship.saveAttachment(AntiGravityForceInducer.class, new AntiGravityForceInducer(shipMass, inActive));
        }
    }

    public AntiGravityForceInducer() { active = false; }
    public AntiGravityForceInducer(double inMass, boolean inActive) { mass = inMass; active = inActive; }

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        if (!active) return;

        //todo every time before apply force, update the shipMass
        physShip.applyInvariantForce(ANTI_GRAVITY.mul(mass, new Vector3d()));
    }
}
