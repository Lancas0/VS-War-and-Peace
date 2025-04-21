package com.lancas.vs_wap.obsolete.ship;

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
public class FinStabilizer implements ShipForcesInducer {
    @JsonIgnore
    private static final double STABILIZATION_FACTOR = 10;  //0.5
    @JsonIgnore
    private static final double DAMPING_FACTOR = 10;  //0.3
    @JsonIgnore
    private static final Vector3d ANTI_GRAVITY = new Vector3d(0, 4, 0);  //40% less gravity

    public int finCount = 0;  //todo more fin more stable but more resist force will increase
    private final Vector3d lastAngularVelocity = new Vector3d();

    public static FinStabilizer getOrCreate(ServerShip ship) {
        FinStabilizer inducer = ship.getAttachment(FinStabilizer.class);
        if (inducer == null) {
            inducer = new FinStabilizer();
            ship.saveAttachment(FinStabilizer.class, inducer);
        }
        return inducer;
    }

    public FinStabilizer() { }

    /*protected double stableFactorByVelocity(Vector3dc vel) {

    }
    protected double dampingFactorByVelocity(Vector3dc vel) {

    }
    protected double antiGravityByVelcoity(Vector3dc vel) {

    }*/

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        if (finCount <= 0) return;

        /*
        //EzDebug.Log("apply fin stabilize force");
        Vector3d angularVel = physShip.getOmega().get(new Vector3d());
        Vector3d angularAccel = new Vector3d(angularVel).sub(lastAngularVelocity);

        // 计算稳定力矩
        Vector3d stabilizationTorque = new Vector3d(angularVel).mul(-STABILIZATION_FACTOR);
        Vector3d dampingTorque = new Vector3d(angularAccel).mul(-DAMPING_FACTOR);

        //EzDebug.Log("stable torque:" + stabilizationTorque + ", damp torque:" + dampingTorque);

        // 施加组合力矩
        physShip.applyInvariantTorque(stabilizationTorque.add(dampingTorque).mul(physShip.getMomentOfInertia()));

        //减少重力 todo only decrease gravity when velocity is high
        physShip.applyInvariantForce(ANTI_GRAVITY.mul(physShip.getMass(), new Vector3d()));

        // update last omega
        lastAngularVelocity.set(angularVel);

         */
    }
}