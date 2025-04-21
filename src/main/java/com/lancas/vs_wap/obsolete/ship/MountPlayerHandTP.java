package com.lancas.vs_wap.obsolete.ship;

/*
import com.lancas.vs_wap.ship.attachment.PlayerHoldingAttachment;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class MountPlayerHandTP extends MountPlayerTP {
    private static final double ROT_RAD = Math.PI / 4;
    private int cantValue = 0;
    public void cant(boolean left) {
        int cantChange = left ? -1 : 1;
        cantValue += cantChange;
        cantValue = Math.max(cantValue, -2);
        cantValue = Math.min(cantValue, 2);
    }


    public MountPlayerHandTP(PlayerHoldingAttachment inHoldingAttachment) {
        super(inHoldingAttachment);
    }

    @Override
    public Vector3d getOffset(Player player) {
        Vec3 offset = new Vec3(0.0, player.getEyeHeight() - 0.4, 0.0)
            .add(player.getLookAngle().scale(4))
            .add(player.getUpVector(1).scale(-0.5));

        return JomlUtil.d(offset);
    }
    @Override
    public Quaterniond getRotateion(Player player) {
        //return new Quaterniond().rotationTo(new Vector3d(0, 0, 1), JomlUtil.d(player.getLookAngle()));
        //return new Quaterniond().lookAlong(JomlUtil.d(player.getLookAngle()), JomlUtil.d(player.getUpVector(1)));
        //player.getViewVector().cross()
        double xRotRad = Math.toRadians(player.getXRot());
        double yRotRad = Math.toRadians(player.getYRot());

        Vector3d locYAxis = JomlUtil.d(player.getUpVector(1));
        Vector3d locXAxis = locYAxis.cross(JomlUtil.d(player.getLookAngle()));

        Quaterniond rot = new Quaterniond().rotateYXZ(Math.toRadians(-player.getYRot()), Math.toRadians(player.getXRot()), cantValue * ROT_RAD);
        return rot;
    }
}
*/