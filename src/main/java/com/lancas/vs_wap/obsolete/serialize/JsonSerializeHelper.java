package com.lancas.vs_wap.obsolete.serialize;

/*
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.lancas.einherjar.debug.EzDebug;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class JsonSerializeHelper {
    public static <T> void serializeListIncludeType(@NotNull JsonGenerator gen, List<T> list, @NotNull Function<T, JSONObject> serializer)
        throws IOException {

        if (list == null || list.isEmpty()) {
            gen.writeNumber(0);
            return;
        }

        int listSize = list.size();
        gen.writeNumber(listSize);

        for (T obj : list) {
            if (obj == null) {
                gen.writeString("NULL");
                continue;
            }

            String className = obj.getClass().getName();
            JSONObject json = serializer.apply(obj);

            gen.writeString(className);
            gen.writeString(json.toJSONString());
        }
    }
    public static <T> void deserializeListIncludeType(@NotNull JsonParser p, @NotNull List<T> dest, @NotNull BiConsumer<T, JSONObject> deserializer)
        throws IOException {
        dest.clear();

        int listSize = p.getIntValue();
        for (int i = 0; i < listSize; ++i) {
            String className = p.getValueAsString();
            if (className.equalsIgnoreCase("NULL")) {
                continue;
            }

            JSONObject json = JSON.parseObject(p.getValueAsString());
            try {
                Object deserialized = Class.forName(className).getDeclaredConstructor().newInstance();

                T t = (T)deserialized;
                deserializer.accept(t, json);
                dest.add(t);

            } catch (Exception e) {
                EzDebug.error("fail to get type:" + className);
            }
        }
    }
    //can not serialzie if key or value is null
    public static <K, V> void serializeMap(
        @NotNull JsonGenerator gen, Map<K, V> map,
        @NotNull Function<K, JSONObject> keySerialize, @NotNull Function<V, JSONObject> valueSerialize
    ) throws IOException {
        if (map == null || map.isEmpty()) {
            gen.writeNumber(0);
            return;
        }

        int mapSize = map.size();
        gen.writeNumber(mapSize);

        for (var entry : map.entrySet()) {
            K key = entry.getKey();
            V val = entry.getValue();

            if (key == null || val == null) {
                gen.writeString("NULL");
                continue;
            }
            String keyClassName = entry.getKey().getClass().getName();
            String valClassName = entry.getValue().getClass().getName();

            JSONObject keyJson = keySerialize.apply(entry.getKey());
            JSONObject valJson = valueSerialize.apply(entry.getValue());

            gen.writeString(keyClassName);
            gen.writeString(keyJson.toJSONString());
            gen.writeString(valClassName);
            gen.writeString(valJson.toJSONString());
        }
    }
    //can not deserialzie if key or value is null
    public static <K, V> void deserializeMap(
        @NotNull JsonParser p, @NotNull Map<K, V> dest,
        @NotNull BiConsumer<K, JSONObject> keyDeserializer, @NotNull BiConsumer<V, JSONObject> valDeserializer
    ) throws IOException {
        dest.clear();

        int mapSize = p.getIntValue();
        for (int i = 0; i < mapSize; ++i) {
            String keyClassName = p.getValueAsString();
            JSONObject keyJson = JSON.parseObject(p.getValueAsString());
            String valClassName = p.getValueAsString();
            JSONObject valJson = JSON.parseObject(p.getValueAsString());

            try {
                Object deKey = Class.forName(keyClassName).getDeclaredConstructor().newInstance();
                Object deVal = Class.forName(valClassName).getDeclaredConstructor().newInstance();

                K key = (K)deKey;
                V val = (V)deVal;

                keyDeserializer.accept(key, keyJson);
                valDeserializer.accept(val, valJson);

                dest.put(key, val);
            } catch (Exception e) {
                EzDebug.error("fail to get entry, key:" + keyClassName + ", val:" + valClassName);
            }
        }
    }


}
*/