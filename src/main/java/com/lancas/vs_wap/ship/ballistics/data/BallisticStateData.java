package com.lancas.vs_wap.ship.ballistics.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vs_wap.content.blocks.artillery.IBarrel;
import com.lancas.vs_wap.content.blocks.artillery.IBreech;
import com.lancas.vs_wap.foundation.Constants;
import com.lancas.vs_wap.util.JomlUtil;
import com.lancas.vs_wap.util.ShipUtil;
import lombok.NoArgsConstructor;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BallisticStateData {
    @JsonIgnore
    private static int STOPPED_TICK = 120;
    @JsonIgnore
    private static double STOPPED_SQ_THERSOLD = 0.01;
    @JsonIgnore
    private static final Vector3dc ANTI_GRAVITY = new Vector3d(0, 10, 0);
    @JsonIgnore
    public static final double PROPELLANT_RELEASE_FACTOR = 0.15;

    private boolean isOutArtillery = false;
    //private boolean isTriggered = false;
    public double timeInBarrel = 0;
    private double remainPower;
    private int stoppedTick = 40;  //todo more time
    private Vector3d lastFrameVelocity = new Vector3d();
    //@JsonIgnore
    //private Long lastChunk;

    public BallisticStateData(double propellantPower) {
        remainPower = propellantPower;
    }
    public boolean getIsOutArtillery() { return isOutArtillery; }
    //public boolean getTriggered() { return isTriggered; }
    //public void setTriggered(boolean val) { isTriggered = val; }
    ///public void updateLastFrameVel(Vector3d newVel) { lastFrameVelocity = newVel; }
    public Vector3dc getLastFrameVel() { return lastFrameVelocity; }

    public void updateState(ServerLevel level, ServerShip projectileShip, @Nullable ServerShip artilleryShip) {
        lastFrameVelocity = projectileShip.getVelocity().get(new Vector3d());

        if (!isOutArtillery) {
            isOutArtillery = updateIsOutArtillery(level, projectileShip, artilleryShip);
        }
    }
    private boolean updateIsOutArtillery(ServerLevel level, ServerShip projectileShip, @Nullable ServerShip artilleryShip) {
        if (artilleryShip == null) {
            Vector3d worldCenter = ShipUtil.getShipGeometryCenterInWorld(projectileShip);
            BlockState centerState = level.getBlockState(JomlUtil.bpContaining(worldCenter));
            if (centerState.getBlock() instanceof IBarrel || centerState.getBlock() instanceof IBreech) {
                return false;
            }

            for (Direction face : Direction.values()) {
                Vector3d faceCenter = ShipUtil.getShipFaceCenterInWorld(projectileShip, face);
                BlockState faceState = level.getBlockState(JomlUtil.bpContaining(faceCenter));
                if (faceState.getBlock() instanceof IBarrel || faceState.getBlock() instanceof IBreech)
                    return false;
            }
            return true;
        } else {
            //inship, simply check whether the aabbs are intersecting
            //todo if the barrel is fixed on a large ship, maybe some changes should be done

            AABBd projectileWorldAABB = new AABBd(projectileShip.getWorldAABB());
            return !(artilleryShip.getWorldAABB().intersectsAABB(projectileWorldAABB));
        }
    }

    @Nullable
    public Vector3d applyForceInBarrel(PhysShipImpl physShip, Vector3dc launchDir) {
        if (isOutArtillery) return null;  //todo apply drag force if out artillery

        boolean debug = false;
        if (timeInBarrel < Constants.PHYS_FRAME_TIME) {
            timeInBarrel = Constants.PHYS_FRAME_TIME;
            debug = true;
        }


        double mass = physShip.get_inertia().getShipMass();

        //F*x=E -> F = sqrt(2E/m)/t
        double applyEnergy = remainPower * PROPELLANT_RELEASE_FACTOR;
        remainPower -= applyEnergy;
        double fScale = Math.sqrt(2 * applyEnergy * mass) / timeInBarrel;  //todo should use timer in barrel or just use tick time(1/60)?
        Vector3d force = launchDir.normalize(fScale, new Vector3d());

        physShip.applyInvariantForce(force);
        physShip.applyInvariantForce(ANTI_GRAVITY.mul(mass, new Vector3d()));

        timeInBarrel += Constants.PHYS_FRAME_TIME;

        return new Vector3d(force);
    }

    public boolean tickStopped(ServerShip projectileShip) {
        if (stoppedTick <= 0) return true;

        if (projectileShip.getVelocity().lengthSquared() <= STOPPED_SQ_THERSOLD) {
            return --stoppedTick < 0;
        } else {  //speed > thershold
            stoppedTick = STOPPED_TICK;
            return false;
        }
    }

    /*public boolean tickChunkStuff(ServerShip projectileShip, ) {
        Vector3dc worldPos = projectileShip.getTransform().getPositionInWorld();
        int chunkX = ((int)worldPos.x()) << 4;
        int chunkY = ((int)worldPos.z()) << 4;

        long curChunk = ChunkPos.asLong();
        if (lastChunk == null || )
    }*/

    /*
    //todo drag force
    Vector3dc vel = physShipImpl.getPoseVel().getVel();
            Vector3d dragForce = vel.mul(-0.5 * vel.length() * dragFactorCalculated, new Vector3d());  //v has length vLen, mul vLen so we get vLen^2
            //EzDebug.Log("velSq:" + vel.lengthSquared() + ", dragForce:" + StringUtil.toNormalString(dragForce));
            physShip.applyInvariantForce(dragForce);


    */

    @Override
    public String toString() {
        return "BallisticStateData{" +
            "isOutArtillery=" + isOutArtillery +
            //", isTriggered=" + isTriggered +
            ", timeInBarrel=" + timeInBarrel +
            ", remainPower=" + remainPower +
            ", stoppedTick=" + stoppedTick +
            ", lastFrameVelocity=" + lastFrameVelocity +
            '}';
    }
}
