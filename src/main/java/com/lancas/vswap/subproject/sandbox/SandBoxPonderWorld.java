package com.lancas.vswap.subproject.sandbox;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.foundation.AlwaysSafeRemoveMap;
import com.lancas.vswap.subproject.sandbox.compact.mc.GroundShipWrapped;
import com.lancas.vswap.subproject.sandbox.constraint.SandBoxConstraintSolver;
import com.lancas.vswap.subproject.sandbox.ship.SandBoxPonderShip;
import com.lancas.vswap.subproject.sandbox.thread.SandBoxThreadRegistry;
import com.lancas.vswap.subproject.sandbox.thread.impl.ponder.SandBoxPonderMainThread;
import com.simibubi.create.foundation.ponder.PonderWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class SandBoxPonderWorld implements ISandBoxWorld<SandBoxPonderShip> {
    protected PonderWorld world;
    protected final AlwaysSafeRemoveMap<UUID, SandBoxPonderShip> ponderShips = new AlwaysSafeRemoveMap<>();
    protected final Set<UUID> hidingShip = ConcurrentHashMap.newKeySet();
    protected GroundShipWrapped wrappedGroundShip;
    protected SandBoxConstraintSolver constraintSolver = new SandBoxConstraintSolver(this);

    protected final SandBoxPonderMainThread mainThread;
    protected final SandBoxThreadRegistry<SandBoxPonderWorld> threadRegistry = new SandBoxThreadRegistry<>();
    public SandBoxPonderMainThread getMainThread() { return mainThread; }
    public void setPhysTimeScale(double tickRate) { mainThread.setPhysTimeScale(tickRate); }
    public void resetPhysTimeScale() { mainThread.setPhysTimeScale(1); }



    private static SandBoxPonderWorld Instance;
    public static Optional<SandBoxPonderWorld> getInstance() { return Optional.ofNullable(Instance); }
    public SandBoxPonderWorld(PonderWorld inWorld) {
        world = inWorld;
        wrappedGroundShip = new GroundShipWrapped(UUID.randomUUID());

        mainThread = new SandBoxPonderMainThread(this);
        //SandBoxPonderPhysThread physThread = new SandBoxPonderPhysThread(this);

        threadRegistry.register(mainThread);
        //threadRegistry.register(physThread);

        mainThread.start();
        //physThread.start();
        Instance = this;
    }

    @Override
    public PonderWorld getWorld() {
        return world;
    }

    @Override
    public @Nullable SandBoxPonderShip getShip(UUID uuid) { return ponderShips.get(uuid); }
    @Override
    public GroundShipWrapped wrapOrGetGround() {
        return wrappedGroundShip;
        //return wrappedGroundShip;
    }

    //public Stream<SandBoxPonderShip> allPonderShips() { return ponderShips.values(); }
    @Override
    public Stream<SandBoxPonderShip> allShips() { return ponderShips.values(); }
    //public Stream<SandBoxPonderShip> allShowingShips() { return ponderShips.values().filter(x -> !hidingShip.contains(x.getUuid())); }

    public void addShip(SandBoxPonderShip ship) {
        if (ponderShips.containsKey(ship.getUuid())) {
            EzDebug.warn("the clientShip with uuid:" + ship.getUuid() + " is existed, may fail to add");
        }

        if (ponderShips.putIfAbsent(ship.getUuid(), ship) == null) {  //if successfully add
            ship.inWorld = true;
        }
    }

    public boolean containsShip(UUID uuid) {
        return ponderShips.containsKey(uuid);
    }

    public boolean isShipHiding(UUID uuid) {
        return hidingShip.contains(uuid);
    }

    @Override
    public void markShipDeleted(UUID uuid) {
        ponderShips.markKeyRemoved(uuid);
        //todo also delete hiding ship?
    }

    @Override
    public void markAllDeleted() {
        ponderShips.markRemoveIf((uuid, ship) -> true);
    }

    public void setShipHideState(UUID uuid, boolean hide) {
        if (hide) {
            hidingShip.add(uuid);
        } else {
            hidingShip.remove(uuid);
        }

        if (!containsShip(uuid)) {
            EzDebug.warn("try hide or unHide a unexisted ship! Anyway the hiding process is completed.");
        }
    }

    @Override
    public SandBoxConstraintSolver getConstraintSolver() {
        return constraintSolver;
    }
}
