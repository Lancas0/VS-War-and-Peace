package com.lancas.vswap.ship.tp;

import com.lancas.vswap.content.block.blocks.industry.projector.VSProjectorBE;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.ship.helper.LazyShip;
import com.lancas.vswap.ship.helper.builder.ShipTransformBuilder;
import com.lancas.vswap.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ServerShipTransformProvider;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;

public class ProjectShipTp implements ServerShipTransformProvider {
    public static final double RPM_TO_RAD_PER_S = 0.10471975511965977;

    //todo    i guess it's server thread?
    private final BlockPos projectorBp;
    private final ServerLevel level;
    //private final long shipId;
    private final LazyShip lazyShip;
    private VSProjectorBE be;
    //public float rpm = 0;
    private double rotateRad = 0;
    public void setRotateRad(double val) {
        rotateRad = val;
    }
    public void rotateTickByRpm(double rpm) {
        rotateRad += rpm * RPM_TO_RAD_PER_S * 0.05;
    }

    public ProjectShipTp(ServerLevel inLevel, long inShipId, BlockPos inProjectorBp) {
        level = inLevel;
        //shipId = inShipId;
        projectorBp = inProjectorBp;

        lazyShip = LazyShip.ofId(inShipId);

        if (!(level.getBlockEntity(projectorBp) instanceof VSProjectorBE projectorBE)) {
            EzDebug.warn("no vs projector at " + projectorBp.toShortString());
        } else {
            be = projectorBE;
        }
    }

    @Override
    public @Nullable NextTransformAndVelocityData provideNextTransformAndVelocity(@NotNull ShipTransform shipTransform, @NotNull ShipTransform shipTransform1) {
        if (be == null)
            return null;

        Vector3d targetProjectPos = WorldUtil.getWorldCenter(level, projectorBp);
        if (be.shouldShowProjectShip()) {
            targetProjectPos.add(0, be.getShipYOffset(), 0);
        } else {
            targetProjectPos.setComponent(1, -100);
        }

        ServerShip ship = lazyShip.get(level, null);
        if (ship == null || ship.getShipAABB() == null) return null;
        Vector3dc massCenter = ship.getInertiaData().getCenterOfMassInShip();

        return new NextTransformAndVelocityData(
            ShipTransformBuilder.copy(shipTransform)
                .setPosInShip(massCenter.add(0.5, 0.5, 0.5, new Vector3d()))
                //.setPosInWorld(targetProjectPos)
                .setWorldPosSoThatFaceCenterAt(ship.getShipAABB(), ship.getShipToWorld(), Direction.DOWN, targetProjectPos)
                .setRotation(new Quaterniond(new AxisAngle4d(/*rpm * RPM_TO_RAD_PER_S * 0.05*/rotateRad, new Vector3d(0, 1, 0))))
                //.rotate(new Quaterniond(new AxisAngle4d(/*rpm * RPM_TO_RAD_PER_S * 0.05*/rotateRad, new Vector3d(0, 1, 0))))
                .setScale(new Vector3d(be.scale))
                .get(),
            new Vector3d(),
            new Vector3d()  //todo set omega by projector
        );
    }
}
