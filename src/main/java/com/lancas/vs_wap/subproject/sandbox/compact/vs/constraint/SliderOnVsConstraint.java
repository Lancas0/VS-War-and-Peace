package com.lancas.vs_wap.subproject.sandbox.compact.vs.constraint;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vs_wap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vs_wap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vs_wap.subproject.sandbox.constraint.base.ISliderConstraint;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vs_wap.util.JomlUtil;
import com.simibubi.create.CreateClient;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class SliderOnVsConstraint extends AbstractSaOnVsConstraint implements ISliderConstraint {
    private final Vector3d vsShipPos = new Vector3d();
    private final Vector3d saLocalPos = new Vector3d();
    private final Vector3d localVsSliderAxis = new Vector3d();
    private final AtomicReference<Double> fixedDistance = new AtomicReference<>(null);  //null for don't fix
    //private double maxDistan
    private volatile double frictionFactor = 0;  //todo do firction
    private volatile double sniffness = 1;  //todo limit 0 - 1

    private SliderOnVsConstraint() { super(null, -1, null); }
    public SliderOnVsConstraint(UUID inSelfUuid, long inVsShipId, UUID inSaShipUuid, Vector3dc inVsShipPos, Vector3dc inSaLocalPos, Vector3dc inVsLocalSliderAxis) {
        super(inSelfUuid, inVsShipId, inSaShipUuid);
        vsShipPos.set(inVsShipPos);
        saLocalPos.set(inSaLocalPos);
        localVsSliderAxis.set(inVsLocalSliderAxis);
    }

    @Override
    public void setFixedDistance(Double inFixedDist) {
        fixedDistance.set(inFixedDist);
    }
    @Override
    public void addFixedDistance(double addition) {
        fixedDistance.updateAndGet(v -> {
            double prev = (v == null ? 0 : v);
            return prev + addition;
        });
    }

    @Override
    public UUID getUuid() { return selfUuid; }

    @Override
    public void project(ISandBoxWorld<?> world) {
        Ship curVsShipCache = getVsShip();
        if (curVsShipCache == null) return;  //maybe it's not ready

        //EzDebug.light("projecting slider att");
        if (!localVsSliderAxis.isFinite()) {
            EzDebug.warn("the local slider axis is not valid!");
            return;  //todo remove constraint
        }

        //ISandBoxShip shipA = world.getShipIncludeVSAndGround(bodyAuuid);
        //ISandBoxShip saShip = world.getShip(saShipUuid);
        //EzDebug.log("a is ground:" + bodyAuuid.equals(world.wrapOrGetGround().getUuid()) + ", b is ground:" + bodyBuuid.equals(world.wrapOrGetGround().getUuid()));
        ISandBoxShip saShip = getSaShip(world);
        if (saShip == null) {
            //EzDebug.log("shipA uuid:" +  bodyAuuid + ", world ground:" + world.warpOrGetGround().getUuid());
            EzDebug.warn("can't find constrainted sa ship, will remove this constraint");
            world.getConstraintSolver().markConstraintRemoved(this.selfUuid);
            return;
        }

        if (saShip.getRigidbody().getDataReader().isStatic()) return;  //no need to apply constraint

        IRigidbodyDataReader rigidReader = saShip.getRigidbody().getDataReader();
        IRigidbodyDataWriter rigidWriter = saShip.getRigidbody().getDataWriter();

        Vector3d worldVsPos = curVsShipCache.getShipToWorld().transformPosition(vsShipPos, new Vector3d());
        Vector3d worldSaPos = rigidReader.localToWorldPos(saLocalPos, new Vector3d());

        Vector3d worldSlideAxis = curVsShipCache.getTransform().getShipToWorldRotation().transform(localVsSliderAxis, new Vector3d());


        Vector3d targetWorldSaPos;
        Double fixedDistVal = fixedDistance.get();
        if (fixedDistVal == null) {
            // 计算相对位置在滑动轴上的投影
            Vector3d relativePos = worldVsPos.sub(worldSaPos, new Vector3d());
            //EzDebug.log("worldAttAPos:" + worldAttAPos + ", worldAttBPos:" + worldAttBPos + ", relative:" + relativePos);
            double projectedLength = relativePos.dot(worldSlideAxis);

            // 施加位置约束（保留相对位置在轴上的分量）
            targetWorldSaPos = worldSlideAxis.mul(projectedLength, new Vector3d()).add(worldVsPos);
        } else {
            targetWorldSaPos = worldSlideAxis.mul(fixedDistVal, new Vector3d()).add(worldVsPos);
        }


        // 使用PBD位置修正（混合系数控制刚度）
        Vector3d correctedAttBPos = worldVsPos.lerp(targetWorldSaPos, sniffness, new Vector3d());
        rigidWriter.moveLocalPosToWorld(saLocalPos, correctedAttBPos);

        //EzDebug.log("saWorldPos:" + worldSaPos + "saLocalPos:" + saLocalPos + "\n saLocalToWorld:" + rigidReader.getLocalToWorld() + ", correctedAttBPos:" + correctedAttBPos);
        //EzDebug.log("vsWorldPos:" + worldVsPos + ", saWorldPos:" + worldSaPos + ", vsShipPos:" + vsShipPos + "\nsaLocalPos:" + saLocalPos);
        //EzDebug.log("worldAttAPos:" + worldAttAPos + ", worldAttBPos:" + worldAttBPos + ", localAttAPos:" + localAttAPos + "localBAttPos:" + localAttBPos);
        CreateClient.OUTLINER.showAABB("slider-a-worldAtt" + selfUuid, JomlUtil.centerExtended(worldVsPos, 0.5)).lineWidth(1/16f).colored(255);
        CreateClient.OUTLINER.showAABB("slider-b-worldAtt" + selfUuid, JomlUtil.centerExtended(worldSaPos, 0.5)).lineWidth(1/8f).colored(500);

        // todo 摩擦处理：沿滑动轴的速度阻尼
        /*Vector3d relativeVel = bRigidReader.getVelocity().sub(aRigidReader.getVelocity(), new Vector3d());
        double velAlongAxis = relativeVel.dot(worldSlideAxis);
        bodyB.velocity = bodyB.velocity.sub(worldSlideAxis.mul(velAlongAxis * friction));*/
    }
}
