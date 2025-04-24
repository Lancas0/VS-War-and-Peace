package com.lancas.vs_wap.ship.ballistics.force;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.joml.Matrix3dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DampingData {
    private static final double torqueDamping = 1.0;

    public void applyDamping(PhysShipImpl physShip) {
        Vector3dc omega = physShip.getPoseVel().getOmega();
        Matrix3dc inertia = physShip.getInertia().getMomentOfInertiaTensor();
        Vector3d dampingTorque = omega.mul(inertia, new Vector3d()).mul(-torqueDamping);

        physShip.applyInvariantTorque(dampingTorque);
    }
}
