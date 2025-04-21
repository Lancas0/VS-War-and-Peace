package com.lancas.vs_wap.ship.attachment.force;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
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
//todo loc force
public class PresistantForceInducer implements ShipForcesInducer {
    private Vector3d force = new Vector3d();

    public static void apply(@NotNull ServerShip ship, Vector3d inForce) {
        PresistantForceInducer inducer = ship.getAttachment(PresistantForceInducer.class);
        if (inducer != null) {
            inducer.force = inForce;
        } else {
            ship.saveAttachment(PresistantForceInducer.class, new PresistantForceInducer(inForce));
        }
    }

    public PresistantForceInducer() {}
    public PresistantForceInducer(Vector3d inForce) { force = new Vector3d(inForce); }

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        physShip.applyInvariantForce(force);
    }
}
