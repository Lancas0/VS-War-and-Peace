package com.lancas.vswap.subproject.sandbox.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class WorldUtil {
    //it should same as VSGameUtils.getDimId
    public String getDimId(ResourceKey<Level> resourceKey) {
        return resourceKey.registry().toString() + ":" + resourceKey.location();
    }
}
