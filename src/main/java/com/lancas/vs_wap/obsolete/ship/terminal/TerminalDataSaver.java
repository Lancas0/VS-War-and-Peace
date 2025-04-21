package com.lancas.vs_wap.obsolete.ship.terminal;

/*
public class TerminalDataSaver {
    public static class TerminalDataSerializer extends JsonSerializer<BallisticsTerminalData> {
        @Override
        public void serialize(BallisticsTerminalData value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            JsonSerializeHelper.serializeListIncludeType(gen, value.terminalEffectorBps, ITerminalEffector::serialize);
        }
    }
    public static class TerminalDataDeserializer extends JsonDeserializer<BallisticsTerminalData> {

        @Override
        public BallisticsTerminalData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            BallisticsTerminalData data = new BallisticsTerminalData();
            data.terminalEffectorBps = data.terminalEffectorBps == null ? new ArrayList<>() : data.terminalEffectorBps;

            JsonSerializeHelper.deserializeListIncludeType(p, data.terminalEffectorBps, ITerminalEffector::deserializeOverwrite);

            return data;
        }
    }
}
*/