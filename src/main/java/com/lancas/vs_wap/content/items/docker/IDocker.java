package com.lancas.vs_wap.content.items.docker;

import com.lancas.vs_wap.ship.attachment.HoldableAttachment;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4dc;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;

public interface IDocker {
    public ShipBuilder makeShipBuilderFromStack(ServerLevel level, ItemStack stack);
    //it will delete, or hide the ship
    public ItemStack saveShipToStack(ServerLevel level, ServerShip ship, ItemStack stack);


    public static ShipBuilder setShipTransformByHoldable(ShipBuilder builder, HoldableAttachment holdable, Vector3dc pos, Vector3dc headWorldDir) {
        Matrix4dc shipToWorld = builder.get().getShipToWorld();

        return builder.doIfElse(
            self -> holdable != null,
            self -> {
                Vector3d worldForward = JomlUtil.dWorldNormal(shipToWorld, holdable.forwardInShip);
                Quaterniond rotation = worldForward.rotationTo(headWorldDir, new Quaterniond());
                self.rotate(rotation).moveShipPosToWorldPos(JomlUtil.dCenter(holdable.holdPivotBpInShip.toBp()), pos);
            },
            self -> self.setWorldPos(pos)  //todo do direciton
        );
    }
}
