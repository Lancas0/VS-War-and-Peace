package com.lancas.vswap.subproject.sandbox.constraint;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vswap.subproject.sandbox.constraint.base.AbstractBiConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.base.IOrientationConstraint;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;

import java.util.UUID;

public class OrientationConstraint extends AbstractBiConstraint implements IOrientationConstraint {
    @JsonIgnore
    private final Object mutex = new Object();

    private final Quaterniond localARot = new Quaterniond();
    private final Quaterniond invLocalBRot = new Quaterniond();

    private OrientationConstraint() { super(null, null, null); }
    public OrientationConstraint(UUID inSelfUuid, UUID inAUuid, UUID inBUuid, Quaterniondc inLocalARot, Quaterniondc inLocalBRot) {
        super(inSelfUuid, inAUuid, inBUuid);
        localARot.set(inLocalARot);
        invLocalBRot.set(inLocalBRot).invert();
    }

    @Override
    public void setTargetLocalRot(Quaterniondc inLocalRot) { synchronized (mutex) { invLocalBRot.set(inLocalRot).invert(); } }

    @Override
    public void project(ISandBoxWorld<?> world, SandBoxConstraintSolver solver) {
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

        synchronized (mutex) {
            if (!localARot.isFinite() || !invLocalBRot.isFinite()) {
                EzDebug.warn("localRot is not finite!");
                return;
            }

            Quaterniond targetSaRot = new Quaterniond(aRigidReader.getRotation()).mul(localARot).mul(invLocalBRot);
            bRigidWriter.setRotation(targetSaRot);
        }
    }
}
