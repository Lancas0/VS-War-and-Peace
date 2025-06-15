package com.lancas.vswap.ship.ballistics.data;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.util.JomlUtil;
import com.lancas.vswap.util.ShipUtil;
import com.lancas.vswap.util.StrUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;

import java.util.Comparator;

//must hit block, only null for miss
//todo hit on entity?
public class BallisticsHitInfo {
    public BlockPos hitBlockPos;
    public Vector3d worldHitPos;
    public Direction face;
    public Vector3dc worldNormal;
    //all hit infos are generated at first, then check each and set speed. so don't record velocity now;
    //public Vector3dc hitVelocity;
    public double sqDist;   //the raycast dist(squared)
    public long hitShipId;  //-1 for null

    protected BallisticsHitInfo() {
        hitBlockPos = new BlockPos(0, 0, 0);
        worldHitPos = new Vector3d();
        face = Direction.UP;
        worldNormal = new Vector3d(0, 1, 0);
        sqDist = Double.MAX_VALUE;
        hitShipId = -1;
    }
    public BallisticsHitInfo(BlockPos inBlockPos, Vector3dc inWorldHitPos, Direction inFace, Vector3dc inWorldNormal/*, Vector3dc inHitVelocity*/, double inSqDist, long inHitShipId) {
        hitBlockPos = inBlockPos;
        worldHitPos = inWorldHitPos.get(new Vector3d());
        face = inFace;
        worldNormal = inWorldNormal;
        //hitVelocity = inHitVelocity.get(new Vector3d());
        sqDist = inSqDist;
        hitShipId = inHitShipId;

        //EzDebug.log("ball hit info: worldNormal is " + StrUtil.F2(worldNormal));
    }

    public static BallisticsHitInfo inWorld(Vec3 from, BlockPos inBlockPos, Vec3 location, Direction inFace/*, Vector3dc inHitVelocity*/) {
        //EzDebug.log("BallisticsHitInfo in world:" + StrUtil.F2(JomlUtil.dNormal(inFace)));
        return new BallisticsHitInfo(
            inBlockPos,
            JomlUtil.d(location),
            inFace,
            JomlUtil.dNormal(inFace),
            //inHitVelocity,
            from.distanceToSqr(location),  //I didn't take mistakes: mojang take the wrong name, it is the squared length. The name should be distanceToSq. (sqr means square root)
            -1
        );
    }
    public static BallisticsHitInfo inShip(Ship shipBeHit, Vec3 formInShip, BlockPos shipBp, Vec3 locationInShip, Direction faceOfShipBlock/*, Vector3dc inHitVelocity*/) {
        ShipTransform shipTransform = shipBeHit instanceof ClientShip cShip ? cShip.getRenderTransform() : shipBeHit.getTransform();
        Matrix4dc shipToWorld = shipTransform.getShipToWorld();
        Quaterniondc shipToWorldRot = shipTransform.getShipToWorldRotation();

        Vector3dc worldFrom = JomlUtil.transformPosD(shipToWorld, formInShip);
        Vector3dc worldHitPos = JomlUtil.transformPosD(shipToWorld, locationInShip);
        double sqDist = worldFrom.distanceSquared(worldHitPos);

        EzDebug.log("BallisticsHitInfo onship, normal:" + StrUtil.F2(shipToWorldRot.transform(JomlUtil.dNormal(faceOfShipBlock))));

        return new BallisticsHitInfo(
            shipBp,
            worldHitPos,
            faceOfShipBlock,
            shipToWorldRot.transform(JomlUtil.dNormal(faceOfShipBlock)),
            //inHitVelocity,
            sqDist,  //I didn't take mistakes: mojang take the wrong name, it is the squared length. The name should be distanceToSq. (sqr means square root)
            shipBeHit.getId()
        );
    }
    public static @Nullable BallisticsHitInfo clipIncludeShip(Level level, ClipContext clip) {
        BlockHitResult hitResult = RaycastUtilsKt.clipIncludeShips(level, clip, false);

        if (hitResult.getType() == HitResult.Type.MISS)
            return null;

        Ship hitShip = ShipUtil.getShipAt(level, hitResult.getBlockPos());
        if (hitShip == null)
            return inWorld(clip.getFrom(), hitResult.getBlockPos(), hitResult.getLocation(), hitResult.getDirection());
        else {
            Vector3d fromInShip = hitShip.getWorldToShip().transformPosition(JomlUtil.d(clip.getFrom()));
            return inShip(hitShip, JomlUtil.v3(fromInShip), hitResult.getBlockPos(), hitResult.getLocation(), hitResult.getDirection());
        }
    }

    @Override
    public String toString() {
        return "BallisticsHitInfo{" +
            "blockPos=" + hitBlockPos +
            ", worldHitPos=" + worldHitPos +
            ", face=" + face +
            ", worldNormal=" + worldNormal +
            //", hitVelocity=" + hitVelocity +
            ", sqDist=" + sqDist +
            ", hitShipId=" + hitShipId +
            '}';
    }

    public BlockState getHitBlockState(Level level) { return level.getBlockState(hitBlockPos); }

    public static Comparator<BallisticsHitInfo> nearFirst() {
        return Comparator.comparingDouble(a -> a.sqDist);
    }
}
