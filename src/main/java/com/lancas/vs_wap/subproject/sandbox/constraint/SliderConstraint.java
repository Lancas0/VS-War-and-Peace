package com.lancas.vs_wap.subproject.sandbox.constraint;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vs_wap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vs_wap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vs_wap.subproject.sandbox.constraint.base.AbstractBiConstraint;
import com.lancas.vs_wap.subproject.sandbox.constraint.base.ISliderConstraint;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vs_wap.util.JomlUtil;
import com.simibubi.create.CreateClient;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;


public class SliderConstraint extends AbstractBiConstraint implements ISliderConstraint {
    //private UUID constraintUuid;
    //private UUID bodyAuuid, bodyBuuid;
    private final Vector3d localAttAPos = new Vector3d();
    private final Vector3d localAttBPos = new Vector3d();
    private final Vector3d localSliderAxis = new Vector3d();
    private final AtomicReference<Double> fixedDistance = new AtomicReference<>(null);  //null for don't fix
    //private double maxDistan
    private volatile double frictionFactor = 0;
    private volatile double sniffness = 1;  //todo limit 0 - 1

    private SliderConstraint() { super(null, null, null); }
    public SliderConstraint(UUID selfUuid, UUID inBodyAuuid, UUID inBodyBuuid, Vector3dc inLocalAPos, Vector3dc inLocalBPos, Vector3dc sliderAxisInALocal) {
        super(selfUuid, inBodyAuuid, inBodyBuuid);
        //constraintUuid = selfUuid;
        //bodyAuuid = inBodyAuuid; bodyBuuid = inBodyBuuid;
        localAttAPos.set(inLocalAPos); localAttBPos.set(inLocalBPos);
        sliderAxisInALocal.normalize(localSliderAxis);
    }
    public SliderConstraint(UUID selfUuid, UUID inBodyAuuid, UUID inBodyBuuid, Vector3dc inLocalAPos, Vector3dc inLocalBPos, Vector3dc sliderAxisInALocal, double inFrictionFactor) {
        super(selfUuid, inBodyAuuid, inBodyBuuid);
        //constraintUuid = selfUuid;
        //bodyAuuid = inBodyAuuid; bodyBuuid = inBodyBuuid;
        localAttAPos.set(inLocalAPos); localAttBPos.set(inLocalBPos);
        sliderAxisInALocal.normalize(localSliderAxis);
        frictionFactor = inFrictionFactor;
    }
    public SliderConstraint(UUID selfUuid, UUID inBodyAuuid, UUID inBodyBuuid, Vector3dc inLocalAPos, Vector3dc inLocalBPos, Vector3dc sliderAxisInALocal, double inFrictionFactor, double inFixedDist) {
        super(selfUuid, inBodyAuuid, inBodyBuuid);
        //constraintUuid = selfUuid;
        //bodyAuuid = inBodyAuuid; bodyBuuid = inBodyBuuid;
        localAttAPos.set(inLocalAPos); localAttBPos.set(inLocalBPos);
        sliderAxisInALocal.normalize(localSliderAxis);
        frictionFactor = inFrictionFactor;
        fixedDistance.set(inFixedDist);
    }


    @Override
    public void project(ISandBoxWorld<?> world) {
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
        Vector3d correctedAttBPos = worldAttBPos.lerp(targetAttBPos, sniffness, new Vector3d());
        bRigidWriter.moveLocalPosToWorld(localAttBPos, correctedAttBPos);

        //EzDebug.log("worldAttAPos:" + worldAttAPos + ", worldAttBPos:" + worldAttBPos + ", localAttBPos:" + localAttBPos + "\nshipBLocalToWorld:" + shipB.getRigidbody().getDataReader().getLocalToWorld());
        //EzDebug.log("worldAttAPos:" + worldAttAPos + ", worldAttBPos:" + worldAttBPos + ", localAttAPos:" + localAttAPos + "localBAttPos:" + localAttBPos);
        //CreateClient.OUTLINER.showAABB("slider-a-worldAtt", JomlUtil.centerExtended(worldAttAPos, 0.5)).lineWidth(1/8f).colored(255);
        //CreateClient.OUTLINER.showAABB("slider-b-worldAtt", JomlUtil.centerExtended(worldAttBPos, 0.5)).lineWidth(1/8f).colored(500);

        // todo 摩擦处理：沿滑动轴的速度阻尼
        /*Vector3d relativeVel = bRigidReader.getVelocity().sub(aRigidReader.getVelocity(), new Vector3d());
        double velAlongAxis = relativeVel.dot(worldSlideAxis);
        bodyB.velocity = bodyB.velocity.sub(worldSlideAxis.mul(velAlongAxis * friction));*/
    }

    @Override
    public void setFixedDistance(@Nullable Double inFixedDist) {
        fixedDistance.set(inFixedDist);
    }
    @Override
    public void addFixedDistance(double addition) {
        fixedDistance.accumulateAndGet(addition, Double::sum);
    }
}
