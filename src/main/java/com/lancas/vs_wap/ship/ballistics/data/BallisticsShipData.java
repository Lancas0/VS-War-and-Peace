package com.lancas.vs_wap.ship.ballistics.data;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vs_wap.ship.helper.LazyShip;
import com.lancas.vs_wap.ship.type.ProjectileWrapper;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.lang.Math;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BallisticsShipData {
    //public final long projectileShipId;
    @NotNull
    public final ProjectileWrapper projectile;
    public final long propellantShipId;
    public final long artilleryShipId;

    private LazyShip projectileLazyShip;
    private LazyShip propellantLazyShip;
    private LazyShip artilleryLazyShip;

    public long getProjectileId() { return projectile.shipId; }
    public Direction getForwardInProjectileShip() { return projectile.forwardInShip; }
    public Vector3d getForwardInWorld(Matrix4dc shipToWorld) { return JomlUtil.dWorldNormal(shipToWorld, projectile.forwardInShip); }
    /*public final Vector3dc launchDir;
    public final Direction headDirInShip;
    public final Vector3dc geoCenterInShip;

    //todo check shipAABB has top and bottom area 1
    private final AABBi shipAABB;

    private final SavedBlockPos headBp;
    private final SavedBlockPos tailBp;*/
    /*@JsonIgnore
    private ServerShip projectileShipCache;
    @JsonIgnore
    private ServerShip propellantShipCache;
    @JsonIgnore
    private ServerShip artilleryShipCache;*/

    //only for serialize
    private BallisticsShipData() {
        projectile = null;
        /*projectileShipId = */propellantShipId = artilleryShipId = -1;
        //launchDir = new Vector3d(0, 0, 1);
        /*headDirInShip = Direction.SOUTH;
        geoCenterInShip = new Vector3d();
        //shipAABB = new AABBi();
        headBp = new SavedBlockPos();
        tailBp = new SavedBlockPos();*/
        projectileLazyShip = propellantLazyShip = artilleryLazyShip = null;
    }
    public BallisticsShipData(ProjectileWrapper inProjectile, long propShipId, long artilShipId) {
        //projectileShipId = projectileShip.getId();
        projectile = inProjectile;
        propellantShipId = propShipId;
        artilleryShipId = artilShipId;

        projectileLazyShip = LazyShip.ofId(inProjectile.shipId);
        propellantLazyShip = LazyShip.ofId(propShipId);
        artilleryLazyShip = LazyShip.ofId(artilShipId);

        //launchDir = inLaunchDir.get(new Vector3d());

        //calculate headDirInShip
        //Vector3d launchDirInShip = inProjectile.getWorldToShip().transformDirection(launchDir, new Vector3d());
        //headDirInShip = Direction.getNearest(launchDirInShip.x, launchDirInShip.y, launchDirInShip.z);
        //geoCenterInShip = ShipUtil.getShipGeometryCenterInShip(inProjectile);

        /*if (inProjectile.getShipAABB() == null) {
            EzDebug.error("fail to get shipAABB in ShipData constructor");
            shipAABB = new AABBi();
        } else {
            shipAABB = new AABBi(inProjectile.getShipAABB());
        }*/

        /*headBp = new SavedBlockPos(
            headDirInShip.getStepX() > 0 ? shipAABB.maxX - 1 : shipAABB.minX,
            headDirInShip.getStepY() > 0 ? shipAABB.maxY - 1 : shipAABB.minY,
            headDirInShip.getStepZ() > 0 ? shipAABB.maxZ - 1 : shipAABB.minZ
        );
        tailBp = new SavedBlockPos(
            headDirInShip.getStepX() < 0 ? shipAABB.maxX - 1 : shipAABB.minX,
            headDirInShip.getStepY() < 0 ? shipAABB.maxY - 1 : shipAABB.minY,
            headDirInShip.getStepZ() < 0 ? shipAABB.maxZ - 1 : shipAABB.minZ
        );*/
        /*EzDebug.Log("launchDirInShip:" + launchDirInShip);
        //倾斜方向最大
        switch (launchDirInShip.absolute(new Vector3d()).maxComponent()) {
            case 0 -> headDirInShip = new Vector3i((int)Math.signum(launchDirInShip.x), 0, 0);
            case 1 -> headDirInShip = new Vector3i(0, (int)Math.signum(launchDirInShip.y), 0);
            case 2 -> headDirInShip = new Vector3i(0, 0, (int)Math.signum(launchDirInShip.z));
            default -> {
                EzDebug.fatal("can not get headDir InShip, launchDirInShip:" + launchDirInShip);
                headDirInShip = new Vector3i(0, 0, 1);
            }
        }*/
    }

    public ServerShip getProjectileShip(ServerLevel level) { return projectileLazyShip.get(level, null); }
    public ServerShip getPropellantShip(ServerLevel level) { return propellantLazyShip.get(level, null); }
    public ServerShip getArtilleryShip(ServerLevel level)  { return artilleryLazyShip.get(level, null); }

    /*public Vector3d getWorldGeoCenter(Matrix4dc shipToWorld) {
        return projectile.getGeoCenterInWorld(shipToWorld);//shipToWorld.transformPosition(geoCenterInShip, new Vector3d());
    }*/
    /*public double getProjectArea(Matrix4dc worldToShip, Vector3dc worldVel) {
        Vector3d velDirInShip = worldToShip.transformDirection(worldVel, new Vector3d()).normalize();
        AABBic shipAABB = projectile.getShipAABB();

        int lengthX = JomlUtil.lengthX(shipAABB);
        int lengthY = JomlUtil.lengthY(shipAABB);
        int lengthZ = JomlUtil.lengthZ(shipAABB);

        double areaTowardsX = lengthY * lengthZ;
        double areaTowardsY = lengthX * lengthZ;
        double areaTowardsZ = lengthX * lengthY;

        return Math.abs(velDirInShip.x) * areaTowardsX + Math.abs(velDirInShip.y) * areaTowardsY + Math.abs(velDirInShip.z) * areaTowardsZ;
    }*/
    public Vector3dc getLaunchDir() { return projectile.getLaunchDir(); }
    public boolean isHead(BlockPos bp) { return projectile.headBp.equalsBp(bp); }
    public boolean isTail(BlockPos bp) { return projectile.tailBp.equalsBp(bp); }
}
