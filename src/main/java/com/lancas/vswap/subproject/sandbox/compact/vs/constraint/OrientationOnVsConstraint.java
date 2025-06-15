package com.lancas.vswap.subproject.sandbox.compact.vs.constraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vswap.subproject.sandbox.constraint.base.IOrientationConstraint;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.UUID;

public class OrientationOnVsConstraint extends AbstractSaOnVsConstraint implements IOrientationConstraint {
    @JsonIgnore
    private final Object mutex = new Object();

    private final Quaterniond localVsRot = new Quaterniond();
    private final Quaterniond invLocalSaRot = new Quaterniond();

    private OrientationOnVsConstraint() { super(null, -1, null); }
    public OrientationOnVsConstraint(UUID inSelfUuid, long inVsShipId, UUID inSaShipUuid, Quaterniondc inLocalVsRot, Quaterniondc inLocalSaRot) {
        super(inSelfUuid, inVsShipId, inSaShipUuid);
        localVsRot.set(inLocalVsRot);
        invLocalSaRot.set(inLocalSaRot).invert();
    }

    @Override
    public void setTargetLocalRot(Quaterniondc inLocalRot) {
        synchronized (mutex) {
            invLocalSaRot.set(inLocalRot).invert();
        }
    }

    @Override
    public void project(ISandBoxWorld<?> world) {
        Ship vsShip = getVsShip();
        if (vsShip == null) return;

        ISandBoxShip saShip = getSaShip(world);
        if (saShip == null) {
            EzDebug.warn("[OrientationOnVsConstraint]can't get sa ship, will remove this constraint");
            world.getConstraintSolver().markConstraintRemoved(this.selfUuid);
            return;
        }

        synchronized (mutex) {
            if (!localVsRot.isFinite() || !invLocalSaRot.isFinite()) {
                EzDebug.warn("localRot is not finite!");
                return;
            }

            IRigidbodyDataReader rigidReader = saShip.getRigidbody().getDataReader();
            IRigidbodyDataWriter rigidWriter = saShip.getRigidbody().getDataWriter();

            Quaterniond targetSaRot = new Quaterniond(vsShip.getTransform().getShipToWorldRotation()).mul(localVsRot).mul(invLocalSaRot);
            rigidWriter.setRotation(targetSaRot);
        }

        /*// 计算期望的全局方向
        Quaterniond desiredWorldRot = vsShip.getTransform().getShipToWorldRotation().mul(localVsRot, new Quaterniond()); // R_A * qA_local
        // 计算bodyB当前的实际全局方向
        Quaterniond currentWorldRot = rigidReader.getRotation().mul(localSaRot, new Quaterniond()); // R_B * qB_local

        // 计算误差四元数：currentBGlobal -> desiredGlobal
        Quaterniond qError = new Quaterniond(desiredWorldRot).conjugate().mul(currentWorldRot);

        // 转换为轴角形式
        //Vector3d axis = new Vector3d();
        //double[] angle = new double[1];
        AxisAngle4d axisAngle = new AxisAngle4d();
        qError.get(axisAngle);
        //angle[0] = normalizeAngle(angle[0]); // 限制到[-π, π]

        if (Math.abs(axisAngle.angle) < 1e-6) return;

        // 计算bodyB的旋转惯性权重
        //double wB = 1.0 / bodyB.getRotationalInertia(axis);

        // 计算修正量（仅作用于bodyB）
        //double lambda = angle[0] * stiffness * wB;

        // 应用旋转修正到bodyB
        Quaterniond deltaQ = new Quaterniond().setAxisAngle(axis, -lambda);
        bodyB.getRotation().premul(deltaQ);*/
    }
}
