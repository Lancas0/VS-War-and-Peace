package com.lancas.vs_wap.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;

public class PosUtil {
    public static Vector3d toV3D(BlockPos blkPos) {
        return new Vector3d(blkPos.getX(), blkPos.getY(), blkPos.getZ());
    }
    public static Vector3d toV3D(Vec3 v) {
        return new Vector3d(v.x, v.y, v.z);
    }
    public static Vector3i toV3I(BlockPos blkPos) {
        return new Vector3i(blkPos.getX(), blkPos.getY(), blkPos.getZ());
    }
    public static Vector3i toV3I(Vector3dc vector) {
        return new Vector3i((int)vector.x(), (int)vector.y(), (int)vector.z());
    }
    public static BlockPos toBlockPos(Vector3dc vector) {
        return new BlockPos((int)vector.x(), (int)vector.y(), (int)vector.z());
    }

    public static BlockPos sub(BlockPos a, BlockPos b) {
        return new BlockPos(
                a.getX() - b.getX(),
                a.getY() - b.getY(),
                a.getZ() - b.getZ()
        );
    }

    public static BlockPos min(BlockPos pos1, BlockPos pos2) {
        if (pos1 == null || pos2 == null)
            throw new NullPointerException();

        return new BlockPos(
                Math.min(pos1.getX(), pos2.getX()),
                Math.min(pos1.getY(), pos2.getY()),
                Math.min(pos1.getZ(), pos2.getZ())
        );
    }

    public static BlockPos max(BlockPos pos1, BlockPos pos2) {
        if (pos1 == null || pos2 == null)
            throw new NullPointerException();

        return new BlockPos(
                Math.max(pos1.getX(), pos2.getX()),
                Math.max(pos1.getY(), pos2.getY()),
                Math.max(pos1.getZ(), pos2.getZ())
        );
    }

    public static Vector3i middle(BlockPos pos1, BlockPos pos2) {
        double middleX =
            Math.min(pos1.getX(), pos2.getX()) +
            (
                Math.max(pos1.getX(), pos2.getX()) -
                Math.min(pos1.getX(), pos2.getX()) + 1
            ) / 2.0;
        double middleY = Math.min(pos1.getY(), pos2.getY()) + (Math.max(pos1.getY(), pos2.getY()) - Math.min(
                pos1.getY(),
                pos2.getY()
        ) + 1) / 2.0;
        double middleZ = Math.min(pos1.getZ(), pos2.getZ()) + (Math.max(pos1.getZ(), pos2.getZ()) - Math.min(
                pos1.getZ(),
                pos2.getZ()
        ) + 1) / 2.0;
        return new Vector3i((int)middleX, (int)middleY, (int)middleZ);
    }
}
