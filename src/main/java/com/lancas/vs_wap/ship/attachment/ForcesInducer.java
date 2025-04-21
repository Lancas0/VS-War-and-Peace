package com.lancas.vs_wap.ship.attachment;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;

import java.util.ArrayList;
import java.util.List;

public class ForcesInducer implements ShipForcesInducer {
    private final List<Vector3dc> forces = new ArrayList<>();
    private final List<Vector3dc> applyingForces = new ArrayList<>();

    public static boolean apply(@NotNull LoadedServerShip ship, Vector3dc force) {
        if (force == null || !force.isFinite())
            return false;

        var att = ship.getAttachment(ForcesInducer.class);
        if (att == null) {
            att = new ForcesInducer();
            ship.saveAttachment(ForcesInducer.class, att);
        }

        synchronized (att.forces) {  //todo: it is effective?
            att.forces.add(force);
        }
        return true;
    }


    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        if (forces.isEmpty())
            return;

        synchronized (forces) {
            applyingForces.addAll(forces);
            forces.clear();
        }

        for (Vector3dc force : applyingForces) {
            if (force == null || !force.isFinite())
                continue;

            physShip.applyInvariantForce(force);
        }
    }
}
