package com.lancas.vs_wap.content.item.items.docker;

import com.lancas.vs_wap.ship.attachment.HoldableAttachment;
import com.lancas.vs_wap.ship.data.IShipSchemeData;
import com.lancas.vs_wap.ship.data.IShipSchemeRandomReader;
import com.lancas.vs_wap.ship.helper.builder.ShipBuilder;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4dc;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.UUID;

public interface IDocker {
    @Nullable
    public static ShipBuilder makeShipBuilderFromStack(ServerLevel level, ItemStack stack) {
        if (!(stack.getItem() instanceof IDocker docker)) return null;
        return docker.makeShipBuilder(level, stack);
    }

    public ShipBuilder makeShipBuilder(ServerLevel level, ItemStack stack);
    //it will delete, or hide the ship
    public ItemStack saveShip(ServerLevel level, ServerShip ship, ItemStack stack);
    public boolean hasShipData(ItemStack stack);
    @Nullable
    public IShipSchemeData getShipData(ItemStack stack);

    @Nullable
    public UUID getOrCreateDockerUuidIfHasData(ItemStack stack);
    @Nullable
    public IShipSchemeRandomReader getShipDataReader(ItemStack stack);


    public static ShipBuilder setShipTransformByHoldable(ShipBuilder builder, HoldableAttachment holdable, Vector3dc moveThePivotTo, Vector3dc rotateTheForwardTo) {
        Matrix4dc shipToWorld = builder.get().getShipToWorld();

        return builder.doIfElse(
            self -> holdable != null,
            self -> {
                Vector3d worldForward = JomlUtil.dWorldNormal(shipToWorld, holdable.forwardInShip);
                Quaterniond rotation = worldForward.rotationTo(rotateTheForwardTo, new Quaterniond());
                self.rotate(rotation).moveShipPosToWorldPos(JomlUtil.dCenter(holdable.holdPivotBpInShip.toBp()), moveThePivotTo);
            },
            self -> self.setWorldPos(moveThePivotTo)  //todo do direciton
        );
    }
}
