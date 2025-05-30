package com.lancas.vswap.mixins.accessor;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ChunkAccess.class)
public interface ChunkAccessor {
    @Accessor
    public Map<Heightmap.Types, Heightmap> getHeightmaps();
}
