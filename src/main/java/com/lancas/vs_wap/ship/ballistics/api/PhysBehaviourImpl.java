package com.lancas.vs_wap.ship.ballistics.api;

import com.lancas.vs_wap.foundation.api.math.ForceOnPos;
import com.lancas.vs_wap.ship.ballistics.data.BallisticsShipData;
import net.minecraft.core.BlockPos;
import org.apache.commons.lang3.function.TriFunction;
import org.joml.Vector3d;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

public class PhysBehaviourImpl implements IPhysBehaviour {
    public TriFunction<PhysShipImpl, BlockPos, BallisticsShipData, Vector3d> forceGetter;
    public TriFunction<PhysShipImpl, BlockPos, BallisticsShipData, ForceOnPos> forceOnPosGetter;
    public TriFunction<PhysShipImpl, BlockPos, BallisticsShipData, Vector3d> torqueGetter;
    //public BiFunction<PhysShipImpl, BlockPos, TorqueOnPos> torqueOnPosGetter;

    public PhysBehaviourImpl(
        TriFunction<PhysShipImpl, BlockPos, BallisticsShipData, Vector3d> inForceGetter,
        TriFunction<PhysShipImpl, BlockPos, BallisticsShipData, ForceOnPos> inForceOnPosGetter,
        TriFunction<PhysShipImpl, BlockPos, BallisticsShipData, Vector3d> inTorqueGetter//,
        //BiFunction<PhysShipImpl, BlockPos, TorqueOnPos> inTorqueOnPosGetter
    ) {
        forceGetter = inForceGetter;
        forceOnPosGetter = inForceOnPosGetter;
        torqueGetter = inTorqueGetter;
        //torqueOnPosGetter = inTorqueOnPosGetter;
    }

    public PhysBehaviourImpl forceBehaviour(TriFunction<PhysShipImpl, BlockPos, BallisticsShipData, Vector3d> inForceGetter) {
        forceGetter = inForceGetter;
        return this;
    }
    public static PhysBehaviourImpl createForceBehaviour(TriFunction<PhysShipImpl, BlockPos, BallisticsShipData, Vector3d> forceGetter) {
        return new PhysBehaviourImpl(forceGetter, null, null);
    }
    public static PhysBehaviourImpl createForceOnPosBehaviour(TriFunction<PhysShipImpl, BlockPos, BallisticsShipData, ForceOnPos> forceOnPosGetter) {
        return new PhysBehaviourImpl(null, forceOnPosGetter, null);
    }
    public static PhysBehaviourImpl createTorqueBehaviour(TriFunction<PhysShipImpl, BlockPos, BallisticsShipData, Vector3d> torqueGetter) {
        return new PhysBehaviourImpl(null, null, torqueGetter);
    }
    /*public static PhysBehaviourImpl createTorqueOnPosBehaviour(BiFunction<PhysShipImpl, BlockPos, TorqueOnPos> torqueOnPosGetter) {
        return new PhysBehaviourImpl(null, null, null, torqueOnPosGetter);
    }*/

    @Override
    public Vector3d getAdditionalForce(PhysShipImpl physShip, BlockPos bpInShip, BallisticsShipData shipData) {
        if (physShip == null || forceGetter == null)
            return null;
        return forceGetter.apply(physShip, bpInShip, shipData);
    }
    @Override
    public ForceOnPos getAdditionalForceOnPos(PhysShipImpl physShip, BlockPos bpInShip, BallisticsShipData shipData) {
        if (physShip == null || forceOnPosGetter == null)
            return null;
        return forceOnPosGetter.apply(physShip, bpInShip, shipData);
    }
    @Override
    public Vector3d getAdditionalTorque(PhysShipImpl physShip, BlockPos bpInShip, BallisticsShipData shipData) {
        if (physShip == null || torqueGetter == null)
            return null;
        return torqueGetter.apply(physShip, bpInShip, shipData);
    }
    /*@Override
    public TorqueOnPos getAdditionalTorqueOnPos(PhysShipImpl physShip, BlockPos bpInShip, BallisticsShipData shipData) {
        if (physShip == null || torqueOnPosGetter == null)
            return null;
        return torqueOnPosGetter.apply(physShip, bpInShip);
    }*/
}
