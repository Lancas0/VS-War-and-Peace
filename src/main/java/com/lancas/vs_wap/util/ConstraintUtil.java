package com.lancas.vs_wap.util;

import net.minecraft.server.level.ServerLevel;
import org.joml.AxisAngle4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint;
import org.valkyrienskies.core.apigame.constraints.VSFixedOrientationConstraint;
import org.valkyrienskies.core.apigame.constraints.VSHingeOrientationConstraint;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class ConstraintUtil {
    public static void Attach(ServerLevel level, ServerShip ship0, ServerShip ship1, Vector3d offset) {
        Vector3dc attLocPos0 = ship0.getTransform().getPositionInShip();
        Vector3dc attLocPos1 = ship1.getTransform().getPositionInShip().sub(offset, new Vector3d());

        double attachmentCompliance = 1e-10;
        double attachmentMaxForce = 1e10;
        double attachmentFixedDistance = 0.0;
        VSAttachmentConstraint constraint = new VSAttachmentConstraint(
            ship0.getId(), ship1.getId(), attachmentCompliance, attLocPos0, attLocPos1,
            attachmentMaxForce, attachmentFixedDistance
        );

        VSGameUtilsKt.getShipObjectWorld(level).createNewConstraint(constraint);
    }
    public static void HingeTest(ServerLevel level, ServerShip ship0, ServerShip ship1) {
        Quaterniond hingeOrientation = new Quaterniond().mul(
            new Quaterniond(new AxisAngle4d(0, 1, 1, 1)),
            new Quaterniond()
        ).normalize();

        double compliance = 1e-10;
        double hingeMaxTorque = 1e10;
        VSHingeOrientationConstraint hingeConstraint = new VSHingeOrientationConstraint(
            ship0.getId(), ship1.getId(), compliance, hingeOrientation, hingeOrientation, hingeMaxTorque
        );
        VSGameUtilsKt.getShipObjectWorld(level).createNewConstraint(hingeConstraint);
    }
    public static void FixedOrientation(ServerLevel level, ServerShip ship0, ServerShip ship1) {
        /*Quaterniond hingeOrientation = new Quaterniond().mul(
            new Quaterniond(new AxisAngle4d(0, 1, 1, 1)),
            new Quaterniond()
        ).normalize();*/

        double compliance = 1e-10;
        double hingeMaxTorque = 1e10;
        VSFixedOrientationConstraint constraint = new VSFixedOrientationConstraint(
            ship0.getId(), ship1.getId(), compliance, new Quaterniond(), new Quaterniond(), hingeMaxTorque
        );
        VSGameUtilsKt.getShipObjectWorld(level).createNewConstraint(constraint);
    }
}
