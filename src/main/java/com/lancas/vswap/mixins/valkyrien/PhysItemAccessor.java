package com.lancas.vswap.mixins.valkyrien;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.core.impl.shadow.Aj;
import org.valkyrienskies.core.impl.shadow.Aq;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mixin(Aq.class)
public interface PhysItemAccessor {
    @Accessor(remap = false)
    public ConcurrentLinkedQueue<Aj> getC();

    @Accessor(remap = false)
    public Map<String, Long2ObjectMap<PhysShipImpl>> getH();

}
