package com.lancas.vs_wap.foundation.api.math;

import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.PhysShip;

public record ForceOnPos(Vector3d force, Vector3d pos) {
    public ForceOnPos scale(double scalar, ForceOnPos dest) {
        force.mul(scalar, dest.force);
        dest.pos.set(pos);
        return dest;
    }
    public ForceOnPos scale(double scalar) {
        force.mul(scalar);
        return this;
    }

    public void applyTo(PhysShip physShip) {
        if (physShip != null)
            physShip.applyInvariantForceToPos(force, pos);
    }
}
