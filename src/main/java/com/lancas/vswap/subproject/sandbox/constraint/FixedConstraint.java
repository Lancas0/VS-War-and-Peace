package com.lancas.vswap.subproject.sandbox.constraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vswap.subproject.sandbox.constraint.base.AbstractBiConstraint;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.UUID;

public class FixedConstraint extends AbstractBiConstraint {
    @JsonIgnore
    private final Object mutex = new Object();

    private final Vector3d localAttAPos = new Vector3d();
    private final Vector3d localAttBPos = new Vector3d();

    private final Quaterniond localARot = new Quaterniond();
    private final Quaterniond invLocalBRot = new Quaterniond();

    private FixedConstraint() { super(null, null, null); }
    public FixedConstraint(UUID inSelfUuid, UUID inAUuid, UUID inBUuid, Vector3dc inLocalAttAPos, Vector3dc inLocalAttBPos, Quaterniondc inLocalARot, Quaterniondc inLocalBRot) {
        super(inSelfUuid, inAUuid, inBUuid);

        localAttAPos.set(inLocalAttAPos); localAttBPos.set(inLocalAttBPos);
        localARot.set(inLocalARot); invLocalBRot.set(inLocalBRot).invert();
    }

    public FixedConstraint setLocalAttAPos(Vector3dc p) {
        synchronized (mutex) { localAttAPos.set(p); }
        return this;
    }
    public FixedConstraint setLocalAttBPos(Vector3dc p) {
        synchronized (mutex) { localAttBPos.set(p); }
        return this;
    }

    public FixedConstraint setLocalBRot(Quaterniond q) {
        synchronized (mutex) { invLocalBRot.set(q).invert(); }
        return this;
    }
    public FixedConstraint setLocalARot(Quaterniond q) {
        synchronized (mutex) { localARot.set(q).invert(); }
        return this;
    }



    @Override
    public void project(ISandBoxWorld<?> world) {
        if (!localARot.isFinite() || !invLocalBRot.isFinite() || !localAttAPos.isFinite() || !localAttBPos.isFinite()) {
            EzDebug.warn("rot or pos is not finite!");
            return;
        }

        ISandBoxShip aShip = getA(world);
        ISandBoxShip bShip = getB(world);

        if (aShip == null || bShip == null) {
            EzDebug.warn("[OrientationConstraint] can't find ship");
            return;
        }

        IRigidbodyDataReader aRigidReader = aShip.getRigidbody().getDataReader();
        IRigidbodyDataWriter bRigidWriter = bShip.getRigidbody().getDataWriter();

        synchronized (mutex) {
            Quaterniond targetSaRot = new Quaterniond(aRigidReader.getRotation()).mul(localARot).mul(invLocalBRot);
            bRigidWriter.setRotation(targetSaRot);

            Vector3d worldAttAPos = aRigidReader.localToWorldPos(localAttAPos, new Vector3d());
            bRigidWriter.moveLocalPosToWorld(localAttBPos, worldAttAPos);
        }
    }
}



