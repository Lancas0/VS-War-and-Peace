package com.lancas.vswap.subproject.sandbox.compact.vs.constraint;

/*
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

public class FixedOnVsConstraint extends AbstractSaOnVsConstraint {
    @JsonIgnore
    private final Object mutex = new Object();

    private final Vector3d localAttAPos = new Vector3d();
    private final Vector3d localAttBPos = new Vector3d();

    private final Quaterniond localARot = new Quaterniond();
    private final Quaterniond invLocalBRot = new Quaterniond();

    private double fixedDistance = 0;

    private FixedOnVsConstraint() { super(null, -1, null); }
    public FixedOnVsConstraint(UUID inSelfUuid, long inVsId, long inSaUuid, Vector3dc inVsAttPosInShip, Vector3dc inLocalAttSaPos, Quaterniondc inLocalARot, Quaterniondc inLocalBRot) {
        super(inSelfUuid, inVsId, inSaUuid);

        localAttAPos.set(inVsAttPosInShip); localAttBPos.set(inLocalAttSaPos);
        localARot.set(inLocalARot); invLocalBRot.set(inLocalBRot).invert();
    }
    public FixedOnVsConstraint setFixed

    public void setLocalAttAPos(Vector3dc p) {
        synchronized (mutex) { localAttAPos.set(p); }
    }
    public void setLocalAttBPos(Vector3dc p) {
        synchronized (mutex) { localAttBPos.set(p); }
    }

    public void setLocalBRot(Quaterniond q) {
        synchronized (mutex) { invLocalBRot.set(q).invert(); }
    }
    public void setLocalARot(Quaterniond q) {
        synchronized (mutex) { localARot.set(q).invert(); }
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
*/