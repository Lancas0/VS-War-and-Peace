package com.lancas.vswap.ship.feature.hide;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lancas.vswap.debug.EzDebug;
import lombok.NoArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ServerShip;

//must have an empty construct
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
public interface IHideAndSeek {
    public boolean hide(@NotNull ServerLevel level, @NotNull ServerShip ship);
    public boolean seek(@NotNull ServerLevel level, @NotNull ServerShip ship);

    public default void onSuccessHide(@NotNull ServerShip ship) {
        EzDebug.highlight("success hide");
    }
    public default void onSuccessSeek(@NotNull ServerShip ship) {
        EzDebug.highlight("success seek");
    }
}
