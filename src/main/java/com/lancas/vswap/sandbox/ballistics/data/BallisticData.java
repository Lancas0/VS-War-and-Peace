package com.lancas.vswap.sandbox.ballistics.data;

import com.lancas.vswap.ship.ballistics.data.BallisticsHitInfo;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentData;
import com.lancas.vswap.subproject.sandbox.api.component.IComponentDataReader;
import com.lancas.vswap.subproject.sandbox.ship.ISandBoxShip;
import com.lancas.vswap.util.NbtBuilder;
import net.minecraft.nbt.CompoundTag;
import org.joml.Vector3d;

public class BallisticData implements IComponentData<BallisticData>, IComponentDataReader<BallisticData> {
    public static final double TIME_OUT_SECONDS = 60;
    public static final double RANGE_OUT_LOWER_Y = -100;

    public final BallisticInitialStateSubData initialStateData = BallisticInitialStateSubData.createDefault();
    public final BallisticBarrelContextSubData barrelCtx = new BallisticBarrelContextSubData();
    public final AirDragSubData airDragData = new AirDragSubData();

    public double elapsedTime = 0;
    public boolean terminated = false;

    //public BallisticsHitInfo stuckHitInfo = null;
    public Vector3d stuckHitPos = null;

    public boolean firstTick = true;

    public int destroyedCnt = 0;

    public long vsShipFirer = -1;


    private BallisticData() {}
    public BallisticData(long inVsShipFirer, BallisticInitialStateSubData inInitialStateData, BallisticBarrelContextSubData inBarrelCtx, AirDragSubData inAirDragData) {
        vsShipFirer = inVsShipFirer;
        initialStateData.copyData(inInitialStateData);
        barrelCtx.copyData(inBarrelCtx);
        airDragData.copyData(inAirDragData);
    }
    public static BallisticData makeDefault() { return new BallisticData(); }


    @Override
    public BallisticData copyData(BallisticData src) {
        initialStateData.copyData(src.initialStateData);
        barrelCtx.copyData(src.barrelCtx);
        airDragData.copyData(src.airDragData);
        terminated = src.terminated;
        return this;
    }
    @Override
    public BallisticData overwriteDataByShip(ISandBoxShip ship) {
        initialStateData.overwriteDataByShip(ship);
        barrelCtx.overwriteDataByShip(ship);
        //airDragData.overwriteDataByShip(ship);
        return this;
    }
    @Override
    public CompoundTag saved() {
        return new NbtBuilder()
            .putLong("vs_ship_firer", vsShipFirer)
            .putCompound("initial_state_data", initialStateData.saved())
            .putCompound("state_data", barrelCtx.saved())
            .putCompound("air_drag_data", airDragData.saved())
            .putBoolean("terminated", terminated)
            .putBoolean("first_tick", firstTick)
            .putInt("destroyed_cnt", destroyedCnt)
            .putIfNonNull("stuck_hit_info", stuckHitPos, NbtBuilder::putVector3d)
            .get();
    }
    @Override
    public IComponentData<BallisticData> load(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readLongDo("vs_ship_firer", v -> vsShipFirer = v)
            .readCompoundDo("initial_state_data", initialStateData::load)
            .readCompoundDo("state_data", barrelCtx::load)
            .readCompoundDo("air_drag_data", airDragData::load)
            .readBooleanDo("terminated", v -> terminated = v)
            .readBooleanDo("first_tick", v -> firstTick = v)
            .readIntDo("destroyed_cnt", v -> destroyedCnt = v)
            .readDoIfExist("stuck_hit_info", v -> stuckHitPos = v, NbtBuilder::getNewVector3d);
        return this;
    }


}
