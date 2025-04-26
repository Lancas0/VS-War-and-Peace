package com.lancas.vs_wap.subproject.sandbox.ship;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.subproject.sandbox.INbtSavedObject;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.IComponentBehaviour;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.SandBoxRigidbody;
import com.lancas.vs_wap.subproject.sandbox.component.data.IComponentData;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxRigidbodyData;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedComponentData;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxBlockClusterData;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.SandBoxShipBlockCluster;
import com.lancas.vs_wap.subproject.sandbox.component.behviour.SandBoxTransform;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxTransformData;
import com.lancas.vs_wap.subproject.sandbox.util.SerializeUtil;
import com.lancas.vs_wap.subproject.sandbox.util.TransformUtil;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaterniondc;
import org.joml.Vector3dc;
import org.joml.Vector3ic;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SandBoxServerShip implements ISandBoxShip, INbtSavedObject<SandBoxServerShip> {
    private UUID uuid;
    private final SandBoxTransform transform = new SandBoxTransform();
    private final SandBoxShipBlockCluster blockCluster = new SandBoxShipBlockCluster();
    private final SandBoxRigidbody rigidbody = new SandBoxRigidbody();

    private final Queue<IComponentBehaviour<?>> behaviours = new ConcurrentLinkedQueue<>();

    private final AABBd cachedWorldAABB = new AABBd();
    private boolean worldAABBDirty = true;

    private boolean destroyMark = false;
    public void addDestroyMark() { destroyMark = true; }
    public boolean isDestroyMarked() { return destroyMark; }


    public void setPosition(Vector3dc pos)    {
        transform.setPosition(pos);
        worldAABBDirty = true;
    }
    public void setRotation(Quaterniondc rot) {
        transform.setRotation(rot);
        worldAABBDirty = true;
    }
    public void setScale(Vector3dc scale)     {
        transform.setScale(scale);
        worldAABBDirty = true;
    }


    public void setBlock(Vector3ic localPos, BlockState state) {
        if (state == null || state.isAir()) {
            removeBlock(localPos);
            return;
        }

        blockCluster.setBlock(localPos, state);
        worldAABBDirty = true;
    }
    public void removeBlock(Vector3ic localPos) {
        blockCluster.removeBlock(localPos);
        worldAABBDirty = true;
    }

    //@Nullable
    //public BlockState getBlockOrNull(Vector3ic localPos) { return blockData.getBlock(localPos); }
    //@NotNull
    //public BlockState getBlock(Vector3ic localPos) { return blockData.getBlock(localPos); }

    /*private SandBoxServerShip() {
        uuid = null;
        transform = new SandBoxTransform();
        blockData = new SandBoxShipBlockCluster();
    }
    public SandBoxServerShip(UUID inId, Vector3dc pos, Quaterniondc rot, Vector3dc scale) {
        uuid = inId;
        transform = new SandBoxTransform(pos, rot, scale);
        blockData = new SandBoxShipBlockCluster();
    }
    public static SandBoxServerShip fromNbt(CompoundTag nbt) {
        SandBoxServerShip ship = new SandBoxServerShip();
        ship.load(nbt);
        return ship;
    }*/
    public SandBoxServerShip(UUID inId, SandBoxTransformData transformData, SandBoxBlockClusterData clusterData, SandBoxRigidbodyData rigidbodyData) {
        uuid = inId;
        transform.loadData(this, transformData);
        blockCluster.loadData(this, clusterData);
        rigidbody.loadData(this, rigidbodyData);
    }
    public SandBoxServerShip(CompoundTag saved) {
        load(saved);
    }

    public <B extends IComponentBehaviour<D>, D extends IComponentData<D> & IExposedComponentData<D>>
        void addBehaviour(B behaviour, D data) {
            behaviour.loadData(this, data);
            behaviours.add(behaviour);
    }
    public Iterable<IComponentBehaviour<?>> allBehaviours() { return behaviours; }


    @Override
    public UUID getUuid() { return uuid; }

    @Override
    public AABBdc getWorldAABB() {
        if (blockCluster.getLocalAABB() == null) return null;

        if (worldAABBDirty) {
            if (blockCluster.getLocalAABB() == null) return null;  //empty aabb, don't set dirty false, it must update later.
            TransformUtil.quickTransform(transform.getLocalToWorld(), blockCluster.getLocalAABB(), cachedWorldAABB);
            worldAABBDirty = true;
        }
        return cachedWorldAABB;
    }
    @Override
    public AABBdc getLocalAABB() {
        return blockCluster.getLocalAABB();
    }

    //todo readonly interface
    @Override
    public SandBoxTransform getTransform() { return transform; }  //todo get Exposed Behaviour?
    @Override
    public SandBoxShipBlockCluster getCluster() { return blockCluster; }

    public SandBoxRigidbody getRigidbody() { return rigidbody; }
    //public IExposedRigidbodyData getRigidbodyData() { return rigidbody.getExposedData(); }

    public double getLengthScale(int component) { return transform.getScale().get(component); }
    public double getAreaScale(int component) {
        double scale = transform.getScale().get(component);
        return scale * scale;
    }
    public double getVolumeScale(int component) {
        double scale = transform.getScale().get(component);
        return scale * scale * scale;
    }

    public ShipClientRenderer createRenderer() {
        return new ShipClientRenderer(
            uuid, transform.getExposedData(), blockCluster.allBlocks()
        );
    }


    //infact it don't inculde transform and block data
    public Iterable<IComponentBehaviour<?>> getAllBehaviours() {
        return behaviours;
    }


    /// todo
    /*
    @Nullable
    public <T extends IComponentBehaviour<?>> T findBehaviourOrChildren(Class<T> type) {
        for (var behaviour : behaviours) {
            if (type.isAssignableFrom(behaviour.getClass())) {
                return (T)behaviour;
            }
        }
        return null;
    }
    @Nullable
    public <T extends IComponentData<T> & IExposedComponentData<T>> T findBehaviourExactly(Class<T> type) {
        for (var behaviour : behaviours) {
            if (type.equals(behaviour.getClass())) {
                return (T)behaviour;
            }
        }
        return null;
    }*/

    @Override
    public CompoundTag saved() {
        return new NbtBuilder()
            .putUUID("uuid", uuid)
            .putCompound("transform_data", transform.getSavedData())
            .putCompound("block_data", blockCluster.getSavedData())
            .putCompound("rigidbody_data", rigidbody.getSavedData())
            .putEach("behaviours", behaviours,
                beh -> new NbtBuilder()
                    .putString("behaviour_type", beh.getClass().getName())
                    .putString("data_type", beh.getExposedData().getClass().getName())
                    .putCompound("data", beh.getSavedData())
                    .get()
            ).get();
    }
    @Override
    public SandBoxServerShip load(CompoundTag tag) {
        behaviours.clear();

        NbtBuilder.modify(tag)
            .readUUIDDo("uuid", v -> uuid = v)
            .readCompoundDo("transform_data",
                t -> transform.loadData(this, new SandBoxTransformData().load(t))
            )
            .readCompoundDo("block_data",
                t -> blockCluster.loadData(this, new SandBoxBlockClusterData().load(t))
            )
            .readCompoundDo("rigidbody_data",
                t -> rigidbody.loadData(this, SandBoxRigidbodyData.createDefault().load(t))
            )
            .readEachCompound("behaviours",
                nbt -> {
                    String typename = nbt.getString("behaviour_type");
                    String dataTypename = nbt.getString("data_type");
                    CompoundTag savedData = nbt.getCompound("data");

                    IComponentBehaviour<?> behaviour;
                    behaviour = SerializeUtil.createByClassName(typename);
                    IComponentData<?> data =SerializeUtil.createByClassName(dataTypename);

                    if (behaviour != null && data != null) {
                        data.load(savedData);
                        behaviour.loadDataUnsafe(this, data);
                        return behaviour;
                    } else {
                        return null;
                    }
                },
                behaviours
            );

        EzDebug.log("after load omega:" + rigidbody.getExposedData().getOmega());

        behaviours.removeIf(Objects::isNull);
        return this;
    }

    /*
    @Override
    public CompoundTag saved() {
        return new NbtBuilder()
            .putUUID("uuid", uuid)
            .putCompound("transform", transform.getSavedData())
            .putCompound("block_data", blockData.getSavedData())
            .get();
    }
    @Override
    public void load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readUUIDDo("id", v -> uuid = v)
            .readCompoundDo("transform", () -> {transform.loadData(new SandBoxTransformData().load(););})
            .readCompoundDo("block_data", blockData::load);
    }*/
    /*
    // ========================= 坐标变换 =========================
    // 局部坐标 → 世界坐标
    public Vector3d localToWorldPosition(Vector3d localPos) {
        Vector4d transformed = new Vector4d(localPos.x, localPos.y, localPos.z, 1.0);
        transformed.mul();
        return transform.get;
    }
    // 世界坐标 → 局部坐标
    public Vector3d worldToLocalPosition(Vector3d worldPos) {
        Vector4d transformed = new Vector4d(worldPos.x, worldPos.y, worldPos.z, 1.0);
        transformed.mul(transform.getWorldToLocal());
        return new Vector3d(transformed.x, transformed.y, transformed.z);
    }
    */
}
