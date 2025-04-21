package com.lancas.vs_wap.ship.ballistics;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vs_wap.foundation.data.SavedBlockPos;
import com.lancas.vs_wap.ship.ballistics.helper.BallisticsUtil;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShipUtil;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBi;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectileShipWrapper {
    public static boolean isValid(ServerShip projectile) {
        AABBic shipAABB = projectile.getShipAABB();
        if (shipAABB == null) return false;

        if (JomlUtil.sideArea(shipAABB, Direction.UP) == 1) return true;
        if (JomlUtil.sideArea(shipAABB, Direction.SOUTH) == 1) return true;
        if (JomlUtil.sideArea(shipAABB, Direction.EAST) == 1) return true;
        return false;
    }

    public final long shipId;

    public final SavedBlockPos tailBp;
    public final SavedBlockPos headBp;
    public final Direction forwardInShip;
    //do not use dc otherwise can't be serialized
    private final Vector3d launchDir;
    private final Vector3d geoCenterInShip;
    private final AABBi shipAABB;

    public Vector3dc getLaunchDir() { return launchDir; }
    public Ship getShip(Level level) { return ShipUtil.getShipByID(level, shipId); }  //todo notice: unloaded ship
    public ServerShip getShip(ServerLevel level) { return ShipUtil.getServerShipByID(level, shipId); }  //todo notice: unloaded ship
    public Vector3dc getGeoCenterInShip() { return geoCenterInShip; }
    public Vector3d getGeoCenterInWorld(Matrix4dc shipToWorld) { return shipToWorld.transformPosition(geoCenterInShip, new Vector3d()); }
    public AABBic getShipAABB() { return shipAABB; }
    public AABBd getWorldAABB(Matrix4dc shipToWorld) { return BallisticsUtil.quickTransformAABB(shipToWorld, new AABBd()); }

    //only for serialize
    private ProjectileShipWrapper() {
        shipId = -1;
        tailBp = headBp = new SavedBlockPos();
        forwardInShip = Direction.SOUTH;
        launchDir = geoCenterInShip = new Vector3d(); //must set value, otherwise throw may-null warnings
        shipAABB = new AABBi();
    }
    protected ProjectileShipWrapper(ServerShip inShip, Direction inForwardInShip) {
        shipId = inShip.getId();
        forwardInShip = inForwardInShip;

        shipAABB = new AABBi(inShip.getShipAABB());
        headBp = new SavedBlockPos(
            forwardInShip.getStepX() == 1 ? shipAABB.maxX() - 1 : shipAABB.minX(),
            forwardInShip.getStepY() == 1 ? shipAABB.maxY() - 1 : shipAABB.minY(),
            forwardInShip.getStepZ() == 1 ? shipAABB.maxZ() - 1 : shipAABB.minZ()
        );
        tailBp = new SavedBlockPos(
            forwardInShip.getStepX() == -1 ? shipAABB.maxX() - 1 : shipAABB.minX(),
            forwardInShip.getStepY() == -1 ? shipAABB.maxY() - 1 : shipAABB.minY(),
            forwardInShip.getStepZ() == -1 ? shipAABB.maxZ() - 1 : shipAABB.minZ()
        );

        launchDir = JomlUtil.dWorldNormal(inShip.getShipToWorld(), forwardInShip);
        geoCenterInShip = shipAABB.center(new Vector3d());

    }
    public static ProjectileShipWrapper ofIfValid(ServerShip ship, Direction inForwardInShip) {
        if (ship == null || !isValid(ship)) return null;

        return new ProjectileShipWrapper(ship, inForwardInShip);
    }
}
