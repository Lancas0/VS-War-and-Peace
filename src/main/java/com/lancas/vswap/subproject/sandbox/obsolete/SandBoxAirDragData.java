package com.lancas.vswap.subproject.sandbox.obsolete;
/*
import com.lancas.vs_wap.content.info.block.WapBlockInfos;
import com.lancas.vs_wap.ship.type.ProjectileWrapper;
import com.lancas.vs_wap.subproject.sandbox.api.component.IExposedComponentData;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.MathUtil;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

import java.util.concurrent.atomic.AtomicReference;

public class SandBoxAirDragData implements IComponentData<SandBoxAirDragData>, IExposedComponentData<SandBoxAirDragData> {
    private Vector3d localAirDragCenter;
    //private double boundAreaYzInShip;
    //private double boundAreaXzInShip;
    //private double boundAreaXyInShip;

    private Direction forwardInShip;
    private double scale;



    private AirDragData() {}
    public AirDragData(ServerLevel level, ProjectileWrapper inProjectile) {
        localAirDragCenter = calAirDragCenter(level, inProjectile.getShip(level));

        AABBic shipAABB = inProjectile.getShipAABB();
        int lengthX = JomlUtil.lengthX(shipAABB);
        int lengthY = JomlUtil.lengthY(shipAABB);
        int lengthZ = JomlUtil.lengthZ(shipAABB);
        boundAreaYzInShip = lengthY * lengthZ;
        boundAreaXzInShip = lengthX * lengthZ;
        boundAreaXyInShip = lengthX * lengthY;

        forwardInShip = inProjectile.getForwardInShip();
        scale = inProjectile.scale;
    }

    public void applyAirDrag(PhysShipImpl physShip) {
        Matrix4dc shipToWorld = physShip.getTransform().getShipToWorld();
        Matrix4dc worldToShip = physShip.getTransform().getWorldToShip();
        Vector3dc worldVel = physShip.getPoseVel().getVel();

        Vector3d worldMassCenter = physShip.getTransform().getPositionInWorld().get(new Vector3d());
        Vector3d worldAirDragCenter = shipToWorld.transformPosition(localAirDragCenter, new Vector3d());

        double airDragArea = getAirDragAreaInWorld(worldToShip, worldVel);

        double dragForceLen = 0.5 * airDragArea * worldVel.lengthSquared();  //todo * airDragMultiplierCalInServer;
        Vector3d airDragForce = worldVel.normalize(-dragForceLen, new Vector3d());

        if (airDragForce.isFinite()) {
            Vector3d linearDrag = new Vector3d();
            Vector3d rotateDrag = new Vector3d();
            MathUtil.orthogonality(airDragForce, JomlUtil.dWorldNormal(shipToWorld, forwardInShip), linearDrag, rotateDrag);

            physShip.applyInvariantForce(linearDrag);
            Vector3d moment = worldAirDragCenter.sub(worldMassCenter, new Vector3d()).cross(rotateDrag);
            physShip.applyInvariantTorque(moment.mul(1));
        }
    }


    private static Vector3d calAirDragCenter(ServerLevel level, ServerShip projectile) {
        //todo will excess the max?
        AtomicReference<Double> totalWeight = new AtomicReference<>((double) 0);
        Vector3d sumCenter = new Vector3d();
        ShipUtil.foreachBlock(projectile, level, (pos, state, be) -> {
            if (state.isAir()) return;

            double curDragFactor = WapBlockInfos.drag_factor.valueOrDefaultOf(state);
            totalWeight.updateAndGet(v -> v + curDragFactor);
            sumCenter.add(JomlUtil.dCenter(pos).mul(curDragFactor));
        });
        return sumCenter.div(totalWeight.get());
    }
    private double getAirDragAreaInWorld(Matrix4dc worldToShip, Vector3dc worldVel) {
        Vector3d velDirInShip = worldToShip.transformDirection(worldVel, new Vector3d()).normalize();

        return scale * scale * (
            Math.abs(velDirInShip.x) * boundAreaYzInShip +
                Math.abs(velDirInShip.y) * boundAreaXzInShip +
                Math.abs(velDirInShip.z) * boundAreaXyInShip
        );
    }
}
*/