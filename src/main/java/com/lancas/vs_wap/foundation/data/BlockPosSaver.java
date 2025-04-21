package com.lancas.vs_wap.foundation.data;

/*
public class BlockPosSaver {
    public static class BlockPosSerializer extends StdSerializer<BlockPos> {
        public BlockPosSerializer() { this(null); }
        protected BlockPosSerializer(Class<BlockPos> t) {
            super(t);
        }

        @Override
        public void serialize(BlockPos value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeNumber(value.getX());
            gen.writeNumber(value.getY());
            gen.writeNumber(value.getZ());
        }
    }
    public static class BlockPosDeserializer extends StdDeserializer<BlockPos> {
        public BlockPosDeserializer() { this(null); }
        protected BlockPosDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public BlockPos deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            return new BlockPos(p.getIntValue(), p.getIntValue(), p.getIntValue());
        }
    }
}
*/