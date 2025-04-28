package com.lancas.vs_wap.ship.tp;

import com.lancas.vs_wap.content.blockentity.VSProjectorBE;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.helper.builder.ShipTransformBuilder;
import com.lancas.vs_wap.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShipTransformProvider;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;

public class ProjectShipTp implements ServerShipTransformProvider {

    //todo    i guess it's server thread?
    private final BlockPos projectorBp;
    private final ServerLevel level;
    public ProjectShipTp(ServerLevel inLevel, BlockPos inProjectorBp) {
        level = inLevel;
        projectorBp = inProjectorBp;
    }

    @Override
    public @Nullable NextTransformAndVelocityData provideNextTransformAndVelocity(@NotNull ShipTransform shipTransform, @NotNull ShipTransform shipTransform1) {
        if (!(level.getBlockEntity(projectorBp) instanceof VSProjectorBE be)) {
            EzDebug.warn("no vs projector at " + projectorBp.toShortString());
            return null;
        }

        Vector3d targetProjectPos = WorldUtil.getWorldCenter(level, projectorBp);
        if (be.shouldShowProjectShip()) {
            targetProjectPos.add(0, be.getShipYOffset(), 0);
        } else {
            targetProjectPos.setComponent(1, -100);
        }

        return new NextTransformAndVelocityData(
            ShipTransformBuilder.copy(shipTransform)
                .setPosInWorld(targetProjectPos)
                .get(),
            new Vector3d(),
            new Vector3d()  //todo set omega by projector
        );
    }
}
