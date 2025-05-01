package com.lancas.vs_wap.content.block.blocks;

import com.lancas.vs_wap.content.block.blocks.abstrac.PoweredDirectionalBlockImpl;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.primitives.AABBd;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class LocatorBlock extends PoweredDirectionalBlockImpl {
    public static final double DETECT_DIST = 1;

    public LocatorBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public void onPowerChange(Level level, BlockPos pos, BlockState state, boolean powered) {
        if (!powered || level.isClientSide) return;

        ServerShip onShip = ShipUtil.getServerShipAt((ServerLevel)level, pos);
        Direction face = state.getValue(FACING);

        AABBd detectAmmoAABB;

        if (onShip == null) {
            Vector3d minCorner = JomlUtil.dLowerCorner(pos.relative(face));
            Vector3d maxCorner = minCorner.add(1, 1, 1, new Vector3d());
            detectAmmoAABB = new AABBd(minCorner, maxCorner);
        } else {
            detectAmmoAABB = new AABBd();
            Matrix4dc shipToWorld = onShip.getShipToWorld();
            for (Vector3d corner : JomlUtil.dCorners(pos.relative(face))) {
                detectAmmoAABB.union(shipToWorld.transformPosition(corner));
            }
        }

        ServerShip catchShip = null;
        for (Ship ship : VSGameUtilsKt.getShipsIntersecting(level, detectAmmoAABB)) {
            if (onShip != null && ship.getId() == onShip.getId()) continue;
            if (ship instanceof ServerShip curServerShip) {
                catchShip = curServerShip;
                break;
            }
        }

        if (catchShip != null) {

        }
    }
}
