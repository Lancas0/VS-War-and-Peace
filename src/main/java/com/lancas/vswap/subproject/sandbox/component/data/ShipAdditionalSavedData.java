package com.lancas.vswap.subproject.sandbox.component.data;

import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentDataReader;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentDataWriter;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class ShipAdditionalSavedData implements IComponentData<ShipAdditionalSavedData> {
    private final ConcurrentHashMap<String, CompoundTag> data = new ConcurrentHashMap<>();

    public void put(String key, CompoundTag tag) { data.put(key, tag.copy()); }  //copy to keep thread safty
    public @Nullable CompoundTag get(String key) { return data.get(key).copy(); }
    public CompoundTag computeIfAbsent(String key, Function<String, CompoundTag> defaultSup) { return data.computeIfAbsent(key, defaultSup).copy(); }

    @Override
    public ShipAdditionalSavedData copyData(ShipAdditionalSavedData src) {
        data.clear();
        src.data.forEach((k, v) -> {
            data.put(k, v.copy());
        });
        return this;
    }

    @Override
    public CompoundTag saved() {
        return new NbtBuilder()
            .putMap("data", data, (k, v) ->
                new NbtBuilder()
                    .putString("key", k)
                    .putCompound("value", v)
                    .get()
            ).get();
    }

    @Override
    public IComponentData<ShipAdditionalSavedData> load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readMapOverwrite("data", t -> {
                BiTuple<String, CompoundTag> entry = new BiTuple<>();
                NbtBuilder.modify(t)
                    .readStringDo("key", entry::setFirst)
                    .readCompoundDo("value", entry::setSecond);
                return entry;
            }, data
        );
        return this;
    }
}
