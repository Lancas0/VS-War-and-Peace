package com.lancas.vswap.util;

import com.lancas.vswap.foundation.data.SavedBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBic;

import java.text.NumberFormat;
import java.util.Locale;

public class StrUtil {
    public static String poslike(double x, double y, double z) { return String.format("(%.2f, %.2f, %.2f)", x, y, z); }
    public static String poslike(int x, int y, int z) { return String.format("(%d, %d, %d)", x, y, z); }
    public static String poslike(Vector3ic p) { return String.format("(%d, %d, %d)", p.x(), p.y(), p.z()); }
    public static String poslike(BlockPos p) { return String.format("(%d, %d, %d)", p.getX(), p.getY(), p.getZ()); }

    public static String F2(Vector3dc v) {
        return String.format("(%.2f, %.2f, %.2f)", v.x(), v.y(), v.z());
    }
    public static String F2(Quaterniondc q) {
        return String.format("(%.2f, %.2f, %.2f, %.2f)", q.x(), q.y(), q.z(), q.w());
    }
    public static String F2(Vec3 v) {
        return String.format("(%.2f, %.2f, %.2f)", v.x(), v.y(), v.z());
    }
    public static String F2(double d) { return String.format("%.2f", d); }
    public static String F2(float f) { return String.format("%.2f", f); }
    public static String toFullString(Vector3ic v) {
        return String.format("(%d, %d, %d)", v.x(), v.y(), v.z());
    }
    public static String toFullString(AABBic a) {
        return String.format("(%d, %d, %d) > (%d, %d, %d)", a.minX(), a.minY(), a.minZ(), a.maxX(), a.maxY(), a.maxZ());
    }

    public static String F2(WorldBorder b) {
        return String.format("(%.2f -> %.2f, %.2f -> %.2f)", b.getMinX(), b.getMaxX(), b.getMinZ(), b.getMaxZ());
    }

    public static String F0(float v) {
        return String.format("%.0f", v);
    }
    public static String F0(double v) {
        return String.format("%.0f", v);
    }

    public static String toIntString(Vector3dc b) {
        return String.format("(%.0f, %.0f, %.0f)", b.x(), b.y(), b.z());
    }

    public static String toNormalString(Vector3f v3f) {
        return v3f == null ? "null" : v3f.toString(format());
    }
    public static String toNormalString(Vector3d v3d) {
        return v3d == null ? "null" : v3d.toString(format());
    }
    public static String toNormalString(Vector3fc v3f) { return v3f == null ? "null" : v3f.get(new Vector3d()).toString(format()); }
    public static String toNormalString(Vector3dc v3d) { return v3d == null ? "null" : v3d.get(new Vector3d()).toString(format()); }
    public static String toNormalString(AABBd aabb) {
        return aabb == null ? "null" : aabb.toString(format());
    }
    public static String toNormalString(Matrix4d m) { return m == null ? "null" : m.toString(format()); }
    public static String toNormalString(Matrix4dc m) { return m == null ? "null" : m.get(new Matrix4d()).toString(format()); }

    public static String getBlockName(@Nullable BlockState state) { return state == null ? "Null" : state.getBlock().getName().getString(); }
    public static String getBlockPos(BlockPos bp) { return String.format("(%d, %d, %d)", bp.getX(), bp.getY(), bp.getZ()); }
    public static String getBlockPos(SavedBlockPos bp) { return getBlockPos(bp.toBp()); }


    public static NumberFormat format() {
        NumberFormat df;
        df = NumberFormat.getNumberInstance(Locale.ENGLISH);
        df.setGroupingUsed(false);
        return df;
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
    public static boolean isNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }
}
