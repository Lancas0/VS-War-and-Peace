package com.lancas.vswap.util;

import com.lancas.vswap.foundation.api.math.ForceOnPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.joml.*;
import org.joml.primitives.AABBd;

public class BytesUtil {
    public static FriendlyByteBuf writeVector3d(FriendlyByteBuf buf, Vector3dc vector) {
        buf.writeVector3f(vector.get(new Vector3f()));
        return buf;
    }
    public static FriendlyByteBuf writeForceOnPos(FriendlyByteBuf buf, ForceOnPos forceOnPos) {
        writeVector3d(buf, forceOnPos.force());
        writeVector3d(buf, forceOnPos.pos());
        return buf;
    }

    public static FriendlyByteBuf readVector3d(FriendlyByteBuf buf, Vector3d dest) {
        dest.set(buf.readVector3f());
        return buf;
    }
    public static Vector3d getVector3d(FriendlyByteBuf buf, Vector3d dest) {
        readVector3d(buf, dest);
        return dest;
    }
    public static ForceOnPos readForceOnPos(FriendlyByteBuf buf) {
        Vector3d force = new Vector3d(), pos = new Vector3d();

        readVector3d(buf, force);
        readVector3d(buf, pos);

        return new ForceOnPos(force, pos);
    }

    public static FriendlyByteBuf writeVector3i(FriendlyByteBuf buf, Vector3ic v) {
        buf.writeInt(v.x())
            .writeInt(v.y())
            .writeInt(v.z());
        return buf;
    }
    public static Vector3i readVector3i(FriendlyByteBuf buf, Vector3i dest) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        return dest.set(x, y, z);
    }

    public static FriendlyByteBuf writeBlockState(FriendlyByteBuf buf, BlockState state) {
        buf.writeId(Block.BLOCK_STATE_REGISTRY, state);
        return buf;
    }
    public static BlockState readBlockState(FriendlyByteBuf buf) {
        return buf.readById(Block.BLOCK_STATE_REGISTRY);
    }

    public static FriendlyByteBuf writeAABB(FriendlyByteBuf buf, AABB aabb) {
        buf.writeDouble(aabb.minX).writeDouble(aabb.minY).writeDouble(aabb.minZ)
            .writeDouble(aabb.maxX).writeDouble(aabb.maxY).writeDouble(aabb.maxZ);
        return buf;
    }
    public static AABB readAABB(FriendlyByteBuf buf) {
        return new AABB(
            buf.readDouble(), buf.readDouble(), buf.readDouble(),
            buf.readDouble(), buf.readDouble(), buf.readDouble()
        );
    }
}
