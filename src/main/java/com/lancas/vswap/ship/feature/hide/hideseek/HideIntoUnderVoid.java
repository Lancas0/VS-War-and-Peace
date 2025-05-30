package com.lancas.vswap.ship.feature.hide.hideseek;

import com.lancas.vswap.ship.feature.hide.IHideAndSeek;
import com.lancas.vswap.ship.helper.builder.TeleportDataBuilder;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ServerShipTransformProvider;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl;

public class HideIntoUnderVoid implements IHideAndSeek {
    public HideIntoUnderVoid() {}

    private Vector3d prePos = new Vector3d();
    private Quaterniond preRot = new Quaterniond();
    private Vector3d preScale = new Vector3d(1, 1, 1);

    private class HideTp implements ServerShipTransformProvider {
        @Override
        public @Nullable NextTransformAndVelocityData provideNextTransformAndVelocity(@NotNull ShipTransform shipTransform, @NotNull ShipTransform shipTransform1) {
            return new NextTransformAndVelocityData(
                new ShipTransformImpl(prePos.setComponent(1, -1000), shipTransform.getPositionInShip(), preRot, preScale),
                new Vector3d(),
                new Vector3d()
            );
        }
    }

    @Override
    public boolean hide(@NotNull ServerLevel level, @NotNull ServerShip ship) {
        ShipTransform transform = ship.getTransform();
        prePos.set(transform.getPositionInWorld());
        preRot.set(transform.getShipToWorldRotation());
        preScale.set(transform.getShipToWorldScaling());
        ship.setTransformProvider(new HideTp());
        //ship.setStatic(true);
        return true;
    }

    @Override
    public boolean seek(@NotNull ServerLevel level, @NotNull ServerShip ship) {
        ship.setTransformProvider(null);
        //ship.setStatic(false);
        ShipUtil.teleport(level, ship, new TeleportDataBuilder(level, prePos, preRot, preScale, new Vector3d(), new Vector3d()).get());
        return true;
    }
}
