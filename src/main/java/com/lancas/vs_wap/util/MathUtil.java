package com.lancas.vs_wap.util;

import com.lancas.vs_wap.debug.EzDebug;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class MathUtil {
    public static Vector3d clamp(Vector3dc val, double minLen, double maxLen, Vector3d dest) {
        if (minLen > maxLen) {
            double t = minLen;
            minLen = maxLen;
            maxLen = t;
        }
        if (val.equals(0, 0, 0)) {
            EzDebug.warn("can not clamp a zero vector!");
            dest.set(val);
            return dest;
        }

        double valLenSq = val.lengthSquared();
        if (valLenSq > maxLen) {
            val.normalize(maxLen, dest);
        } else if (valLenSq < minLen) {
            val.normalize(minLen, dest);
        } else {
            dest.set(val);
        }
        return dest;
    }
    public static Vector3d clamp(Vector3dc val, double maxLen, Vector3d dest) {
        if (val.equals(0, 0, 0)) {
            EzDebug.warn("can not clamp a zero vector!");
            dest.set(val);
            return dest;
        }

        double valLenSq = val.lengthSquared();
        if (valLenSq > maxLen) {
            val.normalize(maxLen, dest);
        } else {
            dest.set(val);
        }
        return dest;
    }
    public static Vector3d clamp(Vector3d val, double minLen, double maxLen) { return clamp(val, minLen, maxLen, val); }
    public static Vector3d clamp(Vector3d val, double maxLen) { return clamp(val, maxLen, val); }

    public static Vector3d project(Vector3dc vector, Vector3dc axis, Vector3d dest) {
        return axis.normalize(vector.dot(axis), dest);
    }
    public static void orthogonality(Vector3dc vector, Vector3dc axis, Vector3d destHorizontal, Vector3d destVertical) {
        project(vector, axis, destHorizontal);
        vector.sub(destHorizontal, destVertical);
    }
}
