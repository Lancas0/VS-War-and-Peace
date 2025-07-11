package com.lancas.vswap.foundation.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.valkyrienskies.core.impl.shadow.S;

import java.util.Objects;


@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class SavedBlockPos {
    public final int x;
    public final int y;
    public final int z;

    public SavedBlockPos() { x = y = z = 0; }
    public SavedBlockPos(BlockPos bp) {
        x = bp.getX();
        y = bp.getY();
        z = bp.getZ();
    }
    public SavedBlockPos(int inX, int inY, int inZ) {
        x = inX; y = inY; z = inZ;
    }

    public BlockPos toBp() { return new BlockPos(x, y, z); }
    public Vec3i toV3I() { return new Vec3i(x, y, z); }
    public Vector3i toJomlI() { return new Vector3i(x, y, z); }

    public Vec3 center() { return new Vec3(x + 0.5, y + 0.5, z + 0.5); }
    public Vector3d dCenter() { return new Vector3d(x + 0.5, y + 0.5, z + 0.5); }
    public Vector3d dLowerCorner() { return new Vector3d(x, y, z); }

    public SavedBlockPos relative(Direction d) {
        return switch (d) {
            case SOUTH -> new SavedBlockPos(x, y, z + 1);
            case NORTH -> new SavedBlockPos(x, y, z - 1);
            case UP ->  new SavedBlockPos(x, y + 1, z);
            case DOWN -> new SavedBlockPos(x, y - 1, z);
            case EAST -> new SavedBlockPos(x + 1, y, z);
            case WEST -> new SavedBlockPos(x - 1, y, z);
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SavedBlockPos that = (SavedBlockPos) o;
        return x == that.x && y == that.y && z == that.z;
    }
    public boolean equals(int ox, int oy, int oz) {
        return x == ox && y == oy && z == oz;
    }
    public boolean equalsBp(Vec3i bp) {
        if (bp == null) return false;
        return x == bp.getX() && y == bp.getY() && z == bp.getZ();
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }


    /*public JSONObject serialize() {
        JSONObject json = new JSONObject();
        json.put("x", x);
        json.put("y", y);
        json.put("z", z);
        return json;
    }
    public void deserialize(JSONObject object) {
        x = object.getIntValue("x");
        y = object.getIntValue("y");
        z = object.getIntValue("z");
    }*/
}
