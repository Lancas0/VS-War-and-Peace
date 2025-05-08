package com.lancas.vs_wap.ship.attachment;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ForcesInducer implements ShipForcesInducer {
    //private final List<Vector3dc> forces = new ArrayList<>();
    private final Queue<Vector3dc> forces = new ConcurrentLinkedQueue<>();
    private final Queue<Vector3dc> torques = new ConcurrentLinkedQueue<>();

    public static ForcesInducer getOrCreate(@NotNull LoadedServerShip ship) {
        var att = ship.getAttachment(ForcesInducer.class);
        if (att == null) {
            att = new ForcesInducer();
            ship.saveAttachment(ForcesInducer.class, att);
        }
        return att;
    }
    public static ForcesInducer applyForce(@NotNull LoadedServerShip ship, Vector3dc force) {
        var inducer = getOrCreate(ship);

        if (force == null || !force.isFinite())
            return inducer;

        inducer.forces.add(new Vector3d(force));
        return inducer;
    }
    public static ForcesInducer applyTorque(@NotNull LoadedServerShip ship, Vector3dc torque) {
        var inducer = getOrCreate(ship);

        if (torque == null || !torque.isFinite())
            return inducer;

        inducer.torques.add(new Vector3d(torque));
        return inducer;
    }



    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        while (!forces.isEmpty()) {
            Vector3dc force = forces.poll();
            physShip.applyInvariantForce(force);
        }
        while (!torques.isEmpty()) {
            Vector3dc torque = torques.poll();
            physShip.applyInvariantTorque(torque);
        }
    }
}
