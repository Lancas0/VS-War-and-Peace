package com.lancas.vswap.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.foundation.TriTuple;
import com.lancas.vswap.foundation.api.Dest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.logging.log4j.util.TriConsumer;
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
import java.util.stream.Stream;

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
    public static CompoundTag tagOfString(String s) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("str", s);
        return nbt;
    }
    public static String stringOf(CompoundTag tag) { return tag.getString("str"); }

    public static Long getValueOfLong(CompoundTag tag, String key) { return tag.getLong(key); }


    public static CompoundTag tagOfVector3d(Vector3dc v) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", v.x());
        tag.putDouble("y", v.y());
        tag.putDouble("z", v.z());
        return tag;
    }
    public static CompoundTag tagOfVector3f(Vector3fc v) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("x", v.x());
        tag.putFloat("y", v.y());
        tag.putFloat("z", v.z());
        return tag;
    }
    public static CompoundTag tagOfVector3i(Vector3ic v) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("x", v.x());
        tag.putInt("y", v.y());
        tag.putInt("z", v.z());
        return tag;
    }
    public static Vector3d vector3dOf(CompoundTag tag, Vector3d dest) {
        dest.x = tag.getDouble("x");
        dest.y = tag.getDouble("y");
        dest.z = tag.getDouble("z");
        return dest;
    }
    public static Vector3f vector3fOf(CompoundTag tag, Vector3f dest) {
        dest.x = tag.getFloat("x");
        dest.y = tag.getFloat("y");
        dest.z = tag.getFloat("z");
        return dest;
    }
    public static Vector3i vector3iOf(CompoundTag tag, Vector3i dest) {
        dest.x = tag.getInt("x");
        dest.y = tag.getInt("y");
        dest.z = tag.getInt("z");
        return dest;
    }
    public static Vector3d vector3dOf(CompoundTag tag) { return vector3dOf(tag, new Vector3d()); }
    public static Vector3f vector3fOf(CompoundTag tag) { return vector3fOf(tag, new Vector3f()); }
    public static Vector3i vector3iOf(CompoundTag tag) { return vector3iOf(tag, new Vector3i()); }

    public static CompoundTag tagOfMatrix3d(Matrix3dc matrix3dc) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("m00", matrix3dc.m00()); tag.putDouble("m01", matrix3dc.m01()); tag.putDouble("m02", matrix3dc.m02());
        tag.putDouble("m10", matrix3dc.m10()); tag.putDouble("m11", matrix3dc.m11()); tag.putDouble("m12", matrix3dc.m12());
        tag.putDouble("m20", matrix3dc.m20()); tag.putDouble("m21", matrix3dc.m21()); tag.putDouble("m22", matrix3dc.m22());
        return tag;
    }
    public static Matrix3d matrix3dOf(CompoundTag tag, Matrix3d dest) {
        dest.m00 = tag.getDouble("m00"); dest.m01 = tag.getDouble("m01"); dest.m02 = tag.getDouble("m02");
        dest.m10 = tag.getDouble("m10"); dest.m11 = tag.getDouble("m11"); dest.m12 = tag.getDouble("m12");
        dest.m20 = tag.getDouble("m20"); dest.m21 = tag.getDouble("m21"); dest.m22 = tag.getDouble("m22");
        return dest;
    }
    public static Matrix3d matrix3dOf(CompoundTag tag) { return matrix3dOf(tag, new Matrix3d()); }

    public static CompoundTag tagOfMatrix4d(Matrix4dc matrix) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("m00", matrix.m00()); tag.putDouble("m01", matrix.m01()); tag.putDouble("m02", matrix.m02()); tag.putDouble("m03", matrix.m03());
        tag.putDouble("m10", matrix.m10()); tag.putDouble("m11", matrix.m11()); tag.putDouble("m12", matrix.m12()); tag.putDouble("m13", matrix.m13());
        tag.putDouble("m20", matrix.m20()); tag.putDouble("m21", matrix.m21()); tag.putDouble("m22", matrix.m22()); tag.putDouble("m23", matrix.m23());
        tag.putDouble("m30", matrix.m30()); tag.putDouble("m31", matrix.m31()); tag.putDouble("m32", matrix.m32()); tag.putDouble("m33", matrix.m33());
        return tag;
    }
    public static Matrix4d matrix4dOf(CompoundTag tag, Matrix4d dest) {
        dest.m00(tag.getDouble("m00")).m01(tag.getDouble("m01")).m02(tag.getDouble("m02")).m03(tag.getDouble("m03"));
        dest.m10(tag.getDouble("m10")).m11(tag.getDouble("m11")).m12(tag.getDouble("m12")).m13(tag.getDouble("m13"));
        dest.m20(tag.getDouble("m20")).m21(tag.getDouble("m21")).m22(tag.getDouble("m22")).m23(tag.getDouble("m23"));
        dest.m30(tag.getDouble("m30")).m31(tag.getDouble("m31")).m32(tag.getDouble("m32")).m33(tag.getDouble("m33"));
        return dest;
    }
    public static Matrix4d matrix4dOf(CompoundTag tag) { return matrix4dOf(tag, new Matrix4d()); }


    private NbtBuilder(Supplier<CompoundTag> nbtSupplier) { nbt = nbtSupplier.get(); }
    public static NbtBuilder copy(CompoundTag tag)   { return new NbtBuilder(tag::copy); }
    public static NbtBuilder modify(CompoundTag tag) { return new NbtBuilder(() -> tag); }
    public static NbtBuilder ofStack(ItemStack stack) { return new NbtBuilder(stack::getOrCreateTag); }

    public boolean contains(String key) {
        //CompoundTag getTag = nbt.getCompound(key);
        //return getTag != null && !getTag.isEmpty();
        return nbt.contains(key);
    }


    public NbtBuilder putInt(String key, int v) {
        nbt.putInt(key, v);
        return this;
    }
    public NbtBuilder putLong(String key, long v) {
        nbt.putLong(key, v);
        return this;
    }
    public NbtBuilder putDouble(String key, double v) {
        nbt.putDouble(key, v);
        return this;
    }
    public NbtBuilder putFloat(String key, float v) {
        nbt.putFloat(key, v);
        return this;
    }

    public NbtBuilder readInt(String key, @NotNull Dest<Integer> v) {
        if (nbt.contains(key))
            v.set(nbt.getInt(key));
        return this;
    }
    public int getInt(String key) { return nbt.getInt(key); }
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
    public NbtBuilder readString(String key, Dest<String> dest) {
        dest.set(nbt.getString(key));
        return this;
    }
    public NbtBuilder readStringDo(String key, Consumer<String> consumer) {
        consumer.accept(nbt.getString(key));
        return this;
    }

    public static CompoundTag tagOfUuid(UUID uuid) { CompoundTag tag = new CompoundTag(); tag.putUUID("uuid", uuid); return tag; }
    public static UUID uuidOf(CompoundTag tag) { EzDebug.warn("uuid of tag by " + tag.toString()); return tag.getUUID("uuid"); }
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

    public NbtBuilder putBytes(String key, byte[] bytes) { nbt.putByteArray(key, bytes); return this; }
    public NbtBuilder readBytes(String key, Dest<byte[]> bytesDest) { Dest.setIfExistDest(bytesDest, nbt.getByteArray(key)); return this; }
    public NbtBuilder readBytesDo(String key, Consumer<byte[]> consumer) { consumer.accept(nbt.getByteArray(key)); return this; }
    public byte[] getBytes(String key) { return nbt.getByteArray(key); }


    public NbtBuilder putVector3d(String key, Vector3dc vec) {
        return this.putCompound(key, tagOfVector3d(vec));
    }
    public NbtBuilder putVector3f(String key, Vector3fc vec) {
        return this.putCompound(key, tagOfVector3f(vec));
    }
    public NbtBuilder putVector3i(String key, Vector3ic vec) {
        return this.putCompound(key, tagOfVector3i(vec));
    }
    public NbtBuilder readVector3d(String key, Vector3d dest) {
        CompoundTag nbt = this.getCompound(key);
        dest.set(vector3dOf(nbt));
        return this;
    }
    public NbtBuilder readVector3f(String key, Vector3f dest) {
        CompoundTag nbt = this.getCompound(key);
        dest.set(vector3fOf(nbt));
        return this;
    }
    public NbtBuilder readVector3i(String key, Vector3i dest) {
        CompoundTag nbt = this.getCompound(key);
        dest.set(vector3iOf(nbt));
        return this;
    }
    public Vector3d getVector3d(String key, Vector3d dest) {
        CompoundTag nbt = this.getCompound(key);
        return dest.set(vector3dOf(nbt));
    }
    public Vector3f getVector3f(String key, Vector3f dest) {
        CompoundTag nbt = this.getCompound(key);
        return dest.set(vector3fOf(nbt));
    }
    public Vector3i getVector3i(String key, Vector3i dest) {
        CompoundTag nbt = this.getCompound(key);
        return dest.set(vector3iOf(nbt));
    }
    public Vector3d getNewVector3d(String key) { return getVector3d(key, new Vector3d()); }
    public Vector3f getNewVector3f(String key) { return getVector3f(key, new Vector3f()); }
    public Vector3i getNewVector3i(String key) { return getVector3i(key, new Vector3i()); }


    public NbtBuilder putMatrix3d(String key, Matrix3dc matrix) { return this.putCompound(key, tagOfMatrix3d(matrix)); }
    public NbtBuilder readMatrix3d(String key, Matrix3d dest) { matrix3dOf(getCompound(key), dest); return this; }
    public Matrix3d getMatrix3d(String key, Matrix3d dest) { return matrix3dOf(getCompound(key), dest); }
    public Matrix3d getMatrix3d(String key) { return matrix3dOf(getCompound(key), new Matrix3d()); }

    public NbtBuilder putMatrix4d(String key, Matrix4dc matrix) { return this.putCompound(key, tagOfMatrix4d(matrix)); }
    public NbtBuilder readMatrix4d(String key, Matrix4d dest) { matrix4dOf(getCompound(key), dest); return this; }
    public Matrix4d getMatrix4d(String key, Matrix4d dest) { return matrix4dOf(getCompound(key), dest); }
    public Matrix4d getMatrix4d(String key) { return matrix4dOf(getCompound(key), new Matrix4d()); }


    public <T> NbtBuilder readIf(String key, Dest<T> val, BiPredicate<NbtBuilder, String> checker, BiFunction<NbtBuilder, String, T> reader) {
        if (checker.test(this, key))
            val.set(reader.apply(this, key));
        return this;
    }
    public <T> NbtBuilder readDoIf(String key, Consumer<T> consumer, BiPredicate<NbtBuilder, String> checker, BiFunction<NbtBuilder, String, T> reader) {
        if (checker.test(this, key))
            consumer.accept(reader.apply(this, key));
        return this;
    }
    public @Nullable <T> T getIf(String key, BiPredicate<NbtBuilder, String> checker, BiFunction<NbtBuilder, String, T> getter) {
        if (checker.test(this, key))
            return getter.apply(this, key);
        return null;
    }
    public <T> NbtBuilder putIf(String key, T val, BiPredicate<NbtBuilder, T> checker, TriConsumer<NbtBuilder, String, T> putter) {
        if (checker.test(this, val))
            putter.accept(this, key, val);
        return this;
    }
    public <T> NbtBuilder putIf(String key, Supplier<T> val, Predicate<NbtBuilder> checker, TriConsumer<NbtBuilder, String, T> putter) {
        if (checker.test(this))
            putter.accept(this, key, val.get());
        return this;
    }

    public <T> NbtBuilder readIfExist(String key, Dest<T> val, BiFunction<NbtBuilder, String, T> reader) {
        return readIf(key, val, NbtBuilder::contains, reader);
    }
    public <T> NbtBuilder readDoIfExist(String key, Consumer<T> consumer, BiFunction<NbtBuilder, String, T> reader) {
        return readDoIf(key, consumer, NbtBuilder::contains, reader);
    }
    public NbtBuilder readCompoundDoIfExist(String key, Consumer<CompoundTag> consumer) {
        return readDoIf(key, consumer, NbtBuilder::contains, NbtBuilder::getCompound);
    }
    public @Nullable <T> T getIfExist(String key, BiFunction<NbtBuilder, String, T> getter) {
        return getIf(key, NbtBuilder::contains, getter);
    }

    public <T> NbtBuilder putIfNonNull(String key, T val, TriConsumer<NbtBuilder, String, T> putter) {
        return putIf(key, val, (b, v) -> Objects.nonNull(v), putter);
    }
    public <T extends Dest<V>, V> NbtBuilder putIfHasVal(String key, T dest, TriConsumer<NbtBuilder, String, V> putter) {
        return putIf(key, dest, (b, v) -> v.hasValue(), (b, k, v) -> putter.accept(b, k, v.get()));
    }

    public <T extends INBTSerializable<CompoundTag>> NbtBuilder putNBTSerializableIfNonNull(String key, T val) {
        return putIf(key, val, (b, v) -> Objects.nonNull(v), (b, k, v) -> b.putCompound(k, v.serializeNBT()));
    }
    /*public NbtBuilder putIf(boolean condition, String key, CompoundTag tag) {
        EzDebug.warn("key:" + key + ", condition:" + condition);
        if (condition) {
            return putCompound(key, tag);
        }
        return this;
    }*/
    /*public <T> NbtBuilder putIf(Predicate<T> checker, Function<T, CompoundTag> saver, String key, T obj) {
        if (checker.test(obj)) {
            return putCompound(key, saver.apply(obj));
        }
        return this;
    }
    public <T> NbtBuilder putIfNonNull(Function<T, CompoundTag> saver, String key, T obj) {
        return putIf(Objects::nonNull, saver, key, obj);
    }*/

    /*public NbtBuilder readIf(boolean condition, String key, Dest<CompoundTag> dest) {
        if (condition)
            return readCompound(key, dest);
        return this;
    }
    public NbtBuilder readDoIf(boolean condition, String key, Consumer<CompoundTag> consumer) {
        if (condition)
            return readCompoundDo(key, consumer);
        return this;
    }
    public <T> NbtBuilder readIf(Predicate<NbtBuilder> checker, String key, Dest<CompoundTag> dest) {
        return readIf(checker.test(this), key, dest);
    }
    public <T> NbtBuilder readDoIf(Predicate<NbtBuilder> checker, String key, Consumer<CompoundTag> consumer) {
        return readDoIf(checker.test(this), key, consumer);
    }
    public <T> NbtBuilder readIfExist(String key, Dest<CompoundTag> dest) {
        return readIf(self -> self.contains(key), key, dest);
    }
    public <T> NbtBuilder readDoIfExist(String key, Consumer<CompoundTag> consumer) {
        EzDebug.warn("exist?:" + contains(key));
        return readDoIf(self -> self.contains(key), key, consumer);
    }*/

    public NbtBuilder ifExist(String key, Consumer<NbtBuilder> consumer) {
        if (contains(key))
            consumer.accept(this);
        return this;
    }





    public static CompoundTag tagOfEnum(Enum<?> enumVal) { CompoundTag tag = new CompoundTag(); tag.putString("enum", enumVal.name()); return tag; };
    public static <T extends Enum<T>> T enumOf(CompoundTag tag, Class<T> enumType) {
        String enumName = tag.getString("enum");
        try {
            T val = Enum.valueOf(enumType, enumName);
            return val;
        } catch (Exception e) {
            EzDebug.error("fail to get enum value with type:" + enumType.getName() + ", and name:" + enumName);
            e.printStackTrace();
            return null;
        }
    };
    public NbtBuilder putEnum(String key, Enum<?> enumVal) { nbt.putString(key, enumVal.name()); return this; }
    public <T extends Enum<T>> NbtBuilder readEnum(String key, Class<T> enumType, Dest<T> dest) {
        String enumName = nbt.getString(key);
        try {
            T val = Enum.valueOf(enumType, enumName);
            dest.set(val);
        } catch (Exception e) {
            EzDebug.warn("fail to get enum value with type:" + enumType.getName() + ", and name:" + enumName);
            e.printStackTrace();
        }

        return this;
    }
    public <T extends Enum<T>> NbtBuilder readEnumDo(String key, Class<T> enumType, Consumer<T> consumer) {
        String enumName = nbt.getString(key);
        try {
            T val = Enum.valueOf(enumType, nbt.getString(key));
            consumer.accept(val);
        } catch (Exception e) {
            EzDebug.warn("fail to get enum value with type:" + enumType.getName() + ", and name:" + enumName);
            e.printStackTrace();
        }

        return this;
    }
    public <T extends Enum<T>> T getEnum(String key, Class<T> enumType) {
        String enumName = nbt.getString(key);
        try {
            return Enum.valueOf(enumType, nbt.getString(key));
        } catch (Exception e) {
            EzDebug.warn("fail to get enum value with type:" + enumType.getName() + ", and name:" + enumName);
            e.printStackTrace();
        }
        return null;
    }


    public NbtBuilder putAABBi(String key, AABBic aabb) {
        nbt.put(key, new NbtBuilder()
            .putInt("minx", aabb.minX())
            .putInt("miny", aabb.minY())
            .putInt("minz", aabb.minZ())
            .putInt("maxx", aabb.maxX())
            .putInt("maxy", aabb.maxY())
            .putInt("maxz", aabb.maxZ())
            .get()
        );
        return this;
    }
    public NbtBuilder putAABBd(String key, AABBdc aabb) {
        nbt.put(key, new NbtBuilder()
            .putDouble("minx", aabb.minX())
            .putDouble("miny", aabb.minY())
            .putDouble("minz", aabb.minZ())
            .putDouble("maxx", aabb.maxX())
            .putDouble("maxy", aabb.maxY())
            .putDouble("maxz", aabb.maxZ())
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
            .readIntDo("maxy", v -> max.y = v)
            .readIntDo("maxz", v -> max.z = v);

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
            .readDoubleDo("maxy", v -> max.y = v)
            .readDoubleDo("maxz", v -> max.z = v);

        dest.setMin(min);
        dest.setMax(max);
        return this;
    }
    public AABBd getAABBd(String key) {
        AABBd aabb = new AABBd();
        readAABBd(key, aabb);
        return aabb;
    }
    public AABBi getAABBi(String key) {
        AABBi aabb = new AABBi();
        readAABBi(key, aabb);
        return aabb;
    }

    public NbtBuilder putQuaternion(String key, Quaterniondc q) {
        return this.putCompound(key,
            new NbtBuilder()
                .putDouble("x", q.x())
                .putDouble("y", q.y())
                .putDouble("z", q.z())
                .putDouble("w", q.w())
                .get()
        );
    }
    public NbtBuilder putQuaternion(String key, Quaternionfc q) {
        return this.putCompound(key,
            new NbtBuilder()
                .putFloat("x", q.x())
                .putFloat("y", q.y())
                .putFloat("z", q.z())
                .putFloat("w", q.w())
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


    public NbtBuilder putBoolean(String key, boolean v) { nbt.putBoolean(key, v); return this; }
    public NbtBuilder readBoolean(String key, Dest<Boolean> dest) { dest.set(nbt.getBoolean(key)); return this; }
    public NbtBuilder readBooleanDo(String key, Consumer<Boolean> consumer) { consumer.accept(nbt.getBoolean(key)); return this; }
    public Boolean getBoolean(String key) { return nbt.getBoolean(key); }

    public <T> NbtBuilder putStream(String key, Stream<T> stream, Function<T, Tag> nbtCreator) {
        ListTag listTag = new ListTag();
        stream.forEach(x -> listTag.add(nbtCreator.apply(x)));
        nbt.put(key, listTag);
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

    private <T> NbtBuilder readEachDoImpl(String key, byte target, Consumer<Tag> consumer) {
        ListTag listTag = nbt.getList(key, target);
        if (listTag.isEmpty()) return this;

        for (Tag eachNbt : listTag) consumer.accept(eachNbt);
        return this;
    }
    public <T> NbtBuilder readEachCompoundDo(String key, Consumer<CompoundTag> consumer) {
        return readEachDoImpl(key, Tag.TAG_COMPOUND, t -> consumer.accept((CompoundTag)t));
    }

    private <T> NbtBuilder readEachImpl(String key, Function<Tag, T> nbtReader, byte target, Collection<T> dest) {
        ListTag listTag = nbt.getList(key, target);
        if (listTag.isEmpty()) return this;

        for (Tag eachNbt : listTag) {
            dest.add(nbtReader.apply(eachNbt));
        }
        return this;
    }
    public <T> NbtBuilder readEachCompound(String key, Function<CompoundTag, T> nbtReader, Collection<T> dest) {
        return readEachImpl(key, (tag) -> nbtReader.apply((CompoundTag)tag), Tag.TAG_COMPOUND, dest);
    }
    public <T> NbtBuilder readEachCompoundOverwrite(String key, Function<CompoundTag, T> nbtReader, Collection<T> dest) {
        dest.clear();
        return readEachImpl(key, (tag) -> nbtReader.apply((CompoundTag)tag), Tag.TAG_COMPOUND, dest);
    }
    public <T> NbtBuilder readEachList(String key, Function<ListTag, T> nbtReader, Collection<T> dest) {
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
    public <K, V> NbtBuilder readMapDo(String key, Function<CompoundTag, BiTuple<K, V>> entryNbtReader, BiConsumer<K, V> consumer) {
        ListTag listTag = nbt.getList(key, Tag.TAG_COMPOUND);
        if (listTag.isEmpty()) return this;

        for (Tag itemNbt : listTag) {
            BiTuple<K, V> entry = entryNbtReader.apply((CompoundTag)itemNbt);
            consumer.accept(entry.getFirst(), entry.getSecond());
        }
        return this;
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
    public NbtBuilder addIntoNewIfAbsentList(String key, Tag tag) {
        var listTag = (ListTag)nbt.get(key);
        if (listTag == null) {
            listTag = new ListTag();
            nbt.put(key, listTag);
        }

        listTag.add(tag);
        nbt.put(key, listTag);
        return this;
        //listTagDest.set((ListTag)nbt.get(key)); return this;
    }

    public NbtBuilder withTagDo(Consumer<CompoundTag> consumer) {
        if (consumer != null)
            consumer.accept(nbt);
        return this;
    }

    public CompoundTag get() { return nbt; }


    public NbtBuilder putBlock(String key, BlockPos pos, BlockState state, @Nullable BlockEntity be) {
        nbt.put(key, tagOfBlock(pos, state, be));
        return this;
    }
    public static CompoundTag tagOfBlockPos(BlockPos pos) { return NbtUtils.writeBlockPos(pos); }
    public static BlockPos blockPosOf(CompoundTag tag) { return NbtUtils.readBlockPos(tag); }

    public static CompoundTag tagOfBlock(TriTuple<BlockPos, BlockState, CompoundTag> tuple) {
        return tagOfBlock(tuple.getFirst(), tuple.getSecond(), tuple.getThird());
    }
    public static TriTuple<BlockPos, BlockState, CompoundTag> blockOf(CompoundTag tag) {
        Dest<BlockPos> bpDest = new Dest<>();
        Dest<BlockState> stateDest = new Dest<>();
        Dest<CompoundTag> beNbtDest = new Dest<>();

        blockOfTag(tag, bpDest, stateDest, beNbtDest);
        return new TriTuple<>(bpDest.get(), stateDest.get(), beNbtDest.get());
    }

    public static CompoundTag tagOfBlock(BlockPos pos, BlockState state, @Nullable BlockEntity be) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());

        tag.put("state", NbtUtils.writeBlockState(state));
        if (be != null)
            tag.put("be", be.saveWithFullMetadata());
        return tag;
    }
    public static CompoundTag tagOfBlock(BlockPos pos, BlockState state, @Nullable CompoundTag beNbt) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());

        tag.put("state", NbtUtils.writeBlockState(state));
        if (beNbt != null)
            tag.put("be", beNbt);
        return tag;
    }
    public static void blockOfTag(CompoundTag tag, Dest<BlockPos> bpDest, Dest<BlockState> stateDest, Dest<CompoundTag> beTag) {
        bpDest.set(new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")));
        stateDest.set(NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("state")));

        if (tag.contains("be")) {
            beTag.set(tag.getCompound("be"));
        }
    }
    public static void blockOfTagDo(CompoundTag tag, TriConsumer<BlockPos, BlockState, CompoundTag> consumer) {
        consumer.accept(
            new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")),
            NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("state")),
            tag.contains("be") ? null : tag.getCompound("be")
        );
    }

    public NbtBuilder putBlockState(String key, BlockState state) { nbt.put(key, NbtUtils.writeBlockState(state)); return this; }
    public NbtBuilder readBlockState(String key, Dest<BlockState> dest) {
        dest.set(NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), nbt.getCompound(key)));
        return this;
    }
    public NbtBuilder readBlockStateDo(String key, Consumer<BlockState> consumer) {
        consumer.accept(NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), nbt.getCompound(key)));
        return this;
    }
    public BlockState getBlockState(String key) {
        return NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), nbt.getCompound(key));
    }

    public NbtBuilder putBlockPos(String key, BlockPos bp) { nbt.put(key, tagOfBlockPos(bp)); return this; }
    public NbtBuilder readBlockPos(String key, Dest<BlockPos> dest) { dest.set(blockPosOf(nbt.getCompound(key))); return this; }
    public NbtBuilder readBlockPosDo(String key, Consumer<BlockPos> consumer) { consumer.accept(blockPosOf(nbt.getCompound(key))); return this; }
    public BlockPos getBlockPos(String key) { return blockPosOf(nbt.getCompound(key)); }

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



    public <T> NbtBuilder putSimpleJackson(String key, T obj) {
        nbt.putString(key, jacksonWriteAsStringRethrown(obj, SIMPLE_MAPPER));
        return this;
    }
    public <T> T readSimpleJackson(String key, Class<T> type) {
        String json = nbt.getString(key);
        return jacksonReadRethrown(json, type, SIMPLE_MAPPER);// SIMPLE_MAPPER.readValue(json, type);
    }
    public <T> NbtBuilder readSimpleJacksonDo(String key, Class<T> type, Consumer<T> consumer) {
        String json = nbt.getString(key);
        consumer.accept(jacksonReadRethrown(json, type, SIMPLE_MAPPER));// SIMPLE_MAPPER.readValue(json, type);
        return this;
    }
    public <T> T readSimpleJackson(String key, TypeReference<T> type) {
        String json = nbt.getString(key);
        return jacksonReadRethrown(json, type, SIMPLE_MAPPER);//SIMPLE_MAPPER.readValue(json, typeRef);
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
        return this.readEachCompound(key,
            nbt -> {
                String json = NbtBuilder.stringOf(nbt);
                JsonNode root = jacksonReadTreeRethrown(json, mapper);

                if (doWhat != null)
                    doWhat.accept(root);

                return jacksonTreeToValueRethrown(root, type, mapper);
            },
            dest
        );
    }
    public <T> NbtBuilder readEachJacksonWhileDo(String key, JavaType type, ObjectMapper mapper, Consumer<JsonNode> doWhat, Collection<T> dest) {
        return this.readEachCompound(key,
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
            return NbtBuilder.tagOfString(json);
        });
    }
    public <T> NbtBuilder readEachSimpleJackson(String key, Class<T> type, Collection<T> dest) {
        return this.readEachCompound(key, nbt -> {
            String json = stringOf(nbt);
            return jacksonReadRethrown(json, type, SIMPLE_MAPPER);
        }, dest);
    }
    public <T> NbtBuilder readEachSimpleJackson(String key, TypeReference<T> type, Collection<T> dest) {
        //todo it's not good to directly cast
        return this.readEachCompound(key, nbt -> {
            String json = stringOf(nbt);
            return jacksonReadRethrown(json, type, SIMPLE_MAPPER);
        }, dest);
    }

    public <T> NbtBuilder putEachJackson(String key, Iterable<T> objs, ObjectMapper mapper) {
        return this.putEach(key, objs, obj -> {
            String json = jacksonWriteAsStringRethrown(obj, mapper);
            return NbtBuilder.tagOfString(json);
        });
    }
    public <T> NbtBuilder readEachJackson(String key, Class<T> type, ObjectMapper mapper, Collection<T> dest) {
        return this.readEachCompound(key, nbt -> {
            String json = stringOf(nbt);
            return jacksonReadRethrown(json, type, mapper);
        }, dest);
    }
    public <T> NbtBuilder readEachJackson(String key, TypeReference<T> type, ObjectMapper mapper, Collection<T> dest) {
        return this.readEachCompound(key, nbt -> {
            String json = stringOf(nbt);
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



    public static String jacksonWriteAsStringRethrown(Object obj, ObjectMapper mapper) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> T jacksonReadRethrown(String json, Class<T> type, ObjectMapper mapper) {
        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> T jacksonReadRethrown(String json, TypeReference<T> type, ObjectMapper mapper) {
        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode jacksonReadTreeRethrown(String json, ObjectMapper mapper) {
        try {
            JsonNode root = mapper.readTree(json);
            return root;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> T jacksonTreeToValueRethrown(JsonNode root, Class<T> type, ObjectMapper mapper) {
        try {
            return mapper.treeToValue(root, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> T jacksonTreeToValueRethrown(JsonNode root, JavaType type, ObjectMapper mapper) {
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
