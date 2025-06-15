package com.lancas.vswap.subproject.sandbox.compact.vs.constraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vswap.subproject.sandbox.component.data.reader.IRigidbodyDataReader;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vswap.subproject.sandbox.constraint.base.IOrientationConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.base.ISliderConstraint;
import com.lancas.vswap.subproject.sandbox.constraint.base.ISliderOrientationConstraint;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class SliderOrientationOnVsConstraint extends AbstractSaOnVsConstraint implements ISliderOrientationConstraint {
    @JsonIgnore
    private final Object mutex = new Object();

    private final Vector3d baseAttPosInShip = new Vector3d();
    private final Vector3d localTargetAttPos = new Vector3d();

    private final Vector3d baseAxisInShip = new Vector3d();
    private double fixedLength = 0;

    private final Quaterniond baseRotInShip = new Quaterniond();
    private final Quaterniond invLocalTargetRot = new Quaterniond();

    private SliderOrientationOnVsConstraint() { super(null, -1, null); }
    public SliderOrientationOnVsConstraint(
        UUID inSelfUuid, long inBaseId, UUID inTargetUuid,
        Vector3dc inBaseAttPosInShip, Vector3dc inLocalTargetAttPos,
        Quaterniondc inBaseRotInShip, Quaterniondc inLocalTargetRot,
        Vector3dc inVsLocalSliderAxis) {
        super(inSelfUuid, inBaseId, inTargetUuid);
        baseAttPosInShip.set(inBaseAttPosInShip); localTargetAttPos.set(inLocalTargetAttPos);
        baseRotInShip.set(inBaseRotInShip); invLocalTargetRot.set(inLocalTargetRot).invert();
        baseAxisInShip.set(inVsLocalSliderAxis);
    }

    @Override
    public void setFixedDistance(Double inFixedDist) {
         synchronized (mutex) { fixedLength = inFixedDist; }
    }
    @Override
    public void addFixedDistance(double addition) {
        synchronized (mutex) { fixedLength += addition; }
    }
    @Override
    public void setTargetLocalRot(Quaterniondc inLocalTargetRot) {
        synchronized (mutex) { invLocalTargetRot.set(inLocalTargetRot).invert(); }
    }

    @Override
    public UUID getUuid() { return selfUuid; }

    @Override
    public void project(ISandBoxWorld<?> world) {
        Ship curVsShipCache = getVsShip();
        if (curVsShipCache == null) return;  //maybe it's not ready

        //EzDebug.light("projecting slider att");
        if (!baseAxisInShip.isFinite()) {
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

        Vector3d worldVsPos = curVsShipCache.getShipToWorld().transformPosition(baseAttPosInShip, new Vector3d());
        Vector3d worldSaPos = rigidReader.localToWorldPos(localTargetAttPos, new Vector3d());

        synchronized (mutex) {
            Quaterniond targetSaRot = new Quaterniond(curVsShipCache.getTransform().getShipToWorldRotation()).mul(baseRotInShip).mul(invLocalTargetRot);
            rigidWriter.setRotation(targetSaRot);

            Vector3d worldSlideAxis = curVsShipCache.getTransform().getShipToWorldRotation().transform(baseAxisInShip, new Vector3d());
            Vector3d targetWorldSaPos;
            targetWorldSaPos = worldSlideAxis.mul(fixedLength, new Vector3d()).add(worldVsPos);
            rigidWriter.moveLocalPosToWorld(localTargetAttPos, targetWorldSaPos);
        }

    }
}
