package com.lancas.vs_wap.subproject.sandbox.constraint;


import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vs_wap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vs_wap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vs_wap.subproject.sandbox.constraint.base.AbstractBiConstraint;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;

import java.util.UUID;

public class OrientationConstraint extends AbstractBiConstraint {
    private final Quaterniond localARot = new Quaterniond();
    private final Quaterniond invLocalBRot = new Quaterniond();

    public OrientationConstraint(UUID inSelfUuid, UUID inAUuid, UUID inBUuid, Quaterniondc inLocalARot, Quaterniondc inLocalBRot) {
        super(inSelfUuid, inAUuid, inBUuid);
        localARot.set(inLocalARot);
        invLocalBRot.set(inLocalBRot).invert();
    }

    @Override
    public void project(ISandBoxWorld<?> world) {
        if (!localARot.isFinite() || !invLocalBRot.isFinite()) {
            EzDebug.warn("localRot is not finite!");
            return;
        }

        ISandBoxShip aShip = getA(world);
        ISandBoxShip bShip = getB(world);

        if (aShip == null || bShip == null) {
            EzDebug.warn("[OrientationConstraint] can't find ship");
            return;
        }

        IRigidbodyDataReader aRigidReader = aShip.getRigidbody().getDataReader();
        //IRigidbodyDataWriter aRigidWriter = aShip.getRigidbody().getDataWriter();
        //IRigidbodyDataReader bRigidReader = bShip.getRigidbody().getDataReader();
        IRigidbodyDataWriter bRigidWriter = bShip.getRigidbody().getDataWriter();

        Quaterniond targetSaRot = new Quaterniond(aRigidReader.getRotation()).mul(localARot).mul(invLocalBRot);
        bRigidWriter.setRotation(targetSaRot);
    }
}
