package com.lancas.vswap.obsolete.ship.trigger;

/*
public class BallisticsTriggersSaver {
    public static class TriggerDataSerializer extends JsonSerializer<BallisticsTriggersData> {
        @Override
        public void serialize(BallisticsTriggersData value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            JsonSerializeHelper.serializeListIncludeType(gen, value.triggerSbps, SavedBlockPos::serialize);
        }
    }
    public static class TriggerDataDeserializer extends JsonDeserializer<BallisticsTriggersData> {
        @Override
        public BallisticsTriggersData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            BallisticsTriggersData data = new BallisticsTriggersData();
            //data.triggerSbps = data.triggerSbps == null ? new ConcurrentHashMap<>() : data.triggerSbps;
            if (data.triggerSbps == null) {
                EzDebug.fatal("data trigger sbps should never be null");
                return null;
            }

            JsonSerializeHelper.deserializeListIncludeType(p, data.triggerSbps, SavedBlockPos::deserialize);

            return data;
        }
    }
}
*/