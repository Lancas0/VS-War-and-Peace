package com.lancas.vs_wap.obsolete.ship;

/*
import com.lancas.vs_wap.ship.attachment.PlayerHoldingAttachment;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShipTransformProvider;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl;


public abstract class MountPlayerTP implements ServerShipTransformProvider {
    public abstract Vector3d getOffset(Player player);
    public abstract Quaterniond getRotateion(Player player);

    private PlayerHoldingAttachment holdingAttachment;

    public MountPlayerTP(PlayerHoldingAttachment inHoldingAttachment) {
        holdingAttachment = inHoldingAttachment;
    }

    private Quaterniond toQuaternoion(Vec3 euler) {
        double cy = Math.cos(euler.x * 0.5);
        double sy = Math.sin(euler.x * 0.5);
        double cp = Math.cos(euler.y * 0.5);
        double sp = Math.sin(euler.y * 0.5);
        double cr = Math.cos(euler.z * 0.5);
        double sr = Math.sin(euler.z * 0.5);

        Quaterniond q = new Quaterniond(
            cy * cp * sr - sy * sp * cr,
            sy * cp * sr + cy * sp * cr,
            sy * cp * cr - cy * sp * sr,
            cy * cp * cr + sy * sp * sr
        );

        return q;
    }
    private Vec3 calculateViewVector(float p_20172_, float p_20173_) {
        float f = p_20172_ * ((float)Math.PI / 180F);
        float f1 = -p_20173_ * ((float)Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
    }
    private Vec3 getMainHandItemRotAngle(float p_20172_, float p_20173_) {
        float f = p_20172_ * ((float)Math.PI / 180F);
        float f1 = -p_20173_ * ((float)Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
    }
    private Vector3d getFarPos() {
        return new Vector3d(0, -1000, 0);
    }

    @Override
    public @Nullable NextTransformAndVelocityData provideNextTransformAndVelocity(@NotNull ShipTransform shipTransform, @NotNull ShipTransform shipTransform1) {
        //EzDebug.Log("Minecraft has instance:" + (Minecraft.getInstance() != null) + "current player uuid:" + Minecraft.getInstance().player.getStringUUID() + "target uuid:" + );
        Vector3d targetPos = getFarPos();
        Quaterniond targetRot = new Quaterniond();

        Player holder = holdingAttachment.getPlayer();

        if (holdingAttachment != null && holdingAttachment.holding) {
            targetPos = JomlUtil.d(holder.position()).add(getOffset(holder));
            targetRot = getRotateion(holder);//targetRot.rotationTo(new Vector3d(1, 0, 0), JomlUtil.d(player.getLookAngle()));
        }

        return new NextTransformAndVelocityData(
            new ShipTransformImpl(
                targetPos,
                shipTransform.getPositionInShip(),
                targetRot,
                shipTransform.getShipToWorldScaling()
            ),
            new Vector3d(),
            new Vector3d()
        );
    }
}
*/