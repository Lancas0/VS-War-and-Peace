package com.lancas.vswap.content.block.blocks.rocket;

import net.minecraft.core.Direction;
import org.joml.Vector3d;

public interface INozzleBlock {
    public Vector3d getPower(Direction dir);
}
