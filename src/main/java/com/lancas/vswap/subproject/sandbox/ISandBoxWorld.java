package com.lancas.vswap.subproject.sandbox;

import com.lancas.vswap.subproject.sandbox.constraint.SandBoxConstraintSolver;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.stream.Stream;

public interface ISandBoxWorld<TShip extends ISandBoxShip> {
    public static ISandBoxWorld<?> fromLevel(@NotNull Level level) {
        if (level.isClientSide)
            return SandBoxClientWorld.INSTANCE;
        return SandBoxServerWorld.getOrCreate((ServerLevel)level);
    }

    public Level getWorld();

    public @Nullable TShip getShip(UUID uuid);
    //public TShip getShipIncludeVS(UUID uuid);
    //public TShip getShipIncludeVSAndGround(UUID uuid);
    //public WrappedVsShip wrapOrGetVs(Ship vsShip);
    public ISandBoxShip wrapOrGetGround();

    public default ISandBoxShip getShipOrGround(UUID uuid) {
        ISandBoxShip ship = getShip(uuid);
        if (ship != null)
            return ship;

        ISandBoxShip ground = wrapOrGetGround();
        return ground.getUuid().equals(uuid) ? ground : null;
    }

    //use stream because it don't provide remove operation.
    //user must use markShipDeleted, which provide safety.
    public Stream<TShip> allShips();
    //public Stream<TShip> allShipsIncludeVs();

    public void markShipDeleted(UUID uuid);
    public void markAllDeleted();

    //public boolean shouldRun();

    public SandBoxConstraintSolver getConstraintSolver();//= new SandBoxConstraintSolver();
}
