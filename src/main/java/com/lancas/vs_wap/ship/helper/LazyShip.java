package com.lancas.vs_wap.ship.helper;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LazyShip {
    private BlockPos bp;
    private Long id;

    @JsonIgnore
    private Ship shipCache;

    private boolean shutdown = false;

    private LazyShip() {}
    public static LazyShip ofId(Long inId) {
        LazyShip lazy = new LazyShip();
        lazy.id = inId;
        return lazy;
    }
    public static LazyShip ofBlockPos(BlockPos inBp) {
        LazyShip lazy = new LazyShip();
        lazy.bp = inBp;
        return lazy;
    }

    @Nullable
    public Ship get(Level level) {
        if (shutdown) return null;
        if (shipCache != null) return shipCache;

        if (bp != null) {
            Ship ship = VSGameUtilsKt.getShipObjectManagingPos(level, bp);
            shipCache =
                ship == null ?
                    VSGameUtilsKt.getShipManagingPos(level, bp) : ship;
        }
        if (id != null) {
            shipCache = ShipUtil.getShipByID(level, id);
        }
        return shipCache;
    }
    @Nullable
    public ServerShip get(ServerLevel level) {
        return (ServerShip)get((Level)level);
    }
    /*public <T extends Ship> T getAs(Level level) {
        if (shutdown) return null;

        return (T)get(level);
    }*/
    @Nullable
    public Ship shutDownIfGetNull(Level level) {
        if (shutdown) return null;

        Ship ship = get(level);
        if (ship == null)
            setShutdown(true);
        return ship;
    }


    public void setShutdown(boolean inShutDown) {
        shutdown = inShutDown;
        if (shutdown) {
            shipCache = null;
        }
    }
}
