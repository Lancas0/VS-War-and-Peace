package com.lancas.vs_wap.subproject.sandbox.ship;

import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.subproject.sandbox.component.data.IExposedTransformData;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxTransformData;
import com.lancas.vs_wap.util.NbtBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ShipClientRenderer {
    public final UUID uuid;
    private final SandBoxTransformData curTransformData;
    private final SandBoxTransformData prevTransformData;
    private final SandBoxTransformData renderTransformData;
    private final SandBoxTransformData latestNetworkTransformData;

    private final Map<BlockPos, BlockState> visibleBlocks = new ConcurrentHashMap<>();  //todo is it updated?

    public ShipClientRenderer(UUID inId, IExposedTransformData inTransformData, Iterable<Map.Entry<BlockPos, BlockState>> inVisibleBlocks) {
        uuid = inId;
        curTransformData = new SandBoxTransformData().set(inTransformData);
        prevTransformData = new SandBoxTransformData().set(inTransformData);
        renderTransformData = new SandBoxTransformData().set(inTransformData);
        latestNetworkTransformData = new SandBoxTransformData().set(inTransformData);

        for (var entry : inVisibleBlocks)
            visibleBlocks.put(entry.getKey(), entry.getValue());
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
            nbt -> new BiTuple<>(
                NbtUtils.readBlockPos(nbt.getCompound("pos")),
                NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), nbt.getCompound("state"))
            ),
            visibleBlocks
        );
    }

    public CompoundTag saved() {
        return new NbtBuilder()
            .putUUID("uuid", uuid)
            .putCompound("transform_data", curTransformData.saved())
            .putMap("visible_blocks", visibleBlocks,
                (pos, state) ->
                    new NbtBuilder().putCompound("pos", NbtUtils.writeBlockPos(pos))
                        .putCompound("state", NbtUtils.writeBlockState(state))
                        .get()
            ).get();
    }

    public void receiveNetworkTransform(SandBoxTransformData networkTransformData) {
        latestNetworkTransformData.set(networkTransformData);
        //prevTransformData.set(renderTransformData);
        //renderTransformData.set(networkTransformData);

    }
    /*public void updateTransform(SandBoxTransformData newTransformData) {
        prevTransformData.set(curTransformData);  //prevTransformData更新为此时的位置(lerpTransformData)
        nextTransformData.set(newTransformData);
        //EzDebug.log("prev:" + StrUtil.F2(prevTransformData.position.y) + ", cur:" + StrUtil.F2(transformData.position.y));
    }*/
    public void postRender() {
        var nextTD = curTransformData.lerp(latestNetworkTransformData, 0.7, new SandBoxTransformData());
        prevTransformData.set(curTransformData);
        curTransformData.set(nextTD);
    }
    public SandBoxTransformData getRenderTransformData(double partialTicks) {
        return prevTransformData.lerp(curTransformData, partialTicks, renderTransformData);
    }
    /*public SandBoxTransformData getCurTransform(double partialTicks) {
        //double newPartialTicks = Math.max(lastPartialTicks, partialTicks);
        //lastPartialTicks = newPartialTicks;
        return prevTransformData.lerp(renderTransformData, partialTicks, renderTransformData);
    }*/

    public Iterable<Map.Entry<BlockPos, BlockState>> getVisibleBlocks() {
        return visibleBlocks.entrySet();
    }


    @Override
    public String toString() {
        return "ShipClientRenderer{" +
            "uuid=" + uuid +
            ", transformData=" + curTransformData +
            '}';
    }
}
