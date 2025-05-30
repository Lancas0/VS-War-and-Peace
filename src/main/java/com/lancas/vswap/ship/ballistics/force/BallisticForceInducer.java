package com.lancas.vswap.ship.ballistics.force;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vswap.foundation.BiTuple;
import com.lancas.vswap.foundation.data.SavedBlockPos;
import com.lancas.vswap.ship.ballistics.api.IPhysBehaviour;
import com.lancas.vswap.ship.ballistics.data.BallisticData;
import com.lancas.vswap.ship.ballistics.data.BallisticsShipData;
import com.lancas.vswap.ship.type.ProjectileWrapper;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BallisticForceInducer implements ShipForcesInducer {
    @JsonIgnore
    private static final Vector3dc ANTI_GRAVITY = new Vector3d(0, 10, 0);
    @JsonIgnore
    private static final double PHYS_TICK_TIME = 0.01667;

    //private ProjectileWrapper projectile;
    private AirDragData airDragData;
    private PropellingData propellingData;
    private DampingData dampingData;
    private BallisticsShipData shipData;

    private double barrelLen;
    private double traveled = 0;

    private boolean terminated = false;

    //private List<SavedBlockPos> phyBehaviourBps = new ArrayList<>();
    //private List<IPhysBehaviour> physBehaviours = new ArrayList<>();
    private List<BiTuple<SavedBlockPos, IPhysBehaviour>> physBehaviours = new ArrayList<>();


    public static BallisticForceInducer apply(ServerLevel level, ProjectileWrapper inProjectile, double totalPropellantEnergy, BallisticData ballisticData) {
        ServerShip projectileShip = inProjectile.getShip(level);

        BallisticForceInducer inducer = new BallisticForceInducer();

        //inducer.projectile = inProjectile;
        inducer.airDragData = new AirDragData(level, inProjectile);
        inducer.propellingData = new PropellingData(inProjectile, totalPropellantEnergy);
        inducer.dampingData = new DampingData();
        inducer.shipData = ballisticData.shipData;
        ballisticData.componentData.getPhysBehaviours(level, (bp, pb) -> new BiTuple<>(new SavedBlockPos(bp), pb), inducer.physBehaviours);
        //ballisticData.componentData.getPhysBehaviours(level, inducer.phyBehaviourBps, inducer.physBehaviours);
        inducer.barrelLen = ballisticData.barrelData.barrelRealLen;
        inducer.terminated = false;

        projectileShip.saveAttachment(BallisticForceInducer.class, inducer);
        return inducer;
    }
    public static void clear(ServerLevel level, ServerShip projectile) {
        BallisticForceInducer inducer = projectile.getAttachment(BallisticForceInducer.class);
        if (inducer == null) return;

        //inducer.projectile = null;
        inducer.airDragData = null;
        inducer.propellingData = null;
        inducer.dampingData = null;
        inducer.shipData = null;
        inducer.physBehaviours.clear();
        inducer.terminated = true;

        projectile.saveAttachment(BallisticForceInducer.class, null);
    }
    private BallisticForceInducer() {}

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        if (terminated) return;

        PhysShipImpl physShipImpl = (PhysShipImpl)physShip;

        traveled += physShipImpl.getPoseVel().getVel().length() * PHYS_TICK_TIME;
        boolean inBarrel = traveled < barrelLen;
        if (inBarrel) {
            physShipImpl.applyInvariantForce(ANTI_GRAVITY.mul(physShipImpl.getInertia().getShipMass(), new Vector3d()));
            propellingData.applyPropellingForce(physShipImpl, traveled);
            //EzDebug.log(physShipImpl.getId() + ", apply propelling force");
        } else {
            airDragData.applyAirDrag(physShipImpl);
            dampingData.applyDamping(physShipImpl);
            //EzDebug.log(physShipImpl.getId() + ", apply drag force");
        }
        //airDragData.applyAirDrag(physShipImpl);

        if (!physBehaviours.isEmpty()) {
            /*//todo check if the size is matched
            for (int i = 0; i < phyBehaviourBps.size(); ++i) {
                physBehaviours.get(i).applyOnShip(physShipImpl, phyBehaviourBps.get(i).toBp(), shipData);
            }*/
            for (var pbTuple : physBehaviours) {
                pbTuple.getSecond().applyOnShip(physShipImpl, pbTuple.getFirst().toBp(), shipData);
            }
        }
    }
}
