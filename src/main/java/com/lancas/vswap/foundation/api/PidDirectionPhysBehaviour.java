package com.lancas.vswap.foundation.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vswap.foundation.Constants;
import com.lancas.vswap.ship.ballistics.api.IPhysBehaviour;
import com.lancas.vswap.ship.ballistics.data.BallisticsShipData;
import com.lancas.vswap.util.JomlUtil;
import net.minecraft.core.BlockPos;
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
public class PidDirectionPhysBehaviour implements IPhysBehaviour {

    private final PidDirectionController pidController;

    public PidDirectionPhysBehaviour() {
        pidController = new PidDirectionController(2, 0, 1, 100);
    }
    public PidDirectionPhysBehaviour(double p, double i, double d, double maxOmega) {
        pidController = new PidDirectionController(p, i, d, maxOmega);
    }

    @Override
    public Vector3d getAdditionalTorque(PhysShipImpl physShip, BlockPos bpInShip, BallisticsShipData shipData) {
        Vector3d worldTowards = JomlUtil.dWorldNormal(physShip.getTransform().getShipToWorld(), shipData.getForwardInProjectileShip());
        Vector3dc velTowards = physShip.getPoseVel().getVel();

        Matrix3dc inertia = physShip.getInertia().getMomentOfInertiaTensor();
        return pidController.getTorque(worldTowards, velTowards, physShip.getPoseVel().getOmega(), inertia, Constants.PHYS_FRAME_TIME);
    }
}
