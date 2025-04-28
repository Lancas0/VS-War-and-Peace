package com.lancas.vs_wap.subproject.sandbox.ship;

import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.ship.ballistics.helper.BallisticsUtil;
import com.lancas.vs_wap.subproject.sandbox.component.data.exposed.IExposedTransformData;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxTransformData;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4d;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ShipClientRenderer {
    public final UUID uuid;
    private final SandBoxTransformData curTransformData;
    private final SandBoxTransformData prevTransformData;
    private final SandBoxTransformData renderTransformData;
    private final SandBoxTransformData latestNetworkTransformData;

    private final AABBd localAABB = new AABBd();

    private final Map<Vector3i, BlockState> visibleBlocks = new ConcurrentHashMap<>();  //todo is it updated?

    public ShipClientRenderer(UUID inId, AABBdc inLocalAABB, IExposedTransformData inTransformData, Iterable<BiTuple<Vector3ic, BlockState>> inVisibleBlocks) {
        uuid = inId;

        localAABB.set(inLocalAABB);

        curTransformData = new SandBoxTransformData().set(inTransformData);
        prevTransformData = new SandBoxTransformData().set(inTransformData);
        renderTransformData = new SandBoxTransformData().set(inTransformData);
        latestNetworkTransformData = new SandBoxTransformData().set(inTransformData);

        for (var entry : inVisibleBlocks)
            visibleBlocks.put(new Vector3i(entry.getFirst()), entry.getSecond());
    }
    public ShipClientRenderer(CompoundTag saved) {
        NbtBuilder nbtBuilder = NbtBuilder.modify(saved);
        uuid = nbtBuilder.getUUID("uuid");

        SandBoxTransformData savedTransformData = new SandBoxTransformData().load(nbtBuilder.getCompound("transform_data"));
        curTransformData = new SandBoxTransformData().copyData(savedTransformData);
        prevTransformData = new SandBoxTransformData().copyData(savedTransformData);
        renderTransformData = new SandBoxTransformData().copyData(savedTransformData);
        latestNetworkTransformData = new SandBoxTransformData().copyData(savedTransformData);

        nbtBuilder.readMapOverwrite("visible_blocks",
            t -> new BiTuple<>(
                NbtBuilder.modify(t).getVector3i("locPos", new Vector3i()),
                NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), t.getCompound("state"))
            ),
            visibleBlocks
        );
    }

    public CompoundTag saved() {
        return new NbtBuilder()
            .putUUID("uuid", uuid)
            .putCompound("transform_data", curTransformData.saved())
            .putMap("visible_blocks", visibleBlocks,
                (locPos, state) ->
                    new NbtBuilder()
                        .putVector3i("locPos", locPos)
                        .putCompound("state", NbtUtils.writeBlockState(state))
                        .get()
            ).get();
    }

    public void receiveNetworkTransform(SandBoxTransformData networkTransformData, AABBdc networkLocalAABB) {
        latestNetworkTransformData.set(networkTransformData);
        localAABB.set(networkLocalAABB);
    }
    public void postRender() {
        var nextTD = curTransformData.lerp(latestNetworkTransformData, 0.4, new SandBoxTransformData());
        prevTransformData.set(curTransformData);
        curTransformData.set(nextTD);
    }
    public SandBoxTransformData getRenderTransformData(double partialTicks) {  //todo expose
        //EzDebug.log("prev:" + prevTransformData + ", cur:" + curTransformData);
        return prevTransformData.lerp(curTransformData, partialTicks, renderTransformData);
    }
    public SandBoxTransformData getCurTransformData() {
        return curTransformData;
    }
    public Iterable<BiTuple<Vector3ic, BlockState>> getVisibleBlocks() {
        return () -> visibleBlocks.entrySet().stream().map(
            entry -> new BiTuple<Vector3ic, BlockState>(entry.getKey(), entry.getValue())
        ).iterator();
    }

    public IExposedTransformData getLatestNetworkTransformData() { return latestNetworkTransformData; }

    public AABBdc getLocalAABB() { return localAABB; }
    public AABBdc getCurWorldAABB() { return BallisticsUtil.quickTransformAABB(curTransformData.makeLocalToWorld(new Matrix4d()), localAABB, new AABBd()); }



    @Override
    public String toString() {
        return "ShipClientRenderer{" +
            "uuid=" + uuid +
            ", transformData=" + curTransformData +
            '}';
    }
}
