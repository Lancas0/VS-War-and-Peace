package com.lancas.vswap.subproject.sandbox.constraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vswap.subproject.sandbox.component.behviour.SandBoxRigidbody;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vswap.subproject.sandbox.constraint.base.AbstractBiConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.base.IOrientationConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.base.ISliderConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.base.ISliderOrientationConstraint;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.UUID;

public class SliderOrientationConstraint extends AbstractBiConstraint implements ISliderOrientationConstraint {
    @JsonIgnore
    private final Object mutex = new Object();

    private final Vector3d localBaseAttPos = new Vector3d();
    private final Vector3d localTargetAttPos = new Vector3d();

    private final Quaterniond localBaseRot = new Quaterniond();
    private final Quaterniond invLocalTargetRot = new Quaterniond();

    private final Vector3d localBaseSliderAxis = new Vector3d();
    private double fixedLength = 0;

    private SliderOrientationConstraint() { super(null, null, null); }
    public SliderOrientationConstraint(
        UUID selfUuid, UUID inBaseUuid, UUID inTargetUuid,
        Vector3dc inLocalBaseAttPos, Vector3dc inLocalTargetAttPos,
        Quaterniondc inLocalBaseRot, Quaterniondc inLocalTargetRot,
        Vector3dc sliderAxisInALocal
    ) {
        super(selfUuid, inBaseUuid, inTargetUuid);
        localBaseAttPos.set(inLocalBaseAttPos); localTargetAttPos.set(inLocalTargetAttPos);
        localBaseRot.set(inLocalBaseRot); invLocalTargetRot.set(inLocalTargetRot).invert();
        sliderAxisInALocal.normalize(localBaseSliderAxis);
    }

    @Override
    public void setTargetLocalRot(Quaterniondc inLocalTargetRot) {
        synchronized (mutex) { invLocalTargetRot.set(inLocalTargetRot).invert(); }
    }

    @Override
    public void setFixedDistance(Double inFixedDist) {
        synchronized (mutex) { fixedLength = inFixedDist; }
    }
    public SliderOrientationConstraint withFixedDistance(Double inFixedDist) {
        setFixedDistance(inFixedDist);
        return this;
    }

    @Override
    public void addFixedDistance(double addition) {
        synchronized (mutex) { fixedLength += addition; }
    }

    @Override
    public void project(ISandBoxWorld<?> world, SandBoxConstraintSolver constraintSolver) {
        if (!localBaseRot.isFinite() || !invLocalTargetRot.isFinite() || !localBaseAttPos.isFinite() || !localTargetAttPos.isFinite()) {
            EzDebug.warn("rot or pos is not finite!");
            return;
        }

        ISandBoxShip aShip = getA(world);
        ISandBoxShip bShip = getB(world);

        if (aShip == null || bShip == null) {
            EzDebug.warn("[OrientationConstraint] can't find ship");
            return;
        }

        RigidbodyData aRigid = SandBoxRigidbody.resolveRigidData(aUuid, constraintSolver);
        RigidbodyData bRigid = SandBoxRigidbody.resolveRigidData(bUuid, constraintSolver);
        //

        if (aRigid == null || bRigid == null) {
            //Fall back
            IRigidbodyDataReader aRigidReader = aShip.getRigidbody().getDataReader();
            IRigidbodyDataWriter bRigidWriter = bShip.getRigidbody().getDataWriter();

            synchronized (mutex) {
                Quaterniond targetSaRot = new Quaterniond(aRigidReader.getRotation()).mul(localBaseRot).mul(invLocalTargetRot);
                bRigidWriter.setRotation(targetSaRot);

                Vector3d worldSlideAxis = aRigidReader.localToWorldNoScaleDir(localBaseSliderAxis, new Vector3d());
                Vector3d worldBaseAttPos = aRigidReader.localToWorldPos(localBaseAttPos, new Vector3d());
                Vector3d newWorldTargetPos = worldSlideAxis.mul(fixedLength, new Vector3d()).add(worldBaseAttPos);
                bRigidWriter.moveLocalPosToWorld(localTargetAttPos, newWorldTargetPos);
            }
        } else {
            synchronized (mutex) {
                Quaterniond targetSaRot = new Quaterniond(aRigid.transform.rotation).mul(localBaseRot).mul(invLocalTargetRot);
                bRigid.setRotImmediately(targetSaRot);

                Vector3d worldSlideAxis = aRigid.localToWorldNoScaleDir(localBaseSliderAxis, new Vector3d());
                Vector3d worldBaseAttPos = aRigid.localToWorldPos(localBaseAttPos, new Vector3d());
                Vector3d newWorldTargetPos = worldSlideAxis.mul(fixedLength, new Vector3d()).add(worldBaseAttPos);


                Vector3d transformedPos = bRigid.localToWorldPos(localTargetAttPos, new Vector3d());
                Vector3d movement = newWorldTargetPos.sub(transformedPos, new Vector3d());
                //the update pos is done sequently, don't worry the concurrent
                bRigid.transform.translate(movement);

                //bRigid.moveLocalPosToWorld(localTargetAttPos, newWorldTargetPos);
            }
        }

    }
}
