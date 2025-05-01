package com.lancas.vs_wap.content.block.blocks.scope;

import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;

public interface IScopeBlock {
    public Vector3d getCameraOffsetAlongForward();
    public default Vector3f getCameraOffsetAlongForwardF() {
        return getCameraOffsetAlongForward().get(new Vector3f());
    }
    public default Vec3 getCameraOffsetAlongForwardV3() {
        return JomlUtil.v3(getCameraOffsetAlongForward());
    }

    public float getFovMultiplier();

    public static Direction getScopeDirection(BlockState state) {
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
            return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        //else if (state.hasProperty(BlockStateProperties.FACING))
        //    return state.getValue(BlockStateProperties.FACING);
        else return null;
    }
    public static Quaterniond getRotationByScopeDir(Direction dir) {
        double yRotRad = Math.toRadians(dir.toYRot());
        return switch (dir) {
            case NORTH, SOUTH -> new Quaterniond().rotateYXZ(yRotRad, 0, 0);
            case WEST, EAST -> new Quaterniond().rotateYXZ(-yRotRad, 0, 0);
            //should not be called
            default -> new Quaterniond().rotateYXZ(yRotRad, 0, 0);
        };
    }
}
