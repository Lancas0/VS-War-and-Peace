package com.lancas.vswap.subproject.sandbox.component.data;

import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HierarchyData implements IComponentData<HierarchyData> {
    //public static record ChildShipData(UUID childShipUuid, @Nullable UUID constraintUuid) {}
    //key is childShipUuid, value is constraintUuid (optional)
    public final Map<UUID, Optional<UUID>> childrenData = new ConcurrentHashMap<>();

    @Override
    public HierarchyData copyData(HierarchyData src) {
        childrenData.clear();
        childrenData.putAll(src.childrenData);
        return this;
    }

    @Override
    public CompoundTag saved() {
        return new NbtBuilder().putMap("children_data", childrenData, (k, v) ->
            new NbtBuilder()
                .putUUID("child_ship_uuid", k)
                .putIfNonNull("constraint_uuid", v.orElse(null), NbtBuilder::putUUID)
                .get()
        ).get();
    }

    @Override
    public IComponentData<HierarchyData> load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readMapOverwrite("children_data", t -> {
                BiTuple<UUID, Optional<UUID>> entry = new BiTuple<>();
                entry.setSecond(Optional.empty());

                NbtBuilder.modify(t)
                    .readUUIDDo("child_ship_uuid", entry::setFirst)
                    .readDoIfExist("constraint_uuid", v -> entry.setSecond(Optional.of(v)), NbtBuilder::getUUID);

                return entry;
            }, childrenData);
        return this;
    }
}
