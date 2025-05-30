package com.lancas.vswap.util;

import com.lancas.vswap.debug.EzDebug;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;
import org.joml.primitives.AABBd;
import org.valkyrienskies.physics_api.voxel.CollisionPoint;
import org.valkyrienskies.physics_api.voxel.LodBlockBoundingBox;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.level.block.Block.box;

public class ShapeBuilder {
    public static ShapeBuilder ofEmpty() { return new ShapeBuilder(); }
    public static ShapeBuilder ofConcaveUp(int thick) { return new ShapeBuilder(concaveUp(thick)); }
    public static ShapeBuilder ofCubicRing(int sx, int sy, int sz, int thick, int height) { return new ShapeBuilder(cubicRing(sx, sy ,sz, thick, height)); }
    public static ShapeBuilder ofCenterBlock(int len) { return new ShapeBuilder(centerBlock(len)); }
    public static ShapeBuilder ofSide(Direction face, int thick) { return new ShapeBuilder(side(face, thick)); }
    public static ShapeBuilder ofBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) { return new ShapeBuilder(box(minX, minY, minZ, maxX, maxY, maxZ)); }
    public static ShapeBuilder ofPrism(Direction dir, double faceLen) { return new ShapeBuilder(prism(dir, faceLen)); }

    public static VoxelShape rotateVoxelShape(VoxelShape defaultShape, Direction defaultDir, Direction toDir) {
        if (defaultDir == Direction.UP) {
            return new ShapeBuilder(defaultShape).getRotated(toDir);  //todo not create ShapeBuilder?
        } else if (defaultDir == Direction.NORTH && (toDir != Direction.UP && toDir != Direction.DOWN)) {
            return new ShapeBuilder(defaultShape).getHorizonRotated(toDir);
        }

        EzDebug.warn("the rotateVoxelShape is still incompleted and fail to handle args: defaultDir:" + defaultDir + ", toDir" + toDir + ", returning default shape");
        return defaultShape;
    }

    private VoxelShape shape;
    public ShapeBuilder() { shape = Shapes.empty(); }
    public ShapeBuilder(VoxelShape inShape) { shape = inShape; }

    public VoxelShape get() { return shape; }
    public AABB getBounds() { return shape.bounds(); }
    public AABBd getDBounds() { return JomlUtil.d(getBounds()); }
    public LodBlockBoundingBox createVSBounds() {
        AABB bound = shape.bounds();
        return LodBlockBoundingBox.Companion.createVSBoundingBox(
            (byte) (16 * bound.minX),
            (byte) (16 * bound.minY),
            (byte) (16 * bound.minZ),
            (byte) (16 * bound.maxX),
            (byte) (16 * bound.maxY),
            (byte) (16 * bound.maxZ)
        );
    }
    public List<CollisionPoint> createCollisionPoints() {
        AABB bound = shape.bounds();
        List<CollisionPoint> collisionPoints = new ArrayList<>();

        for (double x : new double[] { bound.minX, bound.maxX })
            for (double y : new double[] { bound.minY, bound.maxY })
                for (double z : new double[] { bound.minZ, bound.maxZ }) {
                    collisionPoints.add(
                        new CollisionPoint(
                            new Vector3f((float)x, (float)y, (float)z),
                            0.1f
                        )
                    );
                }
        return collisionPoints;
    }

    public ShapeBuilder rotate(Direction dir) {
        shape = getRotated(dir);
        return this;
    }
    public VoxelShape getRotated(Direction dir) {
        final VoxelShape[] rotated = {Shapes.empty()};

        shape.forAllBoxes(
            (minX, minY, minZ, maxX, maxY, maxZ) -> {
                AABB rotatedAABB = switch (dir) {
                    case UP -> new AABB(minX, minY, minZ, maxX, maxY, maxZ);
                    case DOWN -> new AABB(minX, 1 - maxY, minZ, maxX, 1 - minY, maxZ);
                    case NORTH -> new AABB(minX, minZ, 1 - maxY, maxX, maxZ, 1 - minY);
                    case SOUTH -> new AABB(minX, minZ, minY, maxX, maxZ, maxY);
                    case WEST -> new AABB(1 - maxY, minX, minZ, 1 - minY, maxX, maxZ);
                    case EAST -> new AABB(minY, minX, minZ, maxY, maxX, maxZ);

                    //default -> new AABB(minX, minY, minZ, maxX, maxY, maxZ);
                };

                rotated[0] = Shapes.or(rotated[0], Shapes.create(rotatedAABB));
            }
        );
        return rotated[0];
    }
    public ShapeBuilder horizonRotated(Direction dir) {
        shape = getHorizonRotated(dir);
        return this;
    }
    public VoxelShape getHorizonRotated(Direction dir) {
        final VoxelShape[] rotated = {Shapes.empty()};

        shape.forAllBoxes(
            (minX, minY, minZ, maxX, maxY, maxZ) -> {
                AABB rotatedAABB = switch (dir) {
                    /*case NORTH -> new AABB(minX, minY, minZ, maxX, maxY, maxZ);
                    case SOUTH -> new AABB(1 - maxZ, minY, 1 - maxX, 1 - minZ, maxY, 1 - minX);
                    case WEST -> new AABB(minZ, minY, 1 - maxX, maxZ, maxY, 1- minX);
                    case EAST -> new AABB(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX);*/
                    case NORTH -> new AABB(minX, minY, minZ, maxX, maxY, maxZ);
                    case EAST -> new AABB(minZ, minY, 1 - maxX, maxZ, maxY, 1 - minX);
                    case SOUTH -> new AABB(1 - maxX, minY, 1 - maxZ, 1 - minX, maxY, 1- minZ);
                    case WEST -> new AABB(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX);

                    default -> new AABB(minX, minY, minZ, maxX, maxY, maxZ);
                };

                rotated[0] = Shapes.or(rotated[0], Shapes.create(rotatedAABB));
            }
        );
        return rotated[0];
    }

    //public static VoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) { return box(minX, minY, minZ, maxX, maxY, maxZ); }
    public static VoxelShape cubicRing(int startX, int startY, int startZ, int thick, int height) {
        int maxX = 16 - startX;
        int maxY = startY + height;
        int maxZ = 16 - startZ;
        return Shapes.join(
            Shapes.join(box(startX, startY, startZ, maxX, maxY, startZ + thick), box(startX, startY, maxZ - thick, maxX, maxY, maxZ), BooleanOp.OR),
            Shapes.join(box(startX, startY, startZ + thick, startX + thick, maxY, maxZ - thick), box(maxX - thick, startY, startZ + thick, maxX, maxY, maxZ - thick), BooleanOp.OR),
            BooleanOp.OR
        );
    }
    public static VoxelShape concaveUp(int thick) {
        return Shapes.or(side(Direction.DOWN, thick), side(Direction.WEST, thick), side(Direction.EAST, thick));
    }
    public static VoxelShape centerBlock(int len) {
        double gap = (16.0 - len) / 2.0;
        return box(gap, gap, gap, gap + len, gap + len, gap + len);
    }
    public static VoxelShape side(Direction face, int thick) {
        return switch (face) {
            case UP -> box(0, 16 - thick, 0, 16, 16, 16);
            case DOWN -> box(0, 0, 0, 16, thick, 16);
            case NORTH -> box(0, 0, 0, 16, 16, thick);
            case EAST -> box(16 - thick, 0, 0, 16, 16, 16);
            case SOUTH -> box(0, 0, 16 - thick, 16, 16, 16);
            case WEST -> box(0, 0, 0, thick, 16, 16);
        };
    }
    public static VoxelShape prism(Direction dir, double faceLen) {
        double min = (16.0 - faceLen) / 2.0;
        double max = min + faceLen;
        return switch (dir) {
            case UP, DOWN -> box(min, 0, min, max, 16, max);
            case NORTH, SOUTH -> box(min, min, 0, max, max, 16);
            case EAST, WEST -> box(0, min, min, 16, max, max);
        };
    }


    public ShapeBuilder append(VoxelShape other) {
        if (other == null) return this;
        shape = Shapes.or(shape, other);
        return this;
    }
    public ShapeBuilder appendBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        shape = Shapes.or(shape, box(minX, minY, minZ, maxX, maxY, maxZ));
        return this;
    }
    public ShapeBuilder append(ShapeBuilder other) {
        return append(other.shape);
    }
    public ShapeBuilder remove(VoxelShape other) {
        if (other == null) return this;
        shape = Shapes.join(shape, other, (first, second) -> first && !second);
        return this;
    }
    public ShapeBuilder remove(ShapeBuilder other) {
        return remove(other.shape);
    }

    @Deprecated  //need testing
    public ShapeBuilder move(double x, double y, double z) {
        shape.move(x / 16.0, y / 16.0, z / 16.0);
        return this;
    }

    /*
    public static AABB rotateX90(AABB bound) {
        Quaterniond rot = new Quaterniond().rotateLocalX(Math.PI / 2.0);
        return new AABB(bound.minX, bound.minZ, bound.minY, bound.maxX, bound.maxZ, bound.maxY);
    }
    /*public static AABB rotateX(AABB bound, double degree) {
        double rad = Math.toRadians(degree);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);

        double y1 = bound.minY * cos - bound.minZ * sin;
        double y2 = bound.maxY * cos - bound.maxZ * sin;

        double z1 = bound.minY * sin + bound.minZ * cos;
        double z2 = bound.maxY * sin + bound.maxZ * cos;

        return new AABB(bound.minX, Math.min(y1, y2), Math.min(z1, z2), bound.maxX, Math.max(y1, y2), Math.max(z1, z2));
    }
    public static AABB rotateZ(AABB bound, double degree) {
        double rad = Math.toRadians(degree);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);

        double x1 = bound.minX * cos - bound.minY * sin;
        double x2 = bound.maxX * cos - bound.maxY * sin;

        double y1 = bound.minX * sin + bound.minY * cos;
        double y2 = bound.maxX * sin + bound.maxY * cos;

        return new AABB(Math.min(x1, x2), Math.min(y1, y2), bound.minZ, Math.max(x1, x2), Math.max(y1, y2), bound.maxZ);
    }
    /*
    // 绕Z轴旋转（角度单位：度）
    private static AABB rotateZ(AABB aabb, double degrees) {
        double rad = Math.toRadians(degrees);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        return new AABB(
            rotateZ(aabb.minX, aabb.minY, aabb.minZ, cos, sin),
            rotateZ(aabb.maxX, aabb.maxY, aabb.maxZ, cos, sin)
        );
    }

    private static AABB rotateZ(double x, double y, double z, double cos, double sin) {
        double newX = x * cos - y * sin;
        double newY = x * sin + y * cos;
        return new AABB(newX, newY, z, newX, newY, z);
    }
    /*
    //todo quick rotation
    public static AABB rotateX90(AABB bound) {
        Quaterniond rot = new Quaterniond().rotateLocalX(Math.PI / 2.0);
        return new AABB(bound.minX, bound.minZ, bound.minY, bound.maxX, bound.maxZ, bound.maxY);
    }

    public static AABB rotateX180(AABB bound) {
        return rotateX90(rotateX90(bound));
    }

    public static AABB rotateX270(AABB bound) {
        return rotateX90(rotateX180(bound));
    }

    public static AABB rotateY90(AABB bound) {
        return new AABB(bound.minZ, bound.minY, bound.minX, bound.maxZ, bound.maxY, bound.maxX);
    }

    public static AABB rotateY180(AABB bound) {
        return rotateY90(rotateY90(bound));
    }

    public static AABB rotateY270(AABB bound) {
        return rotateY90(rotateY180(bound));
    }*/
}