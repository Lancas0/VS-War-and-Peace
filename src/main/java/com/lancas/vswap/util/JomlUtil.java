package com.lancas.vswap.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.*;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;

import java.lang.Math;
import java.util.ArrayList;

public class JomlUtil {
    public static double rad(double deg) { return Math.toRadians(deg); }
    public static double deg(double rad) { return Math.toDegrees(rad); }

    public static double yRotRad(Entity entity) { return rad(entity.getYRot()); }
    public static double xRotRad(Entity entity) { return rad(entity.getXRot()); }
    public static Vector3d dLookDir(Entity entity) { return d(entity.getLookAngle()); }
    public static Vector3d dPosition(Entity entity) { return d(entity.position()); }

    public static boolean approximateZero(double v) {
        return -1E-15 < v && v < 1E-15;
    }
    public static boolean approximateZero(Vector3dc v) {
        return v.lengthSquared() < 1E-15;
    }

    public static int cross(Vector3ic a, Vector3ic b) {
        return a.x() * b.x() + a.y() * b.y() + a.z() * b.z();
    }


    public static Vector3d d(Vec3 v) { return new Vector3d(v.x, v.y, v.z); }

    public static Vector3d d(Vector3ic pos) { return new Vector3d(pos.x(), pos.y(), pos.z()); }
    public static Vector3d d(Vector3i pos) { return new Vector3d(pos.x(), pos.y(), pos.z()); }
    public static Vector3d d(Vec3i pos) { return new Vector3d(pos.getX(), pos.getY(), pos.getZ()); }
    public static Vector3f f(Vec3 v) {
        return new Vector3f((float)v.x, (float)v.y, (float)v.z);
    }
    public static Vector3f f(Vec3i v) {
        return new Vector3f((float)v.getX(), (float)v.getY(), (float)v.getZ());
    }
    public static Vector3f f(BlockPos pos) {
        return new Vector3f(pos.getX(), pos.getY(), pos.getZ());
    }
    public static Vector3i i(BlockPos pos) {  return new Vector3i(pos.getX(), pos.getY(), pos.getZ()); }
    public static Vector3i i(Vec3i v) { return new Vector3i(v.getX(), v.getY(), v.getZ()); }
    public static Vector3i i(Vector3dc v) { return new Vector3i((int)v.x(), (int)v.y(), (int)v.z()); }
    public static Vector3i iNormal(Direction dir) { return i(dir.getNormal()); }

    public static Vec3 v3(Vector3dc v) {
        return new Vec3(v.x(), v.y(), v.z());
    }
    public static Vec3 v3(Vector3fc v) {
        return new Vec3(v.x(), v.y(), v.z());
    }

    public static BlockPos bp(Vec3i v) { return new BlockPos(v.getX(), v.getY(), v.getZ()); }
    public static BlockPos bp(Vector3ic v) { return new BlockPos(v.x(), v.y(), v.z()); }


    public static Vector3d dSet(Vector3d v, Vec3 val) { return v.set(val.x, val.y, val.z); }
    public static Vector3i iSet(Vector3i v, Vec3i val) { return v.set(val.getX(), val.getY(), val.getZ()); }

    /*
    public static BlockPos bp(Vector3ic v) {
        return new BlockPos(v.x(), v.y(), v.z());
    }
    public static BlockPos bp(Vector3dc v) {
        return new BlockPos((int)v.x(), (int)v.y(), (int)v.z());
    }
       */
    public static Vec3 v3(Vec3i v) { return new Vec3(v.getX(), v.getY(), v.getZ()); }

    public static AABB aabb(AABBdc aabbC) { return new AABB(aabbC.minX(), aabbC.minY(), aabbC.minZ(), aabbC.maxX(), aabbC.maxY(), aabbC.maxZ()); }
    public static AABB aabb(AABBic aabbC) { return new AABB(aabbC.minX(), aabbC.minY(), aabbC.minZ(), aabbC.maxX(), aabbC.maxY(), aabbC.maxZ()); }
    public static AABBd d(AABB aabb) {
        return new AABBd(
            aabb.minX,
            aabb.minY,
            aabb.minZ,
            aabb.maxX,
            aabb.maxY,
            aabb.maxZ
        );
    }

    public static Vec3 v3Add(Vector3dc a, Vector3dc b) { return new Vec3(a.x() + b.x(), a.y() + b.y(), a.z() + b.z()); }
    public static Vec3 v3Add(Vec3 a, Vector3dc b) { return new Vec3(a.x() + b.x(), a.y() + b.y(), a.z() + b.z()); }
    public static Vec3 v3Add(Vector3dc a, Vec3 b) { return new Vec3(a.x() + b.x(), a.y() + b.y(), a.z() + b.z()); }

    public static Vector3d relativeFromCenter(BlockPos bp, Direction dir, double len) {
        return dCenter(bp).add(dir.getStepX() * len, dir.getStepY() * len, dir.getStepZ() * len);
    }
    public static Vector3d relativeFromCenter(BlockPos bp, Direction dir) { return relativeFromCenter(bp, dir, 1); }
    public static Vector3d relative(Vector3dc p, Vector3dc dir, double len) {
        if (approximateZero(dir)) return new Vector3d(p);

        return dir.normalize(len, new Vector3d()).add(p);
    }
    public static Vector3d relative(Vec3 p, Vector3dc dir, double len) {
        Vector3d dP = d(p);
        if (approximateZero(dir)) return dP;

        return dir.normalize(len, new Vector3d()).add(dP);
    }
    public static Vector3d relative(Vec3 p, Direction dir, double len) {
        return JomlUtil.d(p.relative(dir, len));
    }
    public static Vector3d relative(Vector3dc p, Direction dir, double len) {
        return JomlUtil.dNormal(dir, len).add(p);
    }
    public static Vec3 relativeV3(Vector3dc p, Vector3dc dir, double len) {
        Vec3 vP = v3(p);
        if (approximateZero(dir)) return vP;

        return v3(dir.normalize(len, new Vector3d())).add(vP);
    }
    public static Vec3 relativeV3(Vec3 p, Vector3dc dir, double len) {
        if (approximateZero(dir)) return p;

        return v3(dir.normalize(len, new Vector3d())).add(p);
    }
    public static Vec3 relativeV3(Vec3 p, Direction dir, double len) {
        return p.relative(dir, len);
    }
    public static Vec3 relativeV3(Vector3dc p, Direction dir, double len) {
        return v3(p).relative(dir, len);
    }

    public static int length(AABBic aabb, int comp) { return aabb.getMax(comp) - aabb.getMin(comp); }
    public static int lengthX(AABBic aabb) { return length(aabb, 0); }
    public static int lengthY(AABBic aabb) { return length(aabb, 1); }
    public static int lengthZ(AABBic aabb) { return length(aabb, 2); }

    public static double length(AABBdc aabb, int comp) { return aabb.getMax(comp) - aabb.getMin(comp); }
    public static double lengthX(AABBdc aabb) { return length(aabb, 0); }
    public static double lengthY(AABBdc aabb) { return length(aabb, 1); }
    public static double lengthZ(AABBdc aabb) { return length(aabb, 2); }


    public static Vector3d dCenter(BlockPos pos) { return JomlUtil.d(pos.getCenter()); }
    public static Vector3d dCenter(int x, int y, int z) { return new Vector3d(x + 0.5, y + 0.5, z + 0.5); }
    public static Vec3 center(int x, int y, int z) { return new Vec3(x + 0.5, y + 0.5, z + 0.5); }
    public static Vector3d dCenter(AABB aabb) { return JomlUtil.d(aabb.getCenter()); }
    public static Vector3d dNormal(Direction dir) { return JomlUtil.d(dir.getNormal()); }
    public static Vector3d dNormal(Direction dir, double len) { return JomlUtil.dNormal(dir).mul(len); }
    public static Vector3f fNormal(Direction dir) { return JomlUtil.f(dir.getNormal()); }
    public static Vector3f fNormal(Direction dir, float len) { return JomlUtil.fNormal(dir).mul(len); }
    public static Vector3d dOpposite(Direction dir) { return JomlUtil.d(dir.getOpposite().getNormal()); }
    public static Vector3d dFaceCenter(BlockPos pos, Direction face) { return JomlUtil.dCenter(pos).add(JomlUtil.dNormal(face, 0.5)); }
    public static Vector3d dFaceCenter(AABB aabb, Direction face) {
        Vector3d faceCenter = dCenter(aabb);
        switch (face) {
            case UP -> faceCenter.setComponent(1, aabb.maxY);  //xz upper face, set y max
            case DOWN -> faceCenter.setComponent(1, aabb.minY);
            case SOUTH -> faceCenter.setComponent(2, aabb.maxZ);  //xy forward face, set z max
            case NORTH -> faceCenter.setComponent(2, aabb.minZ);
            case EAST -> faceCenter.setComponent(0, aabb.maxX);  //yz left face, set x max
            case WEST -> faceCenter.setComponent(0, aabb.minX);
        }
        return faceCenter;
    }
    public static Vector3d dFaceCenter(AABBdc aabb, Direction face) {
        Vector3d faceCenter = aabb.center(new Vector3d());
        switch (face) {
            case UP -> faceCenter.setComponent(1, aabb.maxY());  //xz upper face, set y max
            case DOWN -> faceCenter.setComponent(1, aabb.minY());
            case SOUTH -> faceCenter.setComponent(2, aabb.maxZ());  //xy forward face, set z max
            case NORTH -> faceCenter.setComponent(2, aabb.minZ());
            case EAST -> faceCenter.setComponent(0, aabb.maxX());  //yz left face, set x max
            case WEST -> faceCenter.setComponent(0, aabb.minX());
        }
        return faceCenter;
    }
    public static Vector3d dFaceCenter(AABBic aabb, Direction face) {
        Vector3d faceCenter = aabb.center(new Vector3d());
        switch (face) {
            case UP -> faceCenter.setComponent(1, aabb.maxY());  //xz upper face, set y max
            case DOWN -> faceCenter.setComponent(1, aabb.minY());
            case SOUTH -> faceCenter.setComponent(2, aabb.maxZ());  //xy forward face, set z max
            case NORTH -> faceCenter.setComponent(2, aabb.minZ());
            case EAST -> faceCenter.setComponent(0, aabb.maxX());  //yz left face, set x max
            case WEST -> faceCenter.setComponent(0, aabb.minX());
        }
        return faceCenter;
    }

    public static Direction nearestDir(Vec3 v) { return Direction.getNearest(v.x, v.y, v.z); }
    public static Direction nearestDir(Vector3dc v) { return Direction.getNearest(v.x(), v.y(), v.z()); }
    public static Direction nearestDir(Vector3ic v) { return Direction.getNearest(v.x(), v.y(), v.z()); }

    //XY, XZ, or YZ
    public static Vector3d faceSize(AABBdc aabb, Direction face) {
        Vector3d size = aabb.extent(new Vector3d()).mul(2);

        return switch (face) {
            case EAST , WEST  -> size.setComponent(0, 0);  //x = 0
            case UP   , DOWN  -> size.setComponent(1, 0);  //y = 0
            case SOUTH, NORTH -> size.setComponent(2, 0);  //z = 0
        };
    }
    public static Vector3i faceSize(AABBic aabb, Direction face) {
        Vector3i size = new Vector3i(aabb.maxX() - aabb.minX(), aabb.maxY() - aabb.minY(), aabb.maxZ() - aabb.minZ());

        return switch (face) {
            case EAST , WEST  -> size.setComponent(0, 0);  //x = 0
            case UP   , DOWN  -> size.setComponent(1, 0);  //y = 0
            case SOUTH, NORTH -> size.setComponent(2, 0);  //z = 0
        };
    }


    private static AABBd boundsFaceImpl(AABBdc bounds, Direction face) {
        return switch (face) {
            case UP -> new AABBd(bounds.minX(), bounds.maxY(), bounds.minZ(), bounds.maxX(), bounds.maxY(), bounds.maxZ());
            case DOWN -> new AABBd(bounds.minX(), bounds.minY(), bounds.minZ(), bounds.maxX(), bounds.minY(), bounds.maxZ());

            case SOUTH -> new AABBd(bounds.minX(), bounds.minY(), bounds.maxZ(), bounds.maxX(), bounds.maxY(), bounds.maxZ());
            case NORTH -> new AABBd(bounds.minX(), bounds.minY(), bounds.minZ(), bounds.maxX(), bounds.maxY(), bounds.minZ());

            case WEST -> new AABBd(bounds.minX(), bounds.minY(), bounds.minZ(), bounds.minX(), bounds.maxY(), bounds.maxZ());
            case EAST -> new AABBd(bounds.maxX(), bounds.minY(), bounds.minZ(), bounds.maxX(), bounds.maxY(), bounds.maxZ());
        };
    }
    public static AABBd dBoundsFace(AABBdc bounds, Direction face) { return boundsFaceImpl(bounds, face); }
    public static AABBd dBoundsFace(AABB bounds, Direction face) { return boundsFaceImpl(d(bounds), face); }
    public static AABB boundsFace(AABBdc bounds, Direction face) { return aabb(boundsFaceImpl(bounds, face)); }
    public static AABB boundsFace(AABB bounds, Direction face) { return aabb(boundsFaceImpl(d(bounds), face)); }
    public static AABB boundsFace(VoxelShape shape, Direction face) { return aabb(boundsFaceImpl(d(shape.bounds()), face)); }
    public static AABBd dBoundsFace(VoxelShape shape, Direction face) { return boundsFaceImpl(d(shape.bounds()), face); }

    public static Vector3d dLowerCorner(BlockPos pos) { return new Vector3d(pos.getX(), pos.getY(), pos.getZ()); }
    public static Iterable<Vector3d> dCorners(BlockPos pos) {
        ArrayList<Vector3d> corners = new ArrayList<>();
        for (int x = 0; x <= 1; ++x)
            for (int y = 0; y <= 1; ++y)
                for (int z = 0; z <= 1; ++z) {
                    corners.add(new Vector3d(pos.getX() + x, pos.getY() + y, pos.getZ() + z));
                }
        return corners;
    }
    public static AABBd dBound(VoxelShape shape) {
        if (shape == null) return new AABBd();
        return JomlUtil.d(shape.bounds());
    }
    public static AABBd dBoundAll(Iterable<Vector3d> points) {
        AABBd bound = new AABBd();
        if (points == null) return bound;

        for (Vector3d point : points) {
            if (point != null)
                bound.union(point);
        }
        return bound;
    }
    public static AABBd dBoundBlock(BlockPos pos) {
        return new AABBd(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }
    public static AABB boundBlock(BlockPos pos) {
        return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }
    public static AABBd dBoundCubic(Vector3dc center, double len) {
        double halfLen = len / 2.0;
        return new AABBd(center.x() - halfLen, center.y() - halfLen, center.z() - halfLen,
            center.x() + halfLen, center.y() + halfLen, center.z() + halfLen);
    }
    

    public static AABBd correctAABBd(Vector3dc a, Vector3dc b) {
        double minX = Math.min(a.x(), b.x());
        double minY = Math.min(a.y(), b.y());
        double minZ = Math.min(a.z(), b.z());

        double maxX = Math.max(a.x(), b.x());
        double maxY = Math.max(a.y(), b.y());
        double maxZ = Math.max(a.z(), b.z());
        return new AABBd(minX, minY, minZ, maxX, maxY, maxZ);
    }
    public static AABBd correctAABBd(Vec3 a, Vector3dc b) {
        double minX = Math.min(a.x(), b.x());
        double minY = Math.min(a.y(), b.y());
        double minZ = Math.min(a.z(), b.z());

        double maxX = Math.max(a.x(), b.x());
        double maxY = Math.max(a.y(), b.y());
        double maxZ = Math.max(a.z(), b.z());
        return new AABBd(minX, minY, minZ, maxX, maxY, maxZ);
    }
    public static AABBd correctAABBd(Vec3 a, Vec3 b) {
        double minX = Math.min(a.x(), b.x());
        double minY = Math.min(a.y(), b.y());
        double minZ = Math.min(a.z(), b.z());

        double maxX = Math.max(a.x(), b.x());
        double maxY = Math.max(a.y(), b.y());
        double maxZ = Math.max(a.z(), b.z());
        return new AABBd(minX, minY, minZ, maxX, maxY, maxZ);
    }
    public static AABBd correctAABBd(Vector3dc a, Vec3 b) {
        double minX = Math.min(a.x(), b.x());
        double minY = Math.min(a.y(), b.y());
        double minZ = Math.min(a.z(), b.z());

        double maxX = Math.max(a.x(), b.x());
        double maxY = Math.max(a.y(), b.y());
        double maxZ = Math.max(a.z(), b.z());
        return new AABBd(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static Vec3 transformPosV3(Matrix4dc transformer, Vector3dc pos) {
        return JomlUtil.v3(transformer.transformPosition(pos, new Vector3d()));
    }
    public static Vec3 transformPosV3(Matrix4dc transformer, Vec3 pos) {
        return JomlUtil.v3(transformer.transformPosition(JomlUtil.d(pos), new Vector3d()));
    }
    public static Vector3d transformPosD(Matrix4dc transformer, Vec3 pos) {
        return transformer.transformPosition(JomlUtil.d(pos));
    }
    public static Vector3d dWorldCenter(Matrix4dc shipToWorld, BlockPos bp) {
        return shipToWorld.transformPosition(JomlUtil.dCenter(bp));
    }
    public static BlockPos worldBp(Matrix4dc shipToWorld, BlockPos bp) {
        return bpContaining(shipToWorld.transformPosition(JomlUtil.dCenter(bp)));
    }
    public static BlockPos worldBp(Matrix4dc shipToWorld, Vector3dc pos) {
        return bpContaining(shipToWorld.transformPosition(pos, new Vector3d()));
    }
    public static Vector3d dWorldFaceCenter(Matrix4dc shipToWorld, BlockPos bp, Direction face) {
        return shipToWorld.transformPosition(dFaceCenter(bp, face));
    }
    public static Vector3d dWorldNormal(Matrix4dc shipToWorld, Direction dir) {
        return shipToWorld.transformDirection(JomlUtil.dNormal(dir)).normalize();
    }
    public static Vector3d dWorldNormal(Quaterniondc rotation, Direction dir) {
        return rotation.transform(JomlUtil.dNormal(dir)).normalize();
    }

    public static BlockPos bpContaining(Vector3dc p) {
        return new BlockPos((int)Math.floor(p.x()), (int)Math.floor(p.y()), (int)Math.floor(p.z()));
    }


    public static double sqDist(Vec3 a, Vec3 b) {
        return a.distanceToSqr(b);
    }
    public static double sqDist(Vector3dc a, Vec3 b) {
        return a.distanceSquared(b.x, b.y, b.z);
    }
    public static double sqDist(Vec3 a, Vector3dc b) {
        return b.distanceSquared(a.x, a.y, a.z);
    }

    public static float lerpF(float a, float b, double t) {
        return (float)((1 - t) * a + t * b);
    }
    public static double lerpD(double a, double b, double t) {
        return (1 - t) * a + t * b;
    }
    public static Matrix4d lerpTransformerD(Matrix4dc a, Matrix4dc b, double t, Matrix4d dest) {
        return dest.translationRotateScale(
            a.getTranslation(new Vector3d()).lerp(b.getTranslation(new Vector3d()), t),
            a.getNormalizedRotation(new Quaterniond()).slerp(b.getNormalizedRotation(new Quaterniond()), t),
            a.getScale(new Vector3d()).lerp(b.getScale(new Vector3d()), t)
        );
    }
    public static Matrix4f lerpTransformerF(Matrix4fc a, Matrix4fc b, float t, Matrix4f dest) {
        return dest.translationRotateScale(
            a.getTranslation(new Vector3f()).lerp(b.getTranslation(new Vector3f()), t),
            a.getNormalizedRotation(new Quaternionf()).slerp(b.getNormalizedRotation(new Quaternionf()), t),
            a.getScale(new Vector3f()).lerp(b.getScale(new Vector3f()), t)
        );
    }
    public static Vec3 lerpV3(Vec3 a, Vec3 b, double t) {
        return new Vec3(
            lerpD(a.x, b.x, t),
            lerpD(a.y, b.y, t),
            lerpD(a.z, b.z, t)
        );
    }
    public static Vector3d dLerp(Vector3dc a, Vector3dc b, double t, Vector3d dest) {
        dest.x = lerpD(a.x(), b.x(), t);
        dest.y = lerpD(a.y(), b.y(), t);
        dest.z = lerpD(a.z(), b.z(), t);
        return dest;
    }
    public static AABB lerpAABB(AABB from, AABB to, double t) {
        return new AABB(
            lerpD(from.minX, to.minX, t),
            lerpD(from.minY, to.minY, t),
            lerpD(from.minZ, to.minZ, t),
            lerpD(from.maxX, to.maxX, t),
            lerpD(from.maxY, to.maxY, t),
            lerpD(from.maxZ, to.maxZ, t)
        );
    }
    public static AABBd lerpAABBd(AABBd from, AABBd to, double t, AABBd dest) {
        return dest.setMin(
            lerpD(from.minX, to.minX, t),
            lerpD(from.minY, to.minY, t),
            lerpD(from.minZ, to.minZ, t)
        ).setMax(
            lerpD(from.maxX, to.maxX, t),
            lerpD(from.maxY, to.maxY, t),
            lerpD(from.maxZ, to.maxZ, t)
        );
    }

    public static AABBd dCenterExtended(Vector3dc center, Vector3dc extend) {
        return new AABBd(
            center.x() - extend.x(),
            center.y() - extend.y(),
            center.z() - extend.z(),
            center.x() + extend.x(),
            center.y() + extend.y(),
            center.z() + extend.z()
        );
    }
    public static AABBd dCenterExtended(Vector3dc center, double xExtend, double yExtend, double zExtend) {
        return new AABBd(
            center.x() - xExtend,
            center.y() - yExtend,
            center.z() - zExtend,
            center.x() +xExtend,
            center.y() + yExtend,
            center.z() + zExtend
        );
    }
    public static AABBd dCenterExtended(Vector3dc center, double halfLen) {
        return dCenterExtended(center, halfLen, halfLen, halfLen);
    }

    public static AABB centerExtended(Vector3dc center, double xExtend, double yExtend, double zExtend) {
        xExtend = Math.abs(xExtend);
        yExtend = Math.abs(yExtend);
        zExtend = Math.abs(zExtend);
        return new AABB(
            center.x() - xExtend,
            center.y() - yExtend,
            center.z() - zExtend,
            center.x() + xExtend,
            center.y() + yExtend,
            center.z() + zExtend
        );
    }
    public static AABB centerExtended(BlockPos pos, double xExtend, double yExtend, double zExtend) {
        Vector3d center = JomlUtil.dCenter(pos);
        xExtend = Math.abs(xExtend);
        yExtend = Math.abs(yExtend);
        zExtend = Math.abs(zExtend);
        return new AABB(
            center.x() - xExtend,
            center.y() - yExtend,
            center.z() - zExtend,
            center.x() + xExtend,
            center.y() + yExtend,
            center.z() + zExtend
        );
    }
    public static AABB centerExtended(Vector3dc center, double halfLen) {
        return centerExtended(center, halfLen, halfLen, halfLen);
    }
    public static AABB centerExtended(BlockPos pos, double halfLen) {
        return centerExtended(pos, halfLen, halfLen, halfLen);
    }

    public static AABBd scaleFromLower(AABBdc aabb, Vector3dc scale, AABBd dest) {
        double minX = aabb.minX();
        double minY = aabb.minY();
        double minZ = aabb.minZ();

        double lenX = aabb.maxX() - aabb.minX();
        double lenY = aabb.maxY() - aabb.minY();
        double lenZ = aabb.maxZ() - aabb.minZ();

        return dest.setMin(minX, minY, minZ)
            .setMax(minX + lenX * scale.x(),
                minY + lenY * scale.y(),
                minZ + lenZ * scale.z()
            );
    }
    public static AABBd scaleFromCenter(AABBdc aabb, Vector3dc scale, AABBd dest) {
        Vector3d c = aabb.center(new Vector3d());
        double halfLenX = (aabb.maxX() - aabb.minX()) / 2.0;
        double halfLenY = (aabb.maxY() - aabb.minY()) / 2.0;
        double halfLenZ = (aabb.maxZ() - aabb.minZ()) / 2.0;

        return dest.setMin(c.x - halfLenX, c.y - halfLenY, c.z - halfLenZ)
            .setMax(
                c.x + halfLenX,
                c.y + halfLenY,
                c.z + halfLenZ
            );
    }
    public static AABBd scaleFromLower(AABBd aabb, Vector3dc scale) {
        return scaleFromLower(aabb, scale, aabb);
    }
    public static AABBd scaleFromCenter(AABBd aabb, Vector3dc scale) {
        return scaleFromCenter(aabb, scale, aabb);
    }
    public static AABBd scaleFromLower(AABBic aabb, Vector3dc scale, AABBd dest) {
        double minX = aabb.minX();
        double minY = aabb.minY();
        double minZ = aabb.minZ();

        double lenX = aabb.maxX() - aabb.minX();
        double lenY = aabb.maxY() - aabb.minY();
        double lenZ = aabb.maxZ() - aabb.minZ();

        return dest.setMin(minX, minY, minZ)
            .setMax(
                minX + lenX * scale.x(),
                minY + lenY * scale.y(),
                minZ + lenZ * scale.z()
            );
    }
    public static AABBd scaleFromCenter(AABBic aabb, Vector3dc scale, AABBd dest) {
        Vector3d c = aabb.center(new Vector3d());
        double halfLenX = (aabb.maxX() - aabb.minX()) / 2.0;
        double halfLenY = (aabb.maxY() - aabb.minY()) / 2.0;
        double halfLenZ = (aabb.maxZ() - aabb.minZ()) / 2.0;

        return new AABBd(c.x - halfLenX, c.y - halfLenY, c.z - halfLenZ,
            c.x + halfLenX,
            c.y + halfLenY,
            c.z + halfLenZ
        );
    }

    public static AABBd moveCenterTo(AABBdc aabb, Vector3dc to, AABBd dest) {
        double hfLenX = lengthX(aabb);
        double hfLenY = lengthY(aabb);
        double hfLenZ = lengthZ(aabb);

        return dest.setMin(to.x() - hfLenX, to.y() - hfLenY, to.z() - hfLenZ)
            .setMax(to.x() + hfLenX, to.y() + hfLenY, to.z() + hfLenZ);
    }
    /*public static AABBd moveCenterTo(AABBd aabb, Vector3dc to) { return moveCenterTo(aabb, to, aabb); }
    public static AABBd moveCenterTo(AABBic aabb, Vector3dc to, AABBd dest) {
        Vector3d originalCenter = aabb.center(new Vector3d());
        Vector3d movement = to.sub(originalCenter, new Vector3d());
        dest.setMin(aabb.minX(), aabb.minY(), aabb.minZ());
        dest.setMax(aabb.maxX(), aabb.maxY(), aabb.maxZ());
        dest.translate(movement);
        return dest;
    }
    public static AABBi moveCenterTo(AABBic aabb, Vector3ic to, AABBi dest) {
        Vector3d originalCenter = aabb.center(new Vector3d());
        Vector3d movement = to.sub(originalCenter, new Vector3d());
        dest.setMin(aabb.minX(), aabb.minY(), aabb.minZ());
        dest.setMax(aabb.maxX(), aabb.maxY(), aabb.maxZ());
        dest.translate(movement);
        return dest;
    }*/
    /*public static Quaterniond pow(Quaterniondc q, double p, Quaterniond dest) {

    }*/
    public static Vector3d scaleVector3d(Vector3dc scalar, Vector3d v) {
        return v.set(v.x * scalar.x(), v.y * scalar.y(), v.z * scalar.z());
    }

    public static AABBd extentFromLower(AABBdc aabb, double x, double y, double z, AABBd dest) {
        return dest
            .setMin(aabb.minX(), aabb.minY(), aabb.minZ())
            .setMax(
            aabb.maxX() + x,
            aabb.maxY() + y,
            aabb.maxZ() + z
        );
    }
    public static AABBd extentFromLower(AABBic aabb, double x, double y, double z, AABBd dest) {
        return dest
            .setMin(aabb.minX(), aabb.minY(), aabb.minZ())
            .setMax(
            aabb.maxX() + x,
            aabb.maxY() + y,
            aabb.maxZ() + z
        );
    }
    public static AABBi extentFromLower(AABBic aabb, int x, int y, int z, AABBi dest) {
        return dest
            .setMin(aabb.minX(), aabb.minY(), aabb.minZ())
            .setMax(
            aabb.maxX() + x,
            aabb.maxY() + y,
            aabb.maxZ() + z
        );
    }
    public static AABBi extentFromLower(AABBi aabb, int x, int y, int z) { return extentFromLower(aabb, x, y, z, aabb); }
    public static AABBd extentFromLower(AABBd aabb, double x, double y, double z) { return extentFromLower(aabb, x, y, z, aabb); }
    public static AABBd extentFromLower(AABBdc aabb, Vector3dc extend, AABBd dest) { return extentFromLower(aabb, extend.x(), extend.y(), extend.z(), dest); }
    public static AABBd extentFromLower(AABBic aabb, Vector3dc extend, AABBd dest) { return extentFromLower(aabb, extend.x(), extend.y(), extend.z(), dest); }
    public static AABBd extentFromLower(AABBd aabb, Vector3dc extend) { return extentFromLower(aabb, extend, aabb); }


    public static AABBd extentFromCenter(AABBdc aabb, Vector3dc extend, AABBd dest) {
        Vector3d halfEx = extend.div(2, new Vector3d());
        return dest.setMin(
            aabb.minX() - halfEx.x,
            aabb.minY() - halfEx.y,
            aabb.minZ() - halfEx.z
        ).setMax(
            aabb.maxX() + halfEx.x(),
            aabb.maxY() + halfEx.y(),
            aabb.maxZ() + halfEx.z()
        );
    }
    public static AABBd extentFromCenter(AABBic aabb, Vector3dc extend, AABBd dest) {
        Vector3d halfEx = extend.div(2, new Vector3d());
        return dest.setMin(
            aabb.minX() - halfEx.x,
            aabb.minY() - halfEx.y,
            aabb.minZ() - halfEx.z
        ).setMax(
            aabb.maxX() + halfEx.x(),
            aabb.maxY() + halfEx.y(),
            aabb.maxZ() + halfEx.z()
        );
    }
    public static AABBd extentFromCenter(AABBd aabb, Vector3dc extend) { return extentFromCenter(aabb, extend, aabb); }


    public static int sideArea(AABBic aabb, Direction dir) {
        int xLen = aabb.maxX() - aabb.minX();
        int yLen = aabb.maxY() - aabb.minY();
        int zLen = aabb.maxZ() - aabb.minZ();

        return switch (dir) {
            case UP, DOWN -> xLen * zLen;
            case SOUTH, NORTH -> xLen * yLen;
            case WEST, EAST -> yLen * zLen;
        };
    }
    public static double sideArea(AABBdc aabb, Direction dir) {
        double xLen = aabb.maxX() - aabb.minX();
        double yLen = aabb.maxY() - aabb.minY();
        double zLen = aabb.maxZ() - aabb.minZ();

        return switch (dir) {
            case UP, DOWN -> xLen * zLen;
            case SOUTH, NORTH -> xLen * yLen;
            case WEST, EAST -> yLen * zLen;
        };
    }

    public static Quaterniond rotateYXZDeg(double yDeg, double xDeg, double zDeg) {
        return new Quaterniond().rotateYXZ(Math.toRadians(yDeg), Math.toRadians(xDeg), Math.toRadians(zDeg));
    }
    public static Quaterniond rotateXYZDeg(double xDeg, double yDeg, double zDeg) {
        return new Quaterniond().rotateXYZ(Math.toRadians(xDeg), Math.toRadians(yDeg), Math.toRadians(zDeg));
    }
    public static Quaterniond rotateZYXDeg(double zDeg, double yDeg, double xDeg) {
        return new Quaterniond().rotateZYX(Math.toRadians(zDeg), Math.toRadians(yDeg), Math.toRadians(xDeg));
    }
    public static Quaterniond rotateLocalXDeg(double deg) { return new Quaterniond().rotateLocalX(Math.toRadians(deg)); }
    public static Quaterniond rotateLocalYDeg(double deg) { return new Quaterniond().rotateLocalY(Math.toRadians(deg)); }
    public static Quaterniond rotateLocalZDeg(double deg) { return new Quaterniond().rotateLocalZ(Math.toRadians(deg)); }

    public static Quaterniond rotateXDeg(double deg) { return new Quaterniond().rotateX(Math.toRadians(deg)); }
    public static Quaterniond rotateYDeg(double deg) { return new Quaterniond().rotateY(Math.toRadians(deg)); }
    public static Quaterniond rotateZDeg(double deg) { return new Quaterniond().rotateZ(Math.toRadians(deg)); }

    public static Quaterniond rotateXRad(double rad) { return new Quaterniond().rotateX(rad); }
    public static Quaterniond rotateYRad(double rad) { return new Quaterniond().rotateY(rad); }
    public static Quaterniond rotateZRad(double rad) { return new Quaterniond().rotateZ(rad); }

    public static Quaterniond rotateXYDeg(double xDeg, double yDeg) { return new Quaterniond().rotateX(Math.toRadians(xDeg)).rotateY(Math.toRadians(yDeg)); }
    public static Quaterniond rotateXZDeg(double xDeg, double zDeg) { return new Quaterniond().rotateY(Math.toRadians(xDeg)).rotateZ(Math.toRadians(zDeg)); }

    public static Quaterniond rotateYXDeg(double yDeg, double xDeg) { return new Quaterniond().rotateX(Math.toRadians(yDeg)).rotateY(Math.toRadians(xDeg)); }
    public static Quaterniond rotateYZDeg(double yDeg, double zDeg) { return new Quaterniond().rotateY(Math.toRadians(yDeg)).rotateZ(Math.toRadians(zDeg)); }

    public static Quaterniond rotateZXDeg(double zDeg, double xDeg) { return new Quaterniond().rotateX(Math.toRadians(zDeg)).rotateY(Math.toRadians(xDeg)); }
    public static Quaterniond rotateZYDeg(double zDeg, double yDeg) { return new Quaterniond().rotateY(Math.toRadians(zDeg)).rotateZ(Math.toRadians(yDeg)); }

    public static Quaterniond rotateXZYDeg(double xDeg, double zDeg, double yDeg) { return new Quaterniond().rotateX(Math.toRadians(xDeg)).rotateZ(Math.toRadians(zDeg)).rotateY(Math.toRadians(yDeg)); }

    public static Quaterniond swingYXRotateTo(Vector3dc from, Vector3dc to, Quaterniond dest) {
        Vector3d euler = new Quaterniond().rotateTo(from, to).getEulerAnglesYXZ(new Vector3d());
        return dest.rotationYXZ(euler.y, euler.x, 0);
    }



}
