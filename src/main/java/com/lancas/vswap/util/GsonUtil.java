package com.lancas.vswap.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lancas.vswap.debug.EzDebug;

import java.util.stream.Stream;

public class GsonUtil {
    public static Stream<JsonElement> elementOfArrayEvenNested(JsonElement element) {
        if (element.isJsonArray()) {
            return element.getAsJsonArray().asList().stream()
                .flatMap(GsonUtil::elementOfArrayEvenNested);
        } else {
            return Stream.of(element);
        }
    }
    public static Stream<JsonObject> objOfArrayEvenNested(JsonElement element) {
        if (element.isJsonArray()) {
            return element.getAsJsonArray().asList().stream()
                .flatMap(GsonUtil::objOfArrayEvenNested);
        } else {
            if (element.isJsonObject()) {
                return Stream.of(element.getAsJsonObject());
            } else {
                EzDebug.warn("the json element is not jsonObj, is null?:" + element.isJsonNull() + ", is primitive?:" + element.isJsonPrimitive() + ", any way will return empty stream");
                return Stream.of();
            }

        }
    }
}
