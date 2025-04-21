package com.lancas.vs_wap.ship.attachment.force;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProjectileReactForceInducer implements ShipForcesInducer {
    public ProjectileReactForceInducer() {}
    public static ProjectileReactForceInducer addForceTo(ServerShip ship, Vector3dc force) {
        var inducer = ship.getAttachment(ProjectileReactForceInducer.class);
        if (inducer == null) {
            inducer = new ProjectileReactForceInducer();
            ship.saveAttachment(ProjectileReactForceInducer.class, inducer);
        }

        inducer.addForce(force);
        return inducer;
    }
    public static ProjectileReactForceInducer addForcesTo(ServerShip ship, Iterable<Vector3dc> forces) {
        var inducer = ship.getAttachment(ProjectileReactForceInducer.class);
        if (inducer == null) {
            inducer = new ProjectileReactForceInducer();
            ship.saveAttachment(ProjectileReactForceInducer.class, inducer);
        }

        inducer.addAll(forces);
        return inducer;
    }

    private Queue<Vector3d> forces = new ConcurrentLinkedQueue<>();

    public void addForce(Vector3dc newForce) {
        if (newForce == null || !newForce.isFinite()) return;
        forces.add(new Vector3d(newForce));
    }
    public void addAll(Iterable<Vector3dc> newForces) {
        if (newForces == null) return;
        newForces.forEach(f -> {
            if (f == null || !f.isFinite()) return;
            forces.add(new Vector3d(f));
        });
    }

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        Vector3d force = forces.poll();
        if (force == null || !force.isFinite()) return;

        physShip.applyInvariantForce(force);
    }
}
