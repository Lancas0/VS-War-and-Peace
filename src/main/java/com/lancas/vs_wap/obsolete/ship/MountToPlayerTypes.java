package com.lancas.vs_wap.obsolete.ship;

import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.function.Function;

public enum MountToPlayerTypes {
    MountToMainHand(
        player -> {
            Vec3 offset = new Vec3(0.0, player.getEyeHeight() - 0.4, 0.0)
                .add(player.getLookAngle().scale(2))
                .add(player.getUpVector(1).scale(-0.5));

            return JomlUtil.d(offset);
        },
        player -> { return new Quaterniond().rotationTo(new Vector3d(1, 0, 0), JomlUtil.d(player.getLookAngle())); }
    ),
    MountToFeet(
        player -> {
            Vec3 offset = new Vec3(0.0, player.getEyeHeight() - 0.4, 0.0)
                .add(player.getLookAngle().scale(2))
                .add(player.getUpVector(1).scale(-0.5));

            return JomlUtil.d(offset);
        },
        player -> {
            return new Quaterniond().rotationTo(
                new Vector3d(1, 0, 0),
                JomlUtil.d(calculateViewVector(0, player.getYRot()))
            );
        }
    );

    private final Function<Player, Vector3d> getOffsetFunc;
    private final Function<Player, Quaterniond> getRotationFunc;

    private MountToPlayerTypes(Function<Player, Vector3d> inGetOffsetFunc, Function<Player, Quaterniond> inGetRotationFunc) {
        getOffsetFunc = inGetOffsetFunc;
        getRotationFunc = inGetRotationFunc;
    }

    public Vector3d getOffset(Player player) {
        if (player == null) return new Vector3d();
        return getOffsetFunc.apply(player);
    }
    public Quaterniond getRotation(Player player) {
        if (player == null) return new Quaterniond();
        return getRotationFunc.apply(player);
    }





    private static Vec3 calculateViewVector(float p_20172_, float p_20173_) {
        float f = p_20172_ * ((float)Math.PI / 180F);
        float f1 = -p_20173_ * ((float)Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
    }
}
