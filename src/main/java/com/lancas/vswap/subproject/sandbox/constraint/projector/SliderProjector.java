package com.lancas.vswap.subproject.sandbox.constraint.projector;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.concurrent.atomic.AtomicReference;
/*
public class SliderProjector {
    private IRigidbodyDataReader baseRigidReader;
    private IRigidbodyDataReader targetRigidReader;

    private IRigidbodyDataWriter targetRigidWriter;

    private final Vector3d localAttAPos = new Vector3d();
    private final Vector3d localAttBPos = new Vector3d();
    private final Vector3d localSliderAxis = new Vector3d();
    private final double fixedDistance;

    public SliderProjector(Vector3dc inLocalAttAPos, Vector3dc inLocalAttBPos, ) {

    }

    public void project() {
        //EzDebug.light("projecting slider att");
        if (!localSliderAxis.isFinite()) {
            EzDebug.warn("the local slider axis is not valid!");
            return;  //todo remove constraint
        }

        ISandBoxShip shipA = world.getShipOrGround(aUuid);
        ISandBoxShip shipB = world.getShipOrGround(bUuid);

        //EzDebug.log("a is ground:" + bodyAuuid.equals(world.wrapOrGetGround().getUuid()) + ", b is ground:" + bodyBuuid.equals(world.wrapOrGetGround().getUuid()));

        if (shipA == null || shipB == null) {
            //EzDebug.log("shipA uuid:" +  bodyAuuid + ", world ground:" + world.warpOrGetGround().getUuid());
            EzDebug.warn("can't find one of or two of constrainted ship, shipA is null?:" + (shipA == null) + ", shipB null?:" + (shipB == null) + ", will remove this constraint");
            world.getConstraintSolver().markConstraintRemoved(this.selfUuid);
            return;  //todo remove the constraint
        }
        if (shipB.getRigidbody().getDataReader().isStatic()) return;  //no need to apply constraint

        IRigidbodyDataReader aRigidReader = shipA.getRigidbody().getDataReader();
        IRigidbodyDataReader bRigidReader = shipB.getRigidbody().getDataReader();

        //IRigidbodyDataWriter aRigidWriter = shipA.getRigidbody().getDataWriter();
        IRigidbodyDataWriter bRigidWriter = shipB.getRigidbody().getDataWriter();

        Vector3d worldAttAPos = aRigidReader.localToWorldPos(localAttAPos, new Vector3d());
        Vector3d worldAttBPos = bRigidReader.localToWorldPos(localAttBPos, new Vector3d());

        Vector3d worldSlideAxis = aRigidReader.localToWorldNoScaleDir(localSliderAxis, new Vector3d());


        Vector3d targetAttBPos;
        Double fixedDistVal = fixedDistance.get();
        if (fixedDistVal == null) {
            // 计算相对位置在滑动轴上的投影
            Vector3d relativePos = worldAttBPos.sub(worldAttAPos, new Vector3d());

            EzDebug.log("worldAttAPos:" + worldAttAPos + ", worldAttBPos:" + worldAttBPos + ", relative:" + relativePos);


            double projectedLength = relativePos.dot(worldSlideAxis);

            // 施加位置约束（保留相对位置在轴上的分量）
            targetAttBPos = worldSlideAxis.mul(projectedLength, new Vector3d()).add(worldAttAPos);
        } else {
            targetAttBPos = worldSlideAxis.mul(fixedDistVal, new Vector3d()).add(worldAttAPos);
        }


        // 使用PBD位置修正（混合系数控制刚度）
        //Vector3d correctedAttBPos = worldAttBPos.lerp(targetAttBPos, sniffness, new Vector3d());
        bRigidWriter.moveLocalPosToWorld(localAttBPos, targetAttBPos);
    }
}
*/