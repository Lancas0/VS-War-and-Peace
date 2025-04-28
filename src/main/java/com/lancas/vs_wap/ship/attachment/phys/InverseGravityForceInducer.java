package com.lancas.vs_wap.ship.attachment.phys;

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
public class InverseGravityForceInducer  implements ShipForcesInducer {
    @JsonIgnore
    public static final Vector3d INV_GRAVITY = new Vector3d(0, 20, 0);
    private double mass;

    public static InverseGravityForceInducer getOrCreate(@NotNull ServerShip ship){
        InverseGravityForceInducer inducer = ship.getAttachment(InverseGravityForceInducer.class);
        if(inducer == null){
            inducer = new InverseGravityForceInducer(ship.getInertiaData().getMass());
            ship.saveAttachment(InverseGravityForceInducer.class, inducer);
        }
        return inducer;
    }

    public InverseGravityForceInducer() {}
    public InverseGravityForceInducer(double inMass) { mass = inMass; }

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        physShip.applyInvariantForce(INV_GRAVITY.mul(mass, new Vector3d()));
    }
}
