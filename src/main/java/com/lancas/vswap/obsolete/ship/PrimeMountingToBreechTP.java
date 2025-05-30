package com.lancas.vswap.obsolete.ship;

/*
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShipTransformProvider;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;

public class PrimeMountingToBreechTP implements ServerShipTransformProvider {
    private BlockPos primerBp;
    private Direction primerDir;

    public PrimeMountingToBreechTP(BlockPos inPrimerBp, Direction inPrimerDir) {
        primerBp = inPrimerBp;
        primerDir = inPrimerDir;
    }

    @Override
    public @Nullable NextTransformAndVelocityData provideNextTransformAndVelocity(@NotNull ShipTransform shipTransform, @NotNull ShipTransform shipTransform1) {
        Vector3d primerOppositeFacePosInShip = JomlUtil.dFaceCenter(primerBp, primerDir.getOpposite());
        Vector3d primerOppositeFacePosInWorld = shipTransform.getShipToWorld().transformPosition(primerOppositeFacePosInShip);

        return null;
    }
}
*/