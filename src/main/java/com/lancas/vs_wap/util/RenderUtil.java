package com.lancas.vs_wap.util;

import com.lancas.vs_wap.debug.EzDebug;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import java.awt.*;

public class RenderUtil {
    public static void renderLine(VertexConsumer buf, Matrix4f matrix, Color color, Vector3d start, Vector3d end, Double width) {
        Vector3d negStart = start.negate(new Vector3d());

        Vector3d wdir = end.sub(start, new Vector3d());
        Vector3d pdir = (
            negStart.sub(wdir.mul(
                negStart.dot(wdir) / wdir.dot(wdir), new Vector3d()
            ), new Vector3d())
        ).normalize();
        Vector3d up = pdir.cross(wdir.normalize(new Vector3d()));
        Vector3d widenUp = up.mul(width, new Vector3d());

        Vector3d lu = widenUp.add(start, new Vector3d());
        Vector3d ld = widenUp.negate(new Vector3d()).add(start);

        Vector3d ru = widenUp.negate(new Vector3d()).add(end);
        Vector3d rd = widenUp.add(end, new Vector3d());

        buf.vertex(matrix, (float)lu.x, (float)lu.y, (float)lu.z).color(color.getRGB()).endVertex();
        buf.vertex(matrix, (float)ld.x, (float)ld.y, (float)ld.z).color(color.getRGB()).endVertex();
        buf.vertex(matrix, (float)ru.x, (float)ru.y, (float)ru.z).color(color.getRGB()).endVertex();
        buf.vertex(matrix, (float)rd.x, (float)rd.y, (float)rd.z).color(color.getRGB()).endVertex();

        EzDebug.log("[RenderUtil]lu:" + lu + ", ld" + ld + ", ru:" + ru + ", rd" + rd);
    }
}

