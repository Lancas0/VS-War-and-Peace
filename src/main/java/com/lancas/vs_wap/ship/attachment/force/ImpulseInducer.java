package com.lancas.vs_wap.ship.attachment.force;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lancas.vs_wap.debug.EzDebug;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImpulseInducer implements ShipForcesInducer {
    @JsonIgnore
    protected static final int DEFAULT_TICKS = 6;

    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
    )
    private static class ImpulseData {
        public Vector3dc force;
        public int ticks;
        public ImpulseData() {}
        public ImpulseData(Vector3dc inForce, int inTicks) {
            force = inForce; ticks = inTicks;
        }
    }

    private List<ImpulseData> impulses = new ArrayList<>();

    //apply a force for ticks to simulate applying the impulse
    //force = impulse / ticks
    public static void apply(ServerShip ship, Vector3dc impulse, int ticks) {
        Vector3d force = impulse.div((double)ticks, new Vector3d());

        ImpulseInducer inducer = ship.getAttachment(ImpulseInducer.class);
        if (inducer == null) {
            inducer = new ImpulseInducer();
            ship.saveAttachment(ImpulseInducer.class, inducer);
        }

        inducer.impulses.add(new ImpulseData(force, ticks));
    }
    public static void apply(ServerShip ship, Vector3dc impulse) {
        apply(ship, impulse, DEFAULT_TICKS);
    }

    public ImpulseInducer() {}


    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        for (int i = impulses.size() - 1; i >= 0; --i) {
            ImpulseData impulse = impulses.get(i);
            EzDebug.log("to apply force:" + impulse.force);
            //physShip.applyInvariantForce(impulse.force);

            if (--impulse.ticks <= 0) {
                EzDebug.log("remove at " + i);
                impulses.remove(i);
            }
        }
    }
}
