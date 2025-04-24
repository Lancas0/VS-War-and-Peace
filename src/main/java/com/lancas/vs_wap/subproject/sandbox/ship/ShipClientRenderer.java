package com.lancas.vs_wap.subproject.sandbox.ship;

import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.subproject.sandbox.component.data.IExposedTransformData;
import com.lancas.vs_wap.subproject.sandbox.component.data.SandBoxTransformData;
import com.lancas.vs_wap.util.NbtBuilder;
import com.lancas.vs_wap.util.StrUtil;
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
    private final SandBoxTransformData transformData;
    private final SandBoxTransformData prevTransformData;
    private final SandBoxTransformData lerpTransformData;

    private final Map<BlockPos, BlockState> visibleBlocks = new ConcurrentHashMap<>();  //todo is it updated?

    public ShipClientRenderer(UUID inId, IExposedTransformData inTransformData, Iterable<Map.Entry<BlockPos, BlockState>> inVisibleBlocks) {
        uuid = inId;
        transformData = new SandBoxTransformData().set(inTransformData);
        prevTransformData = new SandBoxTransformData().set(inTransformData);
        lerpTransformData = new SandBoxTransformData().set(inTransformData);

        for (var entry : inVisibleBlocks)
            visibleBlocks.put(entry.getKey(), entry.getValue());
    }
    public ShipClientRenderer(CompoundTag saved) {
        NbtBuilder nbtBuilder = NbtBuilder.modify(saved);
        uuid = nbtBuilder.getUUID("uuid");

        SandBoxTransformData savedTransformData = new SandBoxTransformData().load(nbtBuilder.getCompound("transform_data"));
        transformData = new SandBoxTransformData().copyData(savedTransformData);
        prevTransformData = new SandBoxTransformData().copyData(savedTransformData);
        lerpTransformData = new SandBoxTransformData().copyData(savedTransformData);

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
            .putCompound("transform_data", transformData.saved())
            .putMap("visible_blocks", visibleBlocks,
                (pos, state) ->
                    new NbtBuilder().putCompound("pos", NbtUtils.writeBlockPos(pos))
                        .putCompound("state", NbtUtils.writeBlockState(state))
                        .get()
            ).get();
    }

    public void updateTransform(SandBoxTransformData newTransformData) {
        prevTransformData.set(transformData);
        transformData.set(newTransformData);
        //EzDebug.log("prev:" + StrUtil.F2(prevTransformData.position.y) + ", cur:" + StrUtil.F2(transformData.position.y));
    }
    public SandBoxTransformData getLerpTransform(double t) {
        return lerpTransformData.lerp(transformData, t, lerpTransformData);
    }

    public Iterable<Map.Entry<BlockPos, BlockState>> getVisibleBlocks() {
        return visibleBlocks.entrySet();
    }


    @Override
    public String toString() {
        return "ShipClientRenderer{" +
            "uuid=" + uuid +
            ", transformData=" + transformData +
            '}';
    }
}
