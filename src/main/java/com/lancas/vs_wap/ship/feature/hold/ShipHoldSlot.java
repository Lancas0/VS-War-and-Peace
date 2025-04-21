package com.lancas.vs_wap.ship.feature.hold;

import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4dc;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public enum ShipHoldSlot {
    MainHand(
        (player, pivotBpInShip, localForward, shipToWorld, shipPosInWorld) -> {
            Vector3d holdWorldPos = JomlUtil.dPosition(player)
                .add(0.0, player.getEyeHeight() - 0.4, 0.0)  //get main hand here. maybe
                .add(JomlUtil.d(player.getLookAngle().scale(2)))
                .add(JomlUtil.d(player.getUpVector(1).scale(-0.5)));

            return holdPosByPivotWorldPos(holdWorldPos, pivotBpInShip, shipToWorld, shipPosInWorld);
        },
        (player, pivotBpInShip, localForward) -> {
            double xRotRad = JomlUtil.xRotRad(player);
            double yRotRad = JomlUtil.yRotRad(player);

            return switch (localForward) {
                case SOUTH -> JomlUtil.rotateYRad(-yRotRad).rotateX(xRotRad) .rotateZ(0);                    //ok
                case NORTH -> JomlUtil.rotateYDeg(180)     .rotateY(-yRotRad).rotateX(-xRotRad).rotateZ(-0); //ok
                case UP ->    JomlUtil.rotateXDeg(90)      .rotateZ(yRotRad) .rotateX(xRotRad) .rotateZ(0);  //ok
                case DOWN ->  JomlUtil.rotateXDeg(-90)     .rotateZ(-yRotRad).rotateX(xRotRad) .rotateZ(0);  //ok
                case EAST ->  JomlUtil.rotateYDeg(-90)     .rotateY(-yRotRad).rotateZ(-xRotRad).rotateX(0);  //ok
                case WEST ->  JomlUtil.rotateYDeg(90)      .rotateY(-yRotRad).rotateZ(xRotRad).rotateX(-0);  //ok
            };
        }
    ),
    Back(
        (player, pivotBpInShip, localForward, shipToWorld, shipPosInWorld) -> {
            double yRotRad = JomlUtil.yRotRad(player);

            Vector3d rotationVec = new Quaterniond().rotateY(-yRotRad).transform(new Vector3d(0, 0, 1));

            Vector3d pivotWorldPos = JomlUtil.dPosition(player)
                .add(0.0, 0.25, 0.0)
                .add(rotationVec.mul(-1));

            return holdPosByPivotWorldPos(pivotWorldPos, pivotBpInShip, shipToWorld, shipPosInWorld);
        },
        (player, pivotBpInShip, localForward) -> {
            return switch (localForward) {
                case SOUTH -> JomlUtil.rotateXDeg(-90);
                case NORTH -> JomlUtil.rotateXDeg(90);
                case UP ->    new Quaterniond();
                case DOWN ->  JomlUtil.rotateXDeg(180);
                case EAST ->  JomlUtil.rotateZDeg(90);
                case WEST ->  JomlUtil.rotateZDeg(-90);
            };
        }
    )

    ;
    private ShipHoldSlot(@NotNull HoldPosGetter inPosGetter, @NotNull HoldRotationGetter inRotationGetter) {
        posGetter = inPosGetter;
        rotationGetter = inRotationGetter;
    }

    @FunctionalInterface
    public static interface HoldPosGetter {
        public Vector3d apply(Player player, BlockPos pivotBpInShip, Direction localForward, Matrix4dc shipToWorld, Vector3dc shipPosInWorld);
    }
    @FunctionalInterface
    public static interface HoldRotationGetter {
        public Quaterniond apply(Player player, BlockPos pivotBpInShip, Direction localForward);
    }
    public static Vector3d holdPosByPivotWorldPos(Vector3dc pivotWorldPos, BlockPos pivotBp, Matrix4dc shipToWorld, Vector3dc shipPosInWorld) {
        Vector3d anchorPosInWorld = JomlUtil.dWorldCenter(shipToWorld, pivotBp);

        Vector3d movement = pivotWorldPos.sub(anchorPosInWorld, new Vector3d());
        Vector3d newPosInWorld = shipPosInWorld.add(movement, new Vector3d());
        return newPosInWorld;
    }


    private final HoldPosGetter posGetter;
    private final HoldRotationGetter rotationGetter;


    public String slotName() {
        return this.name();
    }
    public Vector3d getHoldPos(Player player, BlockPos pivotBpInShip, Direction localForward, Matrix4dc shipToWorld, Vector3dc shipPosInWorld) {
        return posGetter.apply(player, pivotBpInShip, localForward, shipToWorld, shipPosInWorld);
    }
    public Quaterniond getHoldRotation(Player player, BlockPos pivotBpInShip, Direction localForward) {
        return rotationGetter.apply(player, pivotBpInShip, localForward);
    }
}
