package com.lancas.vs_wap.ship.phys;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vs_wap.debug.EzDebug;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
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
public class CartridgeReactionController implements ShipForcesInducer {

    private Vector3d reactionDir;
    private double power;

    public static void appply(ServerShip ship, Vector3dc inReactionDir, double inPower) {
        CartridgeReactionController controller = ship.getAttachment(CartridgeReactionController.class);
        if (controller == null) {
            EzDebug.log("try create new reaction");
            controller = new CartridgeReactionController();
            ship.saveAttachment(CartridgeReactionController.class, controller);
        }

        controller.reactionDir = inReactionDir.get(new Vector3d());
        controller.power = inPower;

        EzDebug.log("dir:" + controller.reactionDir + ", power:" + controller.power);
    }

    private CartridgeReactionController() {}

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        EzDebug.log("try apply force:" + reactionDir.mul(power, new Vector3d()));
        physShip.applyInvariantForce(reactionDir.mul(power, new Vector3d()));
    }
}
