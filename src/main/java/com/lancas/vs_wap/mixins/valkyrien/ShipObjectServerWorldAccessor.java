package com.lancas.vs_wap.mixins.valkyrien;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;
import org.valkyrienskies.core.impl.game.ships.ShipObjectServerWorld;
import org.valkyrienskies.core.impl.shadow.Af;

import java.util.List;
import java.util.Map;

@Mixin(ShipObjectServerWorld.class)
public interface ShipObjectServerWorldAccessor {
    @Accessor
    public List<Af> getDisabledCollisionPairsThisTick();
    @Accessor
    public List<Af> getEnabledCollisionPairsThisTick();

    @Accessor
    Map<Integer, VSConstraint> getConstraints();
}
