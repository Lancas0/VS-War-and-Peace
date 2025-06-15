package com.lancas.vswap.mixins.valkyrien;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;
import org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld;
import org.valkyrienskies.core.impl.shadow.Af;

import java.util.List;
import java.util.Map;

@Mixin(ShipObjectServerWorld.class)
public interface ShipObjectServerWorldAccessor {
    @Accessor(remap = false)
    public List<Af> getDisabledCollisionPairsThisTick();
    @Accessor(remap = false)
    public List<Af> getEnabledCollisionPairsThisTick();

    @Accessor(remap = false)
    public Map<Integer, VSConstraint> getConstraints();
}
