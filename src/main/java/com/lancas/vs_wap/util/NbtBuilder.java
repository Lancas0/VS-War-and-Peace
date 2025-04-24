package com.lancas.vs_wap.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lancas.vs_wap.debug.EzDebug;
import com.lancas.vs_wap.foundation.BiTuple;
import com.lancas.vs_wap.foundation.api.Dest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.*;

//todo NbtReader, Writer NbtRW
public class NbtBuilder {
    private static final ObjectMapper SIMPLE_MAPPER = new ObjectMapper();

    private final CompoundTag nbt;
    public NbtBuilder() { nbt = new CompoundTag(); }

    public static CompoundTag valueOfLong(String key, long l) {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong(key, l);
        return nbt;
    }
    public static CompoundTag valueOfString(String key, String s) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString(key, s);
        return nbt;
    }
    public static Long getValueOfLong(CompoundTag tag, String key) { return tag.getLong(key); }
    public static String getValueOfString(CompoundTag tag, String key) { return tag.getString(key); }

    private NbtBuilder(Supplier<CompoundTag> nbtSupplier) { nbt = nbtSupplier.get(); }
    public static NbtBuilder copy(CompoundTag tag) {
        NbtBuilder builder = new NbtBuilder(tag::copy);
        return builder;
    }
    public static NbtBuilder modify(CompoundTag tag) {
        NbtBuilder builder = new NbtBuilder(() -> tag);
        return builder;
    }

    public boolean contains(String key) { return nbt.contains(key); }


    public NbtBuilder putNumber(String key, int v) {
        nbt.putInt(key, v);
        return this;
    }
    public NbtBuilder putNumber(String key, long v) {
        nbt.putLong(key, v);
        return this;
    }
    public NbtBuilder putNumber(String key, double v) {
        nbt.putDouble(key, v);
        return this;
    }
    public NbtBuilder putNumber(String key, float v) {
        nbt.putFloat(key, v);
        return this;
    }

    public NbtBuilder readInt(String key, @NotNull Dest<Integer> v) {
        if (nbt.contains(key))
            v.set(nbt.getInt(key));
        return this;
    }
    public NbtBuilder readLong(String key, @NotNull Dest<Long> v) {
        if (nbt.contains(key))
            v.set(nbt.getLong(key));
        return this;
    }
    public Long getLong(String key) {
        return nbt.getLong(key);
    }
    public NbtBuilder readDouble(String key, @NotNull Dest<Double> v) {
        if (nbt.contains(key))
            v.set(nbt.getDouble(key));
        return this;
    }
    public NbtBuilder readFloat(String key, @NotNull Dest<Float> v) {
        if (nbt.contains(key))
            v.set(nbt.getFloat(key));
        return this;
    }

    public NbtBuilder readIntDo(String key, Consumer<Integer> consumer) {
        if (nbt.contains(key))
            consumer.accept(nbt.getInt(key));
        return this;
    }
    public NbtBuilder readLongDo(String key, Consumer<Long> consumer) {
        if (nbt.contains(key))
            consumer.accept(nbt.getLong(key));
        return this;
    }
    public NbtBuilder readDoubleDo(String key, Consumer<Double> consumer) {
        if (nbt.contains(key))
            consumer.accept(nbt.getDouble(key));
        return this;
    }
    public NbtBuilder readFloatDo(String key, Consumer<Float> consumer) {
        if (nbt.contains(key))
            consumer.accept(nbt.getFloat(key));
        return this;
    }

    public NbtBuilder putString(String key, String s) {
        nbt.putString(key, s);
        return this;
    }
    public String getString(String key) {
        return nbt.getString(key);
    }
    public NbtBuilder readStringDo(String key, Consumer<String> consumer) {
        consumer.accept(nbt.getString(key));
        return this;
    }


    public NbtBuilder putUUID(String key, UUID uuid) {
        nbt.putUUID(key, uuid);
        return this;
    }
    public NbtBuilder readUUID(String key, Dest<UUID> uuidDest) {
        uuidDest.set(nbt.getUUID(key));
        return this;
    }
    public NbtBuilder readUUIDDo(String key, Consumer<UUID> consumer) {
        consumer.accept(nbt.getUUID(key));
        return this;
    }
    public UUID getUUID(String key) {
        return nbt.getUUID(key);
    }


    public NbtBuilder putVector3(String key, Vector3dc vec) {
        return this.putCompound(key, new NbtBuilder()
            .putNumber("x", vec.x())
            .putNumber("y", vec.y())
            .putNumber("z", vec.z())
            .get()
        );
    }
    public NbtBuilder putVector3(String key, Vector3fc vec) {
        return this.putCompound(key, new NbtBuilder()
            .putNumber("x", vec.x())
            .putNumber("y", vec.y())
            .putNumber("z", vec.z())
            .get()
        );
    }
    public NbtBuilder putVector3(String key, Vector3ic vec) {
        return this.putCompound(key, new NbtBuilder()
            .putNumber("x", vec.x())
            .putNumber("y", vec.y())
            .putNumber("z", vec.z())
            .get()
        );
    }
    public NbtBuilder readVector3d(String key, Vector3d vec) {
        Dest<CompoundTag> vTagDest = new Dest<>();
        this.readCompound(key, vTagDest);

        NbtBuilder.modify(vTagDest.get())
            .readDoubleDo("x", v -> vec.x = v)
            .readDoubleDo("y", v -> vec.y = v)
            .readDoubleDo("z", v -> vec.z = v);

        return this;
    }
    public NbtBuilder readVector3f(String key, Vector3f vec) {
        Dest<CompoundTag> vTagDest = new Dest<>();
        this.readCompound(key, vTagDest);

        NbtBuilder.modify(vTagDest.get())
            .readFloatDo("x", v -> vec.x = v)
            .readFloatDo("y", v -> vec.y = v)
            .readFloatDo("z", v -> vec.z = v);

        return this;
    }
    public NbtBuilder readVector3i(String key, Vector3i vec) {
        Dest<CompoundTag> vTagDest = new Dest<>();
        this.readCompound(key, vTagDest);

        NbtBuilder.modify(vTagDest.get())
            .readIntDo("x", v -> vec.x = v)
            .readIntDo("y", v -> vec.y = v)
            .readIntDo("z", v -> vec.z = v);

        return this;
    }

    public NbtBuilder putAABBi(String key, AABBic aabb) {
        nbt.put(key, new NbtBuilder()
            .putNumber("minx", aabb.minX())
            .putNumber("miny", aabb.minY())
            .putNumber("minz", aabb.minZ())
            .putNumber("maxx", aabb.maxX())
            .putNumber("maxy", aabb.maxY())
            .putNumber("maxz", aabb.maxZ())
            .get()
        );
        return this;
    }
    public NbtBuilder putAABBd(String key, AABBdc aabb) {
        nbt.put(key, new NbtBuilder()
            .putNumber("minx", aabb.minX())
            .putNumber("miny", aabb.minY())
            .putNumber("minz", aabb.minZ())
            .putNumber("maxx", aabb.maxX())
            .putNumber("maxy", aabb.maxY())
            .putNumber("maxz", aabb.maxZ())
            .get()
        );
        return this;
    }
    public NbtBuilder readAABBi(String key, AABBi dest) {
        Vector3i min = new Vector3i();
        Vector3i max = new Vector3i();
        NbtBuilder.modify(nbt.getCompound(key))
            .readIntDo("minx", v -> min.x = v)
            .readIntDo("miny", v -> min.y = v)
            .readIntDo("minz", v -> min.z = v)
            .readIntDo("maxx", v -> max.x = v)
            .readIntDo("maxy", v -> max.x = v)
            .readIntDo("maxz", v -> max.x = v);

        dest.setMin(min);
        dest.setMax(max);
        return this;
    }
    public NbtBuilder readAABBd(String key, AABBd dest) {
        Vector3d min = new Vector3d();
        Vector3d max = new Vector3d();
        NbtBuilder.modify(nbt.getCompound(key))
            .readDoubleDo("minx", v -> min.x = v)
            .readDoubleDo("miny", v -> min.y = v)
            .readDoubleDo("minz", v -> min.z = v)
            .readDoubleDo("maxx", v -> max.x = v)
            .readDoubleDo("maxy", v -> max.x = v)
            .readDoubleDo("maxz", v -> max.x = v);

        dest.setMin(min);
        dest.setMax(max);
        return this;
    }

    public NbtBuilder putQuaternion(String key, Quaterniondc q) {
        return this.putCompound(key,
            new NbtBuilder()
                .putNumber("x", q.x())
                .putNumber("y", q.y())
                .putNumber("z", q.z())
                .putNumber("w", q.w())
                .get()
        );
    }
    public NbtBuilder putQuaternion(String key, Quaternionfc q) {
        return this.putCompound(key,
            new NbtBuilder()
                .putNumber("x", q.x())
                .putNumber("y", q.y())
                .putNumber("z", q.z())
                .putNumber("w", q.w())
                .get()
        );
    }

    public NbtBuilder readQuaternionD(String key, Quaterniond dest) {
        Dest<CompoundTag> qTagDest = new Dest<>();
        this.readCompound(key, qTagDest);

        NbtBuilder.modify(qTagDest.get())
            .readDoubleDo("x", v -> dest.x = v)
            .readDoubleDo("y", v -> dest.y = v)
            .readDoubleDo("z", v -> dest.z = v)
            .readDoubleDo("w", v -> dest.w = v);
        return this;
    }
    public NbtBuilder readQuaternionF(String key, Quaternionf dest) {
        Dest<CompoundTag> qTagDest = new Dest<>();
        this.readCompound(key, qTagDest);

        NbtBuilder.modify(qTagDest.get())
            .readFloatDo("x", v -> dest.x = v)
            .readFloatDo("y", v -> dest.y = v)
            .readFloatDo("z", v -> dest.z = v)
            .readFloatDo("w", v -> dest.w = v);
        return this;
    }


    public NbtBuilder putBoolean(String key, boolean v) {
        nbt.putBoolean(key, v);
        return this;
    }

    public <T> NbtBuilder putEach(String key, Iterable<T> list, Function<T, Tag> nbtCreator) {
        ListTag listTag = new ListTag();
        for (T item : list) {
            listTag.add(nbtCreator.apply(item));
        }
        nbt.put(key, listTag);
        return this;
    }
    private <T> NbtBuilder readEachImpl(String key, Function<Tag, T> nbtReader, byte target, Collection<T> dest) {
        dest.clear();
        ListTag listTag = nbt.getList(key, target);
        if (listTag.isEmpty()) return this;

        for (Tag itemNbt : listTag) {
            dest.add(nbtReader.apply(itemNbt));
        }
        return this;
    }
    public <T> NbtBuilder readEachAsCompound(String key, Function<CompoundTag, T> nbtReader, Collection<T> dest) {
        return readEachImpl(key, (tag) -> nbtReader.apply((CompoundTag)tag), Tag.TAG_COMPOUND, dest);
    }
    public <T> NbtBuilder readEachAsList(String key, Function<ListTag, T> nbtReader, Collection<T> dest) {
        return readEachImpl(key, (tag) -> nbtReader.apply((ListTag)tag), Tag.TAG_LIST, dest);
    }

    public <K, V> NbtBuilder putMap(String key, Map<K, V> map, BiFunction<K, V, CompoundTag> entryNbtCreator) {
        ListTag listTag = new ListTag();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            listTag.add(entryNbtCreator.apply(entry.getKey(), entry.getValue()));
        }
        nbt.put(key, listTag);
        return this;
    }
    public <K, V> NbtBuilder readMapOverwrite(String key, Function<CompoundTag, BiTuple<K, V>> entryNbtReader, Map<K, V> dest) {
        dest.clear();
        return readMapAppend(key, entryNbtReader, dest);
    }
    public <K, V> NbtBuilder readMapAppend(String key, Function<CompoundTag, BiTuple<K, V>> entryNbtReader, Map<K, V> dest) {
        ListTag listTag = nbt.getList(key, Tag.TAG_COMPOUND);
        if (listTag.isEmpty()) return this;

        for (Tag itemNbt : listTag) {
            BiTuple<K, V> entry = entryNbtReader.apply((CompoundTag)itemNbt);
            dest.put(entry.getFirst(), entry.getSecond());
        }
        return this;
    }

    public NbtBuilder putList(String key, ListTag listTag) { nbt.put(key, listTag); return this; }
    public NbtBuilder getList(String key, Dest<ListTag> listTagDest) { listTagDest.set((ListTag)nbt.get(key)); return this; }

    public NbtBuilder withTagDo(Consumer<CompoundTag> consumer) {
        if (consumer != null)
            consumer.accept(nbt);
        return this;
    }

    public CompoundTag get() { return nbt; }


    public NbtBuilder putBlock(String key, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
        nbt.put(key, ofBlock(pos, state, be));
        return this;
    }

    public static CompoundTag ofBlockPos(BlockPos pos) { return NbtUtils.writeBlockPos(pos); }
    public static BlockPos blockPosValueOf(CompoundTag tag) { return NbtUtils.readBlockPos(tag); }
    public static CompoundTag ofBlock(BlockPos pos, BlockState state, @Nullable BlockEntity be) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());

        tag.put("state", NbtUtils.writeBlockState(state));
        if (be != null)
            tag.put("be", be.saveWithFullMetadata());
        return tag;
    }
    public static void getBlock(CompoundTag tag, Level level, Dest<BlockPos> bpDest, Dest<BlockState> stateDest, Dest<CompoundTag> beTag) {
        bpDest.set(new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")));
        stateDest.set(NbtUtils.readBlockState(level.holderLookup(Registries.BLOCK), tag.getCompound("state")));

        if (tag.contains("be")) {
            beTag.set(tag.getCompound("be"));
        }
    }

    public NbtBuilder putCompound(String key, CompoundTag compound) {
        nbt.put(key, compound);
        return this;
    }
    public NbtBuilder readCompound(String key, Dest<CompoundTag> compoundDest) {
        compoundDest.set(nbt.getCompound(key));
        return this;
    }
    public CompoundTag getCompound(String key) { return nbt.getCompound(key); }
    public NbtBuilder readCompoundDo(String key, Consumer<CompoundTag> consumer) {
        consumer.accept(nbt.getCompound(key));
        return this;
    }



    public <T> NbtBuilder putSimpleJackson(String key, T obj) throws JsonProcessingException {
        nbt.putString(key, SIMPLE_MAPPER.writeValueAsString(obj));
        return this;
    }
    public <T> T readSimpleJackson(String key, Class<T> type) throws JsonProcessingException {
        String json = nbt.getString(key);
        return SIMPLE_MAPPER.readValue(json, type);
    }
    public <T> T readSimpleJackson(String key, TypeReference<T> typeRef) throws JsonProcessingException {
        String json = nbt.getString(key);
        return SIMPLE_MAPPER.readValue(json, typeRef);
    }


    public <T> NbtBuilder putJackson(String key, T obj, ObjectMapper mapper) throws JsonProcessingException {
        nbt.putString(key, mapper.writeValueAsString(obj));
        return this;
    }
    public <T> T readJackson(String key, Class<T> type, ObjectMapper mapper) throws JsonProcessingException {
        String json = nbt.getString(key);
        return mapper.readValue(json, type);
    }
    public <T> T readJackson(String key, TypeReference<T> typeRef, ObjectMapper mapper) throws JsonProcessingException {
        String json = nbt.getString(key);
        return mapper.readValue(json, typeRef);
    }
    public <T> T readJacksonWhileDo(String key, Class<T> type, ObjectMapper mapper, Consumer<JsonNode> doWhat) {
        String json = nbt.getString(key);
        JsonNode root = jacksonReadTreeRethrown(json, mapper);

        if (doWhat != null)
            doWhat.accept(root);

        return jacksonTreeToValueRethrown(root, type, mapper);
    }
    public <T> NbtBuilder readEachJacksonWhileDo(String key, Class<T> type, ObjectMapper mapper, Consumer<JsonNode> doWhat, Collection<T> dest) {
        return this.readEachAsCompound(key,
            nbt -> {
                String json = NbtBuilder.getValueOfString(nbt, "json");
                JsonNode root = jacksonReadTreeRethrown(json, mapper);

                if (doWhat != null)
                    doWhat.accept(root);

                return jacksonTreeToValueRethrown(root, type, mapper);
            },
            dest
        );
    }
    public <T> NbtBuilder readEachJacksonWhileDo(String key, JavaType type, ObjectMapper mapper, Consumer<JsonNode> doWhat, Collection<T> dest) {
        return this.readEachAsCompound(key,
            nbt -> {
                String json = (nbt).getString(key);
                JsonNode root = jacksonReadTreeRethrown(json, mapper);

                if (doWhat != null)
                    doWhat.accept(root);

                return jacksonTreeToValueRethrown(root, type, mapper);
            },
            dest
        );
    }


    public <T> NbtBuilder putEachSimpleJackson(String key, Iterable<T> objs) {
        return putEach(key, objs, obj -> {
            String json = jacksonWriteAsStringRethrown(obj, SIMPLE_MAPPER);
            return NbtBuilder.valueOfString("json", json);
        });
    }
    public <T> NbtBuilder readEachSimpleJackson(String key, Class<T> type, Collection<T> dest) {
        return this.readEachAsCompound(key, nbt -> {
            String json = getValueOfString(nbt, "json");
            return jacksonReadRethrown(json, type, SIMPLE_MAPPER);
        }, dest);
    }
    public <T> NbtBuilder readEachSimpleJackson(String key, TypeReference<T> type, Collection<T> dest) {
        //todo it's not good to directly cast
        return this.readEachAsCompound(key, nbt -> {
            String json = getValueOfString(nbt, "json");
            return jacksonReadRethrown(json, type, SIMPLE_MAPPER);
        }, dest);
    }

    public <T> NbtBuilder putEachJackson(String key, Iterable<T> objs, ObjectMapper mapper) {
        return this.putEach(key, objs, obj -> {
            String json = jacksonWriteAsStringRethrown(obj, mapper);
            return NbtBuilder.valueOfString("json", json);
        });
    }
    public <T> NbtBuilder readEachJackson(String key, Class<T> type, ObjectMapper mapper, Collection<T> dest) {
        return this.readEachAsCompound(key, nbt -> {
            String json = getValueOfString(nbt, "json");
            return jacksonReadRethrown(json, type, mapper);
        }, dest);
    }
    public <T> NbtBuilder readEachJackson(String key, TypeReference<T> type, ObjectMapper mapper, Collection<T> dest) {
        return this.readEachAsCompound(key, nbt -> {
            String json = getValueOfString(nbt, "json");
            return jacksonReadRethrown(json, type, mapper);
        }, dest);
    }


    public <K, V> NbtBuilder putJacksonMap(String key, Map<K, V> map, ObjectMapper keyMapper, ObjectMapper valMapper) {
        if (map == null) return this;

        return this.putCompound(key,
            new NbtBuilder()
                .putEachJackson("keys", map.keySet(), keyMapper)
                .putEachJackson("values", map.values(), valMapper)
                .get()
        );
    }
    public <K, V> NbtBuilder putSimpleJacksonMap(String key, Map<K, V> map) {
        if (map == null) return this;

        return this.putCompound(key,
            new NbtBuilder()
                .putEachJackson("keys", map.keySet(), SIMPLE_MAPPER)
                .putEachJackson("values", map.values(), SIMPLE_MAPPER)
                .get()
        );
    }
    private <K, V> NbtBuilder readJacksonMapImpl(String key, BiConsumer<NbtBuilder, List<K>> keyReader, BiConsumer<NbtBuilder, List<V>> valReader, Map<K, V> dest) {
        Dest<CompoundTag> mapNbt = new Dest<>();
        this.readCompound(key, mapNbt);
        NbtBuilder mapNB = NbtBuilder.copy(mapNbt.get());

        List<K> keys = new ArrayList<>();
        keyReader.accept(mapNB, keys);
        List<V> values = new ArrayList<>();
        valReader.accept(mapNB, values);

        if (keys.size() != values.size()) {
            EzDebug.warn("key size don't match value size!");
            return this;
        }

        for (int i = 0; i < keys.size(); ++i) {
            dest.put(keys.get(i), values.get(i));
        }
        return this;
    }
    public <K, V> NbtBuilder readJacksonMap(String key, Class<K> keyType, Class<V> valType, ObjectMapper keyMapper, ObjectMapper valMapper, Map<K, V> dest) {
        return this.readJacksonMapImpl(key,
            (mapNB, keys) -> mapNB.readEachJackson("keys", keyType, keyMapper, keys),
            (mapNB, vals) -> mapNB.readEachJackson("values", valType, valMapper, vals),
            dest
        );
    }
    public <K, V> NbtBuilder readJacksonMap(String key, TypeReference<K> keyType, TypeReference<V> valType, ObjectMapper keyMapper, ObjectMapper valMapper, Map<K, V> dest) {
        return this.readJacksonMapImpl(key,
            (mapNB, keys) -> mapNB.readEachJackson("keys", keyType, keyMapper, keys),
            (mapNB, vals) -> mapNB.readEachJackson("values", valType, valMapper, vals),
            dest
        );
    }
    public <K, V> NbtBuilder readSimpleJacksonMap(String key, Class<K> keyType, Class<V> valType, Map<K, V> dest) {
        return this.readJacksonMapImpl(key,
            (mapNB, keys) -> mapNB.readEachJackson("keys", keyType, SIMPLE_MAPPER, keys),
            (mapNB, vals) -> mapNB.readEachJackson("values", valType, SIMPLE_MAPPER, vals),
            dest
        );
    }
    public <K, V> NbtBuilder readSimpleJacksonMap(String key, TypeReference<K> keyType, TypeReference<V> valType, Map<K, V> dest) {
        return this.readJacksonMapImpl(key,
            (mapNB, keys) -> mapNB.readEachJackson("keys", keyType, SIMPLE_MAPPER, keys),
            (mapNB, vals) -> mapNB.readEachJackson("values", valType, SIMPLE_MAPPER, vals),
            dest
        );
    }



    private String jacksonWriteAsStringRethrown(Object obj, ObjectMapper mapper) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    private <T> T jacksonReadRethrown(String json, Class<T> type, ObjectMapper mapper) {
        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    private <T> T jacksonReadRethrown(String json, TypeReference<T> type, ObjectMapper mapper) {
        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonNode jacksonReadTreeRethrown(String json, ObjectMapper mapper) {
        try {
            JsonNode root = mapper.readTree(json);
            return root;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public <T> T jacksonTreeToValueRethrown(JsonNode root, Class<T> type, ObjectMapper mapper) {
        try {
            return mapper.treeToValue(root, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public <T> T jacksonTreeToValueRethrown(JsonNode root, JavaType type, ObjectMapper mapper) {
        try {
            return mapper.treeToValue(root, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }



    public byte[] toBytes() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            NbtIo.writeCompressed(nbt, buffer);
        } catch (
            IOException e) {
            throw new RuntimeException(e);
        }
        return buffer.toByteArray();
    }

}
