package com.lancas.vswap.content.saved.vs_constraint;

import com.lancas.vswap.util.NbtBuilder;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class ConstraintTarget implements INBTSerializable<CompoundTag> {
    private boolean isGround;
    private long notGroundShipId;

    protected ConstraintTarget(boolean inIsGround, long inShipId) {
        isGround = inIsGround;
        notGroundShipId = inShipId;
    }
    public ConstraintTarget(CompoundTag saved) { deserializeNBT(saved); }

    public static ConstraintTarget ground() { return new ConstraintTarget(true, -1); }
    public static ConstraintTarget ofId(@NotNull ServerLevel level, long id) {
        long groundId = ShipUtil.getGroundId(level);
        if (groundId == id)
            return ground();
        return new ConstraintTarget(false, id);
    }
    public static ConstraintTarget ofBlockNow(@NotNull ServerLevel level, BlockPos bp) {
        ServerShip ship = ShipUtil.getServerShipAt(level, bp);
        if (ship == null)
            return ground();
        else
            return of(ship);
    }
    public static ConstraintTarget of(@NotNull ServerShip ship) { return new ConstraintTarget(false, ship.getId()); }

    public boolean is(@NotNull ServerLevel level, long otherId) {
        if (isGround)
            return ShipUtil.getGroundId(level) == otherId;
        return otherId == notGroundShipId;
    }
    //must accept vsShip's Id
    public boolean isSameShip(long otherId) {
        if (isGround)
            return false;  //ground must be ship
            //return ShipUtil.getGroundId(level) == otherId;
        return otherId == notGroundShipId;
    }

    public boolean exist(@NotNull ServerLevel level) {
        if (isGround)
            return true;
        return VSGameUtilsKt.getShipObjectWorld(level).getAllShips().contains(notGroundShipId);
    }

    public long getId(@NotNull ServerLevel level) {
        if (isGround)
            return ShipUtil.getGroundId(level);
        return notGroundShipId;
    }


    @Override
    public CompoundTag serializeNBT() {
        return new NbtBuilder()
            .putBoolean("is_ground", isGround)
            .putLong("id", notGroundShipId)
            .get();
    }
    @Override
    public void deserializeNBT(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readBooleanDo("is_ground", v -> isGround = v)
            .readLongDo("id", v -> notGroundShipId = v);
    }
}
