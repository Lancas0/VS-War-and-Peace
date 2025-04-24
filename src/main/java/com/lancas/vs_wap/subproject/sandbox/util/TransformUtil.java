package com.lancas.vs_wap.subproject.sandbox.util;

import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;

public class TransformUtil {
    public static AABBd quickTransform(Matrix4dc transform, AABBic aabbI, AABBd dest) {
        double w = transform.get(3, 3);

        Vector3d col0 = transform.getColumn(0, new Vector3d());
        Vector3d col1 = transform.getColumn(1, new Vector3d());
        Vector3d col2 = transform.getColumn(2, new Vector3d());
        Vector3d col3 = transform.getColumn(3, new Vector3d());

        Vector3d xa = col0.mul(aabbI.minX(), new Vector3d());
        Vector3d xb = col0.mul(aabbI.maxX(), new Vector3d());

        Vector3d ya = col1.mul(aabbI.minY(), new Vector3d());
        Vector3d yb = col1.mul(aabbI.maxY(), new Vector3d());

        Vector3d za = col2.mul(aabbI.minZ(), new Vector3d());
        Vector3d zb = col2.mul(aabbI.maxZ(), new Vector3d());

        Vector3d min = xa.min(xb, new Vector3d()).add(ya.min(yb, new Vector3d())).add(za.min(zb, new Vector3d())).add(col3);
        Vector3d max = xa.max(xb, new Vector3d()).add(ya.max(yb, new Vector3d())).add(za.max(zb, new Vector3d())).add(col3);

        return dest.setMin(min.div(w)).setMax(max.div(w));
    }
}
