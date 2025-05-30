package com.lancas.vswap.subproject.sandbox.ship;

import com.lancas.vswap.subproject.sandbox.api.component.IClientBehaviour;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentBehaviour;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vswap.subproject.sandbox.api.data.ITransformPrimitive;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.subproject.sandbox.component.behviour.SandBoxRigidbody;
import com.lancas.vswap.subproject.sandbox.component.behviour.SandBoxShipBlockCluster;
import com.lancas.vswap.subproject.sandbox.component.data.BlockClusterData;
import com.lancas.vswap.subproject.sandbox.component.data.RigidbodyData;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

//note that client ship will not be saved
//todo extract a abstract ship from serverShip and clientShip
public class SandBoxClientShip implements IClientSandBoxShip {
    private UUID uuid;
    private final SandBoxRigidbody rigidbody = new SandBoxRigidbody();
    private final SandBoxShipBlockCluster blockCluster = new SandBoxShipBlockCluster();

    //latestNetworkTransform is null means it's a client only ship
    //(or simply havn't be synced)
    private volatile TransformPrimitive latestNetworkTransform = null;
    private final TransformPrimitive prevTransform = new TransformPrimitive();
    private final TransformPrimitive curTransform = new TransformPrimitive();
    private final TransformPrimitive renderTransform = new TransformPrimitive();


    private final Queue<IClientBehaviour<?>> behaviours = new ConcurrentLinkedQueue<>();

    //private final AABBd cachedWorldAABB = new AABBd();
    //private boolean worldAABBDirty = true;
    private volatile AABBd worldAABBSnapshot = new AABBd();

    private final AtomicInteger remainLifeTick = new AtomicInteger(-1);  //todo make it a tick destory. -1 for no destroy, 0 for should, >0 for ticking down

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

    public SandBoxClientShip(UUID inId, RigidbodyData rigidbodyData, BlockClusterData clusterData) {
        uuid = inId;
        //transform.loadData(this, transformData);
        blockCluster.loadData(this, clusterData);
        //must load blockCluster first because rigidbody mass is calcualted by all blocks of ship
        rigidbody.loadData(this, rigidbodyData);

        prevTransform.set(rigidbodyData.transform);
        curTransform.set(rigidbodyData.transform);
        renderTransform.set(rigidbodyData.transform);
    }
    /*public SandBoxClientShip(ServerLevel level, CompoundTag saved) {
        load(level, saved);
    }*/

    public <B extends IClientBehaviour<D>, D extends IComponentData<D>>
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
    //in fact it doesn't inculde transform and block data
    /*public Iterable<IClientBehaviour<?>> getAllBehaviours() {
        return behaviours;
    }*/

    public void receiveNetworkTransform(ITransformPrimitive inNetworkTransform) {
        latestNetworkTransform = new TransformPrimitive(inNetworkTransform);
    }
    public void postRender() {
        ITransformPrimitive nextTransform =
            latestNetworkTransform == null ? rigidbody.getDataReader().getTransform() : latestNetworkTransform;

        prevTransform.set(curTransform);
        curTransform.lerp(nextTransform, 0.2, curTransform);

        /*EzDebug.log(
            "latestNetworkTransform pos:" + StrUtil.F2(latestNetworkTransform.position) +
            "\ncurTransform pos:" + StrUtil.F2(curTransform.position)
        );*/
    }
    public ITransformPrimitive getRenderTransform(double partialTicks) {
        //EzDebug.log("prev:" + prevTransformData + ", cur:" + curTransformData);
        return prevTransform.lerp(curTransform, partialTicks, renderTransform);
    }
    public ITransformPrimitive getCurrentTransform() { return curTransform; }


    @Override
    public void clientTick(ClientLevel level) {
        rigidbody.clientTick(level);
        behaviours.forEach(b -> b.clientTick(level));
    }
    @Override
    public void physTick() {
        //EzDebug.log("client phys tick, uuid:" + uuid + ", pos:" + StrUtil.F2(rigidbody.getDataReader().getPosition()));
        rigidbody.physTick();
        behaviours.forEach(IComponentBehaviour::physTick);
        //move worldAABBSnapshot to client tick?
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

    /*@Override
    public CompoundTag saved(ServerLevel level) {
        return new NbtBuilder()
            .putUUID("uuid", uuid)
            .putNumber("timeout", timeout.get())
            .putCompound("rigidbody_data", rigidbody.getSavedData())
            //.putCompound("transform_data", transform.getSavedData())
            .putCompound("block_data", blockCluster.getSavedData())
            .putEach("behaviours", behaviours,
                beh -> new NbtBuilder()
                    .putString("behaviour_type", beh.getClass().getName())
                    .putString("data_type", beh.getDataType().getName())
                    .putCompound("data", beh.getSavedData())
                    .get()
            ).get();
    }
    @Override
    public SandBoxClientShip load(ServerLevel level, CompoundTag tag) {
        behaviours.clear();

        NbtBuilder.modify(tag)
            .readUUIDDo("uuid", v -> uuid = v)
            .readIntDo("timeout", timeout::set)
            /.*.readCompoundDo("transform_data",
                t -> transform.loadData(this, new TransformData().load(t))
            )*./
            .readCompoundDo("rigidbody_data",
                t -> rigidbody.loadData(this, RigidbodyData.createDefault().load(t))
            )
            .readCompoundDo("block_data",
                t -> blockCluster.loadData(this, new BlockClusterData().load(t))
            )
            .readEachCompound("behaviours",
                nbt -> {
                    String typename = nbt.getString("behaviour_type");
                    String dataTypename = nbt.getString("data_type");
                    CompoundTag savedData = nbt.getCompound("data");

                    IServerBehaviour<?> behaviour;
                    behaviour = SerializeUtil.createByClassName(typename);
                    IComponentData<?> data = SerializeUtil.createByClassName(dataTypename);

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

        behaviours.removeIf(Objects::isNull);
        return this;
    }*/

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
