package com.lancas.vs_wap.util;

import com.lancas.vs_wap.foundation.api.math.ForceOnPos;
import net.minecraft.network.FriendlyByteBuf;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;

public class BytesUtil {
    public static FriendlyByteBuf writeVector(FriendlyByteBuf buf, Vector3dc vector) {
        buf.writeVector3f(vector.get(new Vector3f()));
        return buf;
    }
    public static FriendlyByteBuf writeForceOnPos(FriendlyByteBuf buf, ForceOnPos forceOnPos) {
        writeVector(buf, forceOnPos.force());
        writeVector(buf, forceOnPos.pos());
        return buf;
    }

    public static FriendlyByteBuf readVector(FriendlyByteBuf buf, Vector3d dest) {
        dest.set(buf.readVector3f());
        return buf;
    }
    public static ForceOnPos readForceOnPos(FriendlyByteBuf buf) {
        Vector3d force = new Vector3d(), pos = new Vector3d();

        readVector(buf, force);
        readVector(buf, pos);

        return new ForceOnPos(force, pos);
    }
}
