package com.lancas.vs_wap.subproject.sandbox.constraint.base;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lancas.vs_wap.subproject.sandbox.ISandBoxWorld;
import com.lancas.vs_wap.subproject.sandbox.constraint.SandBoxConstraintSolver;
import com.lancas.vs_wap.subproject.sandbox.ship.ISandBoxShip;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.Ship;

import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface IConstraint {
    public UUID getUuid();

    public boolean involveShip(UUID inShipUuid);
    public default boolean involveShip(ISandBoxShip inShip) {
        if (inShip == null) return false;
        return involveShip(inShip.getUuid());
    }
    public boolean involveVsShip(long inVsShipId);
    public default boolean involveVsShip(Ship inVsShip) {
        if (inVsShip == null) return false;
        return involveVsShip(inVsShip.getId());
    }

    public void project(ISandBoxWorld<?> world);
    public default void tick(Level level, SandBoxConstraintSolver solver) {}
}
