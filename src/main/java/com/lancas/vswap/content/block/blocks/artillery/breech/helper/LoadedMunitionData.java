package com.lancas.vswap.content.block.blocks.artillery.breech.helper;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vswap.subproject.sandbox.SandBoxServerWorld;
import com.lancas.vswap.subproject.sandbox.constraint.base.ISliderOrientationConstraint;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxServerShip;
import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public record LoadedMunitionData(UUID shipUuid, UUID constraintUuid) {
    private LoadedMunitionData() { this(null, null); }

    public SandBoxServerShip getShip(ServerLevel level) { return SandBoxServerWorld.getOrCreate(level).getServerShip(shipUuid); }
    public ISliderOrientationConstraint getConstraint(ServerLevel level) { return SandBoxServerWorld.getOrCreate(level).getConstraintSolver().getConstraint(constraintUuid); }
}