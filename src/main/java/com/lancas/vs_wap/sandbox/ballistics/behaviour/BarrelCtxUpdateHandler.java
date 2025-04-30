package com.lancas.vs_wap.sandbox.ballistics.behaviour;

import com.lancas.vs_wap.content.blocks.artillery.IBarrel;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.sandbox.ballistics.data.BallisticBarrelContextSubData;
import com.lancas.vs_wap.sandbox.ballistics.data.BallisticData;
import com.lancas.vs_wap.subproject.sandbox.ship.SandBoxServerShip;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBdc;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class BarrelCtxUpdateHandler {
    public static void updateBarrelCtx(ServerLevel level, SandBoxServerShip ship, BallisticData data) {
        var barrelCtx = data.barrelCtx;
        AABBdc localAABB = ship.getLocalAABB();
        AABBdc worldAABB = ship.getWorldAABB();




        //EzDebug.log("barrelCtx, tick:" + barrelCtx.exitedBarrelTicks + ", alws:" + barrelCtx.alwaysInBarrelSinceLaunch);

        //localAABB == null : the ship is empty, how can I know if it's in barrel?
        //set barrelCtx after abs exited: for safe
        if (localAABB == null || worldAABB == null || barrelCtx.isAbsoluteExitBarrel()) {
            barrelCtx.alwaysInBarrelSinceLaunch = false;
            barrelCtx.exitedBarrelTicks = BallisticBarrelContextSubData.ABSOLUTE_EXIT_BARREL_TICK + 1;
            return;
        }

        Direction localForward = JomlUtil.nearestDir(data.initialStateData.localForward);


        Vector3dc[] checkWorldPoses = new Vector3dc[] {
            new Vector3d(ship.getRigidbody().getDataReader().getTransform().getPosition()),
            ship.getRigidbody().getDataReader().getLocalToWorld().transformPosition(JomlUtil.dFaceCenter(localAABB, localForward)),
            ship.getRigidbody().getDataReader().getLocalToWorld().transformPosition(JomlUtil.dFaceCenter(localAABB, localForward.getOpposite()))
        };
        //check world
        for (Vector3dc checkWorldPos : checkWorldPoses) {
            BlockPos worldBp = JomlUtil.bpContaining(checkWorldPos);

            BlockState findBlockState = level.getBlockState(worldBp);
            if (!(findBlockState.getBlock() instanceof IBarrel)) continue;  //find a non-barrel block

            //the world block is barrel
            barrelCtx.exitedBarrelTicks = 0;
            return;
        }
        //check world fail, check ships
        for (Ship nearVsShip : VSGameUtilsKt.getShipsIntersecting(level, worldAABB)) {
            for (Vector3dc checkWorldPos : checkWorldPoses) {
                Vector3d checkPosInVsShip = nearVsShip.getWorldToShip().transformPosition(checkWorldPos, new Vector3d());
                BlockPos bpInVsShip = JomlUtil.bpContaining(checkPosInVsShip);

                BlockState findBlockState = level.getBlockState(bpInVsShip);
                if (!(findBlockState.getBlock() instanceof IBarrel)) continue;  //find a non-barrel block

                //the ship block is barrel
                barrelCtx.exitedBarrelTicks = 0;
                return;
            }
        }

        //EzDebug.log("loc:" + localAABB + ", wor:" + worldAABB + ", not in barrel");

        //the projectile is not in barrel now
        barrelCtx.alwaysInBarrelSinceLaunch = false;
        barrelCtx.exitedBarrelTicks++;
        return;
    }
}
