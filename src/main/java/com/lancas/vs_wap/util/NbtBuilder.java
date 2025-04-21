package com.lancas.vs_wap.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lancas.vs_wap.foundation.api.Dest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public class NbtBuilder {
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

    private NbtBuilder(CompoundTag copyFrom) { nbt = copyFrom.copy(); }
    public static NbtBuilder copy(CompoundTag tag) {
        NbtBuilder builder = new NbtBuilder(tag);
        return builder;
    }

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
    public <T> NbtBuilder readEachCompound(String key, Function<CompoundTag, T> nbtReader, Collection<T> dest) {
        return readEachImpl(key, (tag) -> nbtReader.apply((CompoundTag)tag), Tag.TAG_COMPOUND, dest);
    }
    public <T> NbtBuilder readEachList(String key, Function<ListTag, T> nbtReader, Collection<T> dest) {
        return readEachImpl(key, (tag) -> nbtReader.apply((ListTag)tag), Tag.TAG_LIST, dest);
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



    public <T> NbtBuilder putSimpleJackson(String key, T obj) throws JsonProcessingException {
        nbt.putString(key, new ObjectMapper().writeValueAsString(obj));
        return this;
    }
    public <T> T readSimpleJackson(String key, Class<T> type) throws JsonProcessingException {
        String json = nbt.getString(key);
        return new ObjectMapper().readValue(json, type);
    }
    public <T> T readSimpleJackson(String key, TypeReference<T> typeRef) throws JsonProcessingException {
        String json = nbt.getString(key);
        return new ObjectMapper().readValue(json, typeRef);
    }


    public <T> NbtBuilder putJackson(String key, T obj, ObjectMapper mapper) throws JsonProcessingException {
        //ObjectMapper mapper = new ObjectMapper()
        //    .activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

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
        ObjectMapper mapper = new ObjectMapper();

        return putEach(key, objs, obj -> {
            String json = jacksonWriteAsStringRethrown(obj, mapper);
            return NbtBuilder.valueOfString("json", json);
        });
    }
    public <T> NbtBuilder readEachSimpleJackson(String key, Class<T> type, Collection<T> dest) {
        ObjectMapper mapper = new ObjectMapper();

        return this.readEachCompound(key, nbt -> {
            String json = getValueOfString(nbt, "json");
            return jacksonReadRethrown(json, type, mapper);
        }, dest);
    }
    public <T> NbtBuilder readEachSimpleJackson(String key, TypeReference<T> type, Collection<T> dest) {
        ObjectMapper mapper = new ObjectMapper();

        //todo it's not good to directly cast
        return this.readEachCompound(key, nbt -> {
            String json = getValueOfString(nbt, "json");
            return jacksonReadRethrown(json, type, mapper);
        }, dest);
    }


    public <T> NbtBuilder putEachJackson(String key, Iterable<T> objs, ObjectMapper mapper) {
        return this.putEach(key, objs, obj -> {
            String json = jacksonWriteAsStringRethrown(obj, mapper);
            return NbtBuilder.valueOfString("json", json);
        });
    }
    public <T> NbtBuilder readEachJackson(String key, Class<T> type, ObjectMapper mapper, Collection<T> dest) {
        return this.readEachCompound(key, nbt -> {
            String json = getValueOfString(nbt, "json");
            return jacksonReadRethrown(json, type, mapper);
        }, dest);
    }
    public <T> NbtBuilder readEachJackson(String key, TypeReference<T> type, ObjectMapper mapper, Collection<T> dest) {
        return this.readEachCompound(key, nbt -> {
            String json = getValueOfString(nbt, "json");
            return jacksonReadRethrown(json, type, mapper);
        }, dest);
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
