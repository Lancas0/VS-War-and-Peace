package com.lancas.vswap.subproject.sandbox.constraint;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vswap.subproject.sandbox.constraint.base.AbstractBiConstraint;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.UUID;

public class HingeConstraint extends AbstractBiConstraint {
    private final Object mutex = new Object();
    private final Vector3d localAxisA = new Vector3d();
    private final Vector3d localAxisB = new Vector3d();

    private HingeConstraint() { super(null, null, null); }

    public HingeConstraint(UUID selfUuid, UUID aUuid, UUID bUuid,
                           Vector3dc localAxisA, Vector3dc localAxisB) {
        super(selfUuid, aUuid, bUuid);
        this.localAxisA.set(localAxisA).normalize();
        this.localAxisB.set(localAxisB).normalize();
    }

    public HingeConstraint(UUID selfUuid, UUID aUuid, UUID bUuid,
                           Vector3dc localAxisA, Vector3dc localAxisB, double stiffness) {
        this(selfUuid, aUuid, bUuid, localAxisA, localAxisB);
        //this.stiffness = Math.min(1.0, Math.max(0.0, stiffness));
    }

    @Override
    public void project(ISandBoxWorld<?> world, SandBoxConstraintSolver solver) {
        ISandBoxShip shipA = getA(world);
        ISandBoxShip shipB = getB(world);

        if (shipA == null || shipB == null) {
            EzDebug.warn("[HingeConstraint] Can't find constrained ships");
            return;
        }
        if (shipB.getRigidbody().getDataReader().isStatic()) return;

        IRigidbodyDataReader aReader = shipA.getRigidbody().getDataReader();
        IRigidbodyDataReader bReader = shipB.getRigidbody().getDataReader();
        IRigidbodyDataWriter bWriter = shipB.getRigidbody().getDataWriter();

        synchronized (mutex) {
            // 获取世界坐标系中的轴向量
            Vector3d axisAWorld = aReader.localToWorldNoScaleDir(localAxisA, new Vector3d()).normalize();
            Vector3d axisBWorld = bReader.localToWorldNoScaleDir(localAxisB, new Vector3d()).normalize();

            // 检查是否已对齐
            double dot = axisBWorld.dot(axisAWorld);
            if (Math.abs(dot) > 0.999) return;  // 阈值检查避免抖动

            // 计算旋转校正 (将B轴旋转到A轴方向)
            Quaterniond correctionRot = new Quaterniond().rotationTo(axisBWorld, axisAWorld);

            // 计算目标旋转并应用刚度系数
            Quaterniond targetRot = correctionRot.mul(bReader.getRotation(), new Quaterniond());
            //Quaterniond newRot = new Quaterniond();
            //bReader.getRotation().slerp(targetRot, stiffness, newRot);

            bWriter.setRotation(targetRot);
        }
    }
}
