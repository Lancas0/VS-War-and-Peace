package com.lancas.vs_wap.ship.ballistics.api;

import com.lancas.vs_wap.foundation.api.math.ForceOnPos;
import com.lancas.vs_wap.ship.ballistics.data.BallisticsShipData;
import net.minecraft.core.BlockPos;
import org.joml.Vector3d;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

public interface IPhysBehaviour {
    public default Vector3d getAdditionalForce(PhysShipImpl physShip, BlockPos bpInShip, BallisticsShipData shipData) { return null; }
    public default ForceOnPos getAdditionalForceOnPos(PhysShipImpl physShip, BlockPos bpInShip, BallisticsShipData shipData) { return null; }
    public default Vector3d getAdditionalTorque(PhysShipImpl physShip, BlockPos bpInShip, BallisticsShipData shipData) { return null; }
    //public default TorqueOnPos getAdditionalTorqueOnPos(PhysShipImpl physShip, BlockPos bpInShip, BallisticsShipData shipData) { return null; }

    public default void applyOnShip(PhysShipImpl physShip, BlockPos bpInShip, BallisticsShipData shipData) {
        Vector3d force = getAdditionalForce(physShip, bpInShip, shipData);
        if (force != null)
            physShip.applyInvariantForce(force);

        ForceOnPos forceOnPos = getAdditionalForceOnPos(physShip, bpInShip, shipData);
        if (forceOnPos != null)
            physShip.applyInvariantForceToPos(forceOnPos.force(), forceOnPos.pos());

        Vector3d torque = getAdditionalTorque(physShip, bpInShip, shipData);
        if (torque != null)
            physShip.applyInvariantTorque(torque);
    }
}
