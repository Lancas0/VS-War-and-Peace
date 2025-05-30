package com.lancas.vswap.subproject.sandbox.ship;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.api.ISavedLevelObject;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentBehaviour;
import com.lancas.vswap.subproject.sandbox.api.component.IServerBehaviour;
import com.lancas.vswap.subproject.sandbox.component.behviour.SandBoxRigidbody;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.behviour.SandBoxShipBlockCluster;
import com.lancas.vswap.util.NbtBuilder;
import com.lancas.vswap.util.SerializeUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

public class SandBoxServerShip implements IServerSandBoxShip, ISavedLevelObject<SandBoxServerShip> {
    private UUID uuid;
    //private final SandBoxTransform transform = new SandBoxTransform();
    private final SandBoxRigidbody rigidbody = new SandBoxRigidbody();
    private final SandBoxShipBlockCluster blockCluster = new SandBoxShipBlockCluster();


    private final Queue<IServerBehaviour<?>> behaviours = new ConcurrentLinkedQueue<>();

    //private final AABBd cachedWorldAABB = new AABBd();
    //private boolean worldAABBDirty = true;
    private volatile AABBd worldAABBSnapshot = new AABBd();

    //private final AtomicInteger remainLifeTick = new AtomicInteger(-1);  //todo make it a tick destory. -1 for no destroy, 0 for should, >0 for ticking down
    /*@Override
    public int getRemainLifeTick() { return remainLifeTick.get(); }
    @Override
    public void setRemainLifeTick(int tick) { remainLifeTick.set(tick); }
    public boolean tickDownTimeOut() {
        return remainLifeTick.updateAndGet(x -> {
            if (x > 0) return x - 1;  //0 or >0
            return x;  //0 or -1
        }) == 0;
    }
    public boolean isTimeOut() { return remainLifeTick.get() == 0; }*/
    /*
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
    }*/
    /*
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
    */
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

    public SandBoxServerShip(UUID inId, RigidbodyData rigidbodyData, BlockClusterData clusterData) {  //todo make sure ship is created in server
        uuid = inId;
        //transform.loadData(this, transformData);
        blockCluster.loadData(this, clusterData);
        //must load blockCluster first because rigidbody mass is calcualted by all blocks of ship
        rigidbody.loadData(this, rigidbodyData);

        worldAABBSnapshot = blockCluster.getDataReader().getLocalAABB().transform(rigidbody.getDataReader().getLocalToWorld(), new AABBd());
    }
    public SandBoxServerShip(ServerLevel level, CompoundTag saved) {
        load(level, saved);
        worldAABBSnapshot = blockCluster.getDataReader().getLocalAABB().transform(rigidbody.getDataReader().getLocalToWorld(), new AABBd());
    }

    public <B extends IServerBehaviour<D>, D extends IComponentData<D>>
        void addBehaviour(B behaviour, D data) {
            behaviour.loadData(this, data);
            behaviours.add(behaviour);
    }
    @Override
    public Stream<IComponentBehaviour<?>> allAddedBehaviours() {
        //return () -> behaviours.stream().map(b -> (IComponentBehaviour)b).iterator();
        return behaviours.stream().map(b -> b);
    }


    @Override
    public UUID getUuid() { return uuid; }

    //@Override
    /*public AABBdc getWorldAABB() {
        if (blockCluster.getLocalAABB() == null) return null;

        if (worldAABBDirty) {
            if (blockCluster.getLocalAABB() == null) return null;  //empty aabb, don't set dirty false, it must update later.
            TransformUtil.quickTransform(transform.getLocalToWorld(), blockCluster.getLocalAABB(), cachedWorldAABB);
            worldAABBDirty = true;
        }
        return cachedWorldAABB;
    }*/
    //@Override
    public AABBdc getWorldAABB() { return worldAABBSnapshot; }
    public AABBdc getLocalAABB() { return blockCluster.getDataReader().getLocalAABB(); }

    //todo readonly interface?
    //@Override
    //public SandBoxTransform getTransform() { return transform; }  //todo get Exposed Behaviour?

    @Override
    public SandBoxRigidbody getRigidbody() { return rigidbody; }
    @Override
    public SandBoxShipBlockCluster getBlockCluster() { return blockCluster; }

    @Nullable
    public <T extends IComponentBehaviour<?>> T getBehaviour(Class<T> type) {
        try {
            return (T)behaviours.stream()
                .filter(x -> type.isAssignableFrom(x.getClass()))
                .findFirst()
                .orElseGet(() -> null);
        } catch (Exception e) {
            EzDebug.warn("type convert exception during get ship behaviour." + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    //public IExposedRigidbodyData getRigidbodyData() { return rigidbody.getExposedData(); }

    public double getLengthScale(int component) { return rigidbody.getDataReader().getScale().get(component); }
    public double getAreaScale(int component) {
        double scale = rigidbody.getDataReader().getScale().get(component);
        return scale * scale;
    }
    public double getVolumeScale(int component) {
        double scale = rigidbody.getDataReader().getScale().get(component);
        return scale * scale * scale;
    }

    /*public ShipClientRenderer createRenderer() {
        return new ShipClientRenderer(
            uuid, getLocalAABB(), transform.getExposedData(), blockCluster.allBlocks()
        );
    }*/

    //in fact it doesn't inculde rigidbody and block data
    /*public Iterable<IServerBehaviour<?>> getAllBehaviours() {
        return behaviours;
    }*/

    @Override
    public void serverTick(ServerLevel level) {
        rigidbody.serverTick(level);
        behaviours.forEach(beh -> beh.serverTick(level));
    }
    @Override
    public void physTick() {
        rigidbody.physTick();
        behaviours.forEach(IComponentBehaviour::physTick);

        //todo move snapshot to server tick?
        worldAABBSnapshot = getLocalAABB().transform(rigidbody.getDataReader().getLocalToWorld(), new AABBd());
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
    public CompoundTag saved(ServerLevel level) {
        return new NbtBuilder()
            .putUUID("uuid", uuid)
            //.putNumber("remain_life_tick", remainLifeTick.get())
            .putCompound("rigidbody_data", rigidbody.getSavedData())
            //.putCompound("transform_data", transform.getSavedData())
            .putCompound("block_data", blockCluster.getSavedData())
            .putEach("behaviours", behaviours,
                beh -> new NbtBuilder()
                    .putString("behaviour_type", beh.getClass().getName())
                    //.putString("data_type", beh.getDataType().getName())
                    .putCompound("data", beh.getSavedData())
                    .get()
            ).get();
    }
    @Override
    public SandBoxServerShip load(ServerLevel level, CompoundTag tag) {
        behaviours.clear();

        NbtBuilder.modify(tag)
            .readUUIDDo("uuid", v -> uuid = v)
            //.readIntDo("remain_life_tick", remainLifeTick::set)
            /*.readCompoundDo("transform_data",
                t -> transform.loadData(this, new TransformData().load(t))
            )*/
            //load block data first, because the mass of rigidbody is calculated from blockCluster
            .readCompoundDo("block_data",
                t -> blockCluster.loadSavedData(this, t)
            )
            .readCompoundDo("rigidbody_data",
                t -> rigidbody.loadSavedData(this, t)
            )
            .readEachCompound("behaviours",
                nbt -> {
                    String typename = nbt.getString("behaviour_type");
                    String dataTypename = nbt.getString("data_type");
                    CompoundTag savedData = nbt.getCompound("data");

                    IServerBehaviour<?> behaviour;
                    behaviour = SerializeUtil.createByClassName(typename);
                    //IComponentData<?> data = SerializeUtil.createByClassName(dataTypename);

                    /*if (behaviour != null && data != null) {
                        data.load(savedData);
                        behaviour.loadDataUnsafe(this, data);
                        return behaviour;
                    } else {
                        return null;
                    }*/
                    if (behaviour == null) return null;
                    behaviour.loadSavedData(this, savedData);
                    return behaviour;
                },
                behaviours
            );

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
