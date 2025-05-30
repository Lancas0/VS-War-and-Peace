package com.lancas.vswap.ship.helper;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.data.SavedBlockPos;
import com.lancas.vswap.util.SerializeUtil;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

import java.io.IOException;
import java.io.Serializable;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LazyShip {
    @FunctionalInterface
    public static interface LazyShipGetter extends Serializable {
        public Ship apply(Level level, Object owner);
    }
    private static class LazyShipGetterSerializer extends JsonSerializer<LazyShipGetter> {
        @Override
        public void serialize(LazyShipGetter value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeBinary(SerializeUtil.serialize(value));
        }
    }
    private static class LazyShipGetterDeserializer extends JsonDeserializer<LazyShipGetter> {
        @Override
        public LazyShipGetter deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            try {
                return SerializeUtil.deserilaize(p.getBinaryValue());
            } catch (ClassNotFoundException e) {
                EzDebug.error("fail to serialize lazy ship getter");
                throw new RuntimeException(e);
            }
        }
    }

    @JsonSerialize(using = LazyShipGetterSerializer.class)
    @JsonDeserialize(using = LazyShipGetterDeserializer.class)
    private LazyShipGetter getter;

    @JsonIgnore
    private Ship shipCache;

    private boolean shutdown = false;

    private LazyShip() {}
    public LazyShip(@NotNull LazyShipGetter inGetter) {
        getter = inGetter;
    }
    public static LazyShip ofId(long id) {
        LazyShip lazy = new LazyShip();
        lazy.getter = (l, owner) -> ShipUtil.getShipByID(l, id);
        return lazy;
    }
    public static LazyShip ofBlockPos(SavedBlockPos sbp) {
        LazyShip lazy = new LazyShip();
        lazy.getter = (l, owner) -> ShipUtil.getShipAt(l, sbp.toBp());
        return lazy;
    }

    @Nullable
    public Ship get(Level level, Object owner) {
        if (shutdown) return null;
        if (shipCache != null) return shipCache;

        try {
            shipCache = getter.apply(level, owner);
        } catch (Exception e) {
            EzDebug.warn("fail to get lazy ship");
            e.printStackTrace();
        }

        return shipCache;
    }
    @Nullable
    public ServerShip get(ServerLevel level, Object owner) {
        return (ServerShip)get((Level)level, owner);
    }
    @Nullable
    public ClientShip get(ClientLevel level, Object owner) {
        return (ClientShip)get((Level)level, owner);
    }
    /*public <T extends Ship> T getAs(Level level) {
        if (shutdown) return null;

        return (T)get(level);
    }*/
    @Nullable
    public Ship shutDownIfGetNull(Level level, Object owner) {
        if (shutdown) return null;

        Ship shipTemp = get(level, owner);
        if (shipTemp == null)
            setShutdown(true);
        return shipTemp;
    }


    public void setShutdown(boolean inShutDown) {
        shutdown = inShutDown;
        if (shutdown) {
            shipCache = null;
        }
    }
}
