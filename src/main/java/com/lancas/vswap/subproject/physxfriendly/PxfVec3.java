package com.lancas.vswap.subproject.physxfriendly;

/*
import com.lancas.vswap.util.NbtBuilder;
import com.lancas.vswap.util.StrUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import physx.common.PxVec3;

public class PxfVec3 implements AutoCloseable {
    private final PxVec3 value = new PxVec3();

    public PxfVec3() { this(0f, 0f, 0f); }
    public PxfVec3(float x, float y, float z) {
        value.setX(x);
        value.setY(y);
        value.setZ(z);
    }
    public PxfVec3(Vector3fc v) { this(v.x(), v.y(), v.z()); }
    public PxfVec3(Vec3 v)      { this((float)v.x(), (float)v.y(), (float)v.z()); }
    public PxfVec3(Vector3dc v) { this((float)v.x(), (float)v.y(), (float)v.z()); }

    public float x() { return value.getX(); }
    public float y() { return value.getY(); }
    public float z() { return value.getZ(); }

    public float magnitude() { return value.magnitude(); }
    public float magnitudeSquared() { return value.magnitudeSquared(); }
    public void normalizeSafe() { value.normalizeSafe(); }



    @Override
    public void close() throws Exception {
        //if (value == null)
        //    return;
        try {
            value.destroy();
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public String toString() {
        return String.format("(%f, %f, %f)", x(), y(), z());
    }
}
*/