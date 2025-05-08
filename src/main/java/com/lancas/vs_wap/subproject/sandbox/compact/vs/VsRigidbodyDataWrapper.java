package com.lancas.vs_wap.subproject.sandbox.compact.vs;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.ship.attachment.ForcesInducer;
import com.lancas.vs_wap.ship.helper.builder.TeleportDataBuilder;
import com.lancas.vs_wap.subproject.sandbox.api.ISavedObject;
import com.lancas.vs_wap.subproject.sandbox.api.data.ITransformPrimitive;
import com.lancas.vs_wap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vs_wap.subproject.sandbox.component.data.IRigidbodyData;
import com.lancas.vs_wap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vs_wap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.joml.*;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

public class VsRigidbodyDataWrapper implements IRigidbodyData, ISavedObject<VsRigidbodyDataWrapper> {
    @Override
    public CompoundTag saved() {
        /*return new NbtBuilder()
            .putNumber("mass", massSnapshot)
            .putVector3d("world_mass_center", worldMassCenterSnapshot)
            .putMatrix3d("inertia", inertiaSnapshot)
            .putCompound("transform", transformSnapshot.saved())
            .putVector3d("velocity", velocitySnapshot)
            .putVector3d("omega", omegaSnapshot)
            .putBoolean("static", staticSnapshot)
            .get();*/
        return new CompoundTag();
    }
    @Override
    public VsRigidbodyDataWrapper load(CompoundTag tag) {
        /*NbtBuilder.modify(tag)
            .readDoubleDo("mass", v -> massSnapshot = v)
            .readVector3d("world_mass_center", worldMassCenterSnapshot)
            .readMatrix3d("inertia", inertiaSnapshot)
            .readCompoundDo("transform", transformSnapshot::load)
            .readVector3d("velocity", velocitySnapshot)
            .readVector3d("omega", omegaSnapshot)
            .readBooleanDo("static", v -> staticSnapshot = v);
        return this;*/
        return this;
    }

    public VsRigidbodyDataWrapper copyData(VsRigidbodyDataWrapper src) {
        /*massSnapshot = src.massSnapshot;
        worldMassCenterSnapshot.set(src.worldMassCenterSnapshot);
        inertiaSnapshot.set(src.inertiaSnapshot);

        transformSnapshot.set(src.transformSnapshot);
        velocitySnapshot.set(src.velocitySnapshot);
        omegaSnapshot.set(src.omegaSnapshot);

        staticSnapshot = src.staticSnapshot;*/
        return this;
    }
    public VsRigidbodyDataWrapper copyData(RigidbodyData src) {
        /*massSnapshot = src.mass;
        worldMassCenterSnapshot.set(src.getWorldMassCenter(new Vector3d()));
        inertiaSnapshot.set(src.getLocalInertia());

        transformSnapshot.set(src.transform);
        velocitySnapshot.set(src.velocity);
        omegaSnapshot.set(src.omega);

        staticSnapshot = src.isStatic();*/
        return this;
    }


    @FunctionalInterface
    public interface VsRigidbodyUpdater {
        public void update(Level level, Ship vsShip);
    }

    public static final Vector3dc VS_GRAVITY = new Vector3d(0, -10, 0);
    /*

    public volatile double massSnapshot;
    public final Vector3d worldMassCenterSnapshot = new Vector3d();
    public final Matrix3d inertiaSnapshot = new Matrix3d();

    public final TransformPrimitive transformSnapshot = new TransformPrimitive();
    public final Vector3d velocitySnapshot = new Vector3d();
    public final Vector3d omegaSnapshot = new Vector3d();

    public volatile boolean staticSnapshot = false;*/

    public final Queue<VsRigidbodyUpdater> updates = new ConcurrentLinkedQueue<>();

    /*public void updateSnapshots(ServerShip ship) {
        ShipInertiaData inertiaData = ship.getInertiaData();
        massSnapshot = inertiaData.getMass();
        worldMassCenterSnapshot.set(ship.getTransform().getPositionInWorld());
        inertiaSnapshot.set(inertiaData.getMomentOfInertiaTensor());  //after seek into vs core, I guess it's local inertia

        ShipTransform transform = ship.getTransform();
        transformSnapshot.set(transform.getPositionInWorld(), transform.getShipToWorldRotation(), transform.getShipToWorldScaling());

        velocitySnapshot.set(ship.getVelocity());
        omegaSnapshot.set(ship.getOmega());

        staticSnapshot = ship.isStatic();
    }*/
    /*public void loadSandBoxRigidbodyData(RigidbodyData ship) {
        ShipInertiaData inertiaData = ship.getInertiaData();
        massSnapshot = inertiaData.getMass();
        worldMassCenterSnapshot.set(ship.getTransform().getPositionInWorld());
        inertiaSnapshot.set(inertiaData.getMomentOfInertiaTensor());  //after seek into vs core, I guess it's local inertia

        ShipTransform transform = ship.getTransform();
        transformSnapshot.set(transform.getPositionInWorld(), transform.getShipToWorldRotation(), transform.getShipToWorldScaling());

        velocitySnapshot.set(ship.getVelocity());
        omegaSnapshot.set(ship.getOmega());

        staticSnapshot = ship.isStatic();
    }*/
    Ship vsShipCache;
    public void setShipCache(Ship inVsShipCache) {
        vsShipCache = inVsShipCache;
    }


    //todo set some default value for client ship

    @Override
    public double getMass() { return vsShipCache instanceof ServerShip sShip ? sShip.getInertiaData().getMass() : 0; }
    @Override
    public Vector3d getLocalMassCenter(Vector3d dest) { return dest.set(vsShipCache.getTransform().getPositionInShip()); }  //todo maybe treat local geo center as local origin?
    @Override
    public Vector3d getWorldMassCenter(Vector3d dest) { return dest.set(vsShipCache.getTransform().getPositionInWorld()); }
    @Override
    public Matrix3dc getLocalInertia() { return vsShipCache instanceof ServerShip sShip ? sShip.getInertiaData().getMomentOfInertiaTensor() : new Matrix3d(); }

    @Override
    public Vector3dc getPosition() { return vsShipCache.getTransform().getPositionInWorld(); }
    @Override
    public Quaterniondc getRotation() { return vsShipCache.getTransform().getShipToWorldRotation(); }
    @Override
    public Vector3dc getScale() { return vsShipCache.getTransform().getShipToWorldScaling(); }
    @Override  //todo the getted transform's makeLocalToWorld or makeWorldToLocal is wrong!
    public ITransformPrimitive getTransform() { return TransformPrimitive.fromVsTransform(vsShipCache.getTransform()); }  //todo snapshot?
    @Override
    public Matrix4dc getLocalToWorld() { return vsShipCache.getShipToWorld(); }  //todo make a snapshot for localToWorld
    @Override
    public Matrix4dc getWorldToLocal() { return vsShipCache.getWorldToShip(); }  //todo make a snapshot for localToWorld

    @Override
    public Vector3dc getVelocity() { return vsShipCache.getVelocity(); }
    @Override
    public Vector3dc getOmega() { return vsShipCache.getOmega(); }
    @Override
    public Vector3dc getGravity() { return VS_GRAVITY; }
    @Override
    public Stream<Vector3dc> allForces() { return Stream.empty(); }
    @Override
    public Stream<Vector3dc> allTorques() { return Stream.empty(); }
    @Override
    public boolean isStatic() { return vsShipCache instanceof ServerShip sShip ? sShip.isStatic() : false; }

    @Override
    public IRigidbodyDataWriter setPosition(Vector3dc p) {
        Vector3d posImmutable = new Vector3d(p);
        updates.add((level, ship) -> {
            if (!(level instanceof ServerLevel sLevel) || !(ship instanceof ServerShip sShip)) {
                EzDebug.warn("client vs ship don't support change pos");
                return;
            }
            VSGameUtilsKt.getShipObjectWorld(sLevel).teleportShip(sShip, TeleportDataBuilder.copy(sLevel, sShip).withPos(posImmutable));
        });
        return this;
    }
    @Override
    public IRigidbodyDataWriter setRotation(Quaterniondc r) {
        Quaterniond rotImmutable = new Quaterniond(r);
        updates.add((level, ship) -> {
            if (!(level instanceof ServerLevel sLevel) || !(ship instanceof ServerShip sShip)) {
                EzDebug.warn("client vs ship don't support set rot");
                return;
            }
            VSGameUtilsKt.getShipObjectWorld(sLevel).teleportShip(sShip, TeleportDataBuilder.copy(sLevel, sShip).withRot(rotImmutable));
        });
        return this;
    }
    @Override
    public IRigidbodyDataWriter setScale(Vector3dc s) {
        Vector3d scaleImmutable = new Vector3d(s);
        updates.add((level, ship) -> {
            if (!(level instanceof ServerLevel sLevel) || !(ship instanceof ServerShip sShip)) {
                EzDebug.warn("client vs ship don't support set scale");
                return;
            }
            //todo 3d scale
            VSGameUtilsKt.getShipObjectWorld(sLevel).teleportShip(sShip, TeleportDataBuilder.copy(sLevel, sShip).withScale(scaleImmutable.x));
        });
        return this;
    }
    @Override
    public IRigidbodyDataWriter setTransform(ITransformPrimitive newTransform) {
        ITransformPrimitive transformImmutable = new TransformPrimitive(newTransform);
        updates.add((level, ship) -> {
            if (!(level instanceof ServerLevel sLevel) || !(ship instanceof ServerShip sShip)) {
                EzDebug.warn("client vs ship don't support set transform");
                return;
            }
            //todo 3d scale
            VSGameUtilsKt.getShipObjectWorld(sLevel).teleportShip(sShip,
                TeleportDataBuilder.copy(sLevel, sShip).withTransform(transformImmutable)
            );
        });
        return this;
    }

    @Override
    public IRigidbodyDataWriter setVelocity(Vector3dc v) {
        Vector3d velImmutable = new Vector3d(v);
        updates.add((level, ship) -> {
            if (!(level instanceof ServerLevel sLevel) || !(ship instanceof ServerShip sShip)) {
                EzDebug.warn("client vs ship don't support set vel");
                return;
            }
            //todo 3d scale
            VSGameUtilsKt.getShipObjectWorld(sLevel).teleportShip(sShip, TeleportDataBuilder.copy(sLevel, sShip).withVel(velImmutable));
        });
        return this;
    }
    @Override
    public IRigidbodyDataWriter setOmega(Vector3dc v) {
        Vector3d omegaImmutable = new Vector3d(v);
        updates.add((level, ship) -> {
            if (!(level instanceof ServerLevel sLevel) || !(ship instanceof ServerShip sShip)) {
                EzDebug.warn("client vs ship don't support set omega");
                return;
            }
            //todo 3d scale
            VSGameUtilsKt.getShipObjectWorld(sLevel).teleportShip(sShip, TeleportDataBuilder.copy(sLevel, sShip).withOmega(omegaImmutable));
        });
        return this;
    }

    @Override
    public IRigidbodyDataWriter setGravity(Vector3dc newGravity) {
        EzDebug.warn("vs ship don't support set gravity");
        return this;
    }
    @Override
    public IRigidbodyDataWriter setStatic(boolean newVal) {
        updates.add((level, ship) -> {
            if (!(ship instanceof ServerShip sShip)) {
                EzDebug.warn("client vs ship don't support set static");
                return;
            }
            //todo 3d scale
            sShip.setStatic(newVal);
        });
        return this;
    }

    @Override
    public IRigidbodyDataWriter applyWorldForce(Vector3dc f) {
        Vector3d forceImmutable = new Vector3d(f);
        updates.add((level, ship) -> {
            if (!(level instanceof ServerLevel sLevel) || !(ship instanceof LoadedServerShip sShip)) {
                EzDebug.warn("client or not loaded vs ship don't support set omega");
                return;
            }
            ForcesInducer.applyForce(sShip, forceImmutable);
        });
        return this;
    }
    @Override
    public IRigidbodyDataWriter applyWorldTorque(Vector3dc t) {
        Vector3d torqueImmutable = new Vector3d(t);
        updates.add((level, ship) -> {
            if (!(level instanceof ServerLevel sLevel) || !(ship instanceof LoadedServerShip sShip)) {
                EzDebug.warn("client or not loaded vs ship don't support set omega");
                return;
            }
            ForcesInducer.applyTorque(sShip, torqueImmutable);
        });
        return this;
    }

    @Override
    public IRigidbodyDataWriter applyWork(double work) {
        EzDebug.notImpl("VsRigidbodyDataWrapper.applyWork");
        return this;
    }


    @Override
    public IRigidbodyDataWriter moveLocalPosToWorld(Vector3dc localPos, Vector3dc toWorld) {
        Vector3d localPosImmutable = new Vector3d(localPos);
        Vector3d toWorldImmutable = new Vector3d(toWorld);
        updates.add((level, ship) -> {
            if (!(level instanceof ServerLevel sLevel) || !(ship instanceof ServerShip sShip)) {
                EzDebug.warn("client vs ship don't support change pos");
                return;
            }
            Vector3d transformedPos = sShip.getTransform().getShipToWorld().transformPosition(localPosImmutable, new Vector3d());
            Vector3d movement = toWorldImmutable.sub(transformedPos, new Vector3d());
            //the update pos is done sequently, don't worry the concurrent
            //d.transform.addPosition(movement);
            VSGameUtilsKt.getShipObjectWorld(sLevel).teleportShip(sShip,
                TeleportDataBuilder.copy(sLevel, sShip).addPos(movement).get()
            );
        });
        return this;
    }
}
