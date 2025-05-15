package com.lancas.vs_wap.ship.attachment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vs_wap.foundation.data.SavedBlockPos;
import com.lancas.vs_wap.ship.data.ISavableAttachment;
import com.lancas.vs_wap.util.JomlUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4dc;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.List;
import java.util.stream.Stream;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HoldableAttachment implements ISavableAttachment {
    //public UUID playerUUID;
    public SavedBlockPos holdPivotBpInShip;
    public Direction forwardInShip;

    public BlockPos getPivotBpInShip() { return holdPivotBpInShip.toBp(); }

    private HoldableAttachment() {}

    public static HoldableAttachment apply(ServerShip ship/*, UUID inPlayerUUID*/, BlockPos inHoldPivotBpInShip, Direction forwardInShip) {
        HoldableAttachment att = ship.getAttachment(HoldableAttachment.class);
        if (att == null) {
            att = new HoldableAttachment();
            ship.saveAttachment(HoldableAttachment.class, att);
        }

        //att.playerUUID = inPlayerUUID;
        att.holdPivotBpInShip = new SavedBlockPos(inHoldPivotBpInShip);
        att.forwardInShip = forwardInShip;
        return att;
    }
    public static HoldableAttachment applyByPlayerForward(ServerShip ship, Player player, BlockPos inHoldPivotBpInShip, boolean oppositeByShift) {
        HoldableAttachment att = ship.getAttachment(HoldableAttachment.class);
        if (att == null) {
            att = new HoldableAttachment();
            ship.saveAttachment(HoldableAttachment.class, att);
        }

        Vector3d forwardInWorld = JomlUtil.d(player.getForward());
        if (player.isShiftKeyDown() && oppositeByShift) {
            forwardInWorld.negate();
        }
        Direction forwardInShip = JomlUtil.nearestDir(ship.getWorldToShip().transformDirection(forwardInWorld));

        //att.playerUUID = player.getUUID();
        att.holdPivotBpInShip = new SavedBlockPos(inHoldPivotBpInShip);
        att.forwardInShip = forwardInShip;
        return att;
    }

    /*
    public Vector3d getHoldPosInWorld(Player player, Matrix4dc shipToWorld, Vector3dc shipPosInWorld) {
        //todo check whether the block is in ship
        Vector3d holdingWorldPos = JomlUtil.d(player.position())
            .add(0.0, player.getEyeHeight() - 0.4, 0.0)  //get main hand here. maybe
            .add(JomlUtil.d(player.getLookAngle().scale(2)))
            .add(JomlUtil.d(player.getUpVector(1).scale(-0.5)));

        Vector3d anchorPosInWorld = JomlUtil.dWorldCenter(shipToWorld, holdPivotBpInShip.toBp());

        Vector3d movement = holdingWorldPos.sub(anchorPosInWorld, new Vector3d());
        Vector3d newPosInWorld = shipPosInWorld.add(movement, new Vector3d());

        //EzDebug.log("holdingWorldPos:" + holdingWorldPos + ", anchorPosInWorld:" + anchorPosInWorld + ", movement:" + movement + ", newPosInWorld:" + newPosInWorld);

        return newPosInWorld;
    }
    private double rotateAroundForward = 0;
    public Quaterniond getHoldRotation(Player player, Matrix4dc shipToWorld) {
        Vector3d forwardInWorld = JomlUtil.dWorldNormal(shipToWorld, forwardInShip);
        Vector3d targetWorldForward = JomlUtil.d(player.getLookAngle());

        Quaterniond defaultRotation = JomlUtil.rotateYXZDeg(-player.getYRot(), player.getXRot(), 0);

        double xRotRad = JomlUtil.xRotRad(player);
        double yRotRad = JomlUtil.yRotRad(player);

        return switch (forwardInShip) {
            case SOUTH -> JomlUtil.rotateYRad(-yRotRad).rotateX(xRotRad) .rotateZ(rotateAroundForward);                    //ok
            case NORTH -> JomlUtil.rotateYDeg(180)     .rotateY(-yRotRad) .rotateX(-xRotRad).rotateZ(-rotateAroundForward);//ok
            case UP ->    JomlUtil.rotateXDeg(90)      .rotateZ(yRotRad) .rotateX(xRotRad) .rotateZ(rotateAroundForward);  //ok
            case DOWN ->  JomlUtil.rotateXDeg(-90)     .rotateZ(-yRotRad).rotateX(xRotRad) .rotateZ(rotateAroundForward);  //ok
            case EAST ->  JomlUtil.rotateYDeg(-90)     .rotateY(-yRotRad).rotateZ(-xRotRad).rotateX(rotateAroundForward);  //ok
            case WEST ->  JomlUtil.rotateYDeg(90)      .rotateY(-yRotRad) .rotateZ(xRotRad).rotateX(-rotateAroundForward); //ok
        };
    }*/

    public static Quaterniond rotateLocDirToZPositive(Direction forwardInShip) {
        return switch (forwardInShip) {
            case SOUTH -> new Quaterniond();
            case NORTH -> JomlUtil.rotateYDeg(180);
            case UP ->    JomlUtil.rotateXDeg(90);
            case DOWN ->  JomlUtil.rotateXDeg(-90);
            case EAST ->  JomlUtil.rotateYDeg(-90);
            case WEST ->  JomlUtil.rotateYDeg(90);
        };
    }
    public static Quaterniond rotateForwardToDirection(Direction forwardInShip) {
        return switch (forwardInShip) {
            case SOUTH -> new Quaterniond();
            case NORTH -> JomlUtil.rotateYDeg(180);
            case UP ->    JomlUtil.rotateXDeg(-90);
            case DOWN ->  JomlUtil.rotateXDeg(90);
            case EAST ->  JomlUtil.rotateYDeg(90);
            case WEST ->  JomlUtil.rotateYDeg(-90);
        };
    }
    public static Quaterniond rotateDirectionToUp(Direction forwardInShip) {
        return switch (forwardInShip) {
            case SOUTH -> JomlUtil.rotateXDeg(-90);
            case NORTH -> JomlUtil.rotateXDeg(90);
            case UP ->    new Quaterniond();
            case DOWN ->  JomlUtil.rotateXDeg(180);
            case EAST ->  JomlUtil.rotateZDeg(90);
            case WEST ->  JomlUtil.rotateZDeg(-90);
        };
    }
    /*public static Quaterniond rotateDirectionToDown(Direction forwardInShip) {
        return switch (forwardInShip) {
            case SOUTH -> JomlUtil.rotateXDeg(-90);
            case NORTH -> JomlUtil.rotateXDeg(90);
            case UP ->    new Quaterniond();
            case DOWN ->  JomlUtil.rotateXDeg(180);
            case EAST ->  JomlUtil.rotateZDeg(90);
            case WEST ->  JomlUtil.rotateZDeg(-90);
        };
    }*/
    public static Quaterniond rotateDirectionToward(Matrix4dc shipToWorld, Direction forwardInShip, Vector3dc toward) {
        Quaterniond forwardToZ = rotateLocDirToZPositive(forwardInShip);
        Vector3d rotatedY = forwardToZ.transform(new Vector3d(0, 1, 0));

        Vector3d zToToward = new Quaterniond().rotateTo(new Vector3d(0, 0, 1), toward).getEulerAnglesYXZ(new Vector3d());

        //Vector3d localXAxis = new Quaterniond().rotateAxis(zToToward.y(), new Vector3d(0, 1, 0)).transform(new Vector3d(1, 0, 0));
        //return rotateDirectionToForward(forwardInShip).rotateAxis(zToToward.y(), new Vector3d(0, 1, 0));//.rotateAxis(zToToward.x(), localXAxis);
        //Vector3d localX = rotateLocDirToZPositive(forwardInShip).transform(new Vector3d(1, 0, 0));
        Vector3d forwardInWorld = JomlUtil.dWorldNormal(shipToWorld, forwardInShip);
        Vector3d worldUp = new Vector3d(0, 1, 0);

        Vector3d worldRight = forwardInWorld.cross(worldUp, new Vector3d()).normalize();

        return rotateLocDirToZPositive(forwardInShip).rotateAxis(zToToward.y, worldUp).rotateAxis(zToToward.x, worldRight);//.rotateLocalY(zToToward.y).rotateLocalX(zToToward.x);//.rotateAxis(zToToward.y(), rotatedY);
    }


    @Override
    public Stream<BlockPos> getAllBpInShipToSave() {
        return Stream.of(holdPivotBpInShip.toBp());
    }
    @Override
    public void loadAllBp(List<BlockPos> bpsInShip) {
        holdPivotBpInShip = new SavedBlockPos(bpsInShip.get(0));
    }
}
